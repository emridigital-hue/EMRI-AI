package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtRedDark
import com.example.viewmodel.DashboardTab

@Composable
fun AppHeader(
    currentTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    historyCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo: YT Voice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("brand_logo_row")
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(YtRed, YtRedDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "YT",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = YtRed
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Voice",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "AI Voice-over YouTube Studio",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Studio Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00C853))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Studio v1.0",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            // Navigation Tabs (Voice Generator, History, Settings)
            TabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                        color = YtRed,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = currentTab == DashboardTab.GENERATOR,
                    onClick = { onTabSelected(DashboardTab.GENERATOR) },
                    modifier = Modifier.testTag("tab_generator"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.GENERATOR) Icons.Filled.Mic else Icons.Outlined.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Voice Generator",
                                fontWeight = if (currentTab == DashboardTab.GENERATOR) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                )

                Tab(
                    selected = currentTab == DashboardTab.HISTORY,
                    onClick = { onTabSelected(DashboardTab.HISTORY) },
                    modifier = Modifier.testTag("tab_history"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "History" + if (historyCount > 0) " ($historyCount)" else "",
                                fontWeight = if (currentTab == DashboardTab.HISTORY) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                )

                Tab(
                    selected = currentTab == DashboardTab.SETTINGS,
                    onClick = { onTabSelected(DashboardTab.SETTINGS) },
                    modifier = Modifier.testTag("tab_settings"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Settings",
                                fontWeight = if (currentTab == DashboardTab.SETTINGS) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                )
            }
        }
    }
}
