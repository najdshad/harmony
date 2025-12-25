package com.harmony.player

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.harmony.player.ui.theme.HarmonyTheme

@Composable
fun MusicPlayerScreen() {
    Text("Harmony Music Player - Setup Complete")
}

@Preview(showBackground = true)
@Composable
fun MusicPlayerScreenPreview() {
    HarmonyTheme {
        MusicPlayerScreen()
    }
}
