package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val currentTime = findViewById<TextView>(R.id.tv_count_down)

        val minuteFocus = habit.minutesFocus

        val workManager = WorkManager.getInstance(applicationContext)
        val dataInput = Data.Builder()
            .putString(NOTIFICATION_CHANNEL_ID, NOTIF_UNIQUE_WORK)
            .build()
        val switchReminder = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInitialDelay(minuteFocus, TimeUnit.MINUTES)
            .setInputData(dataInput)
            .build()

        val viewModel = ViewModelProvider(this).get(CountDownViewModel::class.java)

        //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
        viewModel.setInitialTime(minuteFocus)
        viewModel.currentTimeString.observe(this, {
            currentTime.text = it
        })
        viewModel.eventCountDownFinish.observe(this, {
            updateButtonState(false)
        })

        //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            updateButtonState(true)
            viewModel.startTimer()
            workManager.enqueue(switchReminder)
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            updateButtonState(false)
            workManager.cancelWorkById(switchReminder.id)
            viewModel.resetTimer()
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}