package com.flatpay.common.core.model

import android.app.Application
import com.flatpay.common.host.IHostProtocolController
import com.flatpay.common.emvEngines.IPaymentEngineHandler
import com.flatpay.common.core.base.BaseViewModel
import com.flatpay.common.core.event.EventBus


enum class DependencyKey {
    DATABASE,
    CURRENT_VIEW_MODEL,
    HOST_PROTOCOL_HANDLER,
    PAYMENT_ENGINE_HANDLER
}

interface AppProvider {
    fun provideApplication(): Application
}

class Dependencies {

    lateinit var eventBus: EventBus
    private val dependencies = mutableMapOf<DependencyKey, Any>()  // ENUM instead of string ?
    //private val dependencies = ConcurrentHashMap<DependencyKey, Any>()
    // database handler


    // Need o have here multiple view models?
    // Consider here ModelFactory
    // View Model to operate on GUI
    var currentViewModel by AtomicPropertyDelegate<BaseViewModel>(dependencies,
        DependencyKey.CURRENT_VIEW_MODEL
    )

    // Host protocol handler, this allow to build different messages
    // it depend on host type
    private var hostProtocolHandler by AtomicPropertyDelegate<IHostProtocolController>(dependencies,
        DependencyKey.HOST_PROTOCOL_HANDLER
    )
    // Host protocol handler, this allow to build different messages
    // it depend on host type
    private var paymentEngineHandler by AtomicPropertyDelegate<IPaymentEngineHandler>(dependencies,
        DependencyKey.PAYMENT_ENGINE_HANDLER
    )
    fun retrieveHostProtocolHandler() = hostProtocolHandler
    fun retrievePaymentEngineHandler() = paymentEngineHandler
    fun retrieveCurrentViewModel() = currentViewModel


    fun hasDependency(key: DependencyKey): Boolean = dependencies.containsKey(key)

    private inline fun <reified T> getDependency(key: DependencyKey): T {
        return dependencies[key] as? T
            ?: throw IllegalStateException("Dependency $key not found or wrong type")
    }
}