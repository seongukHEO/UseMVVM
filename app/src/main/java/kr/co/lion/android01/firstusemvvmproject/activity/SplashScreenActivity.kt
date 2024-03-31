package kr.co.lion.android01.firstusemvvmproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.lion.android01.firstusemvvmproject.MainActivity
import kr.co.lion.android01.firstusemvvmproject.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        showSplashScreen()
    }


    private fun showSplashScreen(){
        lifecycleScope.launch {
            delay(1300)
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        }
    }
}