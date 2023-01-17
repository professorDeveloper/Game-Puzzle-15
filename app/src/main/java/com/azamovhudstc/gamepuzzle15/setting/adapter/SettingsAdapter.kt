package com.azamovhudstc.gamepuzzle15.setting.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.azamovhudstc.gamepuzzle15.setting.OnePage
import com.azamovhudstc.gamepuzzle15.setting.ThirdPage
import com.azamovhudstc.gamepuzzle15.setting.TwoPage

class SettingsAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                OnePage()
            }
            1->{
                TwoPage()
            }
            else->{
                ThirdPage()
            }
        }
    }
}