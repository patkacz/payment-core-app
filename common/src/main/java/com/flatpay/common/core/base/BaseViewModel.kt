package com.flatpay.common.core.base

import androidx.lifecycle.ViewModel
import com.flatpay.common.database.WorkflowContext
import kotlinx.coroutines.Job
import java.beans.PropertyChangeListener


abstract class BaseViewModel : ViewModel(), PropertyChangeListener
{
    abstract val context: WorkflowContext
    protected var currentOperation: Job? = null
    //protected var waitingTimeout: Time

    fun cancelCurrentOperation() {
        currentOperation?.cancel()
    }
}