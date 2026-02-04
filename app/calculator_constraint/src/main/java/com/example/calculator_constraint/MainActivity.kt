package com.example.calculator_constraint

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var editTextText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextText = findViewById<TextView?>(R.id.textViewResult)
        setButtonClickListeners()
    }

    private fun setButtonClickListeners() {
        val buttonIds = intArrayOf(
            R.id.button0,
            R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8,
            R.id.button9, R.id.buttonDot, R.id.buttonAC, R.id.buttonAdd,
            R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide,
            R.id.buttonPercent, R.id.buttonParentheses, R.id.buttonEqual,
            R.id.buttonBack
        )
        for (buttonId in buttonIds) {
            val button = findViewById<Button?>(buttonId)
            button.setOnClickListener(View.OnClickListener { view: View? -> onButtonClick(view) })
        }
    }

    private fun onButtonClick(view: View?) {
        val button = view as Button
        val buttonText = button.getText().toString()
        when (buttonText) {
            "=" -> calculateResult()
            "()" -> handleParentheses()
            "⌫" -> removeLastInput()
            "AC" -> clearInput()
            else -> appendInput(buttonText)
        }
    }

    private fun appendInput(input: String?) {
        editTextText!!.setText(editTextText!!.getText().toString() + input)
    }

    private fun removeLastInput() {
        val s = editTextText!!.getText().toString()
        if (s.length > 0) {
            editTextText!!.setText(s.substring(0, s.length - 1))
        }
    }

    private fun clearInput() {
        editTextText!!.setText("")
    }

    private var isOpenParentheses = false
    private fun handleParentheses() {
        if (isOpenParentheses) {
            appendInput(")")
            isOpenParentheses = false
        } else {
            appendInput("(")
            isOpenParentheses = true
        }
    }

    private fun calculateResult() {
        try {
            val expression = editTextText!!.getText().toString()
            val expressionEval: org.mariuszgromada.math.mxparser.Expression =
                org.mariuszgromada.math.mxparser.Expression(expression)
            val result: Double = expressionEval.calculate()
            editTextText!!.setText(result.toString())
        } catch (e: Exception) {
            editTextText!!.setText("Error")
        }
    }
}