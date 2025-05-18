package com.example.beautysalonapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FeedbackActivity : AppCompatActivity() {

    private lateinit var serviceNameLbl: TextView
    private lateinit var dateLbl: TextView
    private lateinit var staffNameLbl: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var commentEditText: EditText
    private lateinit var submitFeedbackBtn: Button
    private lateinit var backToHomeBtn: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind UI
        serviceNameLbl = findViewById(R.id.serviceNameLbl)
        dateLbl = findViewById(R.id.dateLbl)
        staffNameLbl = findViewById(R.id.staffNameLbl)
        ratingBar = findViewById(R.id.ratingBar)
        commentEditText = findViewById(R.id.commentEditText)
        submitFeedbackBtn = findViewById(R.id.submitFeedbackBtn)
        backToHomeBtn = findViewById(R.id.backToHomeBtn)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Intent data
        val service = intent.getStringExtra("service") ?: "Unknown"
        val staff = intent.getStringExtra("staff") ?: "N/A"
        val date = intent.getStringExtra("date") ?: "N/A"
        val time = intent.getStringExtra("time") ?: "N/A"
        val userId = auth.currentUser?.uid ?: ""

        // Set values to labels
        serviceNameLbl.text = service
        staffNameLbl.text = "Staff: $staff"
        dateLbl.text = "$date at $time"

        // Submit button logic
        submitFeedbackBtn.setOnClickListener {
            val rating = ratingBar.rating
            val comment = commentEditText.text.toString().trim()

            if (comment.isEmpty() && rating == 0f) {
                Toast.makeText(this, "Please write a comment or give a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if feedback already exists
            db.collection("feedback")
                .whereEqualTo("userId", userId)
                .whereEqualTo("service", service)
                .whereEqualTo("staff", staff)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Toast.makeText(this, "Feedback already submitted for this appointment", Toast.LENGTH_SHORT).show()
                    } else {
                        val feedback = hashMapOf(
                            "userId" to userId,
                            "service" to service,
                            "staff" to staff,
                            "date" to date,
                            "time" to time,
                            "rating" to rating,
                            "comment" to comment,
                            "timestamp" to Date()
                        )

                        db.collection("feedback")
                            .add(feedback)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error checking feedback history", Toast.LENGTH_SHORT).show()
                }
        }


        // Back to Home button
        backToHomeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
