package com.moky.timebattle.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moky.timebattle.data.local.LocalStorage
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.NotificationItem
import com.moky.timebattle.data.model.TaskItem
import com.moky.timebattle.data.model.TaskStatus
import com.moky.timebattle.data.model.TaskType
import com.moky.timebattle.data.model.TradeOffer
import com.moky.timebattle.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel(
    application: Application,
    private val enableTimer: Boolean = true
) : AndroidViewModel(application) {

    private val storage = LocalStorage(application.applicationContext)

    private val _state = MutableStateFlow(loadInitialState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        com.moky.timebattle.util.VibrationHelper.enabled = _state.value.vibrationEnabled
        if (enableTimer) {
            startLifeTimer()
        }
    }

    private fun loadInitialState(): AppState {
        val saved = storage.loadAppState()
        val tasks = saved.tasks.ifEmpty { defaultTasks() }
        val offers = saved.tradeOffers.ifEmpty { defaultTradeOffers() }
        return saved.copy(tasks = tasks, tradeOffers = offers)
    }

    private fun defaultTradeOffers(): List<TradeOffer> = listOf(
        TradeOffer(sellerName = "夜行者", amountSeconds = 3600, pricePerHour = 12.0, totalPrice = 12.0),
        TradeOffer(sellerName = "星河", amountSeconds = 7200, pricePerHour = 11.5, totalPrice = 23.0),
        TradeOffer(sellerName = "北风", amountSeconds = 1800, pricePerHour = 13.0, totalPrice = 6.5),
        TradeOffer(sellerName = "灰鸦", amountSeconds = 10800, pricePerHour = 10.0, totalPrice = 30.0)
    )

    private fun defaultTasks(): List<TaskItem> = listOf(
        TaskItem(
            title = "每日签到",
            description = "连续签到递增奖励，7天达最大收益",
            rewardSeconds = 30 * 60,
            type = TaskType.SYSTEM,
            actionText = "领取"
        ),
        TaskItem(
            title = "完成新手引导",
            description = "浏览核心功能，了解时间经济体系",
            rewardSeconds = 3 * 60 * 60,
            type = TaskType.SYSTEM,
            actionText = "前往"
        ),
        TaskItem(
            title = "翻译一篇短文",
            description = "@夜行者 · 剩余 2 名额",
            rewardSeconds = 5 * 60 * 60 + 30 * 60,
            type = TaskType.USER,
            publisher = "夜行者",
            remainingQuota = 2,
            actionText = "接取"
        ),
        TaskItem(
            title = "设计一张海报",
            description = "@星河 · 剩余 1 名额",
            rewardSeconds = 12 * 60 * 60,
            type = TaskType.USER,
            publisher = "星河",
            remainingQuota = 1,
            actionText = "接取"
        )
    )

    private fun startLifeTimer() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _state.update { current ->
                    if (!current.user.isLoggedIn || current.user.remainingSeconds <= 0) {
                        current
                    } else {
                        val newRemaining = current.user.remainingSeconds - 1
                        val updatedUser = current.user.copy(remainingSeconds = newRemaining)
                        val newState = current.copy(user = updatedUser)
                        storage.saveAppState(newState)
                        newState
                    }
                }
            }
        }
    }

    fun login(nickname: String = "管理局成员") {
        _state.update { current ->
            val user = current.user.copy(
                nickname = nickname.ifBlank { "管理局成员" },
                isLoggedIn = true
            )
            val newState = current.copy(user = user)
            storage.saveAppState(newState)
            newState
        }
    }

    fun claimTask(taskId: String) {
        _state.update { current ->
            val task = current.tasks.find { it.id == taskId } ?: return@update current
            val tasks = current.tasks.map { t ->
                if (t.id == taskId && t.status == TaskStatus.AVAILABLE) {
                    t.copy(status = TaskStatus.IN_PROGRESS)
                } else t
            }
            val newState = current.copy(
                tasks = tasks,
                totalTaskCount = current.totalTaskCount + 1
            )
            storage.saveAppState(newState)
            newState
        }
    }

    fun completeTask(taskId: String) {
        _state.update { current ->
            val task = current.tasks.find { it.id == taskId } ?: return@update current
            val tasks = current.tasks.map { t ->
                if (t.id == taskId) t.copy(status = TaskStatus.CLAIMED) else t
            }
            val updatedUser = current.user.copy(
                remainingSeconds = current.user.remainingSeconds + task.rewardSeconds
            )
            val newState = current.copy(user = updatedUser, tasks = tasks)
            storage.saveAppState(newState)
            newState
        }
    }

    fun publishTask(title: String, description: String, rewardSeconds: Long) {
        _state.update { current ->
            val newTask = TaskItem(
                title = title,
                description = "@${current.user.nickname} · 刚刚发布",
                rewardSeconds = rewardSeconds,
                type = TaskType.USER,
                publisher = current.user.nickname,
                remainingQuota = 1,
                actionText = "接取"
            )
            val notification = NotificationItem(
                title = "任务发布成功",
                message = "你发布的「$title」已上架任务中心"
            )
            val updatedUser = current.user.copy(
                remainingSeconds = (current.user.remainingSeconds - rewardSeconds).coerceAtLeast(0)
            )
            val newState = current.copy(
                user = updatedUser,
                tasks = current.tasks + newTask,
                notifications = listOf(notification) + current.notifications
            )
            storage.saveAppState(newState)
            newState
        }
    }

    fun buyTime(offerId: String): Boolean {
        var success = false
        _state.update { current ->
            val offer = current.tradeOffers.find { it.id == offerId } ?: return@update current
            if (current.reputationPoints < offer.totalPrice.toInt()) {
                return@update current
            }
            success = true
            val updatedUser = current.user.copy(
                remainingSeconds = current.user.remainingSeconds + offer.amountSeconds
            )
            val notification = NotificationItem(
                title = "交易成功",
                message = "你从 ${offer.sellerName} 处购买了 ${offer.amountSeconds / 3600}h ${(offer.amountSeconds % 3600) / 60}m"
            )
            val newState = current.copy(
                user = updatedUser,
                reputationPoints = current.reputationPoints - offer.totalPrice.toInt(),
                tradeOffers = current.tradeOffers.filter { it.id != offerId },
                totalTradeCount = current.totalTradeCount + 1,
                notifications = listOf(notification) + current.notifications
            )
            storage.saveAppState(newState)
            newState
        }
        return success
    }

    fun sellTime(amountSeconds: Long, pricePerHour: Double): Boolean {
        var success = false
        _state.update { current ->
            if (current.user.remainingSeconds < amountSeconds) {
                return@update current
            }
            success = true
            val totalPrice = amountSeconds / 3600.0 * pricePerHour
            val updatedUser = current.user.copy(
                remainingSeconds = current.user.remainingSeconds - amountSeconds
            )
            val offer = TradeOffer(
                sellerName = current.user.nickname,
                amountSeconds = amountSeconds,
                pricePerHour = pricePerHour,
                totalPrice = totalPrice
            )
            val notification = NotificationItem(
                title = "挂单成功",
                message = "你挂牌出售 ${amountSeconds / 3600}h ${(amountSeconds % 3600) / 60}m"
            )
            val newState = current.copy(
                user = updatedUser,
                tradeOffers = current.tradeOffers + offer,
                reputationPoints = current.reputationPoints + totalPrice.toInt(),
                totalTradeCount = current.totalTradeCount + 1,
                notifications = listOf(notification) + current.notifications
            )
            storage.saveAppState(newState)
            newState
        }
        return success
    }

    fun markNotificationRead(notificationId: String) {
        _state.update { current ->
            val notifications = current.notifications.map { n ->
                if (n.id == notificationId) n.copy(isRead = true) else n
            }
            val newState = current.copy(notifications = notifications)
            storage.saveAppState(newState)
            newState
        }
    }

    fun checkIn(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return if (_state.value.lastCheckInDate == today) {
            false
        } else {
            _state.update { current ->
                val streak = if (isConsecutiveDay(current.lastCheckInDate, today)) current.checkInStreak + 1 else 1
                val multiplier = 1 + streak.coerceAtMost(7) * 0.1
                val reward = (30 * 60 * multiplier).toLong()
                val updatedUser = current.user.copy(
                    remainingSeconds = current.user.remainingSeconds + reward
                )
                val newState = current.copy(
                    user = updatedUser,
                    checkInStreak = streak,
                    lastCheckInDate = today,
                    totalEarnedSeconds = current.totalEarnedSeconds + reward
                )
                storage.saveAppState(newState)
                newState
            }
            true
        }
    }

    fun restart() {
        _state.update {
            val newState = AppState(
                user = User(isLoggedIn = true),
                tasks = defaultTasks()
            )
            storage.saveAppState(newState)
            newState
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _state.update { current ->
            val newState = current.copy(vibrationEnabled = enabled)
            com.moky.timebattle.util.VibrationHelper.enabled = enabled
            storage.saveAppState(newState)
            newState
        }
    }

    fun clearCache() {
        _state.update { current ->
            val newState = current.copy(
                notifications = emptyList(),
                tradeOffers = emptyList()
            )
            storage.saveAppState(newState)
            newState
        }
    }

    fun logout() {
        _state.update { current ->
            val newState = current.copy(
                user = current.user.copy(isLoggedIn = false)
            )
            storage.saveAppState(newState)
            newState
        }
    }

    private fun isConsecutiveDay(lastDate: String?, today: String): Boolean {
        if (lastDate.isNullOrEmpty()) return false
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val last = sdf.parse(lastDate)?.time ?: return false
            val now = sdf.parse(today)?.time ?: return false
            (now - last) <= 24 * 60 * 60 * 1000
        } catch (e: Exception) {
            false
        }
    }
}
