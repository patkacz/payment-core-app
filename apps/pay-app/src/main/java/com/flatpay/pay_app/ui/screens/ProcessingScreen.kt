package com.flatpay.pay_app.ui.screens

import androidx.collection.emptyLongSet
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.flatpay.pay_app.R
import com.flatpay.pay_app.viewmodels.AppViewModel

@Composable
fun ProcessingScreen(myViewModel: AppViewModel, navController: NavController) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            AsyncImage(
                model = R.drawable.processing,
                contentDescription = "Loading GIF"
            )
    }
}
