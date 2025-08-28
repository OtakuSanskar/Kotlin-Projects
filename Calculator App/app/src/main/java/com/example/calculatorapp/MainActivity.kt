package com.example.calculatorapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding

class MainActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var previousCalculationTextView: TextView

    private var firstNumber = 0.0
    private var operation = ""
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        resultTextView = findViewById(R.id.resultTextView)
        previousCalculationTextView = findViewById(R.id.previousCalculationTextView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn0 = findViewById<Button>(R.id.btn0)
        val btn1 = findViewById<Button>(R.id.btn1)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn3 = findViewById<Button>(R.id.btn3)
        val btn4 = findViewById<Button>(R.id.btn4)
        val btn5 = findViewById<Button>(R.id.btn5)
        val btn6 = findViewById<Button>(R.id.btn6)
        val btn7 = findViewById<Button>(R.id.btn7)
        val btn8 = findViewById<Button>(R.id.btn8)
        val btn9 = findViewById<Button>(R.id.btn9)

        val add:Button = findViewById(R.id.btnPlus)
        val sub:Button = findViewById(R.id.btnMinus)
        val mul:Button = findViewById(R.id.btnMultiply)
        val div:Button = findViewById(R.id.btnDivide)

        val equal = findViewById<Button>(R.id.btnEquals)
        val clear = findViewById<Button>(R.id.btnClear)
        val backspace = findViewById<Button>(R.id.btnBackspace)
        val percent = findViewById<Button>(R.id.btnPercent)
        val dot = findViewById<Button>(R.id.btnDot)

        btn0.setOnClickListener {appendNumber("0")}
        btn1.setOnClickListener {appendNumber("1")}
        btn2.setOnClickListener {appendNumber("2")}
        btn3.setOnClickListener {appendNumber("3")}
        btn4.setOnClickListener {appendNumber("4")}
        btn5.setOnClickListener {appendNumber("5")}
        btn6.setOnClickListener {appendNumber("6")}
        btn7.setOnClickListener {appendNumber("7")}
        btn8.setOnClickListener {appendNumber("8")}
        btn9.setOnClickListener {appendNumber("9")}
        dot.setOnClickListener {appendNumber(".")}

        percent.setOnClickListener {setOperation("%")}
        add.setOnClickListener {setOperation("+")}
        sub.setOnClickListener {setOperation("-")}
        mul.setOnClickListener {setOperation("*")}
        div.setOnClickListener {setOperation("/")}

        equal.setOnClickListener {calculateResult()}
        clear.setOnClickListener {clearCalculator()}
        backspace.setOnClickListener {delNum()}

    }
    private fun appendNumber(number: String){
        if(isNewOperation){
            resultTextView.text = number
            isNewOperation = false
        }else{
            resultTextView.text = "${resultTextView.text}$number"
        }
    }

    private fun setOperation(op: String){
        firstNumber = resultTextView.text.toString().toDouble()
        operation = op
        isNewOperation = true
        previousCalculationTextView.text = "$firstNumber $operation"
        resultTextView.text = "0"
    }

    private fun calculateResult(){
        val secondNumber = resultTextView.text.toString().toDouble()
        try{
            val result: Double = when(operation) {
                "+" -> firstNumber + secondNumber
                "-" -> firstNumber - secondNumber
                "*" -> firstNumber * secondNumber
                "/" -> firstNumber / secondNumber
                "%" -> firstNumber % secondNumber
                else -> secondNumber
            }
            previousCalculationTextView.text = "$firstNumber $operation $secondNumber ="
            resultTextView.text = result.toString()
            isNewOperation = true
        }catch (e: Exception){
            resultTextView.text = "Error"
        }
    }

    private fun clearCalculator(){
        resultTextView.text = "0"
        previousCalculationTextView.text = ""
        firstNumber = 0.0
        operation = ""
        isNewOperation = true
    }

    private fun delNum(){
        if(resultTextView.text.isNotEmpty() && resultTextView.text != "0.0" && resultTextView.text != "ERROR"){
            resultTextView.text = resultTextView.text.dropLast(1)
        } else{
            val toast = Toast(this)
            val view:TextView = TextView(this).apply{
                text = "No more numbers to delete"
                setTextColor(Color.CYAN)
                setBackgroundColor(Color.BLACK)
                setPadding(32, 16, 32, 16)
            }
            toast.view = view
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }
    }
}