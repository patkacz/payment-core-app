package com.flatpay.common.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import java.beans.PropertyChangeListener


abstract class BaseViewModel : ViewModel(), PropertyChangeListener
{
    protected var currentOperation: Job? = null
    //protected var waitingTimeout: Time

    fun cancelCurrentOperation() {
        currentOperation?.cancel()
    }
}