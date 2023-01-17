package com.azamovhudstc.gamepuzzle15.screen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import com.azamovhudstc.gamepuzzle15.BuildConfig
import com.azamovhudstc.gamepuzzle15.R
import com.azamovhudstc.gamepuzzle15.database.MySharedPreferences
import com.azamovhudstc.gamepuzzle15.extensions.invisible
import com.azamovhudstc.gamepuzzle15.extensions.onClick
import com.azamovhudstc.gamepuzzle15.extensions.visible
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game_expert.*
import kotlinx.android.synthetic.main.activity_game_expert.button_exit
import kotlinx.android.synthetic.main.activity_game_expert.button_restart
import kotlinx.android.synthetic.main.activity_game_expert.button_resume
import kotlinx.android.synthetic.main.activity_game_expert.chronometer
import kotlinx.android.synthetic.main.activity_game_expert.container
import kotlinx.android.synthetic.main.activity_game_expert.container_timer_moves
import kotlinx.android.synthetic.main.activity_game_expert.image
import kotlinx.android.synthetic.main.activity_game_expert.info
import kotlinx.android.synthetic.main.activity_game_expert.moves
import kotlinx.android.synthetic.main.activity_game_expert.tv_play
import kotlinx.android.synthetic.main.activity_game_medium.*
import kotlinx.android.synthetic.main.dialog_about.view.*
import kotlinx.android.synthetic.main.dialog_winner.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class GameExpertActivity : AppCompatActivity() {
    private val isGameRestarted: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isGamePaused: MutableLiveData<Boolean> = MutableLiveData(false)
    private val cell = Array(6) { arrayOfNulls<AppCompatButton>(6) }
    private var numbers: ArrayList<Int> = ArrayList(36)
    private var x = 0//[]
    private var y = 0
    private var counter = 0
    private var mediaPlayerMusic: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "winn"
    private var soundPlayer: Int = 0
    private var winPlayer: Int = 0
    private var onBackPressedTime: Long = 0
    private var timeWhenPaused: Long = 0

    /**
     * [0][0] =1
     * [0][1] =2
     * [0][2] =3
     * [0][3] =4
     * [0][4] =5
     * [0][5] =6
     * [1][0] =7
     * [1][1] =8
     * [1][2] =9
     * [1][3] =10
     * [1][4] =11
     * [1][5] =12
     * [2][0] =13
     * [2][1] =14
     * [2][2] =15
     * [2][3] =16
     * [2][4] =17
     * [2][5] =18
     * [3][0] =19
     * [3][1] =20
     * [3][2] =21
     * [3][3] =22
     * [3][4] =23
     * [3][5] =24
     * [4][0] =25
     * [4][1] =26
     * [4][2] =27
     * [4][3] =28
     * [4][4] =29
     * [4][5] =30
     * [5][0] =31
     * [5][1] =32
     * [5][2] =33
     * [5][3] =34
     * [5][4] =35
     * [5][5] =36
     * **/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_expert)
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())

        if (MySharedPreferences.getIsPlayingExpert()!!) {
            counter = MySharedPreferences.getScoreExpert()
            moves.text = counter.toString()
            val numbersText: String = MySharedPreferences.getNumbersExpert().toString()
            val numbersArray = numbersText.split("#").toTypedArray()
            val numberList: List<String> = java.util.ArrayList(Arrays.asList(*numbersArray))
            numbers.clear()
            numbers.addAll(loadSavedNumbers(numberList))
        } else {
            generateNumbers()
        }

        if (MySharedPreferences.getSound()!!) {
            image.setImageResource(R.drawable.ic_music_on)
        } else {
            image.setImageResource(R.drawable.ic_music_off)
        }
        createSoundPool()
        createMediaPlayer()
        startGame()
        subscribeViewBinding()


    }

    private fun restartGame() {
        isGameRestarted.value = false
        isGameRestarted.observe(this) { isGameRestartedObserver(it) }
    }

    private fun isGameRestartedObserver(isActive: Boolean) {
        counter = 0
        button_restart.isClickable = true
//            button_pause.isClickable = true
        button_restart.visibility = visible()
//            button_pause.visibility = visible()


        button_exit.visibility = visible()
        menu_expert.visibility = visible()
        info.visibility = visible()
        button_exit.visibility = visible()
        container_timer_moves.visibility = visible()
        moves.text = counter.toString()
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        game_board_expert.background = AppCompatResources.getDrawable(
            this, R.drawable.shape_game_board_parent
        )
        for (i in 0..game_board_expert.childCount) {
            val currentButton = game_board_expert.getChildAt(i) as AppCompatButton?
            currentButton?.visibility = visible()
        }
        numbers.shuffle()
        startGame()


    }

    private fun checkForWin(): Boolean {
        //The first column of the each row must have a value
        if (cell[0][0]?.text.toString().isNotEmpty() &&
            cell[1][0]?.text.toString().isNotEmpty() &&
            cell[2][0]?.text.toString().isNotEmpty() &&
            cell[3][0]?.text.toString().isNotEmpty() &&
            cell[4][0]?.text.toString().isNotEmpty() &&
            cell[5][0]?.text.toString().isNotEmpty() &&
            cell[5][5]?.text.toString().isNotEmpty()
        ) {
            //Checking the each row's first column values and the last cell should be checked for the emptiness
            if (cell[0][0]?.text.toString().toInt() == 1//
                && cell[1][0]?.text.toString().toInt() == 7//
                && cell[2][0]?.text.toString().toInt() == 13
                && cell[3][0]?.text.toString().toInt() == 19
                && cell[4][0]?.text.toString().toInt() == 25
                && cell[5][0]?.text.toString().toInt() == 31
                && cell[5][5]?.text.toString().toInt() == 36
            ) {
                /*If the first column of the each row is in the accurate position and if the last is empty,
                     then start checking the entire cell */
                var counter = 1
                for (i in 0 until game_board_expert.childCount - 1) {
                    val button = game_board_expert.getChildAt(i) as AppCompatButton
                    if (button.text.toString().isEmpty()) break
                    if (counter == (button.text.toString().toInt()))
                        counter++
                }
                //If the counter is 16 then the values are in their accurate positions
                return counter == 36
            }
        }
        return false
    }


    private fun pauseGame() {
        isGamePaused.value = true
        isGamePaused.observe(this) { isGamePausedObserver(it) }
    }

    private fun isGamePausedObserver(isPaused: Boolean) {
        if (isPaused) {
            button_restart.isClickable = false
            timeWhenPaused = chronometer.base - SystemClock.elapsedRealtime()
            chronometer.stop()
//            button_pause.text = resources.getText(R.string.play)
            button_resume.visibility = visible()
            button_exit.visibility = invisible()
            menu_expert.visibility = invisible()
            button_restart.visibility = invisible()
            info.visibility = invisible()
            button_exit.visibility = invisible()
            button_resume.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.anim_scale
                )
            )
