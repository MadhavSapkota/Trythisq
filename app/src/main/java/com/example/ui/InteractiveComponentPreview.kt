package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun parseHexColor(hex: String): Color {
    val cleanHex = hex.replace("#", "").trim()
    return try {
        if (cleanHex.length == 6) {
            Color(android.graphics.Color.parseColor("#$cleanHex"))
        } else if (cleanHex.length == 8) {
            Color(android.graphics.Color.parseColor("#$cleanHex"))
        } else {
            Color(0xFF6750A4) // Fallback default purple
        }
    } catch (e: Exception) {
        Color(0xFF6750A4)
    }
}

@Composable
fun InteractiveComponentPreview(
    type: String,
    name: String,
    colorHex: String,
    borderRadius: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val parsedColor = parseHexColor(colorHex)
    val shape = RoundedCornerShape(borderRadius.dp)

    Box(
        modifier = modifier
            .testTag("interactive_preview_container")
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            "Button" -> {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = parsedColor),
                    shape = shape,
                    modifier = Modifier.testTag("preview_button")
                ) {
                    Text(text = text, color = Color.White)
                }
            }
            "Badge" -> {
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(parsedColor.copy(alpha = 0.15f))
                        .border(1.dp, parsedColor, shape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("preview_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = parsedColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            "Card" -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = parsedColor.copy(alpha = 0.05f)),
                    shape = shape,
                    border = androidx.compose.foundation.BorderStroke(2.dp, parsedColor),
                    modifier = Modifier
                        .widthIn(min = 120.dp, max = 220.dp)
                        .testTag("preview_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = parsedColor
                        )
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "Unknown Component: $type",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
