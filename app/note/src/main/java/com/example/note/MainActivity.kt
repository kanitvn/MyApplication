package com.example.note

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    var calendarView: CalendarView? = null
    var noteEditText: EditText? = null
    var saveButton: Button? = null
    var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        calendarView = findViewById<CalendarView?>(R.id.calendarView)
        noteEditText = findViewById<EditText?>(R.id.noteEditText)
        saveButton = findViewById<Button?>(R.id.saveButton)
        val pref = getPreferences(MODE_PRIVATE)
        val savedYear = pref.getInt("year", 0)
        if (savedYear != 0) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, savedYear)
            cal.set(Calendar.MONTH, pref.getInt("month", 0))
            cal.set(Calendar.DAY_OF_MONTH, pref.getInt("dayOfMonth", 0))
            calendarView!!.setDate(cal.getTimeInMillis())
        }

        calendarView!!.setOnDateChangeListener(OnDateChangeListener { calendarView: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            fileName = String.format("%02d_%02d_%04d", dayOfMonth, month + 1, year)
            noteEditText!!.setText("")
            try {
                val fis = openFileInput(fileName)
                val isr = InputStreamReader(fis)
                val bufferedReader = BufferedReader(isr)
                val sb = StringBuilder()
                var line: String?
                while ((bufferedReader.readLine().also { line = it }) != null) {
                    sb.append(line).append("\n")
                }
                fis.close()
                noteEditText!!.setText(sb)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })


        saveButton!!.setOnClickListener(View.OnClickListener { view: View? ->
            if (fileName == null) {
                val cal = Calendar.getInstance()

                cal.setTime(Date(calendarView!!.getDate()))

                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)

                fileName = String.format("%02d_%02d_%04d", dayOfMonth, month + 1, year)
            }
            val noteContent = noteEditText!!.getText().toString()
            try {
                // Ghi nội dung vào file
                val fos = openFileOutput(fileName, MODE_PRIVATE)
                fos.write(noteContent.toByteArray())
                fos.close()
                Toast.makeText(this, "Đã lưu ghi chú", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Lỗi khi lưu ghi chú", Toast.LENGTH_LONG).show()
            }
        })
    }
}