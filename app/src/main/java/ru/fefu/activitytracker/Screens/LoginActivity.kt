package ru.fefu.activitytracker.Screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import ru.fefu.activitytracker.R

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        val backButton : ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }
}