package ru.fefu.activitytracker.Screens

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan

import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import ru.fefu.activitytracker.R


class SignUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        class MyClickableSpan : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(this@SignUpActivity, "test", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#4B09F3")
                ds.isUnderlineText = false
            }
        }

        val backButton : ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, WelcomeActivity::class.java)
            startActivity(intent)
        }
        val text = SpannableString("Нажимая на кнопку, вы соглашаетесь с политикой конфиденциальности и обработки персональных данных, а также принимаете пользовательское соглашение ")
        text.setSpan(MyClickableSpan(), 37, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(MyClickableSpan(), 118, 145, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val confirmText : TextView = findViewById(R.id.textConfirm)
        confirmText.text = text
        confirmText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}