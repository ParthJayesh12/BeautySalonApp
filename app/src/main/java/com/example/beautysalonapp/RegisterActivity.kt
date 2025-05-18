package com.example.beautysalonapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.*
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var fullNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerBtn: Button
    private lateinit var loginText: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind Views
        auth = FirebaseAuth.getInstance()
        fullNameInput = findViewById(R.id.full_name_input)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        registerBtn = findViewById(R.id.register_Btn)
        loginText = findViewById(R.id.login_Btn)
        progressBar = findViewById(R.id.registerProgressBar)

        // Go to login page
        loginText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Register button logic
        registerBtn.setOnClickListener {
            val fullName = fullNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = ProgressBar.VISIBLE

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("Register", "Firebase account created for $email")

                            val userId = auth.currentUser?.uid
                            val db = FirebaseFirestore.getInstance()

                            val userMap = hashMapOf(
                                "fullName" to fullName,
                                "phone" to phone,
                                "username" to username,
                                "email" to email
                            )

                            if (userId != null) {
                                db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener {
                                        Log.i("Firestore", "User profile added for $userId")
                                        progressBar.visibility = ProgressBar.GONE
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        progressBar.visibility = ProgressBar.GONE
                                        Log.e("Firestore", "Failed to store user data", e)
                                        Toast.makeText(this, "Account created, but failed to store profile", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                progressBar.visibility = ProgressBar.GONE
                                Toast.makeText(this, "Account created but user ID is null", Toast.LENGTH_LONG).show()
                            }

                        } else {
                            progressBar.visibility = ProgressBar.GONE
                            Log.e("Register", "Registration failed", task.exception)
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
