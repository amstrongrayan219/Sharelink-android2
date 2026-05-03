package com.sharelink.app
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class SetupActivity : AppCompatActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
 super.onCreate(savedInstanceState)
 if (Prefs.getName(this) != null) {
 startActivity(Intent(this, MainActivity::class.java))
 finish()
 return
 }
 setContentView(R.layout.activity_setup)
 val etName = findViewById<EditText>(R.id.etDeviceName)
 val btnConfirm = findViewById<Button>(R.id.btnConfirm)
 btnConfirm.setOnClickListener {
 val name = etName.text.toString().trim()
 if (name.length < 3) {
 Toast.makeText(this, "Nom trop court", Toast.LENGTH_SHORT).show()
 return@setOnClickListener
 }
 Prefs.saveName(this, name)
 startActivity(Intent(this, MainActivity::class.java))
 finish()
 }
 }
}
