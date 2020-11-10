package ru.korotkov.timefixer

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ru.korotkov.timefixer.db.DeedsDbHelper
import kotlin.math.max

class AddDeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_add_deed)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val name = findViewById<EditText>(R.id.name_input)
        val maxCount = findViewById<EditText>(R.id.max_count_input)
        findViewById<Button>(R.id.add_deed_btn).setOnClickListener {
            when {
                name.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Name is required", LENGTH_SHORT).show()
                }
                maxCount.text.toString().isEmpty() -> {
                    Toast.makeText(this, "Max count is required", LENGTH_SHORT).show()
                }
                maxCount.text.toString().toInt() > 99 -> {
                    Toast.makeText(this, "Max count should be less then 100", LENGTH_SHORT).show()
                }
                else -> {
                    DeedsDbHelper.getInstance(this).addDeed(name.text.toString(), maxCount.text.toString().toInt())
                    onBackPressed()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.END
        window.exitTransition = slide
        window.enterTransition = slide
    }
}