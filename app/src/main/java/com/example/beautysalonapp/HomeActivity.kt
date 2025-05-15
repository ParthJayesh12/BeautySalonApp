package com.example.beautysalonapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView




class HomeActivity : AppCompatActivity() {

    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val recyclerView = findViewById<RecyclerView>(R.id.serviceRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        val adapter = ServiceAdapter(getServiceList())
        recyclerView.adapter = adapter
        logoutBtn = findViewById(R.id.logoutBtn)
        val viewAptBtn = findViewById<Button>(R.id.viewAptBtn)

        logoutBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        viewAptBtn.setOnClickListener {
            val intent = Intent(this, ViewAppointmentsActivity::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getServiceList(): List<Service> {
        return listOf(
            Service("Haircut", "Professional trim", R.drawable.ic_service_placeholder),
            Service("Facial", "Glowing skin treatment", R.drawable.ic_service_placeholder),
            Service("Manicure", "Clean & stylish nails", R.drawable.ic_service_placeholder),
            Service("Massage", "Relaxing therapy", R.drawable.ic_service_placeholder),
            Service("Hair Color", "New look shades", R.drawable.ic_service_placeholder),
            Service("Waxing", "Smooth skin", R.drawable.ic_service_placeholder),
            Service("Threading", "Eyebrow & face", R.drawable.ic_service_placeholder),
            Service("Pedicure", "Foot spa treatment", R.drawable.ic_service_placeholder)
        )
    }
}
