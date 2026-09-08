package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Voice
import kotlinx.coroutines.launch
import com.example.model.VoiceCatalog
import com.example.ui.components.AppHeader
import com.example.ui.components.AudioPlayerCard
import com.example.ui.components.GenerateVoiceButton
import com.example.ui.components.HistoryCard
import com.example.ui.components.ScriptInputCard
import com.example.ui.components.SettingsCard
import com.example.ui.components.VoiceSelectionCard
import com.example.ui.components.VoiceSettingsCard
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.YtRed
import com.example.viewmodel.DashboardTab
import com.example.viewmodel.YtVoiceViewModel

@Composable
fun DashboardScreen(
    viewModel: YtVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val scriptText by viewModel.scriptText.collectAsStateWithLifecycle()
    val selectedGender by viewModel.selectedGender.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val voiceSettings by viewModel.voiceSettings.collectAsStateWithLifecycle()
    val previewingVoiceId by viewModel.previewingVoiceId.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generationProgress by viewModel.generationProgress.collectAsStateWithLifecycle()
    val lastResult by viewModel.lastResult.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val notification by viewModel.notification.collectAsStateWithLifecycle()
    val validationError by viewModel.validationError.collectAsStateWithLifecycle()

    val activeEngineType by viewModel.activeEngineType.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val customApiEndpoint by viewModel.customApiEndpoint.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 840
    val mobileListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(it.message)
            viewModel.dismissNotification()
        }
    }

    LaunchedEffect(lastResult) {
        if (lastResult != null && !isWideScreen) {
            mobileListState.animateScrollToItem(5)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) },
                historyCount = historyList.size
            )
        },
        bottomBar = {
            if (currentTab == DashboardTab.GENERATOR && !isWideScreen) {
                StickyGeneratorBottomBar(
                    selectedVoice = selectedVoice,
                    charCount = scriptText.length,
                    charLimit = viewModel.charLimit,
                    isGenerating = isGenerating,
                    progress = generationProgress,
                    onGenerate = { viewModel.generateVoice() },
                    onSelectVoiceClick = {
                        coroutineScope.launch {
                            mobileListState.animateScrollToItem(2)
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val wideScreenLayout = maxWidth >= 840.dp || isWideScreen

            when (currentTab) {
                DashboardTab.GENERATOR -> {
                    if (wideScreenLayout) {
                        // Wide screen / Tablet Studio layout (2 columns)
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Left Column: Script & Voice selection
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    MainContentHeader()
                                }
                                item {
                                    ScriptInputCard(
                                        text = scriptText,
                                        onTextChanged = { viewModel.onScriptTextChanged(it) },
                                        onClear = { viewModel.clearScriptText() },
                                        onUseSample = { viewModel.setSampleScript() },
                                        charLimit = viewModel.charLimit,
                                        warningThreshold = viewModel.warningThreshold,
                                        validationError = validationError
                                    )
                                }
                                item {
                                    VoiceSelectionCard(
                                        selectedGender = selectedGender,
                                        selectedVoice = selectedVoice,
                                        previewingVoiceId = previewingVoiceId,
                                        onGenderSelected = { viewModel.onGenderSelected(it) },
                                        onVoiceSelected = { viewModel.onVoiceSelected(it) },
                                        onPreviewClicked = { viewModel.previewVoice(it) }
                                    )
                                }
                                item {
                                    GenerateVoiceButton(
                                        isGenerating = isGenerating,
                                        progress = generationProgress,
                                        charCount = scriptText.length,
                                        charLimit = viewModel.charLimit,
                                        onGenerate = { viewModel.generateVoice() }
                                    )
                                }
                            }

                            // Right Column: Settings, Audio Player & History
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    VoiceSettingsCard(
                                        settings = voiceSettings,
                                        onSpeedChanged = { viewModel.onSpeedChanged(it) },
                                        onPitchChanged = { viewModel.onPitchChanged(it) },
                                        onVolumeChanged = { viewModel.onVolumeChanged(it) },
                                        onReset = { viewModel.resetSettings() }
                                    )
                                }
                                item {
                                    AudioPlayerCard(
                                        result = lastResult,
                                        playerState = playerState,
                                        onPlayPause = { viewModel.playCurrentResult() },
                                        onSeek = { viewModel.audioPlayerController.seekTo(it) },
                                        onVolumeChanged = { viewModel.audioPlayerController.setVolume(it) },
                                        onDownloadMp3 = { path, name -> viewModel.downloadOrExport(path, name) },
                                        onShare = { path, name -> viewModel.shareAudio(path, name) }
                                    )
                                }
                                item {
                                    HistoryCard(
                                        historyList = historyList,
                                        playerState = playerState,
                                        onPlayItem = { viewModel.playHistoryItem(it) },
                                        onDownloadItem = { path, name -> viewModel.downloadOrExport(path, name) },
                                        onDeleteItem = { viewModel.deleteHistoryItem(it) },
                                        onClearAll = { viewModel.clearAllHistory() }
                                    )
                                }
                            }
                        }
                    } else {
                        // Mobile Portrait layout (Single fluid column)
                        LazyColumn(
                            state = mobileListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    MainContentHeader()
                                    StudioWorkflowSteps(
                                        selectedVoiceName = selectedVoice.displayName,
                                        hasResult = lastResult != null,
                                        onStepClick = { stepIndex ->
                                            coroutineScope.launch {
                                                mobileListState.animateScrollToItem(stepIndex)
                                            }
                                        }
                                    )
                                }
                            }
                            // 1. Textarea naskah & 2. Character counter
                            item {
                                ScriptInputCard(
                                    text = scriptText,
                                    onTextChanged = { viewModel.onScriptTextChanged(it) },
                                    onClear = { viewModel.clearScriptText() },
                                    onUseSample = { viewModel.setSampleScript() },
                                    charLimit = viewModel.charLimit,
                                    warningThreshold = viewModel.warningThreshold,
                                    validationError = validationError
                                )
                            }
                            // 3. Voice selection
                            item {
                                VoiceSelectionCard(
                                    selectedGender = selectedGender,
                                    selectedVoice = selectedVoice,
                                    previewingVoiceId = previewingVoiceId,
                                    onGenderSelected = { viewModel.onGenderSelected(it) },
                                    onVoiceSelected = { viewModel.onVoiceSelected(it) },
                                    onPreviewClicked = { viewModel.previewVoice(it) }
                                )
                            }
                            // 4. Voice settings
                            item {
                                VoiceSettingsCard(
                                    settings = voiceSettings,
                                    onSpeedChanged = { viewModel.onSpeedChanged(it) },
                                    onPitchChanged = { viewModel.onPitchChanged(it) },
                                    onVolumeChanged = { viewModel.onVolumeChanged(it) },
                                    onReset = { viewModel.resetSettings() }
                                )
                            }
                            // 5. Generate Voice Button
                            item {
                                GenerateVoiceButton(
                                    isGenerating = isGenerating,
                                    progress = generationProgress,
                                    charCount = scriptText.length,
                                    charLimit = viewModel.charLimit,
                                    onGenerate = { viewModel.generateVoice() }
                                )
                            }
                            // 6. Result Audio Player
                            item {
                                AudioPlayerCard(
                                    result = lastResult,
                                    playerState = playerState,
                                    onPlayPause = { viewModel.playCurrentResult() },
                                    onSeek = { viewModel.audioPlayerController.seekTo(it) },
                                    onVolumeChanged = { viewModel.audioPlayerController.setVolume(it) },
                                    onDownloadMp3 = { path, name -> viewModel.downloadOrExport(path, name) },
                                    onShare = { path, name -> viewModel.shareAudio(path, name) }
                                )
                            }
                            // 7. History
                            item {
                                HistoryCard(
                                    historyList = historyList,
                                    playerState = playerState,
                                    onPlayItem = { viewModel.playHistoryItem(it) },
                                    onDownloadItem = { path, name -> viewModel.downloadOrExport(path, name) },
                                    onDeleteItem = { viewModel.deleteHistoryItem(it) },
                                    onClearAll = { viewModel.clearAllHistory() }
                                )
                            }
                        }
                    }
                }

                DashboardTab.HISTORY -> {
                    // Full History tab view
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Persistent audio player at the top of history if playing
                            AudioPlayerCard(
                                result = lastResult,
                                playerState = playerState,
                                onPlayPause = { viewModel.playCurrentResult() },
                                onSeek = { viewModel.audioPlayerController.seekTo(it) },
                                onVolumeChanged = { viewModel.audioPlayerController.setVolume(it) },
                                onDownloadMp3 = { path, name -> viewModel.downloadOrExport(path, name) },
                                onShare = { path, name -> viewModel.shareAudio(path, name) }
                            )
                        }
                        item {
                            HistoryCard(
                                historyList = historyList,
                                playerState = playerState,
                                onPlayItem = { viewModel.playHistoryItem(it) },
                                onDownloadItem = { path, name -> viewModel.downloadOrExport(path, name) },
                                onDeleteItem = { viewModel.deleteHistoryItem(it) },
                                onClearAll = { viewModel.clearAllHistory() }
                            )
                        }
                    }
                }

                DashboardTab.SETTINGS -> {
                    // Full Settings tab view
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            SettingsCard(
                                activeEngineType = activeEngineType,
                                customApiKey = customApiKey,
                                customEndpoint = customApiEndpoint,
                                onSaveConfig = { type, key, endpoint ->
                                    viewModel.updateEngineConfig(type, key, endpoint)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContentHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("main_content_header")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(YtRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Buat Voice-over YouTube",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Ubah naskah menjadi suara natural dalam beberapa detik.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StudioWorkflowSteps(
    selectedVoiceName: String,
    hasResult: Boolean,
    onStepClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { onStepClick(1) },
            label = { Text("1. Naskah", style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = YtRed
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )

        AssistChip(
            onClick = { onStepClick(2) },
            label = {
                Text("2. Suara ($selectedVoiceName)", style = MaterialTheme.typography.labelMedium, maxLines = 1)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = YtRed
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )

        AssistChip(
            onClick = { onStepClick(3) },
            label = { Text("3. Pengaturan", style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )

        if (hasResult) {
            AssistChip(
                onClick = { onStepClick(5) },
                label = { Text("4. Putar Audio", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SuccessGreen
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = SuccessGreen.copy(alpha = 0.15f),
                    labelColor = SuccessGreen
                )
            )
        }
    }
}

@Composable
fun StickyGeneratorBottomBar(
    selectedVoice: Voice,
    charCount: Int,
    charLimit: Int,
    isGenerating: Boolean,
    progress: Float,
    onGenerate: () -> Unit,
    onSelectVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExceeded = charCount > charLimit
    val isBlank = charCount == 0
    val canGenerate = !isGenerating && !isExceeded && !isBlank

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Voice & Char info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onSelectVoiceClick)
                    .padding(end = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = YtRed
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedVoice.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$charCount / $charLimit karakter",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Ganti Suara",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = YtRed
                    )
                }
            }

            // Action Button: Generate Voice
            Button(
                onClick = onGenerate,
                enabled = canGenerate,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YtRed,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                modifier = Modifier
                    .height(46.dp)
                    .testTag("button_sticky_generate")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Generate",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
