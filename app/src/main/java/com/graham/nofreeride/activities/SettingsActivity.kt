package com.graham.nofreeride.activities

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.graham.nofreeride.R
import com.graham.nofreeride.fragments.preferences.PreferencesFragment

/**
 * Created by grahamherceg on 2/3/18.
 */

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar : Toolbar = findViewById(R.id.tb_settings)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.settings_frag_container, PreferencesFragment()).commit()

    }
}
