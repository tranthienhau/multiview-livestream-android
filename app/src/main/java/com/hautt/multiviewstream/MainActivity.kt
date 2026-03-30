package com.hautt.multiviewstream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hautt.multiviewstream.ui.home.HomeScreen
import com.hautt.multiviewstream.ui.theme.MultiViewStreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiViewStreamTheme {
                HomeScreen()
            }
        }
    }
}
