package com.example.beautysalonapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var selectedServiceLbl: TextView
    private lateinit var serviceDescriptionLbl: TextView
    private lateinit var preferredStaffSpinner: Spinner
    private lateinit var selectDateBtn: Button
    private lateinit var selectTimeBtn: Button
    private lateinit var confirmBookingBtn: Button
    private lateinit var cancelBookingBtn: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var serviceName = ""
    private var serviceDesc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Bind views
        selectedServiceLbl = findViewById(R.id.selectedServiceLbl)
        serviceDescriptionLbl = findViewById(R.id.serviceDescriptionLbl)
        preferredStaffSpinner = findViewById(R.id.preferredStaffSpinner)
        selectDateBtn = findViewById(R.id.selectDateBtn)
        selectTimeBtn = findViewById(R.id.selectTimeBtn)
        confirmBookingBtn = findViewById(R.id.confirmBookingBtn)
        cancelBookingBtn = findViewById(R.id.cancelBookingBtn)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get data from HomeActivity
        serviceName = intent.getStringExtra("service_name") ?: "Service Not Found"
        serviceDesc = intent.getStringExtra("service_desc") ?: "No description available"

        // Set labels
        selectedServiceLbl.text = getString(R.string.selected_service_dynamic, serviceName)
        serviceDescriptionLbl.text = serviceDesc

        // Staff spinner setup
        val staffList = listOf("Select Staff", "Aarti", "Nisha", "Ravina", "Zara")
        val adapter = ArrayAdapter(this, R.layout.spinner_item_white, staffList)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white)
        preferredStaffSpinner.adapter = adapter

        // Date Picker
        selectDateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val formattedDate = "$day/${month + 1}/$year"
                    selectDateBtn.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = calendar.timeInMillis
            datePicker.show()
        }

        // Time Picker
        selectTimeBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hour, minute ->
                    val formattedTime = String.format("%02d:%02d", hour, minute)
                    selectTimeBtn.text = formattedTime
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        // Confirm Booking
        confirmBookingBtn.setOnClickListener {
            val selectedStaff = preferredStaffSpinner.selectedItem.toString()
            val selectedDate = selectDateBtn.text.toString()
            val selectedTime = selectTimeBtn.text.toString()
            val userId = auth.currentUser?.uid

            // Validation
            if (selectedStaff == "Select Staff") {
                Toast.makeText(this, "Please select a staff member", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedDate == "Select Date") {
                Toast.makeText(this, "Please pick a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedTime == "Select Time") {
                Toast.makeText(this, "Please pick a time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userId == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Build appointment object
            val appointment = hashMapOf(
                "userId" to userId,
                "service" to serviceName,
                "description" to serviceDesc,
                "staff" to selectedStaff,
                "date" to selectedDate,
                "time" to selectedTime,
                "status" to "Upcoming"
            )

            // Upload to Firestore
            db.collection("appointments").add(appointment)
                .addOnSuccessListener {
                    val intent = Intent(this, ConfirmationActivity::class.java)
                    intent.putExtra("service", serviceName)
                    intent.putExtra("staff", selectedStaff)
                    intent.putExtra("date", selectedDate)
                    intent.putExtra("time", selectedTime)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to book appointment: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Cancel Button
        cancelBookingBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
