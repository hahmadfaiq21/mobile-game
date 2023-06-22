package mobile.game.slidingpuzzle

import android.content.Context
import android.content.SharedPreferences

class Database(context: Context) {
    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    fun saveMode(mode: Boolean) {
        editor.putBoolean(IS_DARK_MODE, mode).commit()
    }

    fun getMode(): Boolean {
        return preferences.getBoolean(IS_DARK_MODE, false)
    }

    fun saveLastStep(steps: Int) {
        editor.putInt(LAST_STEP, steps).commit()
    }

    fun getLastStep(): Int {
        return preferences.getInt(LAST_STEP, 0)
    }

    fun saveLastTime(seconds: Int) {
        editor.putInt(LAST_TIME, seconds).commit()
    }

    fun getLastTime(): Int {
        return preferences.getInt(LAST_TIME, 0)
    }

    fun saveBestStep(steps: Int) {
        editor.putInt(BEST_STEP, steps).commit()
    }

    fun getBestStep(): Int {
        return preferences.getInt(BEST_STEP, 0)
    }

    fun saveBestTime(seconds: Int) {
        editor.putInt(BEST_TIME, seconds).commit()
    }

    fun getBestTime(): Int {
        return preferences.getInt(BEST_TIME, 0)
    }

    companion object {
        const val SHARED_PREF = "sharedPref"
        const val LAST_STEP = "lastStep"
        const val LAST_TIME = "lastTime"
        const val BEST_STEP = "bestStep"
        const val BEST_TIME = "bestTime"
        const val IS_DARK_MODE = "isDarkMode"
    }
}
