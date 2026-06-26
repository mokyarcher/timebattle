package com.moky.timebattle.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.LeaderboardEntry
import com.moky.timebattle.data.model.User
import com.moky.timebattle.ui.components.icons.groupIcon
import com.moky.timebattle.ui.components.icons.homeIcon
import com.moky.timebattle.ui.components.icons.taskIcon
import com.moky.timebattle.ui.components.icons.tradeIcon
import com.moky.timebattle.ui.components.icons.userIcon
import com.moky.timebattle.ui.screens.AllianceScreen
import com.moky.timebattle.ui.screens.GameOverScreen
import com.moky.timebattle.ui.screens.HomeScreen
import com.moky.timebattle.ui.screens.LoginScreen
import com.moky.timebattle.ui.screens.NotificationsScreen
import com.moky.timebattle.ui.screens.LeaderboardScreen
import com.moky.timebattle.ui.screens.ProfileScreen
import com.moky.timebattle.ui.screens.PublishTaskScreen
import com.moky.timebattle.ui.screens.SettingsScreen
import com.moky.timebattle.ui.screens.TasksScreen
import com.moky.timebattle.ui.screens.TradeMarketScreen
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight

sealed class Screen(val route: String, val label: String) {
    data object Login : Screen("login", "登录")
    data object Home : Screen("home", "主页")
    data object Tasks : Screen("tasks", "任务")
    data object PublishTask : Screen("publish_task", "发布任务")
    data object Notifications : Screen("notifications", "通知")
    data object Trade : Screen("trade", "交易")
    data object Alliance : Screen("alliance", "联盟")
    data object Profile : Screen("profile", "我的")
    data object Leaderboard : Screen("leaderboard", "排行榜")
    data object Settings : Screen("settings", "设置")
    data object GameOver : Screen("game_over", "终局")
}

val bottomNavItems = listOf(
    Screen.Home to { homeIcon() },
    Screen.Tasks to { taskIcon() },
    Screen.Trade to { tradeIcon() },
    Screen.Profile to { userIcon() }
)

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    navController: NavHostController = rememberNavController()
) {
    val state by viewModel.state.collectAsState()
    val currentRoute = currentRoute(navController)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showMessage: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val showBottomNav = currentRoute in listOf(Screen.Home.route, Screen.Tasks.route, Screen.Trade.route, Screen.Profile.route)

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = if (showBottomNav) 72.dp else 16.dp)
            )
        },
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        containerColor = AbyssBlack
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (state.user.isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClick = {
                        viewModel.login()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onShowMessage = showMessage,
                    onTaskClick = { taskId ->
                        viewModel.completeTask(taskId)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToAlliance = {
                        navController.navigate(Screen.Alliance.route)
                    },
                    onNavigateToSync = {
                        showMessage("同步功能即将开放")
                    }
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen(
                    viewModel = viewModel,
                    onShowMessage = showMessage,
                    onBack = {
                        navController.popBackStack()
                    },
                    onPublishTask = {
                        navController.navigate(Screen.PublishTask.route)
                    }
                )
            }
            composable(Screen.PublishTask.route) {
                PublishTaskScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onShowMessage = showMessage
                )
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Trade.route) {
                TradeMarketScreen(
                    viewModel = viewModel,
                    onShowMessage = showMessage
                )
            }
            composable(Screen.Alliance.route) {
                AllianceScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToLeaderboard = {
                        navController.navigate(Screen.Leaderboard.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            composable(Screen.Settings.route) {
                val state by viewModel.state.collectAsState()
                SettingsScreen(
                    vibrationEnabled = state.vibrationEnabled,
                    onVibrationToggle = { viewModel.setVibrationEnabled(it) },
                    onClearCache = {
                        viewModel.clearCache()
                        showMessage("缓存已清除")
                    },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onRestart = {
                        viewModel.restart()
                        showMessage("已重新开始")
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Settings.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Leaderboard.route) {
                val entries = generateLeaderboard(state.user)
                LeaderboardScreen(entries = entries)
            }
            composable(Screen.GameOver.route) {
                GameOverScreen(
                    viewModel = viewModel,
                    onRestart = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.GameOver.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }

    // 生命值归零时自动跳转到终局页
    val shouldGoGameOver = state.user.isLoggedIn && state.user.remainingSeconds <= 0 && currentRoute != Screen.GameOver.route
    LaunchedEffect(shouldGoGameOver) {
        if (shouldGoGameOver) {
            navController.navigate(Screen.GameOver.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(AbyssBlack)
            .border(1.dp, StrokeLight)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { (screen, icon) ->
            val selected = currentRoute == screen.route
            val color = if (selected) LifeRed else DimWhite
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { onItemClick(screen.route) }
                    )
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon(),
                    contentDescription = screen.label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = screen.label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = color,
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
        )
    }
}

private fun generateLeaderboard(currentUser: User): List<LeaderboardEntry> {
    val base = listOf(
        LeaderboardEntry(rank = 1, nickname = "星河", value = 168 * 3600L),
        LeaderboardEntry(rank = 2, nickname = "夜行者", value = 132 * 3600L),
        LeaderboardEntry(rank = 3, nickname = "北风", value = 96 * 3600L),
        LeaderboardEntry(rank = 4, nickname = "灰鸦", value = 84 * 3600L),
        LeaderboardEntry(rank = 5, nickname = "赤焰", value = 72 * 3600L)
    )
    val userEntry = LeaderboardEntry(
        rank = 0,
        nickname = currentUser.nickname.ifBlank { "你" },
        value = currentUser.remainingSeconds,
        isCurrentUser = true
    )
    val sorted = (base + userEntry).sortedByDescending { it.value }
    return sorted.mapIndexed { index, entry ->
        entry.copy(rank = index + 1)
    }
}

@Composable
private fun currentRoute(navController: NavController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: Screen.Login.route
}
