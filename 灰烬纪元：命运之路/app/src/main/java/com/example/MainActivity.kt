package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.MainUiView
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Obtain the main game state model with SQLite persistence
    val viewModel = ViewModelProvider(this)[GameViewModel::class.java]
    
    setContent {
      MyApplicationTheme(dynamicColor = false, darkTheme = true) {
        MainUiView(viewModel = viewModel)
      }
    }
  }
}
