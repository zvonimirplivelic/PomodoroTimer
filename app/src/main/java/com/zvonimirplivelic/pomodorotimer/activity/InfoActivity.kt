package com.zvonimirplivelic.pomodorotimer.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zvonimirplivelic.pomodorotimer.R
import com.zvonimirplivelic.pomodorotimer.util.Constants

class InfoActivity : AppCompatActivity() {

    private lateinit var btnWikiInfo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        setSupportActionBar(findViewById(R.id.toolbar_info))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Information"
        }

        btnWikiInfo = findViewById(R.id.btn_open_wiki_info)
        btnWikiInfo.setOnClickListener {
            val infoURL = Constants.INFO_URL
            val infoIntent = Intent(Intent.ACTION_VIEW)
            infoIntent.data = Uri.parse(infoURL)
            startActivity(infoIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}