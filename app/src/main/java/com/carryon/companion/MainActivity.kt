package com.carryon.companion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carryon.companion.ui.theme.CarryOnCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarryOnCompanionTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CompanionScreen()
                }
            }
        }
    }
}

@Composable
private fun CompanionScreen(viewModel: CompanionViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var seekMsInput by remember { mutableStateOf("60000") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ),
            )
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "CarryOn Companion",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Connect to your CarryOn Cinema server and control playback.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.baseUrl,
                    onValueChange = viewModel::updateBaseUrl,
                    label = { Text("Server base URL") },
                    placeholder = { Text("https://your-server:9000") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = uiState.bearerToken,
                    onValueChange = viewModel::updateBearerToken,
                    label = { Text("Bearer token") },
                    placeholder = { Text("Paste token from CarryOn Cinema") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = viewModel::connect,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(18.dp)
                                .width(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Checking...")
                    } else {
                        Text("Test connection")
                    }
                }
                uiState.statusMessage?.let {
                    Text(
                        text = it,
                        color = uiState.statusColor,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Playback controls", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::pausePlayback, modifier = Modifier.weight(1f)) {
                        Text("Pause")
                    }
                    Button(onClick = viewModel::resumePlayback, modifier = Modifier.weight(1f)) {
                        Text("Resume")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = seekMsInput,
                        onValueChange = { seekMsInput = it },
                        label = { Text("Seek (ms)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = { viewModel.seekPlayback(seekMsInput.toLongOrNull() ?: 0L) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Seek")
                    }
                }
                Button(
                    onClick = viewModel::refreshStatus,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text("Refresh status")
                }
                uiState.playbackStatus?.let { status ->
                    Text(
                        text = "${if (status.isPlaying) "Playing" else "Paused"} • ${status.positionMs} / ${status.durationMs}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    status.mediaId?.let { mediaId ->
                        Text(
                            text = "Media ID: $mediaId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Next steps", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "• Use /api/watch/{mediaId} to resolve stream URLs.\n" +
                        "• Plug in Media3 for local playback.\n" +
                        "• Add Live TV channel browsing and remote controls.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
