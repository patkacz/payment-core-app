package com.flatpay.common.core.event

import com.flatpay.common.screens.Screen


sealed class AppEvent {

    data class ChipCardInserted(val cardId: String) : AppEvent()
    object ChipCardRemoved : AppEvent()
    data class GuiResultEvent(val message: String) : AppEvent()
    object Cancel : AppEvent()
    data class CardDataAvailable(val cardData: String) : AppEvent()

    sealed class Navigation : AppEvent() {
        data class NavigateTo(val screen: Screen) : Navigation()  // Extends Navigation
        object NavigateNext : Navigation()        // Extends Navigation
    }

    object HardwareEvent : AppEvent() //
    object NetworkEvent : AppEvent()

}