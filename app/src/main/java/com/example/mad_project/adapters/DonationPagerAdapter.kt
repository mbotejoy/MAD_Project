package com.example.mad_project.adapters



import com.example.mad_project.fragments.AvailableDonationsFragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mad_project.fragments.MyDonationsFragment


class DonationPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AvailableDonationsFragment()
            1 -> MyDonationsFragment()
            else -> AvailableDonationsFragment()
        }
    }
}