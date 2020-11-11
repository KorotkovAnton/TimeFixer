package ru.korotkov.timefixer

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import ru.korotkov.timefixer.db.DeedsDbHelper

class AddDeedActivity : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var maxCount: EditText
    private lateinit var chooseIconButton: Button
    private var menu: Menu? = null
    private var correctName: Boolean = false
    private var correctMaxCount: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation(window)
        setContentView(R.layout.activity_add_deed)

        initToolbar()
        initInputs()
        initIconChooserButton()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun initInputs() {
        name = findViewById(R.id.name_input)
        maxCount = findViewById(R.id.max_count_input)
        changeTintColor(name, false)
        changeTintColor(maxCount, false)

        name.addTextChangedListener(afterTextChanged = {
            correctName = it.toString().isNotEmpty()
            changeTintColor(name, correctName)
            enableOrDisableCreateBtn()
        })

        maxCount.addTextChangedListener(afterTextChanged = {
            correctMaxCount = (it.toString().isNotEmpty() && it.toString().toInt() < 100)
            changeTintColor(maxCount, correctMaxCount)
            enableOrDisableCreateBtn()
        })
    }

    private fun enableOrDisableCreateBtn() {
        menu?.findItem(R.id.create_deed_menu_item)?.isEnabled = correctMaxCount && correctName
    }

    private fun changeTintColor(textEdit: EditText, correct: Boolean) = when (correct) {
        true -> textEdit.background.setTint(Color.BLACK)
        false -> textEdit.background.setTint(Color.RED)
    }

    private fun initIconChooserButton() {
        chooseIconButton = findViewById(R.id.choose_icon_btn)
        chooseIconButton.setOnClickListener {
            window.exitTransition = Slide(Gravity.START)
            window.enterTransition = Slide(Gravity.END)
            val intent = Intent(this, IconChooserActivity::class.java)
            startActivityForResult(
                intent,
                2,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.add_deed, menu)
        menu?.findItem(R.id.create_deed_menu_item)?.isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_deed_menu_item -> {
                DeedsDbHelper.getInstance(this)
                    .addDeed(name.text.toString(), maxCount.text.toString().toInt())
                val intent = Intent()
                intent.putExtra("created", true)
                setResult(RESULT_OK, intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}