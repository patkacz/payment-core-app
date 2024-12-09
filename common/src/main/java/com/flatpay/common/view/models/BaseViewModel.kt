package com.flatpay.common.view.models

import androidx.lifecycle.ViewModel
import java.beans.PropertyChangeListener


abstract class BaseViewModel : ViewModel(), PropertyChangeListener
{
    abstract fun navigateToNextScreen()
    abstract fun hideProcessingScreen()

}