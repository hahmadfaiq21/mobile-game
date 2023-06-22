package mobile.game.slidingpuzzle

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    private var REQUEST_CODE = 0
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        preferences = Preferences(this)

        val buttonStartGame = findViewById<Button>(R.id.button_start_game)
        buttonStartGame.setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
            finish()
        }

        val switchDarkMode = findViewById<SwitchCompat>(R.id.switch_dark_mode)
        switchDarkMode.setOnCheckedChangeListener { _, b ->
            switchDarkMode.isChecked = b
            preferences.saveMode(b)
            if (preferences.getMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            setResult(0)
        }

        switchDarkMode.isChecked = preferences.getMode()
        loadMode()
        loadData()
    }

    private fun loadMode() {
        if (preferences.getMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun loadData() {
        val textLastStep = findViewById<TextView>(R.id.text_last_step)
        textLastStep.text = preferences.getLastStep().toString()

        val textLastTime = findViewById<TextView>(R.id.text_last_time)
        val lastTimePref = preferences.getLastTime()
        val lastSecond = lastTimePref %60
        val lastHour = lastTimePref / 3600
        val lastMinute = (lastTimePref - lastHour * 3600) / 60
        textLastTime.text = String.format("%02d:%02d:%02d", lastHour, lastMinute, lastSecond)

        val textBestStep = findViewById<TextView>(R.id.text_best_step)
        textBestStep.text = preferences.getBestStep().toString()

        val textBestTime = findViewById<TextView>(R.id.text_best_time)
        val bestTimePref = preferences.getBestTime()
        val bestSecond = bestTimePref %60
        val bestHour = bestTimePref / 3600
        val bestMinute = (bestTimePref - bestHour * 3600) / 60
        textBestTime.text = String.format("%02d:%02d:%02d", bestHour, bestMinute, bestSecond)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE) {
            val textLastStep = findViewById<TextView>(R.id.text_last_step)
            textLastStep.text = preferences.getLastStep().toString()

            val textLastTime = findViewById<TextView>(R.id.text_last_time)
            val lastTimePref = preferences.getLastTime()
            val second = lastTimePref %60
            val hour = lastTimePref / 3600
            val minute = (lastTimePref - hour * 3600) / 60
            textLastTime.text = String.format("%02d:%02d:%02d", hour, minute, second)

            val textBestStep = findViewById<TextView>(R.id.text_best_step)
            textBestStep.text = preferences.getBestStep().toString()

            val textBestTime = findViewById<TextView>(R.id.text_best_time)
            val bestTimePref = preferences.getBestTime()
            val bestSecond = bestTimePref %60
            val bestHour = bestTimePref / 3600
            val bestMinute = (bestTimePref - bestHour * 3600) / 60
            textBestTime.text = String.format("%02d:%02d:%02d", bestHour, bestMinute, bestSecond)

            if (preferences.getMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}