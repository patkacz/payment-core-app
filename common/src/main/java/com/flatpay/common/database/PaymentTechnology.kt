package com.flatpay.common.database

enum class PaymentTechnology {
    NONE,
    ICC,
    CONTACTLESS,
    MAGSTRIPE,
    PKE,
    ALTERNATIVE_PAYMENT,
    CASH
}