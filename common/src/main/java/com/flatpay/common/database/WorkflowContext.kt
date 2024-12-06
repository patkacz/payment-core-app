package com.flatpay.common.database

import android.content.Context
import com.flatpay.common.Txn
import com.flatpay.log.AppLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random

data class ObjectWithMethod(
    var data: AtomicReference<Any>,
    val query: AtomicReference<Any>
)

class WorkflowContext(val context: Context, val txnUUID: String? = null) {
    val database = DatabaseModule(context).database
    val registryMap: MutableMap<String, ObjectWithMethod> = mutableMapOf()


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
    fun <T : Any> register(instance: T, query: Any) {
        val className = instance::class.simpleName ?: "Unknown?"
        AppLog.LOGI("DBContext: className = ${className}, instance = $instance")

        registryMap[className] =
            ObjectWithMethod(data = AtomicReference(instance), query = AtomicReference(query))
    }

    // Function to retrieve a object by type
    //@Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> get(): T? {
        val className = T::class.simpleName ?: "Unknown?"
        return registryMap[className]?.data?.get() as? T
    }

    inline fun <reified T : Any> set(instance: T): T? {
        val className = T::class.simpleName ?: "Unknown?"

        var container = registryMap[className]
        if (container != null) {
            container.data.set(instance)
            registryMap[className] = container
            return container.data.get() as T
        }

        return null
    }

    // Function to save a object by type
    //@Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> save() {
        val className = T::class.simpleName ?: "Unknown?"
        val obj = registryMap[className]?.data?.get() as? Txn

        if (obj != null) {
            database.transactionQueries.insertOrReplace(obj)
        }
    }

    // Function to retrieve a query object by type
    //@Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> query(): Any? {
        val className = T::class.simpleName ?: "Unknown?"
        return registryMap[className]?.query?.get()
    }
}
