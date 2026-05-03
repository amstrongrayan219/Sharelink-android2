package com.sharelink.app
import android.content.Context
object Prefs {
 private const val FILE = "sharelink_prefs"
 private const val KEY_NAME = "device_name"
 fun saveName(context: Context, name: String) {
 val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
 prefs.edit().putString(KEY_NAME, name.uppercase().trim()).apply()
 }
 fun getName(context: Context): String? {
 val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
 return prefs.getString(KEY_NAME, null)
 }
}
