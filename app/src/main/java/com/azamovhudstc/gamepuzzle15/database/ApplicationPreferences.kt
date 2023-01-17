package com.azamovhudstc.gamepuzzle15.database

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import com.azamovhudstc.gamepuzzle15.R

object MySharedPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun initPreferences(context: Context) {
        if (sharedPreferences == null) sharedPreferences =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun putStringSet(key: String?, value: MutableSet<String>?) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putStringSet(key, value)
        preferenceEditor.apply()
    }

    fun getStringSet(key: String?, defaultValue: MutableSet<String>?): MutableSet<String>? {
        return sharedPreferences!!.getStringSet(key, defaultValue)
    }

    fun putString(key: String?, value: String?) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putString(key, value)
        preferenceEditor.apply()
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return sharedPreferences!!.getString(key, defaultValue)
    }

    fun putInt(key: String?, value: Int) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putInt(key, value)
        preferenceEditor.apply()
    }

    fun setLevel(level: String?) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putString("level", level)
        preferenceEditor.apply()
    }

    fun getLevel(): String? {
        return sharedPreferences!!.getString(
            "level",
            "easy"
        )
    }
    fun setRecord(record: String?) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putString("record", record)
        preferenceEditor.apply()
    }

    fun getRecord(): String? {
        return sharedPreferences!!.getString(
            "record",
            null
        )
    }

    fun setPauseTime(pause_time: Long) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putLong("pause_time", pause_time)
        preferenceEditor.apply()
    }


    fun getPauseTime(): Long? {
        return this.getLong(
            "pause_time",
            0
        )
    }
    fun setPauseTimeExpert(pause_time_expert: Long) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putLong("pause_time_expert", pause_time_expert)
        preferenceEditor.apply()
    }


    fun getPauseTimeExpert(): Long? {
        return this.getLong(
            "pause_time_expert",
            0
        )
    }
    fun setPauseTimeMedium(pause_time_medium: Long) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putLong("pause_time_medium", pause_time_medium)
        preferenceEditor.apply()
    }


    fun getPauseTimeMedium(): Long? {
        return this.getLong(
            "pause_time_medium",
            0
        )
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return sharedPreferences!!.getInt(key, defaultValue)
    }

    fun putLong(key: String?, value: Long) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putLong(key, value)
        preferenceEditor.apply()
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        return sharedPreferences!!.getLong(key, defaultValue)
    }

    fun putBoolean(key: String?, value: Boolean) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putBoolean(key, value)
        preferenceEditor.apply()
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(key, defaultValue)
    }

    fun putFloat(key: String?, value: Float) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putFloat(key, value)
        preferenceEditor.apply()
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return sharedPreferences!!.getFloat(key, defaultValue)
    }

    fun setSound(sound: Boolean) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putBoolean("sound", sound)
        preferenceEditor.apply()
    }

    fun getSound(): Boolean? {
        return this.getBoolean(
            "sound",
            true
        )
    }
    @SuppressLint("CommitPrefEdits")
    fun setNumbers(numbers: String?) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putString("numbers", numbers)
        preferenceEditor.apply()
    }

    fun getNumbers(): String? {
        return this.getString(
            "numbers",
            "1#2#3#4#5#6#7#8#9#10#11#12#13#14#15#16#"
        )
    }
    fun setNumbersMedium(numbers_medium: String?) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putString("numbers_medium", numbers_medium)
        preferenceEditor.apply()
    }

    fun getNumbersMedium(): String? {
        return this.getString(
            "numbers_medium",
            "1#2#3#4#5#6#7#8#9#10#11#12#13#14#15##"
        )
    }
    fun setScore(score: Int) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putInt("score", score)
        preferenceEditor.apply()
    }

    fun getScore(): Int {
        return this.getInt("score", 0)
    }
    fun setScoreMedium(score_medium: Int) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putInt("score_medium", score_medium)
        preferenceEditor.apply()
    }

    fun getScoreMedium(): Int {
        return this.getInt("score_medium", 0)
    }

    fun setNumbersExpert(numbers: String?) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putString("numbers_hard", numbers)
            preferenceEditor.apply()
    }
    fun setScoreExpert(score_expert: Int) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putInt("score_expert", score_expert)
        preferenceEditor.apply()
    }

    fun getScoreExpert(): Int {
        return this.getInt("score_expert", 0)
    }

    fun setIsPlaying(isPlaying: Boolean) {
        val preferenceEditor = sharedPreferences!!.edit()
preferenceEditor.putBoolean("is_playing", isPlaying)

preferenceEditor.apply()
    }

    fun getIsPlaying(): Boolean? {
        return this.getBoolean(
            "is_playing",
            false
        )
    }
    fun setIsPlayingMedium(isPlaying: Boolean) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putBoolean("is_playing_medium", isPlaying)

        preferenceEditor.apply()
    }

    fun getIsPlayingMedium(): Boolean? {
        return this.getBoolean(
            "is_playing_medium",
            false
        )
    }
    fun setIsPlayingExpert(isPlaying: Boolean) {
        val preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor.putBoolean("is_playing_expert", isPlaying)

        preferenceEditor.apply()
    }

    fun getIsPlayingExpert(): Boolean? {
        return this.getBoolean(
            "is_playing_expert",
            false
        )
    }

    fun getNumbersExpert(): String? {
        return this.getString(
            "numbers_hard",
            "1#2#3#4#5#6#7#8#9#10#11#12#13#14#15#16#17#18#19#20#21#22#23#24#25#26#27#28#29#30#31#32#33#34#35##"
        )
    }
    fun setCurrentItem(current: Int) {
        val preferenceEditor = sharedPreferences!!.edit()

        preferenceEditor.putInt("current", current)
        preferenceEditor.apply()
    }

    fun getCurrentItem(): Int {
        return this.getInt("current", 0)
    }
    fun getBackgroundResource():Int=this.getInt("resource", R.drawable.img)
    fun setBackgroundResource(@DrawableRes resource:Int){
        var preferences= sharedPreferences!!.edit()
        preferences.putInt("resource",resource)
        preferences.apply()
    }

    fun getButtonBackground():Int=this.getInt("button_background", R.drawable.shape_button)
    fun setButtonBackground(@DrawableRes button_background:Int){
        var preferences= sharedPreferences!!.edit()
        preferences.putInt("button_background",button_background)
        preferences.apply()
    }
}