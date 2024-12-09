package com.flatpay.pay_app

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.flatpay.common.core.event.EventBus
import com.flatpay.common.core.model.Dependencies
import com.flatpay.pay_app.navigation.NavigationState
import com.flatpay.pay_app.data.datastore.DataStore

class PaymentApplication : Application() {
    companion object {
        lateinit var instance: PaymentApplication
            private set
    }

    lateinit var dependencies: Dependencies
    lateinit var navigationState: NavigationState
        private set
    private lateinit var dataStore: DataStore
    private lateinit var eventBus: EventBus

    override fun onCreate() {
        super.onCreate()
        instance = this
        navigationState = NavigationState()
        dependencies = Dependencies()
        eventBus = EventBus()   //Events, hardware and gui
        initializeDependencies()
    }

    private fun initializeDependencies() {
        eventBus.initialize()

        // Sdk.PaymentReader.initialize(this)
        // DatabaseHandler.initialize(this)
    }

    fun getEventBus(): EventBus = eventBus
}