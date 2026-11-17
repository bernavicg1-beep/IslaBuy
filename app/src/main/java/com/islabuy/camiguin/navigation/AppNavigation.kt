package com.islabuy.camiguin.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.islabuy.camiguin.ui.screens.HomeScreen
import com.islabuy.camiguin.ui.screens.WelcomeScreen

@Composable
fun AppNavigation() {

    var currentScreen by remember {
        mutableStateOf("welcome")
    }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        when (currentScreen) {

            "welcome" -> {
                WelcomeScreen(
                    onGetStarted = {
                        currentScreen = "home"
                    },
                    onLogin = {
                        currentScreen = "home"
                    }
                )
            }

            "home" -> {
                HomeScreen(
                    selectedIndex = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                    }
                )
            }
        }
    }
}