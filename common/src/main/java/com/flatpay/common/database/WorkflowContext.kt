package com.flatpay.common.database

import android.content.Context
import com.flatpay.common.TransactionQueries
import com.flatpay.common.Txn
import com.flatpay.log.AppLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random
import kotlin.reflect.KClass

// Define the wrapper for the object and its manipulator
data class ObjectEntry(
    val instance: AtomicReference<Any>,
    val manipulator: AtomicReference<Any>
)

class WorkflowContext(val context: Context, private val txnUUID: String? = null) {
    private val database = DatabaseModule(context).database

    // Define a registry that maps KClass to a wrapper containing the object and its manipulation class
    val registryMap: MutableMap<KClass<*>, ObjectEntry> = mutableMapOf()

    val controllerManager = ControllerManager()

    private fun generateUUIDv7(): String {
        val timestamp = System.currentTimeMillis()

        // 48 bits for timestamp
        val timestampHigh = (timestamp shr 16).toInt() and 0xFFFF
        val timestampLow = (timestamp and 0xFFFF).toInt()

        // 4 bits for version (0111 for UUIDv7)
        val version = 0x7 shl 12

        // Combine timestampHigh and version
        val mostSignificantBits =
            ((timestampHigh.toLong() shl 32) or (version.toLong() shl 16) or timestampLow.toLong())

        // 62 bits for random
        val randomBits = Random.nextLong(0, 1L shl 62)

        // Add variant (2 most significant bits must be 10)
        val leastSignificantBits = (randomBits and 0x3FFFFFFFFFFFFFFFL) or (0x2L shl 62)

        // Convert to UUID string format
        return UUID(mostSignificantBits, leastSignificantBits).toString()
    }

    init {
        if (txnUUID == null) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)

            val txn = Txn(
                uuid = generateUUIDv7(),
                amount = 0L,
                currencyCode = "EUR",
                currencyNumber = "208",
                createdAt = formattedDateTime,
                transactionStatus = TransactionStatus.IN_PROGRESS,
                stan = null,
                paymentTechnology = PaymentTechnology.NONE,
                paymentMethodDetails = null,
            )

            database.transactionQueries.insert(txn)

            register(txn, database.transactionQueries)
        } else {
            val txn = database.transactionQueries.get(txnUUID).executeAsOne()
            register(txn, database.transactionQueries)
        }
    }

    // Generic register method
    private inline fun <reified T : Any, reified M : Any> register(instance: T, manipulator: M) {
        AppLog.LOGI("WorkflowContext: Register class = ${T::class.simpleName}, query = ${M::class.simpleName}")
        registryMap[T::class] = ObjectEntry(AtomicReference(instance), AtomicReference(manipulator))
    }

    // Function to retrieve a object by type
    inline fun <reified T : Any> get(): T? {
        return registryMap[T::class]?.instance?.get() as? T
    }

    inline fun <reified T : Any> set(data: T): T? {
        val container = registryMap[T::class]
        if (container != null) {
            container.instance.set(data)
            registryMap[T::class] = container
            return container.instance.get() as T
        }

        return null
    }

    // Function to save a object by type
    inline fun <reified T : Any> save() {
        val instance = registryMap[T::class]?.instance?.get() as? T
        val manipulator = registryMap[T::class]?.manipulator?.get() as? T

        if (instance != null && manipulator != null) {
            if (instance is Txn)
                (manipulator as TransactionQueries).insertOrReplace(instance as Txn)
        }
    }

    // Function to retrieve a query object by type
    inline fun <reified T : Any, reified M : Any> query(): M? {
        return registryMap[T::class]?.manipulator?.get() as? M
    }

    // Generic register method
    inline fun <reified T> registerController(instance: T) {
        controllerManager.register<T>(instance)
    }

    inline fun <reified T> getController(): T? {
        return controllerManager.getController<T>()
    }
}
