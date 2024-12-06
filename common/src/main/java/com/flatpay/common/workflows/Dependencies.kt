package com.flatpay.common.workflows

import com.flatpay.common.protocol.IHostProtocolHandler
import com.flatpay.common.emvEngines.IPaymentEngineHandler
import com.flatpay.common.view.models.BaseViewModel
import com.flatpay.log.AppLog
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

enum class DependencyKey {
    DATABASE,
    CURRENT_VIEW_MODEL,
    HOST_PROTOCOL_HANDLER,
    PAYMENT_ENGINE_HANDLER
}

class Dependencies {
    private val dependencies = mutableMapOf<DependencyKey, Any>()  // ENUM instead of string ?
    //private val dependencies = ConcurrentHashMap<DependencyKey, Any>()
    // database handler
    private val database = AtomicReference<IDatabase?>()

    // Need o have here multiple view models?
    // Consider here ModelFactory
    // View Model to operate on GUI
    private var currentViewModel by AtomicPropertyDelegate<BaseViewModel>(dependencies, DependencyKey.CURRENT_VIEW_MODEL)

    // Host protocol handler, this allow to build different messages
    // it depend on host type
    private var hostProtocolHandler by AtomicPropertyDelegate<IHostProtocolHandler>(dependencies, DependencyKey.HOST_PROTOCOL_HANDLER)
    // Host protocol handler, this allow to build different messages
    // it depend on host type
    private var paymentEngineHandler by AtomicPropertyDelegate<IPaymentEngineHandler>(dependencies, DependencyKey.PAYMENT_ENGINE_HANDLER)
    fun retrieveHostProtocolHandler() = hostProtocolHandler
    fun retrievePaymentEngineHandler() = paymentEngineHandler
    fun retrieveCurrentViewModel() = currentViewModel

    fun initDependencies() {
        AppLog.LOGI("Dependencies init")
    }

    fun setDatabase(db: IDatabase?) {
        database.set(db)
        dependencies[DependencyKey.DATABASE] = database
    }

    fun getDatabase(): IDatabase {
        return database.get() ?: throw IllegalStateException("Database is null")
    }

    fun hasDatabase(): Boolean {
        return database.get() != null
    }

    fun hasDependency(key: DependencyKey): Boolean = dependencies.containsKey(key)

    private inline fun <reified T> getDependency(key: DependencyKey): T {
        return dependencies[key] as? T
            ?: throw IllegalStateException("Dependency $key not found or wrong type")
    }
}