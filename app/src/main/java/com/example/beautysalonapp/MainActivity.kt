package com.example.beautysalonapp

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerText: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Init Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind views
        emailInput = findViewById(R.id.username_input) // This is actually email now
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerText = findViewById(R.id.registerText)
        progressBar = findViewById(R.id.loginProgressBar)

        // Login logic
        loginBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = ProgressBar.VISIBLE

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = ProgressBar.GONE
                        if (task.isSuccessful) {
                            Log.i("Login", "Login successful for $email")
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("Login", "Login failed", task.exception)
                            Toast.makeText(this, "Login failed. Check email or password.", Toast.LENGTH_LONG).show()

                        }
                    }
            } else {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Go to Register screen
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
