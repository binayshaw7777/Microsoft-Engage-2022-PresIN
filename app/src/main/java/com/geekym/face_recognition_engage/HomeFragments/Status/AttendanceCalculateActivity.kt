package com.geekym.face_recognition_engage.HomeFragments.Status

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

@RequiresApi(Build.VERSION_CODES.O)

class AttendanceCalculateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceCalculateBinding
    private lateinit var reference: DatabaseReference
    private var count: Long = 0
    private val selectedDates = HashSet<Long>() // Use a HashSet to store selected dates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reference = FirebaseDatabase.getInstance().getReference();

        setView()
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
                    val count: Float = count.toFloat()
                    val absent = todayDate - count
                    val percent = count / todayDate * 100
                    val expected: Float = (count + (daysInMonth - todayDate)) / daysInMonth * 100

                    Log.d(
                        "",
                        "workingdays: $daysInMonth\ncount: $count\nabsent: $absent\npercent: $percent\nexpected: $expected"
                    )

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}