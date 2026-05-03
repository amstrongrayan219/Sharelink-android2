package com.sharelink.app
import android.content.Context
import fi.iki.elonen.NanoHTTPD
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
class FileServer(
 private val context: Context,
 private val deviceName: String,
 port: Int = 8080,
 private val onTransferRequest: (String, List<String>) -> Boolean,
 private val onFileReceived: (String) -> Unit
) : NanoHTTPD(port) {
 override fun serve(session: IHTTPSession): Response {
 return when {
 session.method == Method.POST && session.uri == "/request-transfer" -> {
 val body = mutableMapOf<String, String>()
 session.parseBody(body)
 val json = JSONObject(body["postData"] ?: "{}")
 val from = json.optString("from", "Inconnu")
 val filesArray = json.optJSONArray("files") ?: JSONArray()
 val fileList = (0 until filesArray.length()).map { filesArray.getString(it) }
 val accepted = onTransferRequest(from, fileList)
 val resp = JSONObject()
 resp.put("accepted", accepted)
 newFixedLengthResponse(resp.toString())
 }
 session.method == Method.POST && session.uri == "/upload" -> {
 val files = mutableMapOf<String, String>()
 session.parseBody(files)
 val tmpFilePath = files["file"]
 val fileName = session.headers["x-filename"] ?: "fichier_recu"
 if (tmpFilePath != null) {
 val saveDir = File(context.getExternalFilesDir(null), "ShareLink")
 if (!saveDir.exists()) saveDir.mkdirs()
 val destFile = File(saveDir, fileName)
package com.sharelink.app
import android.content.Context
import fi.iki.elonen.NanoHTTPD
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
class FileServer(
 private val context: Context,
 private val deviceName: String,
 port: Int = 8080,
 private val onTransferRequest: (String, List<String>) -> Boolean,
 private val onFileReceived: (String) -> Unit
) : NanoHTTPD(port) {
 override fun serve(session: IHTTPSession): Response {
 return when {
 session.method == Method.POST && session.uri == "/request-transfer" -> {
 val body = mutableMapOf<String, String>()
 session.parseBody(body)
 val json = JSONObject(body["postData"] ?: "{}")
 val from = json.optString("from", "Inconnu")
 val filesArray = json.optJSONArray("files") ?: JSONArray()
 val fileList = (0 until filesArray.length()).map { filesArray.getString(it) }
 val accepted = onTransferRequest(from, fileList)
 val resp = JSONObject()
 resp.put("accepted", accepted)
 newFixedLengthResponse(resp.toString())
 }
 session.method == Method.POST && session.uri == "/upload" -> {
 val files = mutableMapOf<String, String>()
 session.parseBody(files)
 val tmpFilePath = files["file"]
 val fileName = session.headers["x-filename"] ?: "fichier_recu"
 if (tmpFilePath != null) {
 val saveDir = File(context.getExternalFilesDir(null), "ShareLink")
 if (!saveDir.exists()) saveDir.mkdirs()
 val destFile = File(saveDir, fileName)
 File(tmpFilePath).copyTo(destFile, overwrite = true)
 onFileReceived(fileName)
 }
 newFixedLengthResponse("{"success":true}")
 }
 else -> newFixedLengthResponse(
 Response.Status.NOT_FOUND, "text/plain", "Not found"
 )
 }
 }
} File(tmpFilePath).copyTo(destFile, overwrite = true)
 onFileReceived(fileName)
 }
 newFixedLengthResponse("{"success":true}")
 }
 else -> newFixedLengthResponse(
 Response.Status.NOT_FOUND, "text/plain", "Not found"
 )
 }
 }
}
