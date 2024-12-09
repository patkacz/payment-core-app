package com.flatpay.common.workflows

import kotlin.reflect.KClass

/**
 * Predefined result codes for task execution outcomes.
 */
enum class TaskStatus {
    /** Task completed successfully */
    OK,

    /** Task execution was cancelled */
    CANCEL,

    /** Task encountered an error during execution */
    ERROR,

    /** Task execution exceeded the allowed time limit */
    TIMEOUT
}

sealed class TaskResult() {
    /**
     * Represents a result that returns a `ResultCodes`.
     */
    data class ResultCode(val status: TaskStatus) : TaskResult()

    /**
     * Represents a result that returns the next workflow item.
     */
    data class NextWorkflowItem(val nextItem: KClass<*>) : TaskResult()
}
