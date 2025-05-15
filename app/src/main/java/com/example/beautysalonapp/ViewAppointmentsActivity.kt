package com.example.beautysalonapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewAppointmentsActivity : AppCompatActivity() {

    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var pastRecyclerView: RecyclerView
    private lateinit var backToHomeBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_appointments)

        upcomingRecyclerView = findViewById(R.id.upcomingRecyclerView)
        pastRecyclerView = findViewById(R.id.pastRecyclerView)

        // 🔹 Sample Data
        val upcomingList = listOf(
            Appointment("14/05/2025", "12:30", "Nisha", "Confirmed", "Massage"),
            Appointment("16/05/2025", "11:00", "Zara", "Pending", "Haircut")

        )

        val pastList = listOf(
            Appointment("10/05/2025", "15:00", "Aarti", "Completed", "Facial"),
            Appointment("07/05/2025", "10:45", "Ravina", "Completed", "Threading")

        )

        // 🔹 Setup RecyclerViews
        upcomingRecyclerView.layoutManager = LinearLayoutManager(this)
        pastRecyclerView.layoutManager = LinearLayoutManager(this)

       upcomingRecyclerView.adapter = AppointmentAdapter(this, upcomingList, showCancelBtn = true)
       pastRecyclerView.adapter = AppointmentAdapter(this,pastList, showCancelBtn = false)

        backToHomeBtn = findViewById(R.id.backToHomeBtn)
        backToHomeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }
}
