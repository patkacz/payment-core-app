package com.flatpay.common.database

import com.flatpay.log.AppLog
import java.util.concurrent.atomic.AtomicReference

class ControllerManager {
    val registryMap: MutableMap<String, AtomicReference<Any>> = mutableMapOf()

    // Generic register method
    inline fun <reified T> register(instance: T) {
        val controllerBaseClass = T::class.supertypes.firstOrNull().toString()
        AppLog.LOGI("ControllerManager: Register controller = ${T::class.simpleName} as $controllerBaseClass")
        registryMap[controllerBaseClass] = AtomicReference(instance)
    }

    inline fun <reified T> getController(): T? {
        //AppLog.LOGI("ControllerManager: getController ${T::class.qualifiedName}")
        val controller = registryMap[T::class.qualifiedName]?.get() as? T
        //AppLog.LOGI("ControllerManager: getController val = $controller")
        return controller
    }
}
