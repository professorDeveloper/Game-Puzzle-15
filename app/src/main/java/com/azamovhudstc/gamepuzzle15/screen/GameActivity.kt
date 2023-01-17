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
import kotlinx.android.synthetic.main.activity_game.button_exit
import kotlinx.android.synthetic.main.activity_game.button_restart
import kotlinx.android.synthetic.main.activity_game.button_resume
import kotlinx.android.synthetic.main.activity_game.chronometer
import kotlinx.android.synthetic.main.activity_game.container
import kotlinx.android.synthetic.main.activity_game.container_timer_moves
import kotlinx.android.synthetic.main.activity_game.image
import kotlinx.android.synthetic.main.activity_game.info
import kotlinx.android.synthetic.main.activity_game.moves
import kotlinx.android.synthetic.main.activity_game.tv_play
import kotlinx.android.synthetic.main.activity_game_expert.*
import kotlinx.android.synthetic.main.dialog_winner.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class GameActivity : AppCompatActivity() {
    private val isGameRestarted: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isGamePaused: MutableLiveData<Boolean> = MutableLiveData(false)
    private val cell = Array(4) { arrayOfNulls<AppCompatButton>(4) }
    private var x = 0
    private var y = 0
    private var numbers: ArrayList<Int> = ArrayList(16)
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
        setContentView(R.layout.activity_game)
        container.setBackgroundResource(MySharedPreferences.getBackgroundResource())

        if (MySharedPreferences.getIsPlaying()!!) {
            counter = MySharedPreferences.getScore()
            moves.text = counter.toString()
            numbers.clear()
            val numbersText: String = MySharedPreferences.getNumbers().toString()
            val numbersArray = numbersText.split("#").toTypedArray()
            val numberList: List<String> = java.util.ArrayList(Arrays.asList(*numbersArray))
            numbers.addAll(loadSavedNumbers(numberList))


        } else {
            generateNumbers()
        }
        createSoundPool()
        createMediaPlayer()
        startGame()
        subscribeViewBinding()

        println("Qanday ekan list:" + numbers)

        if (MySharedPreferences.getSound()!!) {
            image.setImageResource(R.drawable.ic_music_on)
        } else {
            image.setImageResource(R.drawable.ic_music_off)
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.music)

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

    private fun subscribeViewBinding() {
        chronometer.start()
        button_restart.onClick {
            restartGame()
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
        info.onClick {
            shareTextOnly("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
        }
        menu.onClick {
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

    private fun startGame() {
        if (!MySharedPreferences.getIsPlaying()!!) {
            initSolvableNumbers()
        }
        loadViews()
        loadDataToViews()
    }

    private fun loadViews() {
        for (i in 0 until game_board.childCount) {
            game_board.getChildAt(i).background =
                AppCompatResources.getDrawable(this, MySharedPreferences.getButtonBackground())

            cell[i / 4][i % 4] = game_board.getChildAt(i) as AppCompatButton?
            cell[i / 4][i % 4]?.tag = i

            cell[i / 4][i % 4]?.onClick { view ->
                val tag: Int = view.tag as Int
                canMove(tag / 4, tag % 4)
            }

        }

    }

    private fun loadDataToViews() {
        for (i in 0..15) {
            if (numbers[i] == 16) {
                x = i / 4
                y = i % 4
                cell[x][y]?.visibility = invisible()
                cell[x][y]?.text = resources.getText(R.string.empty_button_text)
                cell[x][y]?.isClickable = false
            } else cell[i / 4][i % 4]!!.text = numbers[i].toString()
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

    private fun swapEmptyButtonWithCurrentButton(x: Int, y: Int, i: Int, j: Int) {
        // for x and y (current swiped button)
        cell[x][y]?.visibility = visible()
        cell[x][y]?.text = cell[i][j]?.text.toString()

        cell[x][y]?.isClickable = true

        // for i and j (empty button)
        cell[i][j]?.visibility = invisible()
        cell[i][j]?.text = "16"
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
            cell[3][0]?.text.toString().isNotEmpty()
        ) {
            //Checking the each row's first column values and the last cell should be checked for the emptiness
            if (cell[0][0]?.text.toString().toInt() == 1
                && cell[1][0]?.text.toString().toInt() == 5
                && cell[2][0]?.text.toString().toInt() == 9
                && cell[3][0]?.text.toString().toInt() == 13
                && cell[3][3]?.text.toString().toInt() == 16
            ) {
                /*If the first column of the each row is in the accurate position and if the last is empty,
                     then start checking the entire cell */
                var counter = 1
                for (i in 0 until game_board.childCount - 1) {
                    val button = game_board.getChildAt(i) as AppCompatButton
                    if (button.text.toString().isEmpty()) break
                    if (counter == (button.text.toString().toInt()))
                        counter++
                }
                //If the counter is 16 then the values are in their accurate positions
                return counter == 16
            }
        }
        return false
    }

    private fun showGameOverAnimationAndDialog() {
        // save record
        saveRecord()
        MySharedPreferences.setIsPlaying(false)

        // stop all actions such as clicking buttons and timer
        stopAllActions()
        playWinSound()
        // win sound
        popUpGameOverDialog()
    }

    private fun saveRecord() {
        MySharedPreferences.setIsPlaying(false)
        val oldRecord =
            MySharedPreferences.getRecord()
        if (oldRecord == null) {
            MySharedPreferences.setRecord(counter.toString())
            println("New Result Counter:::" + counter)
        } else {
            val newResult = "$oldRecord#$counter"
            MySharedPreferences.setRecord(newResult)
            println("New Result:::" + newResult)
        }
    }

    private fun playWinSound() {
        if (MySharedPreferences.getSound()!!
        )mediaPlayerMusic?.start()
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
            soundPool?.stop(winPlayer)
            numbers.shuffle()
            restartGame()
        }
        binding.home.onClick {
            soundPool?.stop(winPlayer)

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
//        button_pause.isClickable = false
        for (i in 0 until game_board.childCount) {
            game_board.getChildAt(i).isClickable = false
        }
    }


    private fun restartGame() {
        isGameRestarted.value = false
        isGameRestarted.observe(this) { isGameRestartedObserver() }
    }

    private fun isGameRestartedObserver() {
        counter = 0
//            button_restart.visibility = visible()
//            button_pause.visibility = visible()

        container_timer_moves.visibility = visible()
        moves.text = counter.toString()
        menu.visibility = visible()
        button_exit.visibility = visible()
        info.visibility = visible()
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        game_board.background = AppCompatResources.getDrawable(
            this, R.drawable.shape_game_board_parent
        )
        for (i in 0..game_board.childCount) {
            val currentButton = game_board.getChildAt(i) as AppCompatButton?
            currentButton?.visibility = visible()
        }
        println(numbers.toString())
        numbers.shuffle()
        println(numbers.toString())
        startGame()


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

    private fun generateNumbers() {
        numbers.clear()
        for (i in 0..15) numbers.add(i + 1)
    }

    private fun initSolvableNumbers() {
        do {
            shuffle()
        } while (!isSolvable(numbers))
    }

    private fun isSolvable(list: List<Int>): Boolean {
        var counter = 0
        for (i in list.indices) {
            if (list[i] == 16) {
                counter += i / 4 + 1
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


    private fun shuffle() = numbers.shuffle()


    private fun makeMoveButtonAnimation(x: Int, y: Int) {
        YoYo
            .with(Techniques.BounceIn)
            .duration(400)
            .playOn(cell[x][y])
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

    private fun playSound() {
        if (MySharedPreferences.getSound()!!
        ) soundPool?.play(soundPlayer, 1f, 1f, 1, 0, 1f)
        else {
            soundPool?.stop(soundPlayer)

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

    override fun onPause() {
        super.onPause()
        mediaPlayerMusic?.stop()
        println("OnPauseOnPauseOnPauseOnPauseOnPauseOnPause")
        val builder = StringBuilder()
        timeWhenPaused = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        if (checkForWin()){
            MySharedPreferences.setIsPlaying(false)
        }
        println(chronometer.text.toString() + "chronometr")
        MySharedPreferences.setIsPlaying(true)
        MySharedPreferences.setScore(counter)
        MySharedPreferences.setPauseTime(SystemClock.elapsedRealtime() - chronometer.base)
        for (i in 0 until game_board.childCount) {

            builder.append((game_board.getChildAt(i) as AppCompatButton).text).append("#")
        }

        println("List:${builder.toString()}")
        MySharedPreferences.setNumbers(builder.toString())
        when {
            MySharedPreferences.getSound()!! -> mediaPlayer?.pause()
        }
    }

    override fun onStop() {
        if (checkForWin()) {
            MySharedPreferences.setIsPlaying(false)
        }
        super.onStop()
    }
    override fun onBackPressed() {

//        object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (onBackPressedTime + 2000 > System.currentTimeMillis()) {
////                        saveGame()
//                    findNavController().popBackStack()
//                    return
//                } else toast(getString(R.string.snackbar_exit_text))
//                onBackPressedTime = System.currentTimeMillis()
//            }
//        })

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

        if (checkForWin()){
            MySharedPreferences.setIsPlaying(false)
        }
        soundPool = null
        mediaPlayer = null
        super.onDestroy()


    }

    override fun onResume() {
        super.onResume()
        if (MySharedPreferences.getIsPlaying()!!) {
            chronometer.setBase(SystemClock.elapsedRealtime() - MySharedPreferences.getPauseTime()!!)
            chronometer.start()

        }
//            createSoundPool()
//            createMediaPlayer()

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
}