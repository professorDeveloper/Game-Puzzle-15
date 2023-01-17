package com.azamovhudstc.gamepuzzle15.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.azamovhudstc.gamepuzzle15.R
import com.azamovhudstc.gamepuzzle15.database.MySharedPreferences
import com.azamovhudstc.gamepuzzle15.extensions.toastShow
import com.azamovhudstc.gamepuzzle15.setting.adapter.SettingsAdapter
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_setting)
        var viewPager = findViewById<ViewPager2>(R.id.viewPager2)
        var adapter = SettingsAdapter(fragmentActivity = this)
        var container = findViewById<ConstraintLayout>(R.id.container)
        viewPager.adapter = adapter
        dotsIndicator.attachTo(viewPager)
        viewPager.currentItem = MySharedPreferences.getCurrentItem()
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("ResourceAsColor")
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                when (position) {
                    0 -> {
                        container.setBackgroundResource(R.drawable.img)
                    }
                    1 -> {
                        container.setBackgroundResource(R.drawable.img_1)
                    }
                    2 -> {
                        container.setBackgroundResource(R.drawable.background3)

                    }
                }
            }
        })
        save_Setting.setOnClickListener {
            when (viewPager2.currentItem) {
                0 -> {
                    MySharedPreferences.setBackgroundResource(R.drawable.img)
                    MySharedPreferences.setButtonBackground(R.drawable.shape_button)
                    MySharedPreferences.setCurrentItem(0)
                    toastShow("Muvaffaqiyatli Saqlandi")
                    finish()
                }
                1 -> {

                    MySharedPreferences.setBackgroundResource(R.drawable.img_1)
                    MySharedPreferences.setButtonBackground(R.drawable.shape_buttonbg2)

                    MySharedPreferences.setCurrentItem(1)
                    toastShow("Muvaffaqiyatli Saqlandi")
                    finish()
                }
                2 -> {

                    MySharedPreferences.setBackgroundResource(R.drawable.background3)
                    MySharedPreferences.setButtonBackground(R.drawable.shape_button3)
                    MySharedPreferences.setCurrentItem(2)
                    toastShow("Muvaffaqiyatli Saqlandi")
                    finish()
                }
            }
        }

    }
}