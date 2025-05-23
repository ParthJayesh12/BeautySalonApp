package com.example.beautysalonapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var appointmentCountLbl: TextView
    private lateinit var backBtn: Button

    private var allAppointments = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        recyclerView = findViewById(R.id.adminRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        appointmentCountLbl = findViewById(R.id.appointmentCountLbl)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish() // return to HomeActivity
        }

        db = FirebaseFirestore.getInstance()
        loadAllAppointments()
    }

    private fun loadAllAppointments() {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { documents ->
                allAppointments.clear()
                for (doc in documents) {
                    val appointment = doc.toObject(Appointment::class.java)
                    appointment.appointmentId = doc.id
                    allAppointments.add(appointment)
                }
                adapter = AppointmentAdapter(this, allAppointments, showCancelBtn = true)
                recyclerView.adapter = adapter
                appointmentCountLbl.text = "Total: ${allAppointments.size}"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show()
            }
    }
}
