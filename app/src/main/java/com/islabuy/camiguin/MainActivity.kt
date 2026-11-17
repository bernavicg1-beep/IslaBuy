package com.islabuy.camiguin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.islabuy.camiguin.navigation.AppNavigation
import com.islabuy.camiguin.ui.theme.IslaBuyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            IslaBuyTheme {
                AppNavigation()
            }
        }
    }
}