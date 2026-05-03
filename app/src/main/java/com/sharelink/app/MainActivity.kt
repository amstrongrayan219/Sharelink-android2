package com.sharelink.app
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
 private val devicesList = mutableListOf<Device>()
 private lateinit var listAdapter: ArrayAdapter<String>
 private lateinit var discovery: UdpDiscovery
 private lateinit var server: FileServer
 override fun onCreate(savedInstanceState: Bundle?) {
 super.onCreate(savedInstanceState)
 setContentView(R.layout.activity_main)
 val myName = Prefs.getName(this) ?: return
 val myIp = NetworkUtils.getLocalIpAddress(this)
 findViewById<TextView>(R.id.tvMyName).text = myName
 val lvDevices = findViewById<ListView>(R.id.lvDevices)
 val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
 val names = mutableListOf<String>()
 listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
 lvDevices.adapter = listAdapter
 lvDevices.setOnItemClickListener { _, _, position, _ ->
 val device = devicesList[position]
 val intent = Intent(this, TransferActivity::class.java).apply {
 putExtra("device_name", device.name)
 putExtra("device_ip", device.ip)
 putExtra("device_port", device.port)
 }
 startActivity(intent)
 }
 discovery = UdpDiscovery(myName, myIp)
 discovery.startListening { device ->
 runOnUiThread {
 if (devicesList.none { it.name == device.name }) {
 devicesList.add(device)
 names.add("${device.name} — ${device.ip}")
 listAdapter.notifyDataSetChanged()
 tvEmpty.visibility = View.GONE
 }
 }
 }
 discovery.startBroadcast()
 server = FileServer(
 context = this,
 deviceName = myName,
 onTransferRequest = { _, _ -> true },
 onFileReceived = { fileName ->
 runOnUiThread {
 android.widget.Toast.makeText(
 this, "Recu : $fileName",
 android.widget.Toast.LENGTH_LONG
 ).show()
 }
 }
 )
 server.start()
 if (devicesList.isEmpty()) tvEmpty.visibility = View.VISIBLE
 }
 override fun onDestroy() {
 super.onDestroy()
 discovery.stop()
 server.stop()
 }
}
