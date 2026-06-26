package com.moky.timebattle.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.moky.timebattle.data.model.AppState
import com.moky.timebattle.data.model.TaskItem
import com.moky.timebattle.data.model.User

private const val PREFS_NAME = "time_battle_prefs"
private const val KEY_APP_STATE = "app_state"
private const val KEY_USER = "user"
private const val KEY_TASKS = "tasks"

class LocalStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAppState(state: AppState) {
        prefs.edit()
            .putString(KEY_APP_STATE, gson.toJson(state))
            .apply()
    }

    fun loadAppState(): AppState {
        val json = prefs.getString(KEY_APP_STATE, null) ?: return AppState()
        return try {
            gson.fromJson(json, AppState::class.java)
        } catch (e: Exception) {
            AppState()
        }
    }

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USER, gson.toJson(user))
            .apply()
    }

    fun loadUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveTasks(tasks: List<TaskItem>) {
        prefs.edit()
            .putString(KEY_TASKS, gson.toJson(tasks))
            .apply()
    }

    fun loadTasks(): List<TaskItem> {
        val json = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<TaskItem>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
