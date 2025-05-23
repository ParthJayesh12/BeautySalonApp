package com.example.beautysalonapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(
    private val context: Context,
    private val appointmentList: List<Appointment>,
    private val showCancelBtn: Boolean
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateLbl: TextView = itemView.findViewById(R.id.dateLbl)
        val timeLbl: TextView = itemView.findViewById(R.id.timeLbl)
        val staffLbl: TextView = itemView.findViewById(R.id.staffNameLbl)
        val serviceLbl: TextView = itemView.findViewById(R.id.serviceNameLbl)
        val statusLbl: TextView = itemView.findViewById(R.id.statusLbl)
        val bookedAtLbl: TextView = itemView.findViewById(R.id.bookedAtLbl)
        val cancelBtn: Button = itemView.findViewById(R.id.cancelBtn)
        val feedbackBtn: Button = itemView.findViewById(R.id.feedbackBtn)
        val rescheduleBtn: Button = itemView.findViewById(R.id.rescheduleBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_card, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentList[position]

        holder.dateLbl.text = appointment.date
        holder.timeLbl.text = appointment.time
        holder.staffLbl.text = context.getString(R.string.staff_label, appointment.staff)
        holder.serviceLbl.text = "Service: ${appointment.service}"
        holder.statusLbl.text = appointment.status

        //  Format createdAt timestamp
        val createdAtMillis = appointment.createdAt
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val createdAtFormatted = formatter.format(Date(createdAtMillis))
        holder.bookedAtLbl.text = "Booked At: $createdAtFormatted"

        // Visibility
        if (appointment.status == "Upcoming") {
            holder.cancelBtn.visibility = View.VISIBLE
            holder.rescheduleBtn.visibility =
                if (context is AdminPanelActivity) View.VISIBLE else View.GONE //  Admin-only
            holder.feedbackBtn.visibility = View.GONE
        } else {
            holder.cancelBtn.visibility = View.GONE
            holder.rescheduleBtn.visibility = View.GONE
            holder.feedbackBtn.visibility = View.VISIBLE
        }

        // Cancel with confirmation
        holder.cancelBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes") { _, _ ->
                    val db = FirebaseFirestore.getInstance()
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                    if (!showCancelBtn || currentUserId == appointment.userId || context is AdminPanelActivity) {
                        db.collection("appointments")
                            .document(appointment.appointmentId)
                            .update("status", "Cancelled")
                            .addOnSuccessListener {
                                Toast.makeText(context, "Appointment cancelled", Toast.LENGTH_SHORT)
                                    .show()
                                if (context is ViewAppointmentsActivity) {
                                    context.loadAppointments()
                                } else if (context is AdminPanelActivity) {
                                    context.recreate()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to cancel", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } else {
                        Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Feedback
        holder.feedbackBtn.setOnClickListener {
            val intent = Intent(context, FeedbackActivity::class.java)
            intent.putExtra("service", appointment.service)
            intent.putExtra("staff", appointment.staff)
            intent.putExtra("date", appointment.date)
            intent.putExtra("time", appointment.time)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = appointmentList.size
}

