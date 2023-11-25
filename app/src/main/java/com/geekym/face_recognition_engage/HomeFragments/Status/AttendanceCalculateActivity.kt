package com.geekym.face_recognition_engage.HomeFragments.Status

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.geekym.face_recognition_engage.R
import com.geekym.face_recognition_engage.databinding.ActivityAttendanceCalculateBinding
import com.geekym.face_recognition_engage.utils.UtilsKt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)

class AttendanceCalculateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceCalculateBinding
    private lateinit var reference: DatabaseReference
    private var count: Long = 0
    private var expectedPercent: Float = 0.0f
    private var todayDate: Int = 1
    private var currentPercent: Float = 0f
    private var monthAttendanceRatio: Float = 0f
    private var daysLeft: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reference = FirebaseDatabase.getInstance().reference;

        setView()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.apply {
            attendanceSeekBar.apply {
                max = 100
                min = 0
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        checkAchievability(p0, p1, p2)
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {

                    }

                })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkAchievability(p0: SeekBar?, p1: Int, p2: Boolean) {
        // Assume you have a seek bar with values ranging from 0 to 100
        val seekBarValue: Int = p1// Get the current value of the seek bar (0 to 100)
        binding.seekbarValue.text = "To achieve $seekBarValue %"

        binding.seekbarValue.visibility = View.VISIBLE
        binding.daysNeededValue.visibility = View.VISIBLE

        val needToAttend: Float = (30 * (seekBarValue - currentPercent)) / 100f
        binding.daysNeededValue.text = if (needToAttend > 0) {
            "Attend more ${needToAttend.roundToInt()} days"
        } else "No need to attend more"


        Log.d("", "DaysLeft: $daysLeft and needToAttend: $needToAttend")

        if (needToAttend <= daysLeft) {
            binding.attendancePossibilityTextView.text = getString(R.string.achievable)
            binding.attendancePossibilityTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.green_desat, null))
            binding.totalAttendanceValue.text = "Total attendance: ${maxOf(seekBarValue, currentPercent.toInt())} %"
            binding.totalDaysValue.text = "Total days: ${maxOf(needToAttend.roundToInt() + count, count)} days"
        } else {
            binding.attendancePossibilityTextView.text = getString(R.string.not_achievable)
            binding.attendancePossibilityTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.red_desat, null))
            Log.d("", "I need more ${needToAttend - daysLeft} days to achieve $seekBarValue % attendance")

            binding.totalAttendanceValue.text = "Total attendance: $currentPercent %"
            binding.totalDaysValue.text = "Total days: $count days"
        }
    }


    private fun setView() {
        val userDataSP = applicationContext.getSharedPreferences("userData", 0)
        val userID = userDataSP.getString("userID", "0")

        val cal = Calendar.getInstance()
        val year = SimpleDateFormat("yyyy").format(cal.time)
        val monthName = SimpleDateFormat("MMM").format(cal.time)
        val month = SimpleDateFormat("MM").format(cal.time)
        val date = SimpleDateFormat("dd").format(cal.time)
        val yearMonthObject = YearMonth.of(year.toInt(), month.toInt())
        val daysInMonth = yearMonthObject.lengthOfMonth()
        todayDate = date.toInt()
        monthAttendanceRatio = 100f / daysInMonth.toFloat()
        daysLeft = daysInMonth - todayDate


        reference.child("Users").child(userID!!).child("Attendance").child(year).child(monthName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n", "DefaultLocale")
                override fun onDataChange(snapshot: DataSnapshot) {
                    count = snapshot.childrenCount

                    val absent = todayDate - count
                    currentPercent = ((count * 100) / daysInMonth).toFloat()
                    binding.attendanceSeekBar.setProgress(currentPercent.toInt(), true)
                    binding.seekbarValue.visibility = View.GONE
                    binding.daysNeededValue.visibility = View.GONE

                    expectedPercent = (((daysInMonth - todayDate) * 100) / daysInMonth.toFloat()) + currentPercent

                    Log.d(
                        "",
                        "workingdays: $daysInMonth\ncount: $count\nabsent: $absent\npercent: $currentPercent\nexpected: $expectedPercent"
                    )

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}