package com.ditzdev.ceditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ditzdev.ceditor.ui.screen.setup.SetupPage
import com.ditzdev.ceditor.ui.theme.CEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CEditorTheme {
                SetupPage()
            }
        }
    }
}