package com.flatpay.common.workflows

import org.junit.Assert.*
import org.junit.Test

class TaskResultTest {
    @Test
    fun `isJumping returns true when nextWorkflowItem is set and retCode is OK`() {
        val result = TaskResult("NextTask", TaskResult.ResultCodes.OK)
        assertTrue(result.isJumping)
    }

    @Test
    fun `isJumping returns false when nextWorkflowItem is null`() {
        val result = TaskResult(null, TaskResult.ResultCodes.OK)
        assertFalse(result.isJumping)
    }

    @Test
    fun `isJumping returns false when retCode is not OK`() {
        val result = TaskResult("NextTask", TaskResult.ResultCodes.ERROR)
        assertFalse(result.isJumping)
    }

    @Test
    fun `constructor with only retCode sets nextWorkflowItem to null`() {
        val result = TaskResult(TaskResult.ResultCodes.CANCEL)
        assertNull(result.nextWorkflowItem)
        assertEquals(TaskResult.ResultCodes.CANCEL, result.retCode)
    }
}