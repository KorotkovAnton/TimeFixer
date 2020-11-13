package ru.korotkov.timefixer.pages.main

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import ru.korotkov.timefixer.pages.addDeed.AddDeedActivity
import ru.korotkov.timefixer.R
import ru.korotkov.timefixer.db.DeedsDbHelper

class MainActivity : AppCompatActivity() {
    private lateinit var deedsViewAdapter: DeedsViewAdapter
    private lateinit var dbHelper: DeedsDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        dbHelper = DeedsDbHelper.getInstance(this)
        deedsViewAdapter = DeedsViewAdapter(this, layoutInflater, dbHelper)
        val deedsListRecyclerView = findViewById<RecyclerView>(R.id.deedsList)
        deedsListRecyclerView.adapter = deedsViewAdapter
        deedsListRecyclerView.layoutManager = LinearLayoutManager(this)
        deedsListRecyclerView.setHasFixedSize(true)
        ItemTouchHelper(callback).attachToRecyclerView(deedsListRecyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_deed_menu_icon -> {
                val intent = Intent(this, AddDeedActivity::class.java)
                startActivityForResult(
                    intent,
                    1,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (data.getBooleanExtra("created", false)) {
            deedsViewAdapter.reloadDeeds()
            deedsViewAdapter.notifyItemRangeInserted(0, 1)
        }
    }

    private var callback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                dbHelper.removeDeed((viewHolder as DeedViewHolder).deedId)
                deedsViewAdapter.reloadDeeds()
                deedsViewAdapter.notifyItemRemoved(viewHolder.layoutPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.red
                        )
                    )
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
}