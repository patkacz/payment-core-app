package com.flatpay.pay_app.tasks

import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.navigation.NavigationModel
import com.flatpay.common.screens.Screen
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.common.workflows.TaskStatus
import com.flatpay.log.AppLog
import com.flatpay.common.core.model.Dependetncies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyProcessingTask : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies,
    ): TaskResult {
        AppLog.LOGI("ProcessingTask: execute()")
        // show processing screen
        AppLog.LOGI("ProcessingTask: waiting......${dependencies.eventBus}\")")

        //example usage of events

        CoroutineScope(Dispatchers.Main.immediate).launch {
            //dependencies.eventBus.post(AppEvent.Navigation.NavigateTo(Screen.Processing))
            context.get<NavigationModel>()?.navigateTo(Screen.Processing)
            //NavigationState.instance.navigateTo(Screen.Processing)
        }
        //context.get<NavigationModel>()?.navigateTo(Screen.Processing)
        //NavigationState.instance.navigateTo(Screen.Processing)
        //context.get<NavigationModel>()?.navigateTo(Screen.Main)

        AppLog.LOGI("ProcessingTask: ends")
        return TaskResult.ResultCode(TaskStatus.OK)
    }
}