//            button_pause.onClick {
//                button_pause.text = resources.getText(R.string.play)
//                isGamePaused.value = false
//            }
        } else {
            button_restart.isClickable = true
            chronometer.base = timeWhenPaused + SystemClock.elapsedRealtime()
            chronometer.start()
//            button_pause.text = resources.getText(R.string.pause)
            button_resume.visibility = invisible()
            button_restart.visibility = visible()
            button_exit.visibility = visible()
            menu_expert.visibility = visible()
            info.visibility = visible()
            button_resume.clearAnimation()
//            button_pause.onClick {
//                button_pause.text = resources.getText(R.string.pause)
//                isGamePaused.value = true
//            }
        }
    }


    private fun subscribeViewBinding() {
        chronometer.start()
        button_restart.onClick {
            restartGame()
        }
        info.onClick {
            shareTextOnly("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
        }
        tv_play.onClick {
            isGameRestarted.value = true
        }
//        button_pause.onClick {
//            pauseGame()
//        }
        button_resume.onClick {
            isGamePaused.value = false
        }
        button_exit.onClick {
            onBackPressed()
        }
        menu_expert.onClick {
            val dialog = AlertDialog.Builder(this).create()
            dialog.setCancelable(false)
            val inflater = layoutInflater
            val mainLayout = findViewById<ConstraintLayout>(R.id.container)
            val dialogView = inflater.inflate(R.layout.settings_dialog, mainLayout, false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val sound = dialogView.findViewById<SwitchCompat>(R.id.switch_sound)
            val ok = dialogView.findViewById<LinearLayout>(R.id.ok)

            ok.setOnClickListener { view: View? ->
                dialog.cancel()
            }

            sound.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    MySharedPreferences.setSound(true)
                    image.setImageResource(R.drawable.ic_music_on)
                    onStart()
                } else {
                    MySharedPreferences.setSound(false)
                    image.setImageResource(R.drawable.ic_music_off)
                    onPause()
                }
            }
            sound.isChecked = MySharedPreferences.getSound()!!
            dialog.setView(dialogView)
            dialog.show()

        }
    }

    private fun makeMoveButtonAnimation(x: Int, y: Int) {
        YoYo
            .with(Techniques.BounceIn)
            .duration(400)
            .playOn(cell[x][y])
    }

    private fun startGame() {
        if (!MySharedPreferences.getIsPlayingExpert()!!) {
            initSolvableNumbers()
        }
        loadViews()
        loadDataToViews()
    }

    private fun saveRecord() {
        MySharedPreferences.setIsPlaying(false)

        val oldRecord =
            MySharedPreferences.getRecord()
        if (oldRecord == null) {
            MySharedPreferences.setRecord(counter.toString())
        } else {
            val newResult = "$oldRecord#$counter"
            MySharedPreferences.setRecord(newResult)
        }
    }

    private fun stopAllActions() {
        if (MySharedPreferences.getBoolean(
                resources.getString(R.string.shp_music),
                false
            )
        ) {
            mediaPlayer?.pause()
        }
        chronometer.stop()
        button_restart.isClickable = false
//        button_pause.isClickable = false
        for (i in 0 until game_board_expert.childCount) {
            game_board_expert.getChildAt(i).isClickable = false
        }
    }

    private fun playWinSound() {
        if (MySharedPreferences.getSound()!!
        ) mediaPlayerMusic?.start()
    }


    private fun showGameOverAnimationAndDialog() {
        // save record
        saveRecord()

        // stop all actions such as clicking buttons and timer
        stopAllActions()
        // win sound
        playWinSound()
        popUpGameOverDialog()

    }

    private fun popUpGameOverDialog() {
        val binding = LayoutInflater.from(this).inflate(R.layout.dialog_winner, container, false)
        val dialog = Dialog(this)
        dialog.setContentView(binding)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        binding.tv_moves_and_timer.text =
            resources.getString(R.string.tv_win_dialog, counter.toString())
        binding.start.onClick {
            dialog.dismiss()
            mediaPlayerMusic?.stop()
            numbers.shuffle()
            restartGame()
        }
        binding.home.onClick {
            soundPool?.stop(winPlayer)

            dialog.dismiss()
            finish()
        }
    }

    private fun playSound() {
        if (MySharedPreferences.getSound()!!
        ) soundPool?.play(soundPlayer, 1f, 1f, 1, 0, 1f)
        else {
            soundPool?.stop(soundPlayer)

        }
    }

    private fun swapEmptyButtonWithCurrentButton(x: Int, y: Int, i: Int, j: Int) {
        // for x and y (current swiped button)
        cell[x][y]?.visibility = visible()
        cell[x][y]?.text = cell[i][j]?.text.toString()

        cell[x][y]?.isClickable = true

        // for i and j (empty button)
        cell[i][j]?.visibility = invisible()
        cell[i][j]?.text = "36"
        cell[i][j]?.isClickable = false

        // changing values of x and y after swap
        this.x = i
        this.y = j
    }

    private fun canMove(i: Int, j: Int): Boolean {
        return when {
            ((abs(x - i) == 0 && abs(y - j) == 1) || (abs(x - i) == 1 && abs(y - j) == 0)) -> {
                // for current movements
                counter++
                // button move animation
                makeMoveButtonAnimation(x, y)
                // for sound effect
                playSound()
                // swap empty button with current button
                swapEmptyButtonWithCurrentButton(x, y, i, j)
                // changing value of moves
                moves.text = counter.toString()

                // checking game for win
                Log.d("WIN", "canMove: ")
                when {
                    checkForWin() -> {
                        Log.d(TAG, "canMove: Win your")
                        showGameOverAnimationAndDialog()
                    }
                }

                return true
            }
            else -> false
        }
    }

    private fun shareTextOnly(title: String) {

        // The value which we will sending through data via
        // other applications is defined
        // via the Intent.ACTION_SEND
        val intentt = Intent(Intent.ACTION_SEND)

        // setting type of data shared as text
        intentt.type = "text/plain"
        intentt.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")

        // Adding the text to share using putExtra
        intentt.putExtra(Intent.EXTRA_TEXT, title)
        startActivity(Intent.createChooser(intentt, "Share Via"))
    }

    private fun loadViews() {
        for (i in 0 until game_board_expert.childCount) {
            game_board_expert.getChildAt(i).background =
                AppCompatResources.getDrawable(this, MySharedPreferences.getButtonBackground())

            cell[i / 6][i % 6] = game_board_expert.getChildAt(i) as AppCompatButton?
            cell[i / 6][i % 6]?.tag = i

            cell[i / 6][i % 6]?.onClick { view ->
                val tag: Int = view.tag as Int
                canMove(tag / 6, tag % 6)
            }

        }

    }

    private fun loadDataToViews() {
        for (i in 0..35) {
            if (numbers[i] == 36) {
                x = i / 6
                y = i % 6
                cell[x][y]?.visibility = invisible()
                cell[x][y]?.text = resources.getText(R.string.empty_button_text36)
                cell[x][y]?.isClickable = false
            } else cell[i / 6][i % 6]!!.text = numbers[i].toString()
        }
    }

    private fun initSolvableNumbers() {
        do {
            shuffle()
        } while (!isSolvable(numbers))
    }

    private fun shuffle() = numbers.shuffle()

    private fun isSolvable(list: List<Int>): Boolean {
        var counter = 0
        for (i in list.indices) {
            if (list[i] == 36) {
                counter += i / 6 + 1
                continue
            }
            for (j in i + 1 until list.size) {
                if (list[i] > list[j]) {
                    counter++
                }
            }
        }
        return counter % 2 == 0
    }


    private fun generateNumbers() {
        numbers.clear()
        for (i in 0..35) numbers.add(i + 1)
    }

    private fun createMediaPlayer() {
        when {
            MySharedPreferences.getSound()!! -> {
                mediaPlayer = MediaPlayer.create(this, R.raw.music)
                mediaPlayer?.isLooping = true
            }
            else -> {
                mediaPlayer?.stop()
            }
        }
    }

    private fun createSoundPool() {
        when {
            MySharedPreferences.getSound()!! -> {
                val audioAttributes =
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
                soundPool =
                    SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build()
                soundPlayer = soundPool!!.load(this, R.raw.sound, 1)
                mediaPlayerMusic = MediaPlayer.create(this, R.raw.win)
            }

        }
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

    override fun onPause() {
        super.onPause()
        soundPool?.stop(winPlayer)
        mediaPlayerMusic?.stop()
        println("OnPauseOnPauseOnPauseOnPauseOnPauseOnPause")
        if (checkForWin()) {
            MySharedPreferences.setIsPlayingExpert(false)
        }

        println("OnPauseOnPauseOnPauseOnPauseOnPauseOnPause")
        val builder = StringBuilder()
        timeWhenPaused = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        println(chronometer.text.toString() + "chronometr")
        MySharedPreferences.setIsPlayingExpert(true)
        MySharedPreferences.setScoreExpert(counter)
        MySharedPreferences.setPauseTimeExpert(SystemClock.elapsedRealtime() - chronometer.base)
        for (i in 0 until game_board_expert.childCount) {

            builder.append((game_board_expert.getChildAt(i) as AppCompatButton).text).append("#")
        }
        println("List:${builder.toString()}")
        MySharedPreferences.setNumbersExpert(builder.toString())
        when {
            MySharedPreferences.getSound()!! -> mediaPlayer?.pause()
        }
    }

    override fun onBackPressed() {
        exitDialog()
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

    override fun onStop() {

        if (checkForWin()) {
            MySharedPreferences.setIsPlayingExpert(false)
        }
        super.onStop()
    }

    override fun onDestroy() {

        if (checkForWin()) {
            MySharedPreferences.setIsPlaying(false)
        }
        super.onDestroy()

        soundPool = null
        mediaPlayer = null
    }


    override fun onResume() {
        super.onResume()
        if (MySharedPreferences.getIsPlayingExpert()!!) {
            chronometer.setBase(SystemClock.elapsedRealtime() - MySharedPreferences.getPauseTimeExpert()!!)
            chronometer.start()

        }
    }

}