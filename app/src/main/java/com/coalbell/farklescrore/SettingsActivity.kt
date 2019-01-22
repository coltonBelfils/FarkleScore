package com.coalbell.farklescrore

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_settings.*

import kotlinx.android.synthetic.main.content_settings.*

class SettingsActivity : AppCompatActivity() {

    var minStartScore: Int = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        minStartScore = getSharedPreferences(MainActivity.SHARED_PREFERENCES_SETTINGS_KEY, Context.MODE_PRIVATE).getInt(MainActivity.MIN_BOARD_SCORE_KEY, 500)
        minStartScoreInput.hint = minStartScore.toString()

        minStartScoreInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isDigitsOnly(s)) {
                    minStartScore = minStartScoreInput.text.toString().toInt()
                }
                getSharedPreferences(MainActivity.SHARED_PREFERENCES_SETTINGS_KEY, Context.MODE_PRIVATE).edit().apply {
                    putInt(MainActivity.MIN_BOARD_SCORE_KEY, minStartScore)
                }.apply()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

}
