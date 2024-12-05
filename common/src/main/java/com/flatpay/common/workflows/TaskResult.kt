package com.flatpay.common.workflows

data class TaskResult(
    var nextWorkflowItem: String? = null, // Equivalent to TaskName nextItem, can be nullable
    var retCode: ResultCodes = ResultCodes.OK // Default to OK as in the constructor
) {

    /** Predefined result codes */
    enum class ResultCodes {
        OK,         /** Successful, normal processing executed */
        CANCEL,     /** Cancelled, due to cancel event or other termination event */
        ERROR,      /** Error, due to a handled error condition */
        TIMEOUT     /** Time-out, operation exceeded the allowed time-out */
    }

    constructor(retCode: ResultCodes) : this(null, retCode)

    /** Check whether task should jump to another task specified by nextItem. */
    fun isJumping(): Boolean {
        return nextWorkflowItem != null && retCode == ResultCodes.OK
    }
}