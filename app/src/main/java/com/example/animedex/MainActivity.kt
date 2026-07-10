package com.example.animedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.animedex.navigation.AnimeDexNavHost
import com.example.animedex.ui.theme.AnimeDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimeDexTheme {
                AnimeDexNavHost()
            }
        }
    }
}
