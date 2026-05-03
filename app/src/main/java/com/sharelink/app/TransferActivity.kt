package com.sharelink.app
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
class TransferActivity : AppCompatActivity() {
 private val selectedFiles = mutableListOf<Pair<Uri, String>>()
 private lateinit var listAdapter: ArrayAdapter<String>
 private lateinit var device: Device
 private lateinit var client: FileTransferClient
 private val filePicker = registerForActivityResult(
 ActivityResultContracts.GetMultipleContents()
 ) { uris ->
 uris.forEach { uri ->
 val name = client.getFileName(uri)
 selectedFiles.add(Pair(uri, name))
 listAdapter.add(name)
 }
 listAdapter.notifyDataSetChanged()
 }
 override fun onCreate(savedInstanceState: Bundle?) {
 super.onCreate(savedInstanceState)
 setContentView(R.layout.activity_transfer)
 device = Device(
 name = intent.getStringExtra("device_name") ?: "",
 ip = intent.getStringExtra("device_ip") ?: "",
 port = intent.getIntExtra("device_port", 8080)
 )
 client = FileTransferClient(this)
 findViewById<TextView>(R.id.tvDestination).text = "Envoyer a : ${device.name}"
 val lvFiles = findViewById<ListView>(R.id.lvFiles)
 listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
 lvFiles.adapter = listAdapter
 findViewById<Button>(R.id.btnAddFiles).setOnClickListener {
 filePicker.launch("*/*")
 }
 findViewById<Button>(R.id.btnSend).setOnClickListener {
 if (selectedFiles.isEmpty()) {
 Toast.makeText(this, "Ajoutez des fichiers", Toast.LENGTH_SHORT).show()
 return@setOnClickListener
 }
 sendAllFiles()
 }
 }
 private fun sendAllFiles() {
 val progressBar = findViewById<ProgressBar>(R.id.progressBar)
 val tvProgress = findViewById<TextView>(R.id.tvProgress)
 val btnSend = findViewById<Button>(R.id.btnSend)
 val btnAdd = findViewById<Button>(R.id.btnAddFiles)
 btnSend.isEnabled = false
 btnAdd.isEnabled = false
 progressBar.visibility = View.VISIBLE
 tvProgress.visibility = View.VISIBLE
 progressBar.max = selectedFiles.size
 val myName = Prefs.getName(this) ?: "INCONNU"
 Thread {
 val fileNames = selectedFiles.map { it.second }
 runOnUiThread { tvProgress.text = "Connexion..." }
 val accepted = client.requestTransfer(device, myName, fileNames)
 if (!accepted) {
 runOnUiThread {
 Toast.makeText(this, "Transfert refuse", Toast.LENGTH_LONG).show()
 btnSend.isEnabled = true
 btnAdd.isEnabled = true
 progressBar.visibility = View.GONE
 tvProgress.visibility = View.GONE
 }
 return@Thread
 }
 selectedFiles.forEachIndexed { index, (uri, name) ->
 runOnUiThread { tvProgress.text = "Envoi : $name" }
 client.sendFile(device, uri, name)
 runOnUiThread { progressBar.progress = index + 1 }
 }
 runOnUiThread {
 Toast.makeText(this, "Envoi termine !", Toast.LENGTH_LONG).show()
 btnSend.isEnabled = true
 btnAdd.isEnabled = true
 progressBar.visibility = View.GONE
 tvProgress.visibility = View.GONE
 }
 }.start()
 }
}
