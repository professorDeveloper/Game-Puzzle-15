package com.azamovhudstc.gamepuzzle15.screen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_game_medium.*
import kotlinx.android.synthetic.main.activity_game_medium.button_exit
import kotlinx.android.synthetic.main.activity_game_medium.button_restart
import kotlinx.android.synthetic.main.activity_game_medium.button_resume
import kotlinx.android.synthetic.main.activity_game_medium.chronometer
import kotlinx.android.synthetic.main.activity_game_medium.container
import kotlinx.android.synthetic.main.activity_game_medium.container_timer_moves
import kotlinx.android.synthetic.main.activity_game_medium.image
import kotlinx.android.synthetic.main.activity_game_medium.info
import kotlinx.android.synthetic.main.activity_game_medium.moves
import kotlinx.android.synthetic.main.activity_game_medium.tv_play
import kotlinx.android.synthetic.main.dialog_about.view.*
import kotlinx.android.synthetic.main.dialog_winner.view.*
import java.util.*
import kotlin.math.abs

/**
 * [0][0] =1
 * [0][1] =2
 * [0][2] =3
 * [0][3] =4
 * [0][4] =5
 * [1][0] =6
 * [1][1] =7
 * [1][2] =8
 * [1][3] =9
 * [1][4] =10
 * [2][0] =11
 * [2][1] =12
 * [2][2] =13
 * [2][3] =14
 * [2][4] =15
 * [3][0] =16
 * [3][1] =17
 * [3][2] =18
 * [3][3] =19
 * [3][4] =20
 * [4][0] =21
 * [4][1] =22
 * [4][2] =23
 * [4][3] =24
 * [4][4] =25
 * **/
