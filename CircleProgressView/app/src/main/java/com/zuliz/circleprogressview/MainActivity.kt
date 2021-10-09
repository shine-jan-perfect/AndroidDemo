package com.zuliz.circleprogressview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.zuliz.lib_circle_progress_view.CircleProgressView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        val circleProgressView = findViewById<CircleProgressView>(R.id.circle_progress_view)

        findViewById<Button>(R.id.btn_random_max_progress).setOnClickListener {
            val maxProgress = Random.nextInt(10000, 20000).toFloat()
            circleProgressView.setMaxProgress(maxProgress)
            var progress = Float.MAX_VALUE
            while (progress > maxProgress) {
                progress = Random.nextInt(0, 20000).toFloat()
            }

            "progress = $progress, maxProgress = $maxProgress".also {
                findViewById<TextView>(R.id.tv_random_max_progress).text = it
            }

            circleProgressView.setProgress(progress, true)
        }
    }
}