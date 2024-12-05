package com.flatpay.common.workflows

import com.flatpay.common.MockLogger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class TestTask1 : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

class TestTask2 : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

class TestTask3 : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

class TestTask4 : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

// Hook tasks
class PreHookTask : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

class PostHookTask : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

// Additional test task for cancellation testing
class CancellingPreHookTask : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.CANCEL)
    }
}

class CancellingPostHookTask : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult(TaskResult.ResultCodes.CANCEL)
    }
}

// Decision maker task that jumps to TestTask3
class TestTaskDecisionMaker : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        // Create a TaskResult with the name of the task to jump to
        return TaskResult("TestTask3", TaskResult.ResultCodes.OK)
    }
}

// Additional decision maker that jumps back to TestTask2
class AnotherDecisionMaker : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult("TestTask2", TaskResult.ResultCodes.OK)
    }
}

// Task that attempts to jump to a non-existent task
class InvalidJumpTask : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult("NonExistentTask", TaskResult.ResultCodes.OK)
    }
}

// Hook that jumps to a specific task
class JumpingPreHookTask : Task() {
    override suspend fun execute(context: DBContext, dependencies: Dependencies): TaskResult {
        WorkflowTest.taskExecutionList.add(taskName)
        return TaskResult("TestTask4", TaskResult.ResultCodes.OK)
    }
}

class WorkflowTest {
    private lateinit var workflow: Workflow

    companion object {
        // Make it accessible to test tasks
        var taskExecutionList: MutableList<String> = mutableListOf()
    }

    @Before
    fun setup() {
        MockLogger.setup()
        workflow = Workflow()
        taskExecutionList = mutableListOf()
    }

    @Test
    fun `addItem adds task to workflow`() = runTest {
        workflow.addItem<TestTask1>()
        assertEquals(1, workflow.workflowItems.size)
        assertTrue(workflow.workflowItems[0] is TestTask1)
    }

    @Test
    fun `getItem returns correct task`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()

        val task = workflow.getItem<TestTask1>()
        assertTrue(task is TestTask1)
    }

    @Test
    fun `insertBefore adds task in correct position`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()
        workflow.insertBefore<TestTask1, TestTask2>()

        assertEquals(3, workflow.workflowItems.size)
        assertTrue(workflow.workflowItems[1] is TestTask1)
    }

    @Test
    fun `execute processes all tasks in order`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()
        workflow.addItem<TestTask3>()
        workflow.addItem<TestTask4>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.OK, result.retCode)
        assertEquals(listOf("TestTask1", "TestTask2", "TestTask3", "TestTask4"), taskExecutionList)
    }
    @Test
    fun `pre-hooks and post-hooks are executed in the correct order`() = runTest {
        // Add main tasks
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()
        workflow.addItem<TestTask3>()
        workflow.addItem<TestTask4>()

        // Add hooks
        workflow.getItem<TestTask1>().addPreHook<PreHookTask>()
        workflow.getItem<TestTask1>().addPostHook<PostHookTask>()
        workflow.getItem<TestTask3>().addPostHook<PostHookTask>()
        workflow.getItem<TestTask4>().addPreHook<PreHookTask>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.OK, result.retCode)
        assertEquals(
            listOf(
                "PreHookTask",  // First pre-hook executes
                "TestTask1",    // Then main task
                "PostHookTask", // Then post-hook
                "TestTask2",     // Then next task
                "TestTask3",
                "PostHookTask",
                "PreHookTask",
                "TestTask4"
            ),
            taskExecutionList
        )
    }

    @Test
    fun `workflow stops when pre-hook returns CANCEL`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()

        // Add cancelling pre-hook
        workflow.getItem<TestTask1>().addPreHook<CancellingPreHookTask>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.CANCEL, result.retCode)
        assertEquals(
            listOf("CancellingPreHookTask"), // Only pre-hook executed
            taskExecutionList
        )
    }

    @Test
    fun `workflow stops when post-hook returns CANCEL`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()

        // Add cancelling post-hook
        workflow.getItem<TestTask1>().addPostHook<CancellingPostHookTask>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.CANCEL, result.retCode)
        assertEquals(
            listOf(
                "TestTask1",
                "CancellingPostHookTask"
            ),
            taskExecutionList
        )
    }

    @Test
    fun `workflow supports jumping to specific tasks`() = runTest {
        // Add tasks in sequence
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTaskDecisionMaker>()
        workflow.addItem<TestTask2>()
        workflow.addItem<TestTask3>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.OK, result.retCode)
        assertEquals(
            listOf(
                "TestTask1",           // First task executes normally
                "TestTaskDecisionMaker", // Decision maker executes
                "TestTask3"            // Jumps directly to Task3, skipping Task2
            ),
            taskExecutionList
        )
    }

    @Test
    fun `workflow supports multiple jumps in sequence`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<AnotherDecisionMaker>()
        workflow.addItem<TestTask4>()
        workflow.addItem<TestTask2>()
        workflow.addItem<TestTaskDecisionMaker>()
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask3>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.OK, result.retCode)
        assertEquals(
            listOf(
                "TestTask1",
                "AnotherDecisionMaker",
                "TestTask2",
                "TestTaskDecisionMaker",
                "TestTask3"
            ),
            taskExecutionList
        )
    }

    @Ignore("Not working properly for now")
    @Test
    fun `workflow handles invalid jump targets gracefully`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<InvalidJumpTask>()
        workflow.addItem<TestTask2>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.CANCEL, result.retCode)
        assertEquals(
            listOf(
                "TestTask1",
                "InvalidJumpTask"
            ),
            taskExecutionList
        )
    }

    @Test
    fun `workflow handles jumps in hooks`() = runTest {
        workflow.addItem<TestTask1>()
        workflow.addItem<TestTask2>()
        workflow.addItem<TestTask3>()
        workflow.addItem<TestTask4>()

        // Add a jumping hook
        workflow.getItem<TestTask2>().addPreHook<JumpingPreHookTask>()

        val context = DBContext()
        val dependencies = Dependencies()
        val result = workflow.execute(context, dependencies)

        assertEquals(TaskResult.ResultCodes.OK, result.retCode)
        assertEquals(
            listOf(
                "TestTask1",
                "JumpingPreHookTask",
                "TestTask4"
            ),
            taskExecutionList
        )
    }
}

