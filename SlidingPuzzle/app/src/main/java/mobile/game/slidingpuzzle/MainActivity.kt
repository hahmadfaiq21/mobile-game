package mobile.game.slidingpuzzle

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences

    private val startGameLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadData()
                applyMode()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        preferences = Preferences(this)

        findViewById<Button>(R.id.button_start_game).setOnClickListener {
            startGameLauncher.launch(Intent(this, GameActivity::class.java))
        }

        findViewById<SwitchCompat>(R.id.switch_dark_mode).apply {
            isChecked = preferences.getMode()
            setOnCheckedChangeListener { _, isChecked ->
                preferences.saveMode(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
                setResult(RESULT_OK)
            }
        }

        applyMode()
        loadData()
    }

    private fun applyMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (preferences.getMode()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun loadData() {
        fun formatTime(time: Int): String {
            val hour = time / 3600
            val minute = (time % 3600) / 60
            val second = time % 60
            return String.format(Locale.US, "%02d:%02d:%02d", hour, minute, second)
        }

        findViewById<TextView>(R.id.text_last_step).text = preferences.getLastStep().toString()
        findViewById<TextView>(R.id.text_last_time).text = formatTime(preferences.getLastTime())
        findViewById<TextView>(R.id.text_best_step).text = preferences.getBestStep().toString()
        findViewById<TextView>(R.id.text_best_time).text = formatTime(preferences.getBestTime())
    }
}