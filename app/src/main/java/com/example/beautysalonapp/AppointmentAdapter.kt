package com.example.beautysalonapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent

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
        holder.staffLbl.text = context.getString(R.string.staff_label, appointment.staffName)
        holder.statusLbl.text = appointment.status

        // Show or hide cancel button depending on section
        holder.cancelBtn.visibility = if (showCancelBtn) View.VISIBLE else View.GONE


        holder.feedbackBtn.setOnClickListener {
            val intent = Intent(context, FeedbackActivity::class.java)
            intent.putExtra("service", appointment.serviceName)
            intent.putExtra("staff", appointment.staffName)
            intent.putExtra("date", appointment.date)
            intent.putExtra("time", appointment.time)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = appointmentList.size
}
