package ru.fefu.activitytracker.Screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.Screens.Login.LoginActivity
import ru.fefu.activitytracker.Screens.SignUp.SignUpActivity
import ru.fefu.activitytracker.Screens.Tracker.Activity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (App.INSTANCE.sharedPrefs.getString("token", null) !== null) {
            val intent = Intent(this@WelcomeActivity, Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)

        setContentView(R.layout.welcome_layout);

        val signUp : Button = findViewById(R.id.buttonSignUp)
        signUp.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        val  login : Button = findViewById(R.id.buttonLogin)
        login.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}