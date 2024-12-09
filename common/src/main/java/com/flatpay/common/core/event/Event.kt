package com.flatpay.common.core.event

import android.view.SurfaceControl

sealed class AppEvent {

    data class ChipCardInserted(val cardId: String) : AppEvent()
    object ChipCardRemoved : AppEvent()
    data class GuiResultEvent(val message: String) : AppEvent()
    object Cancel : AppEvent()
    data class CardDataAvailable(val cardData: String) : AppEvent()

    object HardwareEvent : AppEvent() //
    object NetworkEvent : AppEvent()

}