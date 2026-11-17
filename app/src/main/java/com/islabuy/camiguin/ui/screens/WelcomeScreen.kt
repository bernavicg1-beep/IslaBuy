package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.islabuy.camiguin.R
import com.islabuy.camiguin.ui.components.*
import com.islabuy.camiguin.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(
                R.drawable.camiguin_splash
            ),
            contentDescription = "Camiguin Island",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.13f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.islabuy_logo
                    ),
                    contentDescription = "IslaBuy Logo",
                    modifier = Modifier.size(170.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Isla",
                        color = NavyBlue,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Buy",
                        color = IslaOrange,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Surface(
                    color = NavyBlue,
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "Camiguin Edition",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(
                            horizontal = 22.dp,
                            vertical = 7.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Buy Local. Sell Local.\nSupport Every Islander.",
                    color = NavyBlue,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 31.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                IslaBuyPrimaryButton(
                    text = "Get Started  →",
                    onClick = onGetStarted
                )

                Spacer(modifier = Modifier.height(14.dp))

                IslaBuyOutlineButton(
                    text = "I already have an account",
                    onClick = onLogin
                )
            }
        }
    }
}