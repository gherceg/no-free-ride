package com.graham.nofreeride.utils
import android.content.Context
import androidx.preference.PreferenceManager

class SharedPreferencesManager(val context: Context) {

    val INSURANCE_PRICE_KEY = "insurance_price_key"
    val MPG_KEY = "mpg_key"
    val PPG_KEY = "ppg_key"
    val DRIVE_IN_PROGRESS_KEY = "drive_in_progress_key"


    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getInsurancePrice() : Float {
        return sharedPreferences.getFloat(INSURANCE_PRICE_KEY, 60f)
    }

    fun saveInsurancePrice(insurancePrice : Float) {
        sharedPreferences.edit().putFloat(INSURANCE_PRICE_KEY, insurancePrice).apply()
    }

    fun getMilesPerGallon() : Int {
        return sharedPreferences.getInt(MPG_KEY, 20)
    }

    fun saveMPG(mpg: Int) {
        sharedPreferences.edit().putInt(MPG_KEY, mpg).apply()
    }

    fun getPPG() : Float {
        return sharedPreferences.getFloat(PPG_KEY, 3.65f)
    }

    fun savePPG(ppg: Float) {
        sharedPreferences.edit().putFloat(PPG_KEY, ppg).apply()
    }

    fun getDriveInProgress() : Boolean {
        return sharedPreferences.getBoolean(DRIVE_IN_PROGRESS_KEY, false)
    }

    fun saveDriveInProgress(inProgress: Boolean) {
        sharedPreferences.edit().putBoolean(DRIVE_IN_PROGRESS_KEY, inProgress).apply()
    }

}