package com.flatpay.payment_core_app

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.flatpay.pay_app.ui.main.MainActivity

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createComposeRule() // Create a Compose Test Rule

    @Test
    fun testMainActivity_onCreate() {

        composeTestRule.setContent {
        }

    }
}
