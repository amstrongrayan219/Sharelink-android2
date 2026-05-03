package com.sharelink.app
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
class UdpDiscovery(
 private val deviceName: String,
 private val localIp: String,
 private val httpPort: Int = 8080,
 private val udpPort: Int = 5000
) {
 private var running = false
 private var listenSocket: DatagramSocket? = null
 fun startBroadcast() {
 Thread {
 try {
 val socket = DatagramSocket()
 socket.broadcast = true
 val message = JSONObject().apply {
 put("name", deviceName)
 put("ip", localIp)
 put("port", httpPort)
 }.toString().toByteArray()
 while (running) {
 val packet = DatagramPacket(
 message, message.size,
 InetAddress.getByName("255.255.255.255"),
 udpPort
 )
 socket.send(packet)
 Thread.sleep(3000)
 }
 socket.close()
 } catch (e: Exception) { e.printStackTrace() }
 }.start()
 }
 fun startListening(onDeviceFound: (Device) -> Unit) {
 running = true
 Thread {
 try {
 listenSocket = DatagramSocket(udpPort)
 val buffer = ByteArray(2048)
 while (running) {
 val packet = DatagramPacket(buffer, buffer.size)
 listenSocket!!.receive(packet)
 val message = String(packet.data, 0, packet.length)
 try {
 val json = JSONObject(message)
 val device = Device(
 name = json.getString("name"),
 ip = json.getString("ip"),
 port = json.getInt("port")
 )
 if (device.name != deviceName) {
onDeviceFound(device)
 }
 } catch (e: Exception) { }
 }
 } catch (e: Exception) { e.printStackTrace() }
 }.start()
 }
 fun stop() {
 running = false
 listenSocket?.close()
 }
}
