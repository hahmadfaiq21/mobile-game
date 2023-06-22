package mobile.game.slidingpuzzle

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.lang.Math.abs
import java.util.Random
import java.util.Timer
import java.util.TimerTask

class GameActivity : AppCompatActivity() {

    private var emptyX = 3
    private var emptyY = 3
    private var stepCount = 0
    private var timeCount = 0
    private lateinit var timer: Timer
    private lateinit var group: RelativeLayout
    private lateinit var buttons: Array<Array<Button?>>
    private lateinit var tiles: IntArray
    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        database = Database(this)
        loadViews()
        loadNumbers()
        generateNumbers()
        loadDataToViews()
    }

    private fun loadViews() {
        group = findViewById(R.id.puzzle_group)
        buttons = Array(4) { arrayOfNulls<Button?>(4) }
        for (i in 0 until group.childCount) {
            buttons[i / 4][i % 4] = group.getChildAt(i) as Button?
        }
        loadTimer()

        val btnShuffle = findViewById<Button>(R.id.button_shuffle)
        btnShuffle.setOnClickListener {
            generateNumbers()
            loadDataToViews()
            timeCount = 0
            stepCount = 0
            val tvSteps = findViewById<TextView>(R.id.text_view_steps)
            val tvTimes = findViewById<TextView>(R.id.text_view_times)
            tvSteps.text = resources.getString(R.string.steps_default)
            tvTimes.text = resources.getString(R.string.times_default)
        }
    }

    private fun loadNumbers() {
        tiles = IntArray(16)
        for (i in 0 until group.childCount - 1) {
            tiles[i] = i + 1
        }
    }

    private fun generateNumbers() {
        var n = 15
        val random = Random()
        while (n > 1) {
            val randomNum = random.nextInt(n--)
            val temp = tiles[randomNum]
            tiles[randomNum] = tiles[n]
            tiles[n] = temp
        }

        if (!isSolvable())
            generateNumbers()
    }

    private fun isSolvable(): Boolean {
        var countInversions = 0
        for (i in 0 until 15) {
            for (j in 0 until i) {
                if (tiles[j] > tiles[i])
                    countInversions++
            }
        }
        return countInversions % 2 == 0
    }

    private fun loadDataToViews() {
        emptyX = 3
        emptyY = 3

        for (i in 0 until group.childCount - 1) {
            buttons[i / 4][i % 4]?.text = tiles[i].toString()
            buttons[i / 4][i % 4]?.setBackgroundResource(android.R.drawable.btn_default)
        }

        buttons[emptyX][emptyY]?.text = ""
        buttons[emptyX][emptyY]?.setBackgroundColor(ContextCompat.getColor(this, R.color.freeButton))
    }

    private fun loadTimer() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                timeCount++
                setTime(timeCount)
            }
        }, 1000, 1000)
    }

    private fun setTime(timeCount: Int) {
        val second = timeCount % 60
        val hour = timeCount / 3600
        val minute = (timeCount - hour * 3600) / 60
        val tvTime = findViewById<TextView>(R.id.text_view_times)
        tvTime.text = String.format("%02d:%02d:%02d", hour, minute, second)
    }

    fun buttonClick(view: View) {
        val button = view as Button
        val tag = button.tag.toString()
        val x = tag[0] - '0'
        val y = tag[1] - '0'

        if ((kotlin.math.abs(emptyX - x) == 1 && emptyY == y) || (kotlin.math.abs(emptyY - y) == 1 && emptyX == x)) {
            buttons[emptyX][emptyY]?.text = button.text.toString()
            buttons[emptyX][emptyY]?.setBackgroundResource(android.R.drawable.btn_default)
            button.text = ""
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.freeButton))
            emptyX = x
            emptyY = y

            stepCount++
            val tvSteps = findViewById<TextView>(R.id.text_view_steps)
            tvSteps.text = stepCount.toString()

            checkWin()
        }
    }

    private fun checkWin() {
        var isWin = false
        if (emptyX == 3 && emptyY == 3) {
            for (i in 0 until group.childCount - 1) {
                if (buttons[i / 4][i % 4]?.text.toString() == (i + 1).toString())
                    isWin = true
                else {
                    isWin = false
                    break
                }
            }
        }

        if (isWin) {
            timer.cancel()
            saveData()
            val stepResult = stepCount.toString()
            val timeResult = timeCount
            showCustomDialog(stepResult, timeResult)
        }
    }

    private fun showCustomDialog(stepResult: String, timeResult: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val textStepResult = dialog.findViewById<TextView>(R.id.text_step_result)
        val textTimeResult = dialog.findViewById<TextView>(R.id.text_time_result)
        textStepResult.text = stepResult
        val second = timeResult % 60
        val hour = timeResult / 3600
        val minute = (timeResult - hour * 3600) / 60
        textTimeResult.text = String.format("%02d:%02d:%02d", hour, minute, second)

        val buttonYes = dialog.findViewById<Button>(R.id.button_yes)
        val buttonNo = dialog.findViewById<Button>(R.id.button_no)

        buttonYes.setOnClickListener {
            generateNumbers()
            loadDataToViews()
            timeCount = 0
            stepCount = 0
            val tvSteps = findViewById<TextView>(R.id.text_view_steps)
            val tvTimes = findViewById<TextView>(R.id.text_view_times)
            tvSteps.text = resources.getString(R.string.steps_default)
            tvTimes.text = resources.getString(R.string.times_default)
            loadTimer()
            dialog.cancel()
        }

        buttonNo.setOnClickListener {
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialog.show()
    }

    private fun saveData() {
        database = Database(this@GameActivity)
        database.saveLastStep(stepCount)
        database.saveLastTime(timeCount)

        if(database.getBestStep() != 0) {
            if (database.getBestStep() > stepCount)
                database.saveBestStep(stepCount)
        } else {
            database.saveBestStep(stepCount)
        }

        if(database.getBestTime() != 0) {
            if (database.getBestTime() > timeCount)
                database.saveBestTime(timeCount)
        } else {
            database.saveBestTime(timeCount)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(0)
        val intent = Intent(this@GameActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}