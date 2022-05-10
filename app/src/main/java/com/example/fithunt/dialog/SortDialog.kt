package com.example.fithunt.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.bumptech.glide.util.Util
import com.example.fithunt.R
import com.example.fithunt.Util.utilvalues
import com.example.fithunt.Util.utilvalues.Companion.listforadapter


class Dialogsort(context: Context) : Dialog(context) {

    init {
        setCancelable(true)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialogsort)
        val sharedPreferences=context.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window?.attributes?.windowAnimations=R.style.DialogAnimation
        editor.putBoolean("bool",false)
        val donebutton=findViewById<Button>(R.id.donesort)
        val cancelbutton=findViewById<Button>(R.id.cancelsort)
        val radiogroup1=findViewById<RadioGroup>(R.id.radioGroup)

        when(sharedPreferences.getInt("typeofsort",0)){
            1 -> findViewById<RadioButton>(R.id.avgspeed).isChecked=true
            2 -> findViewById<RadioButton>(R.id.datesort).isChecked=true
            3 -> findViewById<RadioButton>(R.id.distancesort).isChecked=true
            4 -> findViewById<RadioButton>(R.id.durationsort).isChecked=true
            5 -> findViewById<RadioButton>(R.id.calorieburntsort).isChecked=true
            else -> findViewById<RadioButton>(R.id.datesort).isChecked=true
        }

        donebutton.setOnClickListener {

            when(radiogroup1.checkedRadioButtonId)
                {
                    R.id.avgspeed -> {
                        utilvalues.listforadapter.value=1
                        editor.putInt("typeofsort",1)}
                    R.id.datesort ->{
                        listforadapter.value=2
                        editor.putInt("typeofsort",2)}
                    R.id.distancesort ->{
                        listforadapter.value=3
                        editor.putInt("typeofsort",3)}
                    R.id.durationsort ->{
                        listforadapter.value=4
                        editor.putInt("typeofsort",4)}
                    R.id.calorieburntsort ->{
                        listforadapter.value=5
                        editor.putInt("typeofsort",5)}
                }


            editor.putInt("listforadapter", listforadapter.value!!)
            editor.apply()

            dismiss()
        }

        cancelbutton.setOnClickListener {
            dismiss()
        }

    }



}