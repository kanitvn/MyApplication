package com.example.quiz

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var questionTextView: TextView? = null
    private var optionButton1: Button? = null
    private var optionButton2: Button? = null
    private var optionButton3: Button? = null
    private var optionButton4: Button? = null

    private val questions: MutableList<Question?> = ArrayList<Question?>()

    private var currentIndex = 0
    private var backButton: Button? = null
    private var nextButton: Button? = null

    private var score = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        questionTextView = findViewById<TextView?>(R.id.questionTextView)
        optionButton1 = findViewById<Button?>(R.id.optionButton1)
        optionButton2 = findViewById<Button?>(R.id.optionButton2)
        optionButton3 = findViewById<Button?>(R.id.optionButton3)
        optionButton4 = findViewById<Button?>(R.id.optionButton4)

        backButton = findViewById<Button?>(R.id.backButton)
        nextButton = findViewById<Button?>(R.id.nextButton)


        val db: AppDatabase = AppDatabase.getDatabase(this)

        Executors.newSingleThreadExecutor().execute(Runnable {
            // This line triggers the database creation and the onCreate callback
            db.questionDao().getAll()

            // Optional: Check log to see if it worked
            println("Database created and opened!")
        })



        try {
            val `is` = getAssets().open("questions.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val json = String(buffer, charset("UTF-8"))
            Log.d("debug", json)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        Thread(Runnable {
            var loadedQuestions: MutableList<Question?>? = db.questionDao().getAll()
            while (loadedQuestions == null || loadedQuestions.isEmpty()) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                loadedQuestions = db.questionDao().getAll()
            }

            val finalQuestions: MutableList<Question?>? = loadedQuestions
            runOnUiThread(Runnable {
                questions.clear()
                questions.addAll(finalQuestions)
                showQuestion(questions.get(0))
            })
        }).start()

        val optionListener = View.OnClickListener { view: View? ->
            val b = view as Button
            val answer = b.getText().toString()
            if (answer == questions.get(currentIndex).answer) {
                score++
            }
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion(questions.get(currentIndex))
            } else {
                questionTextView!!.setText("Quiz Finished! Your score: " + score)
                optionButton1!!.setVisibility(View.GONE)
                optionButton2!!.setVisibility(View.GONE)
                optionButton3!!.setVisibility(View.GONE)
                optionButton4!!.setVisibility(View.GONE)
                nextButton!!.setVisibility(View.GONE)
                backButton!!.setVisibility(View.GONE)
            }
        }

        optionButton1!!.setOnClickListener(optionListener)
        optionButton2!!.setOnClickListener(optionListener)
        optionButton3!!.setOnClickListener(optionListener)
        optionButton4!!.setOnClickListener(optionListener)
        nextButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            currentIndex = currentIndex + 1
            showQuestion(questions.get(currentIndex))
        })
        backButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            currentIndex = currentIndex - 1
            showQuestion(questions.get(currentIndex))
        })
    }

    private fun showQuestion(question: Question) {
        val type: Type? = object : com.google.gson.reflect.TypeToken<MutableList<String?>?>() {
        }.getType()
        val options: MutableList<String?> = com.google.gson.Gson().fromJson(question.options, type)
        optionButton1!!.setVisibility(View.VISIBLE)
        optionButton2!!.setVisibility(View.VISIBLE)
        optionButton3!!.setVisibility(View.VISIBLE)
        optionButton4!!.setVisibility(View.VISIBLE)

        questionTextView.setText(question.content)
        optionButton1!!.setText(options.get(0))
        optionButton2!!.setText(options.get(1))
        optionButton3!!.setText(options.get(2))
        optionButton4!!.setText(options.get(3))

        if (currentIndex > 0) {
            backButton!!.setVisibility(View.VISIBLE)
        } else {
            backButton!!.setVisibility(View.GONE)
        }

        if (currentIndex < questions.size - 1) {
            nextButton!!.setVisibility(View.VISIBLE)
        } else {
            nextButton!!.setVisibility(View.GONE)
        }
    }
}