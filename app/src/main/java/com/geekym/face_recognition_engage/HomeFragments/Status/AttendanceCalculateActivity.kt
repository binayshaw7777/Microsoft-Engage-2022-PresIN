package com.geekym.face_recognition_engage.HomeFragments.Status

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
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
    private val selectedDates = HashSet<Long>() // Use a HashSet to store selected dates
    private var attendanceSeekValue: Int = 0
    private var expectedPercent: Float = 0.0f

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

    private fun checkAchievability(p0: SeekBar?, p1: Int, p2: Boolean) {
        // Assume you have a seek bar with values ranging from 0 to 100
        val seekBarValue: Int = p1// Get the current value of the seek bar (0 to 100)

        // Calculate the desired attendance percentage based on the seek bar value
        val desiredPercent: Float = seekBarValue.toFloat()
        val daysInMonth = UtilsKt.getWorkingDaysInCurrentMonth()
        // Calculate the remaining days in the month
        val remainingDays = daysInMonth - count

        // Calculate the current attendance percentage
        val currentPercent: Long = (count * 100) / daysInMonth
        Log.d("", "Current percent: $currentPercent")

        // Calculate the required attendance to achieve the desired percentage
        val requiredAttendance = (desiredPercent / 100) * daysInMonth - count

        Log.d("", "Curr: $currentPercent :: req: $requiredAttendance :: expected: $expectedPercent")


//        Log.d(
//            "",
//            "workingdays: $daysInMonth\ncount: $count\nabsent: $absent\npercent: $percent\nexpected: $expected"
//        )
        binding.seekbarValue.text = "$desiredPercent %"

// Check if the current attendance percentage is less than the desired percentage
        if (expectedPercent + currentPercent < desiredPercent) {
            // Display the number of days needed to achieve the desired percentage
            Log.d(
                "",
                "You need to be present for ${requiredAttendance.roundToInt()} more days to achieve $desiredPercent% attendance."
            )
            binding.attendancePossibilityTextView.apply {
                text = getString(R.string.not_achievable)
                setTextColor(ResourcesCompat.getColor(resources, R.color.red_desat, null))
            }
        } else {
            // You have already achieved or surpassed the desired percentage
            Log.d(
                "",
                "Congratulations! You have already achieved or surpassed $desiredPercent% attendance."
            )
            binding.attendancePossibilityTextView.apply {
                text = getString(R.string.achievable)
                setTextColor(ResourcesCompat.getColor(resources, R.color.green_desat, null))
            }
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
        val daysInMonth = UtilsKt.getWorkingDaysInCurrentMonth()
        val todayDate = date.toFloat()


        reference.child("Users").child(userID!!).child("Attendance").child(year).child(monthName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n", "DefaultLocale")
                override fun onDataChange(snapshot: DataSnapshot) {
                    count = snapshot.childrenCount

                    val absent = todayDate - count
                    val percent = count / todayDate * 100
                    expectedPercent = (count + (daysInMonth - todayDate)) / daysInMonth * 100

                    Log.d(
                        "",
                        "workingdays: $daysInMonth\ncount: $count\nabsent: $absent\npercent: $percent\nexpected: $expectedPercent"
                    )

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}