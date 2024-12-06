package com.flatpay.common.workflows

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import java.util.concurrent.atomic.AtomicReference

class AtomicPropertyDelegate<T>(
    private val dependencies: MutableMap<DependencyKey, Any>,
    private val key: DependencyKey
) : ReadWriteProperty<Any?, T> {
    private val ref = AtomicReference<T?>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        ref.get() ?: throw IllegalStateException("${property.name} not initialized")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        dependencies[key] = ref
        ref.set(value)
    }
}