package ru.fefu.activitytracker.Screens.SignUp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan

import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Enums.GenderEnum
import ru.fefu.activitytracker.Retrofit.Result
import ru.fefu.activitytracker.Retrofit.response.TokenUserModel
import ru.fefu.activitytracker.Screens.Tracker.Activity
import ru.fefu.activitytracker.Screens.WelcomeActivity


class SignUpActivity : AppCompatActivity() {
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        setContentView(R.layout.signup_layout)
        val items = mutableListOf<String>()
        enumValues<GenderEnum>().forEach { items.add(it.type) }
        val adapter = ArrayAdapter(this, R.layout.gender_item, items)
        val menuText = findViewById<AutoCompleteTextView>(R.id.autoComplete)
        menuText.setAdapter(adapter)

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

        viewModel.dataFlow
            .onEach {
                if (it is Result.Success<TokenUserModel>) {
                    App.INSTANCE.sharedPrefs.edit().putString("token", it.result.token).apply()
                    val intent = Intent(this@SignUpActivity, Activity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                else if (it is Result.Error<TokenUserModel>) {
                    Toast.makeText(this, it.e.toString(), Toast.LENGTH_LONG).show()
                }
            }
            .launchIn(lifecycleScope)

        val registerBtn = findViewById<com.google.android.material.button.MaterialButton>(R.id.buttonContinue)
        registerBtn.setOnClickListener {
            val login = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.login)
                .editText?.text.toString()
            val password = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.password)
                .editText?.text.toString()
            val name = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.nickname)
                .editText?.text.toString()
            var gender = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.gender)
                .editText?.text.toString()
            var genderOrdinal = 0
            for (i in enumValues<GenderEnum>()) {
                if (i.type == gender) {
                    genderOrdinal = i.ordinal
                }
            }
            viewModel.register(login, password, name, genderOrdinal)
        }

        val text = SpannableString("Нажимая на кнопку, вы соглашаетесь с политикой конфиденциальности и обработки персональных данных, а также принимаете пользовательское соглашение ")
        text.setSpan(MyClickableSpan(), 37, 66, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(MyClickableSpan(), 118, 145, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val confirmText : TextView = findViewById(R.id.textConfirm)
        confirmText.text = text
        confirmText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}