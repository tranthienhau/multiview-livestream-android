package com.hautt.multiviewstream.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hautt.multiviewstream.model.StreamLayout
import com.hautt.multiviewstream.ui.components.PerformanceOverlayView
import com.hautt.multiviewstream.ui.fullscreen.FullscreenPlayerScreen
import com.hautt.multiviewstream.ui.grid.MultiStreamGridView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val sources by viewModel.playerManager.sources.collectAsState()
    val statuses by viewModel.playerManager.statuses.collectAsState()
    val currentLayout by viewModel.currentLayout.collectAsState()
    val primaryIndex by viewModel.primaryIndex.collectAsState()
    val showPerformance by viewModel.showPerformanceOverlay.collectAsState()
    val showFullscreen by viewModel.showFullscreen.collectAsState()
    val fullscreenIndex by viewModel.fullscreenIndex.collectAsState()
    val fps by viewModel.performanceMonitor.fps.collectAsState()
    val memoryMB by viewModel.performanceMonitor.memoryMB.collectAsState()

    DisposableEffect(Unit) {
        viewModel.initialize()
        onDispose { viewModel.cleanup() }
    }

    if (showFullscreen && fullscreenIndex < sources.size) {
        val source = sources[fullscreenIndex]
        FullscreenPlayerScreen(
            source = source,
            player = viewModel.playerManager.players[source.id],
            status = statuses[source.id] ?: com.hautt.multiviewstream.model.StreamStatus.IDLE,
            onDismiss = { viewModel.dismissFullscreen() }
        )
        return
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MultiView Stream",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Klic.gg Style Multi-Cam",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(if (showPerformance) "Hide Stats" else "Show Stats")
                            },
                            onClick = {
                                viewModel.togglePerformanceOverlay()
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Layout toolbar
                LayoutToolbar(
                    currentLayout = currentLayout,
                    onLayoutSelected = { viewModel.setLayout(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // Stream grid
                MultiStreamGridView(
                    sources = sources,
                    players = viewModel.playerManager.players,
                    statuses = statuses,
                    layout = currentLayout,
                    primaryIndex = primaryIndex,
                    onTileTap = { viewModel.handleTileTap(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            // Performance overlay
            if (showPerformance) {
                PerformanceOverlayView(
                    fps = fps,
                    memoryMB = memoryMB,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun LayoutToolbar(
    currentLayout: StreamLayout,
    onLayoutSelected: (StreamLayout) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StreamLayout.entries.forEach { layout ->
            val isSelected = layout == currentLayout
            val icon = when (layout) {
                StreamLayout.GRID_2X2 -> Icons.Default.GridView
                StreamLayout.PRIMARY_WITH_THUMBNAILS -> Icons.Default.ViewAgenda
                StreamLayout.SIDE_BY_SIDE -> Icons.Default.ViewColumn
            }

            LayoutButton(
                icon = icon,
                label = layout.displayName,
                isSelected = isSelected,
                onClick = { onLayoutSelected(layout) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LayoutButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(
                if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 9.sp
        )
    }
}
