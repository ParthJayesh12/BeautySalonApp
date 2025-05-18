package com.example.beautysalonapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewAppointmentsActivity : AppCompatActivity() {

    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var pastRecyclerView: RecyclerView
    private lateinit var backToHomeBtn: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_appointments)

        upcomingRecyclerView = findViewById(R.id.upcomingRecyclerView)
        pastRecyclerView = findViewById(R.id.pastRecyclerView)
        backToHomeBtn = findViewById(R.id.backToHomeBtn)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        upcomingRecyclerView.layoutManager = LinearLayoutManager(this)
        pastRecyclerView.layoutManager = LinearLayoutManager(this)

        backToHomeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        loadAppointments()
    }

         fun loadAppointments() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val upcoming = mutableListOf<Appointment>()
                val past = mutableListOf<Appointment>()

                for (doc in result) {
                    val appointment = doc.toObject(Appointment::class.java)
                        .copy(appointmentId = doc.id) // inject the doc ID

                    if (appointment.status == "Upcoming") {
                        upcoming.add(appointment)
                    } else if (appointment.status == "Completed") {
                        past.add(appointment)
                    }
                }

                upcomingRecyclerView.adapter =
                    AppointmentAdapter(this, upcoming, showCancelBtn = true)
                pastRecyclerView.adapter =
                    AppointmentAdapter(this, past, showCancelBtn = false)
            }
            .addOnFailureListener { e ->
                Log.e("Appointments", "Error loading appointments", e)
            }
    }
}
