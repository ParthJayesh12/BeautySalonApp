package com.example.beautysalonapp

import Service
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(
    private val context: Context,
    private val serviceList: List<Service>
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.serviceName)
        val serviceDescription: TextView = itemView.findViewById(R.id.serviceDescription)
        val serviceImage: ImageView = itemView.findViewById(R.id.serviceImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_card, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = serviceList[position]

        holder.serviceName.text = currentService.name
        //holder.serviceDescription.text = currentService.description

        val imageResId = context.resources.getIdentifier(
            currentService.imageName,
            "drawable",
            context.packageName
        )
        holder.serviceImage.setImageResource(imageResId)

        holder.serviceImage.setOnClickListener {
            val intent = Intent(context, BookingActivity::class.java)
            intent.putExtra("service_name", currentService.name)
            intent.putExtra("service_desc", currentService.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = serviceList.size
}
