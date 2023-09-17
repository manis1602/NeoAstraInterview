package com.neoastra.neoastrainterview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.neoastra.neoastrainterview.ui.theme.NeoAstraInterviewTheme
import com.neoastra.neoastrainterview.views.home_screen.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeoAstraInterviewTheme {
                HomeScreen()
            }
        }
    }
}