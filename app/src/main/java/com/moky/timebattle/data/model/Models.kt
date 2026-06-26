package com.moky.timebattle.data.model

import java.util.UUID

enum class TaskType { SYSTEM, USER, LIMITED }

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class TaskStatus { AVAILABLE, IN_PROGRESS, COMPLETED, CLAIMED }

data class User(
    val id: String = UUID.randomUUID().toString(),
    val nickname: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val remainingSeconds: Long = 7 * 24 * 3600, // 初始生命：7 天
    val maxRemainingSeconds: Long = 7 * 24 * 3600,
    val isLoggedIn: Boolean = false
)

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val rewardSeconds: Long,
    val type: TaskType = TaskType.SYSTEM,
    val status: TaskStatus = TaskStatus.AVAILABLE,
    val publisher: String? = null,
    val remainingQuota: Int? = null,
    val actionText: String = "领取",
    val createdAt: Long = System.currentTimeMillis()
)

data class TradeOffer(
    val id: String = UUID.randomUUID().toString(),
    val sellerName: String,
    val amountSeconds: Long,
    val pricePerHour: Double, // 单价：每小时的金币/积分，这里简化为「信誉点」
    val totalPrice: Double
)

data class LeaderboardEntry(
    val rank: Int,
    val nickname: String,
    val value: Long, // 时长或交易量
    val isCurrentUser: Boolean = false
)

data class AppState(
    val user: User = User(),
    val tasks: List<TaskItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val tradeOffers: List<TradeOffer> = emptyList(),
    val checkInStreak: Int = 0,
    val lastCheckInDate: String? = null,
    val totalEarnedSeconds: Long = 0,
    val totalTaskCount: Int = 0,
    val totalTradeCount: Int = 0,
    val reputationPoints: Int = 100, // 信誉点，交易用
    val vibrationEnabled: Boolean = true
)

fun Long.formatAsLifeTime(): String {
    val total = coerceAtLeast(0)
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    val seconds = total % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun Long.formatCompact(): String {
    val total = coerceAtLeast(0)
    val days = total / (24 * 3600)
    val hours = (total % (24 * 3600)) / 3600
    val minutes = (total % 3600) / 60
    return when {
        days > 0 -> "${days}d ${hours}h ${minutes}m"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

fun Long.formatReward(): String {
    val total = coerceAtLeast(0)
    val hours = total / 3600
    val minutes = (total % 3600) / 60
    return String.format("+%dh %02dm", hours, minutes)
}
