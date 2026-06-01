package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val LightColorScheme =
  lightColorScheme(
    primary = ConsolePrimary,
    onPrimary = Color.White,
    secondary = ActivePill,
    onSecondary = OnActivePill,
    background = CosmicBackground,
    surface = SurfaceGrey,
    onBackground = CosmicText,
    onSurface = CosmicText
  )

@Composable
fun MyApplicationTheme(
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val context = LocalContext.current
      dynamicLightColorScheme(context)
    } else {
      LightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
