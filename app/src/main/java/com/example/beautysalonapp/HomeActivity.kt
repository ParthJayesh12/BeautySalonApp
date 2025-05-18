package com.example.beautysalonapp

import Service
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var logoutBtn: Button
    private lateinit var welcomeText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        welcomeText = findViewById(R.id.welcomeText)
        logoutBtn = findViewById(R.id.logoutBtn)
        val viewAptBtn = findViewById<Button>(R.id.viewAptBtn)
        val recyclerView = findViewById<RecyclerView>(R.id.serviceRecyclerView)

        recyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        fetchServicesFromFirestore()


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get logged-in user ID and fetch Firestore data
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        welcomeText.text = "Welcome, $username!"
                    } else {
                        Log.w("Firestore", "No such document")
                        welcomeText.text = "Welcome!"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to fetch username", e)
                    welcomeText.text = "Welcome!"
                    Toast.makeText(this, "Could not load user info", Toast.LENGTH_SHORT).show()
                }
        } else {
            welcomeText.text = "Welcome!"
        }

        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        viewAptBtn.setOnClickListener {
            val intent = Intent(this, ViewAppointmentsActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchServicesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val recyclerView = findViewById<RecyclerView>(R.id.serviceRecyclerView)

        db.collection("services")
            .get()
            .addOnSuccessListener { result ->
                val serviceList = mutableListOf<Service>()
                for (document in result) {
                    val service = document.toObject(Service::class.java)
                    serviceList.add(service)
                }
                recyclerView.adapter = ServiceAdapter(this, serviceList)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting services", e)
            }
    }

}
