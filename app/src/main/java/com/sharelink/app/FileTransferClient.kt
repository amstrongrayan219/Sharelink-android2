package com.sharelink.app
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
class FileTransferClient(private val context: Context) {
 fun requestTransfer(device: Device, fromName: String, fileNames: List<String>): Boolean {
 return try {
 val url = URL("http://${device.ip}:${device.port}/request-transfer")
 val conn = url.openConnection() as HttpURLConnection
 conn.requestMethod = "POST"
 conn.doOutput = true
 conn.connectTimeout = 5000
 conn.readTimeout = 10000
 conn.setRequestProperty("Content-Type", "application/json")
 val body = JSONObject().apply {
 put("from", fromName)
 put("files", JSONArray(fileNames))
 }.toString()
 conn.outputStream.use { it.write(body.toByteArray()) }
 val response = conn.inputStream.bufferedReader().readText()
 JSONObject(response).optBoolean("accepted", false)
 } catch (e: Exception) { false }
 }
 fun sendFile(device: Device, uri: Uri, fileName: String): Boolean {
 return try {
 val url = URL("http://${device.ip}:${device.port}/upload")
 val conn = url.openConnection() as HttpURLConnection
 conn.requestMethod = "POST"
 conn.doOutput = true
 conn.connectTimeout = 5000
 conn.setRequestProperty("Content-Type", "application/octet-stream")
 conn.setRequestProperty("x-filename", fileName)
 context.contentResolver.openInputStream(uri)?.use { input ->
 conn.outputStream.use { output -> input.copyTo(output) }
 }
 conn.responseCode == 200
 } catch (e: Exception) { false }
 }
 fun getFileName(uri: Uri): String {
 var name = "fichier_${System.currentTimeMillis()}"
 context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
 val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
 if (index >= 0 && cursor.moveToFirst()) name = cursor.getString(index)
 }
 return name
 }
}
