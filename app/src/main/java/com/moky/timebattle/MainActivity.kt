package com.moky.timebattle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.ui.navigation.AppNavigation
import com.moky.timebattle.ui.theme.TimebattleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimebattleTheme {
                val viewModel: AppViewModel = viewModel(
                    factory = AppViewModelFactory(application)
                )
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
