package com.sharelink.app
import android.content.Context
import android.net.wifi.WifiManager
object NetworkUtils {
 fun getLocalIpAddress(context: Context): String {
 return try {
 val wifiManager = context.applicationContext
 .getSystemService(Context.WIFI_SERVICE) as WifiManager
 val ip = wifiManager.connectionInfo.ipAddress
 String.format(
 "%d.%d.%d.%d",
 ip and 0xff,
 ip shr 8 and 0xff,
 ip shr 16 and 0xff,
 ip shr 24 and 0xff
 )
 } catch (e: Exception) {
 "0.0.0.0"
 }
 }
}
