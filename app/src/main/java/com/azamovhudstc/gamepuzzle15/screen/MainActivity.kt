package com.azamovhudstc.gamepuzzle15.screen

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.azamovhudstc.gamepuzzle15.R
import com.azamovhudstc.gamepuzzle15.database.MySharedPreferences
import com.azamovhudstc.gamepuzzle15.extensions.gone
import com.azamovhudstc.gamepuzzle15.extensions.onClick
import com.azamovhudstc.gamepuzzle15.extensions.visible
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_level.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.container
import kotlinx.android.synthetic.main.dialog_about.view.*
import kotlinx.android.synthetic.main.dialog_records.*
import kotlinx.android.synthetic.main.dialog_records.view.*
import kotlinx.android.synthetic.main.dialog_records.view.button_about_ok

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main)
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())
        button_about.onClick {
            val currentBinding =
                LayoutInflater.from(this).inflate(R.layout.dialog_about_all, container, false)
            val dialog = Dialog(this)
            dialog.setContentView(currentBinding)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            currentBinding.button_about_ok.onClick {
                dialog.dismiss()
            }
        }
        button_start.onClick {
            val dialog = Dialog(this)

            dialog.setCancelable(false)
            val dialogView: View =
                LayoutInflater.from(this).inflate(R.layout.activity_level, container, false)
            val currentBinding =
                LayoutInflater.from(this).inflate(R.layout.activity_level, container, false)
            dialog.setContentView(currentBinding)
            dialogView.cancel_dialog.onClick {
                dialog.dismiss()
                dialog.cancel()
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogView.level_easy.onClick {
                MySharedPreferences.setLevel("easy")
                val intent = Intent(Intent(this@MainActivity, GameActivity::class.java))
                intent.putExtra("time", MySharedPreferences.getPauseTime())
                startActivity(intent)
                dialog.dismiss()
                dialog.cancel()
            }




            



            dialogView.level_medium.onClick {
                MySharedPreferences.setLevel("medium")
                val intent = Intent(Intent(this@MainActivity, GameMediumActivity::class.java))
                intent.putExtra("time", MySharedPreferences.getPauseTime())
                startActivity(intent)

                dialog.dismiss()
                dialog.cancel()
            }

            dialogView.level_expert.onClick {
                MySharedPreferences.setLevel("hard")
                val intent = Intent(Intent(this@MainActivity, GameExpertActivity::class.java))
                intent.putExtra("time", MySharedPreferences.getPauseTime())
                startActivity(intent)

                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setContentView(dialogView)
            dialog.show()
        }
        button_records.onClick {
            dialogRecords()
        }
        button_settings.onClick {
            var intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        button_exit.onClick {
            exitDialog()
        }
    }

    private fun exitDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setCancelable(false)
        val inflater = layoutInflater
        val mainLayout = findViewById<View>(R.id.container) as ConstraintLayout
        val dialogView: View = inflater.inflate(R.layout.dialog_exit_app, mainLayout, false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        val no = dialogView.findViewById<MaterialButton>(R.id.button_exit_no)
        val yes = dialogView.findViewById<MaterialButton>(R.id.button_exit_yes)
        no.setOnClickListener { view: View? -> dialog.cancel() }
        yes.setOnClickListener { view: View? -> finish() }
        dialog.setView(dialogView)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun dialogRecords() {

        var temp = MySharedPreferences.getRecord()
        println("Temp:${temp.toString()}")
        if (temp != null) {
            println("My Record:${temp.toString()}")
            println(temp + "TEmpppppppppppppppp")
            val values = mutableListOf<Int>()
            println(temp + "TEmpppppppppppppppp")
            val currentBinding =
                LayoutInflater.from(this).inflate(R.layout.dialog_records, container, false)
            val dialog = Dialog(this)
            dialog.setContentView(currentBinding)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            currentBinding.button_about_ok.onClick {
                dialog.dismiss()
            }
            var tempChar = temp.split("#")
            for (element in tempChar) {
                values.add(element.toInt())
            }

            println("My Record:${values.toString()}")
            when {
                values.size == 1 -> {
                    currentBinding.first_place.text = "Moves:${values[0]}"
                    currentBinding.first_place.visibility = visible()
                    currentBinding.img_1.visibility = visible()
                    currentBinding.second_place.visibility = gone()
                    currentBinding.img_2.visibility = gone()
                    currentBinding.third_place.visibility = gone()
                    currentBinding.img_3.visibility = gone()
                }
                values.size == 2 -> {
                    if (values[0] != values[1]) {
                        currentBinding.first_place.text = "Moves:${values[0]}"
                        currentBinding.second_place.text = "Moves:${values[1]}"
                        currentBinding.first_place.visibility = visible()
                        currentBinding.img_1.visibility = visible()
                        currentBinding.second_place.visibility = visible()
                        currentBinding.img_2.visibility = visible()
                        currentBinding.third_place.visibility = gone()
                        currentBinding.img_3.visibility = gone()
                    } else {
                        currentBinding.first_place.text = "Moves:${values[0]}"
                        currentBinding.first_place.visibility = visible()
                        currentBinding.img_1.visibility = visible()
                        currentBinding.second_place.visibility = gone()
                        currentBinding.img_2.visibility = gone()
                        currentBinding.third_place.visibility = gone()
                        currentBinding.img_3.visibility = gone()
                    }
                }
                values.size >= 3 -> {
                    if (values[1] != values[0]) {
                        currentBinding.first_place.text = "Moves:${values[0]}"
                        currentBinding.second_place.text = "Moves:${values[1]}"
                        currentBinding.third_place.text =
                            resources.getString(R.string.tv_records_dialog, values[2].toString())
                        currentBinding.first_place.visibility = visible()
                        currentBinding.img_1.visibility = visible()
                        currentBinding.second_place.visibility = visible()
                        currentBinding.img_2.visibility = visible()
                        currentBinding.third_place.visibility = visible()
                        currentBinding.img_3.visibility = visible()
                    }
                }
                values.size >= 0 -> {

                }
            }
        } else {
            val currentBindingPlaceHolder =
                LayoutInflater.from(this)
                    .inflate(R.layout.dialog_records_place_holder, container, false)
            val dialogPlaceeHolder = Dialog(this)
            dialogPlaceeHolder.setContentView(currentBindingPlaceHolder)
            dialogPlaceeHolder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogPlaceeHolder.show()
            currentBindingPlaceHolder.button_about_ok.onClick {
                dialogPlaceeHolder.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())

    }

    override fun onPause() {
        super.onPause()
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())

    }
}