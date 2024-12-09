package com.flatpay.common.workflows

import com.flatpay.common.database.WorkflowContext
import io.mockk.mockk
import com.flatpay.common.core.model.Dependencies
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class TaskTest {
    class TestTask : Task() {
        override suspend fun execute(
            context: WorkflowContext,
            dependencies: Dependencies
        ): TaskResult {
            return TaskResult.ResultCode(TaskStatus.OK)
        }
    }

    class TestHook : Task() {
        override suspend fun execute(
            context: WorkflowContext,
            dependencies: Dependencies
        ): TaskResult {
            return TaskResult.ResultCode(TaskStatus.OK)
        }
    }

    @Test
    fun `task name is set correctly`() {
        val task = TestTask()
        assertEquals("TestTask", task.taskName)
    }

    @Test
    fun `addPreHook adds hook correctly`() = runTest {
        val task = TestTask()
        task.addPreHook<TestHook>()

        assertTrue(task.hasPreHooks())
        assertEquals(1, task.preHooks.size)
        assertTrue(task.preHooks[0] is TestHook)
    }

    @Test
    fun `addPostHook adds hook correctly`() = runTest {
        val task = TestTask()
        task.addPostHook<TestHook>()

        assertTrue(task.hasPostHooks())
        assertEquals(1, task.postHooks.size)
        assertTrue(task.postHooks[0] is TestHook)
    }

    @Test
    fun `runTask executes task and returns result`() = runTest {
        val task = TestTask()
        val mockContext = mockk<WorkflowContext>(relaxed = true) {}
        val dependencies = Dependencies()

        val result = task.runTask(mockContext, dependencies)
        assertEquals(TaskResult.ResultCode(TaskStatus.OK), result)
    }
}
