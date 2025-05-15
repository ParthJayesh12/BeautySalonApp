package com.example.beautysalonapp
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var selectedServiceLbl: TextView
    private lateinit var serviceDescriptionLbl: TextView
    private lateinit var preferredStaffSpinner: Spinner
    private lateinit var selectDateBtn: Button
    private lateinit var selectTimeBtn: Button
    private lateinit var confirmBookingBtn: Button
    private lateinit var cancelBookingBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking) // 🔗 This connects to the XML file

        // 🔗 Bind the views to their IDs
        selectedServiceLbl = findViewById(R.id.selectedServiceLbl)
        serviceDescriptionLbl = findViewById(R.id.serviceDescriptionLbl)
        preferredStaffSpinner = findViewById(R.id.preferredStaffSpinner)
        selectDateBtn = findViewById(R.id.selectDateBtn)
        selectTimeBtn = findViewById(R.id.selectTimeBtn)
        confirmBookingBtn = findViewById(R.id.confirmBookingBtn)
        cancelBookingBtn = findViewById(R.id.cancelBookingBtn)


        // 🔌 Get the data sent from HomeActivity
        val serviceName = intent.getStringExtra("service_name") ?: "Service Not Found"
        val serviceDesc = intent.getStringExtra("service_desc") ?: "No description available"
        val staffList = listOf("Select Staff", "Aarti", "Nisha", "Ravina", "Zara")

        val adapter = ArrayAdapter(this, R.layout.spinner_item_white, staffList)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white)

        selectDateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                    selectDateBtn.text = formattedDate
                },
                year,
                month,
                day
            )

            datePicker.datePicker.minDate = calendar.timeInMillis // Prevent past dates
            datePicker.show()
        }

        selectTimeBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    selectTimeBtn.text = formattedTime
                },
                hour,
                minute,
                true // 24-hour format; set to false for AM/PM
            )

            timePicker.show()
        }


        confirmBookingBtn.setOnClickListener {
            val selectedStaff = preferredStaffSpinner.selectedItem.toString()
            val selectedDate = selectDateBtn.text.toString()
            val selectedTime = selectTimeBtn.text.toString()

            // Basic validation
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

            val intent = Intent(this, ConfirmationActivity::class.java)
            intent.putExtra("service", serviceName)
            intent.putExtra("staff", selectedStaff)
            intent.putExtra("date", selectedDate)
            intent.putExtra("time", selectedTime)
            startActivity(intent)
        }

        cancelBookingBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


        // 📝 Set the data into the labels
        selectedServiceLbl.text = getString(R.string.selected_service_dynamic, serviceName)

        serviceDescriptionLbl.text = serviceDesc

        preferredStaffSpinner.adapter = adapter

    }
}
