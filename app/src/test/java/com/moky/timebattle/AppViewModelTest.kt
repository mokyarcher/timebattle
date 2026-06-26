package com.moky.timebattle

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.TaskStatus
import com.moky.timebattle.data.model.TaskType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AppViewModelTest {

    private lateinit var application: Application
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = ApplicationProvider.getApplicationContext()
    }

    private fun createViewModel(): AppViewModel {
        return AppViewModel(application, enableTimer = false)
    }

    @Test
    fun `login sets nickname and logged in flag`() = runTest {
        val viewModel = createViewModel()

        viewModel.login("测试玩家")

        val state = viewModel.state.value
        assertTrue(state.user.isLoggedIn)
        assertEquals("测试玩家", state.user.nickname)
        assertNotEquals("", state.user.id)
    }

    @Test
    fun `default tasks are loaded on init`() = runTest {
        val viewModel = createViewModel()

        val state = viewModel.state.value
        assertTrue(state.tasks.isNotEmpty())
        assertTrue(state.tasks.any { it.type == TaskType.SYSTEM })
    }

    @Test
    fun `claimTask moves task to in progress`() = runTest {
        val viewModel = createViewModel()
        val taskId = viewModel.state.value.tasks.first { it.status == TaskStatus.AVAILABLE }.id

        viewModel.claimTask(taskId)

        val task = viewModel.state.value.tasks.find { it.id == taskId }
        assertEquals(TaskStatus.IN_PROGRESS, task?.status)
    }

    @Test
    fun `completeTask rewards remaining seconds`() = runTest {
        val viewModel = createViewModel()
        val task = viewModel.state.value.tasks.first { it.status == TaskStatus.AVAILABLE }
        val taskId = task.id
        val reward = task.rewardSeconds
        val beforeSeconds = viewModel.state.value.user.remainingSeconds

        viewModel.completeTask(taskId)

        val state = viewModel.state.value
        assertEquals(TaskStatus.CLAIMED, state.tasks.find { it.id == taskId }?.status)
        assertEquals(beforeSeconds + reward, state.user.remainingSeconds)
    }

    @Test
    fun `checkIn rewards time and prevents same day re-check`() = runTest {
        val viewModel = createViewModel()
        val beforeSeconds = viewModel.state.value.user.remainingSeconds

        val firstResult = viewModel.checkIn()
        assertTrue(firstResult)

        val afterFirst = viewModel.state.value
        assertTrue(afterFirst.user.remainingSeconds > beforeSeconds)
        assertEquals(1, afterFirst.checkInStreak)

        val secondResult = viewModel.checkIn()
        assertFalse(secondResult)

        val afterSecond = viewModel.state.value
        assertEquals(afterFirst.user.remainingSeconds, afterSecond.user.remainingSeconds)
    }

    @Test
    fun `publishTask deducts reward from user`() = runTest {
        val viewModel = createViewModel()
        viewModel.login("发布者")
        val beforeSeconds = viewModel.state.value.user.remainingSeconds
        val reward = 3600L

        viewModel.publishTask("测试悬赏", "测试描述", reward)

        val state = viewModel.state.value
        assertEquals(beforeSeconds - reward, state.user.remainingSeconds)
        assertTrue(state.tasks.any { it.title == "测试悬赏" && it.type == TaskType.USER })
        assertTrue(state.notifications.any { it.title == "任务发布成功" })
    }

    @Test
    fun `buyTime transfers seconds when reputation is sufficient`() = runTest {
        val viewModel = createViewModel()
        val offer = viewModel.state.value.tradeOffers.first()
        val beforeSeconds = viewModel.state.value.user.remainingSeconds
        val beforeReputation = viewModel.state.value.reputationPoints

        val result = viewModel.buyTime(offer.id)

        assertTrue(result)
        val state = viewModel.state.value
        assertEquals(beforeSeconds + offer.amountSeconds, state.user.remainingSeconds)
        assertEquals(beforeReputation - offer.totalPrice.toInt(), state.reputationPoints)
        assertTrue(state.tradeOffers.none { it.id == offer.id })
    }

    @Test
    fun `buyTime fails for nonexistent offer`() = runTest {
        val viewModel = createViewModel()

        val result = viewModel.buyTime("non-existent-id")

        assertFalse(result)
    }

    @Test
    fun `sellTime lists offer and increases reputation`() = runTest {
        val viewModel = createViewModel()
        viewModel.login("卖家")
        val beforeSeconds = viewModel.state.value.user.remainingSeconds
        val amount = 3600L
        val pricePerHour = 10.0

        val result = viewModel.sellTime(amount, pricePerHour)

        assertTrue(result)
        val state = viewModel.state.value
        assertEquals(beforeSeconds - amount, state.user.remainingSeconds)
        assertTrue(state.tradeOffers.any { it.sellerName == "卖家" && it.amountSeconds == amount })
    }

    @Test
    fun `sellTime fails when balance is insufficient`() = runTest {
        val viewModel = createViewModel()
        viewModel.login("卖家")
        val amount = viewModel.state.value.user.remainingSeconds + 1

        val result = viewModel.sellTime(amount, 10.0)

        assertFalse(result)
    }

    @Test
    fun `restart resets progress while keeping logged in`() = runTest {
        val viewModel = createViewModel()
        viewModel.login("玩家")
        viewModel.checkIn()
        viewModel.completeTask(viewModel.state.value.tasks.first().id)

        viewModel.restart()

        val state = viewModel.state.value
        assertTrue(state.user.isLoggedIn)
        assertEquals(7 * 24 * 3600L, state.user.remainingSeconds)
        assertEquals(0, state.totalTaskCount)
        assertEquals(0, state.totalTradeCount)
        assertTrue(state.tasks.isNotEmpty())
    }

    @Test
    fun `clearCache removes notifications and trade offers`() = runTest {
        val viewModel = createViewModel()
        viewModel.publishTask("任务", "描述", 60)
        assertTrue(viewModel.state.value.notifications.isNotEmpty())

        viewModel.clearCache()

        val state = viewModel.state.value
        assertTrue(state.notifications.isEmpty())
        assertTrue(state.tradeOffers.isEmpty())
    }

    @Test
    fun `logout clears logged in flag`() = runTest {
        val viewModel = createViewModel()
        viewModel.login("玩家")

        viewModel.logout()

        assertFalse(viewModel.state.value.user.isLoggedIn)
    }

    @Test
    fun `setVibrationEnabled updates state and helper`() = runTest {
        val viewModel = createViewModel()
        assertTrue(viewModel.state.value.vibrationEnabled)

        viewModel.setVibrationEnabled(false)

        assertFalse(viewModel.state.value.vibrationEnabled)
        assertFalse(com.moky.timebattle.util.VibrationHelper.enabled)

        viewModel.setVibrationEnabled(true)

        assertTrue(viewModel.state.value.vibrationEnabled)
        assertTrue(com.moky.timebattle.util.VibrationHelper.enabled)
    }
}
