package fastcampus.part1.chpater6

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fastcampus.part1.chpater6.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private var countDownSecond = 0
    private var workoutSecond = 0
    private var restSecond = 0
    private var roundCount = 0
    private var totalMilliSecond = 0

    private var thread: Thread? = null
    private var isWorkout = true
    private var progressTime = 0
    private var progressRoundCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        updateUi()

        binding.startButton.setOnClickListener {
            start()
            binding.startButton.isVisible = false
            binding.pauseButton.isVisible = true
        }

        binding.pauseButton.setOnClickListener {
            pause()
            binding.startButton.isVisible = true
            binding.pauseButton.isVisible = false
        }

        binding.stopButton.setOnClickListener {
            stop()
        }
    }

    private fun start() {
        thread = Thread {
            while (totalMilliSecond > 0) {
                try {
                    Thread.sleep(10)
                    totalMilliSecond -= 10
                    progressTime -= 10
                    if (progressTime == 0) {
                        if (restSecond > 0) {
                            isWorkout = isWorkout.not()
                        }
                        progressTime = (if (isWorkout) workoutSecond else restSecond).times(1000)
                        if (isWorkout) progressRoundCount += 1
                    }
                    runOnUiThread { updateUi() }
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        thread?.start()
    }

    private fun pause() {
        thread?.interrupt()
    }

    private fun stop() {
        AlertDialog.Builder(this).apply {
            setMessage("타이머를 종료하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                finish()
            }
            setNegativeButton("취소", null)
        }.show()
    }

    private fun initData() {
        countDownSecond = intent.getIntExtra(COUNTDOWN_SECOND, 0)
        workoutSecond = intent.getIntExtra(WORKOUT_SECOND, 0)
        restSecond = intent.getIntExtra(REST_SECOND, 0)
        roundCount = intent.getIntExtra(ROUND_COUNT, 0)
        totalMilliSecond =
            (countDownSecond + workoutSecond * roundCount + restSecond * maxOf(
                roundCount.dec(),
                0
            )) * 1000

        progressTime = workoutSecond * 1000
    }

    private fun updateUi() {
        val totalMilliSecond = totalMilliSecond - countDownSecond * 1000
        binding.timeTextView.text =
            String.format(
                "%02d:%02d",
                progressTime.div(1000) / 60,
                progressTime.div(1000) % 60
            )

        progressRoundCount = minOf(progressRoundCount, roundCount)
        binding.roundTextView.text = "$progressRoundCount/$roundCount"
        binding.millisecondTextView.text = String.format("%02d", totalMilliSecond.div(10) % 100)

        binding.typeTextView.text = if (isWorkout) "운동" else "휴식"

        binding.timeProgressBar.progress = progressTime.div(1000f).div(
            if (isWorkout) workoutSecond else restSecond
        ).times(100).toInt()
    }
}