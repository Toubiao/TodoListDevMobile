package ch.hearc.todolist.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import ch.hearc.todolist.R
import dagger.android.support.DaggerAppCompatActivity

class SplashActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
//        Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
//        },3000)
    }
}