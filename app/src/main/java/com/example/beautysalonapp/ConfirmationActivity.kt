package com.example.beautysalonapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var thankYouMsg: TextView
    private lateinit var bookingSummary: TextView
    private lateinit var backToHomeBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        thankYouMsg = findViewById(R.id.thankYouMsg)
        bookingSummary = findViewById(R.id.bookingSummary)

        // Receive data from intent
        val service = intent.getStringExtra("service") ?: "Unknown"
        val staff = intent.getStringExtra("staff") ?: "Unknown"
        val date = intent.getStringExtra("date") ?: "Not selected"
        val time = intent.getStringExtra("time") ?: "Not selected"

        val summary = """
            Service: $service
            Staff: $staff
            Date: $date
            Time: $time
        """.trimIndent()

        bookingSummary.text = summary

        backToHomeBtn = findViewById(R.id.backToHomeBtn)
        backToHomeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }
}
