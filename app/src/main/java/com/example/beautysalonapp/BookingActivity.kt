package com.example.beautysalonapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var selectedServiceLbl: TextView
    private lateinit var serviceDescriptionLbl: TextView
    private lateinit var preferredStaffSpinner: Spinner
    private lateinit var dateBtn: Button
    private lateinit var timeSlotSpinner: Spinner
    private lateinit var confirmBookingBtn: Button
    private lateinit var cancelBookingBtn: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var serviceName = ""
    private var serviceDesc = ""
    private var serviceDuration = 30
    private var selectedDate = ""
    private var selectedTimeSlot = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        selectedServiceLbl = findViewById(R.id.selectedServiceLbl)
        serviceDescriptionLbl = findViewById(R.id.serviceDescriptionLbl)
        preferredStaffSpinner = findViewById(R.id.preferredStaffSpinner)
        dateBtn = findViewById(R.id.selectDateBtn)
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner)
        confirmBookingBtn = findViewById(R.id.confirmBookingBtn)
        cancelBookingBtn = findViewById(R.id.cancelBookingBtn)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        serviceName = intent.getStringExtra("service_name") ?: "Service Not Found"
        serviceDesc = intent.getStringExtra("service_desc") ?: "No description"
        selectedServiceLbl.text = getString(R.string.selected_service_dynamic, serviceName)
        serviceDescriptionLbl.text = serviceDesc

        db.collection("services")
            .whereEqualTo("name", serviceName)
            .get()
            .addOnSuccessListener { docs ->
                val doc = docs.firstOrNull()
                serviceDuration = doc?.getLong("duration")?.toInt() ?: 30
            }

        val staffList = listOf("Choose Staff", "Aarti", "Nisha", "Ravina", "Zara")
        val staffAdapter = ArrayAdapter(this, R.layout.spinner_item_white, staffList)
        staffAdapter.setDropDownViewResource(R.layout.spinner_dropdown_white)
        preferredStaffSpinner.adapter = staffAdapter

        dateBtn.text = getString(R.string.select_date)
        dateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val picker = DatePickerDialog(this, { _, year, month, day ->
                selectedDate = "$day/${month + 1}/$year"
                dateBtn.text = selectedDate
                loadAvailableTimeSlots()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            picker.datePicker.minDate = calendar.timeInMillis
            picker.show()
        }

        preferredStaffSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                if (selectedDate.isNotEmpty()) {
                    loadAvailableTimeSlots()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        confirmBookingBtn.setOnClickListener {
            val selectedStaff = preferredStaffSpinner.selectedItem.toString()
            selectedTimeSlot = timeSlotSpinner.selectedItem?.toString() ?: ""
            val userId = auth.currentUser?.uid

            if (selectedStaff == "Choose Staff" || selectedDate.isEmpty() || selectedTimeSlot == "Choose Time" || selectedTimeSlot.isEmpty()) {
                Toast.makeText(this, "Please select staff, date, and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userId == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  Appointment object including createdAt timestamp
            val appointment = hashMapOf(
                "userId" to userId,
                "service" to serviceName,
                "description" to serviceDesc,
                "staff" to selectedStaff,
                "date" to selectedDate,
                "time" to selectedTimeSlot,
                "status" to "Upcoming",
                "createdAt" to System.currentTimeMillis() //  NEW LINE
            )

            db.collection("appointments").add(appointment)
                .addOnSuccessListener {
                    val intent = Intent(this, ConfirmationActivity::class.java)
                    intent.putExtra("service", serviceName)
                    intent.putExtra("staff", selectedStaff)
                    intent.putExtra("date", selectedDate)
                    intent.putExtra("time", selectedTimeSlot)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        cancelBookingBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
    }

    private fun loadAvailableTimeSlots() {
        val selectedStaff = preferredStaffSpinner.selectedItem.toString()
        if (selectedStaff == "Choose Staff" || selectedDate.isEmpty()) return

        val openingHour = 9
        val closingHour = 18
        val slotList = mutableListOf<String>()
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        val baseCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, openingHour)
            set(Calendar.MINUTE, 0)
        }

        while (baseCalendar.get(Calendar.HOUR_OF_DAY) < closingHour) {
            slotList.add(formatter.format(baseCalendar.time))
            baseCalendar.add(Calendar.MINUTE, serviceDuration)
        }

        db.collection("appointments")
            .whereEqualTo("staff", selectedStaff)
            .whereEqualTo("date", selectedDate)
            .whereEqualTo("status", "Upcoming")
            .get()
            .addOnSuccessListener { appointments ->
                val bookedTimes = appointments.mapNotNull { it.getString("time") }.toSet()
                val available = slotList.filter { time ->
                    !bookedTimes.any { booked -> overlaps(time, booked) }
                }

                val dropdownItems = listOf("Choose Time") + available
                val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropdownItems)
                timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                timeSlotSpinner.adapter = timeAdapter
            }
    }

    private fun overlaps(time1: String, time2: String): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val t1 = Calendar.getInstance()
        val t2 = Calendar.getInstance()
        t1.time = sdf.parse(time1)!!
        t2.time = sdf.parse(time2)!!

        val t1End = t1.clone() as Calendar
        val t2End = t2.clone() as Calendar
        t1End.add(Calendar.MINUTE, serviceDuration)
        t2End.add(Calendar.MINUTE, serviceDuration)

        return t1.timeInMillis < t2End.timeInMillis && t2.timeInMillis < t1End.timeInMillis
    }
}
