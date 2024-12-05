package com.flatpay.log

import android.util.Log

class AppLog {
        companion object {
            private const val TAG = "payment-core-app"
            fun LOGI(text: String) {
                Log.i(TAG,text)
            }
            fun LOGE(text: String) {
                Log.e(TAG,text)
            }
            fun LOGD(text: String) {
                Log.d(TAG,text)
            }
       }
}

