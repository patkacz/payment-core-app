package com.flatpay.pay_app

import android.app.Application
import com.flatpay.common.core.event.EventBus
import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.core.model.Dependencies
import com.flatpay.common.navigation.NavigationModel

class PaymentApplication : Application() {
    private val _dependencies by lazy { Dependencies() }
    private val _workflowContext by lazy { WorkflowContext(applicationContext) }

    // NavigationModel needs the Application instance, so we can't use lazy here
    private lateinit var _navigationModel: NavigationModel

    // Public read-only access if needed
    val dependencies: Dependencies get() = _dependencies
    val workflowContext: WorkflowContext get() = _workflowContext
    val eventBus: EventBus get() = EventBus.instance
    val navigationModel: NavigationModel get() = _navigationModel

    override fun onCreate() {
        super.onCreate()
        try {
            initializeApplication()
        } catch (e: Exception) {
            handleInitializationError(e)
        }
    }

    private fun initializeApplication() {
        initializeNavigation()
        initializeEventBus()
        initializeDependencies()
        initializeWorkflow()
    }

    private fun initializeNavigation() {
        _navigationModel = NavigationModel()
    }

    private fun initializeEventBus() {
        eventBus.initialize()

    }

    private fun initializeDependencies() {
        // Initialize dependencies in specific order
        dependencies.eventBus = eventBus

    }

    private fun initializeWorkflow() {
        // Initialize workflow specific components
    }

    private fun handleInitializationError(error: Exception) {
        // Log error, show user feedback, etc.
    }

    companion object {
        @Volatile
        private var INSTANCE: PaymentApplication? = null

        fun getInstance(): PaymentApplication =
            INSTANCE ?: throw IllegalStateException("Application not initialized")

    }

    init {
        if (INSTANCE != null) {
            throw IllegalStateException("Multiple instances not allowed")
        }
        INSTANCE = this
    }
}