class GameMediumActivity : AppCompatActivity() {
    private val isGameRestarted: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isGamePaused: MutableLiveData<Boolean> = MutableLiveData(false)
    private val cell = Array(5) { arrayOfNulls<AppCompatButton>(5) }
    private var numbers: ArrayList<Int> = ArrayList(25)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_game_medium)
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())

        if (MySharedPreferences.getIsPlayingMedium()!!) {
            counter = MySharedPreferences.getScoreMedium()
            moves.text = counter.toString()
            val numbersText: String = MySharedPreferences.getNumbersMedium().toString()
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

    private fun pauseGame() {
        isGamePaused.value = true
        isGamePaused.observe(this) { isGamePausedObserver(it) }
    }

    private fun isGamePausedObserver(isPaused: Boolean) {
        if (isPaused) {
            button_restart.isClickable = false
            timeWhenPaused = chronometer.base - SystemClock.elapsedRealtime()
            chronometer.stop()
            button_resume.visibility = visible()
            button_exit.visibility = invisible()
            menu_medium.visibility = invisible()
            button_restart.visibility = invisible()
            info.visibility = invisible()
            button_exit.visibility = invisible()
            button_resume.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.anim_scale
                )
            )

        } else {
            button_restart.isClickable = true
            chronometer.base = timeWhenPaused + SystemClock.elapsedRealtime()
            chronometer.start()
            button_resume.visibility = invisible()
            button_restart.visibility = visible()
            button_exit.visibility = visible()
            menu_medium.visibility = visible()
            info.visibility = visible()
            button_resume.clearAnimation()

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

        button_resume.onClick {
            isGamePaused.value = false
        }
        button_exit.onClick {
            onBackPressed()
        }
        menu_medium.onClick {
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

    private fun loadDataToViews() {
        for (i in 0..24) {
            if (numbers[i] == 25) {
                x = i / 5
                y = i % 5
                cell[x][y]?.visibility = invisible()
                cell[x][y]?.text = resources.getText(R.string.empty_button_text26)
                cell[x][y]?.isClickable = false
            } else cell[i / 5][i % 5]!!.text = numbers[i].toString()
        }
    }

    override fun onStop() {
        if (checkForWin()) {
            MySharedPreferences.setIsPlayingMedium(false)
        }
        super.onStop()
    }

    private fun generateNumbers() {
        numbers.clear()
        for (i in 0..24) numbers.add(i + 1)
    }

    private fun shuffle() = numbers.shuffle()

    private fun startGame() {
        if (!MySharedPreferences.getIsPlayingMedium()!!) {
            initSolvableNumbers()
        }
        loadViews()
        loadDataToViews()
    }

    private fun initSolvableNumbers() {
        do {
            shuffle()
        } while (!isSolvable(numbers))
    }

    private fun isSolvable(list: List<Int>): Boolean {
        var counter = 0
        for (i in list.indices) {
            if (list[i] == 25) {
                counter += i / 5 + 1
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


    private fun makeMoveButtonAnimation(x: Int, y: Int) {
        YoYo
            .with(Techniques.BounceIn)
            .duration(400)
            .playOn(cell[x][y])
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
        for (i in 0 until game_board_medium.childCount) {
            game_board_medium.getChildAt(i).background =
                AppCompatResources.getDrawable(this, MySharedPreferences.getButtonBackground())

            cell[i / 5][i % 5] = game_board_medium.getChildAt(i) as AppCompatButton?
            cell[i / 5][i % 5]?.tag = i

            cell[i / 5][i % 5]?.onClick { view ->
                val tag: Int = view.tag as Int
                canMove(tag / 5, tag % 5)
            }

        }

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
        cell[i][j]?.text = "25"
        cell[i][j]?.isClickable = false

        // changing values of x and y after swap
        this.x = i
        this.y = j
    }

    private fun checkForWin(): Boolean {
        //The first column of the each row must have a value
        if (cell[0][0]?.text.toString().isNotEmpty() &&
            cell[1][0]?.text.toString().isNotEmpty() &&
            cell[2][0]?.text.toString().isNotEmpty() &&
            cell[3][0]?.text.toString().isNotEmpty() &&
            cell[4][0]?.text.toString().isNotEmpty()
        ) {
            //Checking the each row's first column values and the last cell should be checked for the emptiness
            if (cell[0][0]?.text.toString().toInt() == 1//
                && cell[1][0]?.text.toString().toInt() == 6//
                && cell[2][0]?.text.toString().toInt() == 11
                && cell[3][0]?.text.toString().toInt() == 16
                && cell[4][0]?.text.toString().toInt() == 21
                && cell[4][4]?.text.toString().toInt() == 25
            ) {
                /*If the first column of the each row is in the accurate position and if the last is empty,
                     then start checking the entire cell */
                var counter = 1
                for (i in 0 until game_board_medium.childCount - 1) {
                    val button = game_board_medium.getChildAt(i) as AppCompatButton
                    if (button.text.toString().isEmpty()) break
                    if (counter == (button.text.toString().toInt()))
                        counter++
                }
                //If the counter is 16 then the values are in their accurate positions
                return counter == 25
            }
        }
        return false
    }

    private fun showGameOverAnimationAndDialog() {
        // save record
        MySharedPreferences.setIsPlayingMedium(false)
        saveRecord()

        // stop all actions such as clicking buttons and timer
        stopAllActions()
        // win sound
        playWinSound()
        popUpGameOverDialog()

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
                mediaPlayerMusic = MediaPlayer.create(this,R.raw.win)
            }

        }
    }

    private fun saveRecord() {

        val oldRecord =
            MySharedPreferences.getRecord()
        if (oldRecord == null) {
            MySharedPreferences.setRecord(counter.toString())
        } else {
            val newResult = "$oldRecord#$counter"
            MySharedPreferences.setRecord(newResult)
        }
    }

    private fun playWinSound() {
        if (MySharedPreferences.getSound()!!
        ) mediaPlayerMusic?.start()
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
            numbers.shuffle()
            onPause()
            mediaPlayerMusic?.stop()
            dialog.dismiss()
            finish()
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
        for (i in 0 until game_board_medium.childCount) {
            game_board_medium.getChildAt(i).isClickable = false
        }
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

    private fun restartGame() {
        isGameRestarted.value = false
        isGameRestarted.observe(this) { isGameRestartedObserver(it) }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerMusic?.stop()

        println("OnPauseOnPauseOnPauseOnPauseOnPauseOnPause")
        soundPool?.stop(winPlayer)
        println("OnPauseOnPauseOnPauseOnPauseOnPauseOnPause")
        if (checkForWin()){
            MySharedPreferences.setIsPlayingMedium(false)
        }
        val builder = StringBuilder()
        timeWhenPaused = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        println(chronometer.text.toString() + "chronometr")
        MySharedPreferences.setIsPlayingMedium(true)
        MySharedPreferences.setScoreMedium(counter)
        MySharedPreferences.setPauseTimeMedium(SystemClock.elapsedRealtime() - chronometer.base)
        for (i in 0 until game_board_medium.childCount) {

            builder.append((game_board_medium.getChildAt(i) as AppCompatButton).text).append("#")
        }
            println("List:${builder.toString()}")
            MySharedPreferences.setNumbersMedium(builder.toString())
            when {
                MySharedPreferences.getSound()!! -> mediaPlayer?.pause()
        }
    }

    private fun isGameRestartedObserver(isActive: Boolean) {
        counter = 0
        button_restart.isClickable = true
        button_restart.visibility = visible()
        button_exit.visibility = visible()
        menu_medium.visibility = visible()
        info.visibility = visible()
        button_exit.visibility = visible()
        container_timer_moves.visibility = visible()
        moves.text = counter.toString()
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        game_board_medium.background = AppCompatResources.getDrawable(
            this, R.drawable.shape_game_board_parent
        )
        for (i in 0..game_board_medium.childCount) {
            val currentButton = game_board_medium.getChildAt(i) as AppCompatButton?
            currentButton?.visibility = visible()
        }
        numbers.shuffle()
        startGame()


    }

    override fun onResume() {
        super.onResume()
        if (MySharedPreferences.getIsPlayingMedium()!!) {
            chronometer.setBase(SystemClock.elapsedRealtime() - MySharedPreferences.getPauseTimeMedium()!!)
            chronometer.start()

        }
    }

    override fun onStart() {
        super.onStart()
        if (MySharedPreferences.getSound()!!
        ) {
            createSoundPool()
            playSound()
            Log.d(TAG, "onStart: Shuyerga tushdi Yani True  )")
        } else {
            Log.d(TAG, "onStart: Shuyerga tushdi Yani False_")
            playSound()
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

    override fun onDestroy() {
        super.onDestroy()
        if (checkForWin()){
            MySharedPreferences.setIsPlayingMedium(false)
        }
        mediaPlayer = null
    }

}