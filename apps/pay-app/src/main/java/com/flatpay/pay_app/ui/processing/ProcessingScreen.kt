package com.flatpay.pay_app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.flatpay.pay_app.R
import com.flatpay.pay_app.ui.processing.ProcessingViewModel

@Composable
fun ProcessingScreen(myViewModel: ProcessingViewModel, navController: NavController) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = R.drawable.processing,
            contentDescription = "Loading GIF"
        )
    }
}
