package com.flatpay.common

import com.flatpay.log.AppLog
import io.mockk.justRun
import io.mockk.mockkObject

object MockLogger {
    fun setup() {
        mockkObject(AppLog)
        justRun { AppLog.LOGI(any()) }
        justRun { AppLog.LOGD(any()) }
        justRun { AppLog.LOGE(any()) }
    }
}
