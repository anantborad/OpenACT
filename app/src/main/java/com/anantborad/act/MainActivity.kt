package com.anantborad.act

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anantborad.act.core.ui.theme.ACTTheme
import com.anantborad.act.main.presentation.ui.navigation.ActNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ACTTheme {
                ActNavHost()
            }
        }
    }
}
