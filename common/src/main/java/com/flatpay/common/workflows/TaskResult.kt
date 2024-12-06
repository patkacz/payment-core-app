package com.flatpay.common.workflows

/**
 * Represents the result of a task execution in a workflow.
 *
 * This class encapsulates both the result status of a task execution and any
 * workflow control information (like jumping to another task).
 *
 * @property nextWorkflowItem The name of the next task to execute when jumping is needed
 * @property retCode The result code indicating the task execution status
 */
data class TaskResult(
    val nextWorkflowItem: String? = null, // Equivalent to TaskName nextItem, can be nullable
    val retCode: ResultCodes = ResultCodes.OK // Default to OK as in the constructor
) {

    /**
     * Predefined result codes for task execution outcomes.
     */
    enum class ResultCodes {
        /** Task completed successfully */
        OK,

        /** Task execution was cancelled */
        CANCEL,

        /** Task encountered an error during execution */
        ERROR,

        /** Task execution exceeded the allowed time limit */
        TIMEOUT
    }

    /**
     * Secondary constructor for creating a TaskResult with just a result code.
     *
     * @param retCode The result code for the task
     */
    constructor(retCode: ResultCodes) : this(null, retCode)

    /**
     * Indicates whether the workflow should jump to another task.
     *
     * @return true if there's a next item specified and the result is OK,
     *         false otherwise
     */
    val isJumping: Boolean
        get() = nextWorkflowItem != null && retCode == ResultCodes.OK
}
