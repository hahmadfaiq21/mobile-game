package mobile.game.slidingpuzzle

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale
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
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        preferences = Preferences(this)
        loadViews()
        resetGame()
    }

    private fun loadViews() {
        group = findViewById(R.id.puzzle_group)
        buttons = Array(4) { arrayOfNulls(4) }

        for (i in 0 until group.childCount) {
            buttons[i / 4][i % 4] = group.getChildAt(i) as Button?
        }

        findViewById<Button>(R.id.button_shuffle).setOnClickListener {
            resetGame()
        }
    }

    private fun resetGame() {
        generateNumbers()
        loadDataToViews()
        resetCounters()
        loadTimer()
    }

    private fun resetCounters() {
        timeCount = 0
        stepCount = 0
        findViewById<TextView>(R.id.text_view_steps).text = getString(R.string.steps_default)
        findViewById<TextView>(R.id.text_view_times).text = getString(R.string.times_default)
    }

    private fun generateNumbers() {
        tiles = (1..15).toList().shuffled().toIntArray()
        if (!isSolvable()) generateNumbers()
    }

    private fun isSolvable(): Boolean {
        val inversions = tiles.withIndex().sumOf { (i, value) ->
            tiles.take(i).count { it > value }
        }
        return inversions % 2 == 0
    }

    private fun loadDataToViews() {
        emptyX = 3
        emptyY = 3
        buttons.flatten().forEachIndexed { index, button ->
            button?.apply {
                text = if (index < 15) tiles[index].toString() else getString(R.string.holder)
                setBackgroundResource(if (index < 15) android.R.drawable.btn_default else R.color.freeButton)
            }
        }
    }

    private fun loadTimer() {
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    timeCount++
                    updateTimerText()
                }
            }, 1000, 1000)
        }
    }

    private fun updateTimerText() {
        val timeFormatted = String.format(
            Locale.US, "%02d:%02d:%02d",
            timeCount / 3600, (timeCount % 3600) / 60, timeCount % 60
        )
        findViewById<TextView>(R.id.text_view_times).text = timeFormatted
    }

    fun buttonClick(view: View) {
        val (x, y) = (view.tag as String).map { it - '0' }
        if (canMove(x, y)) {
            swapTiles(view as Button, x, y)
            stepCount++
            findViewById<TextView>(R.id.text_view_steps).text = stepCount.toString()
            checkWin()
        }
    }

    private fun canMove(x: Int, y: Int) =
        (kotlin.math.abs(emptyX - x) == 1 && emptyY == y) || (kotlin.math.abs(emptyY - y) == 1 && emptyX == x)

    private fun swapTiles(button: Button, x: Int, y: Int) {
        buttons[emptyX][emptyY]?.apply {
            text = button.text
            setBackgroundResource(android.R.drawable.btn_default)
        }
        button.apply {
            text = getString(R.string.holder)
            setBackgroundColor(ContextCompat.getColor(this@GameActivity, R.color.freeButton))
        }
        emptyX = x
        emptyY = y
    }

    private fun checkWin() {
        if (emptyX == 3 && emptyY == 3 && buttons.flatten().take(15).withIndex()
                .all { (index, button) ->
                    button?.text == (index + 1).toString()
                }
        ) {
            timer.cancel()
            saveData()
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.custom_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)

            val stepResult = stepCount.toString()
            val timeFormatted = String.format(
                Locale.US, "%02d:%02d:%02d",
                timeCount / 3600, (timeCount % 3600) / 60, timeCount % 60
            )

            findViewById<TextView>(R.id.text_step_result).text = stepResult
            findViewById<TextView>(R.id.text_time_result).text = timeFormatted

            findViewById<Button>(R.id.button_yes).setOnClickListener {
                resetGame()
                cancel()
            }

            findViewById<Button>(R.id.button_no).setOnClickListener {
                startActivity(Intent(this@GameActivity, MainActivity::class.java))
                finish()
            }

            show()
        }
    }

    private fun saveData() {
        preferences.apply {
            saveLastStep(stepCount)
            saveLastTime(timeCount)

            if (getBestStep() == 0 || getBestStep() > stepCount) saveBestStep(stepCount)
            if (getBestTime() == 0 || getBestTime() > timeCount) saveBestTime(timeCount)
        }
    }
}