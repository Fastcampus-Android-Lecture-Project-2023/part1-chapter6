package fastcampus.part1.chpater6

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import fastcampus.part1.chpater6.databinding.ActivityMainBinding
import fastcampus.part1.chpater6.databinding.DialogTimeSettingBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var countDownSecond = 0
    private var workoutSecond = 10
    private var restSecond = 0
    private var roundCount = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateValueUi()

        binding.countDownLayer.setOnClickListener {
            showTimeSettingDialog(Type.CountDown)
        }

        binding.workoutLayer.setOnClickListener {
            showTimeSettingDialog(Type.Workout)
        }

        binding.restLayer.setOnClickListener {
            showTimeSettingDialog(Type.Rest)
        }

        binding.roundLayer.setOnClickListener {
            showTimeSettingDialog(Type.Round)
        }

        binding.startButton.setOnClickListener {
            startTimer()
        }
    }

    private fun showTimeSettingDialog(type: Type) {
        AlertDialog.Builder(this)
            .apply {
                val binding = DialogTimeSettingBinding.inflate(layoutInflater)
                setView(binding.root)
                setTitle(type.title)
                binding.secondNumberPicker.isVisible = type != Type.Round
                binding.firstNumberPicker.apply {
                    maxValue = 60
                    minValue = if (type == Type.Round) 1 else 0
                    value = when (type) {
                        Type.CountDown -> countDownSecond / 60
                        Type.Rest -> restSecond / 60
                        Type.Round -> roundCount
                        Type.Workout -> workoutSecond / 60
                    }
                }
                binding.secondNumberPicker.apply {
                    maxValue = 60
                    minValue = 0
                    value = when (type) {
                        Type.CountDown -> countDownSecond % 60
                        Type.Rest -> restSecond % 60
                        Type.Round -> 0
                        Type.Workout -> workoutSecond % 60
                    }
                }

                setPositiveButton("확인") { _, _ ->
                    when (type) {
                        Type.CountDown -> countDownSecond = type.calculateValue(
                            binding.firstNumberPicker.value,
                            binding.secondNumberPicker.value
                        )
                        Type.Rest -> restSecond = type.calculateValue(
                            binding.firstNumberPicker.value,
                            binding.secondNumberPicker.value
                        )
                        Type.Round -> roundCount = type.calculateValue(
                            binding.firstNumberPicker.value,
                            binding.secondNumberPicker.value
                        )
                        Type.Workout -> workoutSecond = type.calculateValue(
                            binding.firstNumberPicker.value,
                            binding.secondNumberPicker.value
                        )
                    }
                    updateValueUi()
                }
                setNegativeButton("취소", null)
                show()
            }
    }

    private fun updateValueUi() {
        binding.countDownTimeTextView.text =
            String.format("%02d:%02d", countDownSecond / 60, countDownSecond % 60)
        binding.workoutTimeTextView.text =
            String.format("%02d:%02d", workoutSecond / 60, workoutSecond % 60)
        binding.restTimeTextView.text = String.format("%02d:%02d", restSecond / 60, restSecond % 60)
        binding.roundCountTextView.text = "$roundCount"

        val totalSecond = workoutSecond * roundCount + restSecond * maxOf(roundCount.dec(), 0)
        binding.totalTimeTextView.text =
            String.format("%02d:%02d", totalSecond / 60, totalSecond % 60)
    }

    private fun startTimer() {
        Intent(this, TimerActivity::class.java)
            .let {
                it.putExtra(COUNTDOWN_SECOND, countDownSecond)
                it.putExtra(WORKOUT_SECOND, workoutSecond)
                it.putExtra(REST_SECOND, restSecond)
                it.putExtra(ROUND_COUNT, roundCount)
                startActivity(it)
            }
    }
}

sealed class Type(val title: String) {
    object CountDown : Type("카운트다운")
    object Workout : Type("운동")
    object Rest : Type("휴식")
    object Round : Type("라운드")

    fun calculateValue(firstValue: Int, secondValue: Int = 0): Int {
        return when (this) {
            is Round -> firstValue
            else -> firstValue * 60 + secondValue
        }
    }
}