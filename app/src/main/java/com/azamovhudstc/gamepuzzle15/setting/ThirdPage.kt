package com.azamovhudstc.gamepuzzle15.setting

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.azamovhudstc.gamepuzzle15.R
import com.azamovhudstc.gamepuzzle15.database.MySharedPreferences
import kotlinx.android.synthetic.main.page_third.*
import java.util.*

class ThirdPage : Fragment(R.layout.page_third) {
    private val cell = Array(4) { arrayOfNulls<AppCompatButton>(4) }
    private var x = 0
    private var y = 0
    private var numbers: ArrayList<Int> = ArrayList(16)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val numbersText: String = MySharedPreferences.getNumbers().toString()
        val numbersArray = numbersText.split("#").toTypedArray()
        val numberList: List<String> = java.util.ArrayList(Arrays.asList(*numbersArray))
        numbers.addAll(loadSavedNumbers(numberList))
        loadViews()
        loadDataToViews()

    }
    private fun loadSavedNumbers(numbers: List<String>): ArrayList<Int> {
        var arrayList = ArrayList<Int>()
        for (element in numbers) {
            if (element.isEmpty()) {
                continue
            }
            println(element)
            arrayList.add(element.toInt())
        }
        return arrayList
    }
    private fun loadViews() {
        for (i in 0 until game_board.childCount) {
            game_board.getChildAt(i).background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.shape_button3)

            cell[i / 4][i % 4] = game_board.getChildAt(i) as AppCompatButton?
            cell[i / 4][i % 4]?.tag = i


        }

    }

    private fun loadDataToViews() {
        for (i in 0..15) {
            if (numbers[i] == 16) {
                x = i / 4
                y = i % 4
                cell[x][y]?.visibility = View.INVISIBLE
                cell[x][y]?.text = resources.getText(R.string.empty_button_text)
                cell[x][y]?.isClickable = false
            } else cell[i / 4][i % 4]!!.text = numbers[i].toString()
        }
    }
}
