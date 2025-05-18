package com.example.beautysalonapp

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

class AppointmentAdapter(
    private val context: Context,
    private val appointmentList: List<Appointment>,
    private val showCancelBtn: Boolean
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateLbl: TextView = itemView.findViewById(R.id.dateLbl)
        val timeLbl: TextView = itemView.findViewById(R.id.timeLbl)
        val staffLbl: TextView = itemView.findViewById(R.id.staffNameLbl)
        val statusLbl: TextView = itemView.findViewById(R.id.statusLbl)
        val cancelBtn: Button = itemView.findViewById(R.id.cancelBtn)
        val feedbackBtn: Button = itemView.findViewById(R.id.feedbackBtn)
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
        holder.statusLbl.text = appointment.status

        holder.cancelBtn.visibility = if (showCancelBtn) View.VISIBLE else View.GONE

        holder.cancelBtn.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUserId == appointment.userId) {
                db.collection("appointments")
                    .document(appointment.appointmentId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Appointment cancelled", Toast.LENGTH_SHORT).show()

                        // 🔄 Refresh the appointment list after cancellation
                        if (context is ViewAppointmentsActivity) {
                            context.loadAppointments()  // make sure this method is public
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to cancel", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show()
            }
        }

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
