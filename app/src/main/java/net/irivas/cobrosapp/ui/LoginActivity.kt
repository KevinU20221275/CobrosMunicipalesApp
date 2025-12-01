package net.irivas.cobrosapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.irivas.cobrosapp.R
import net.irivas.cobrosapp.data.CobrosDBHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var txtUsername : EditText
    private lateinit var txtPassword : EditText
    private lateinit var btnLogin: Button
    private lateinit var db: CobrosDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = CobrosDBHelper(this)
        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtUserPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val user = txtUsername.text.toString()
            val pass = txtPassword.text.toString()

            if (user.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Por favor ingrese sus credenciales", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.validarUsuario(user, pass)) {
                guardarSesion()
                val intent = Intent(this, PanelAdministracionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                Toast.makeText(this, "Bienvenid@ ${user}!", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarSesion() {
        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
        prefs.edit().putBoolean("logueado", true).apply()
    }
}