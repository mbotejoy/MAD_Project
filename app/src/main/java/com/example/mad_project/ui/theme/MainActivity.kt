package com.example.mad_project.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.mad_project.R
import com.example.mad_project.adapters.DonationPagerAdapter
import com.example.mad_project.ui.theme.viewmodel.AuthViewModel
import com.example.mad_project.ui.theme.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fabAddDonation: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupViewModels()
        setupViewPager()
        setupClickListeners()
    }

    private fun initializeViews() {
        findViewById<ComposeView>(R.id.logo_compose_view).setContent {
            Image(
                painter = painterResource(id = R.drawable.our_logo),
                contentDescription = "App Logo"
            )
        }
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        fabAddDonation = findViewById(R.id.fabAddDonation)
    }

    private fun setupViewModels() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mainViewModel.loadDonations()
    }

    private fun setupViewPager() {
        val adapter = DonationPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Available Donations"
                1 -> "My Donations"
                else -> "Tab $position"
            }
        }.attach()
    }

    private fun setupClickListeners() {
        fabAddDonation.setOnClickListener {
            val intent = Intent(this, CreateDonationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                authViewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            R.id.menu_profile -> {
                // Navigate to profile (you can create ProfileActivity later)
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
