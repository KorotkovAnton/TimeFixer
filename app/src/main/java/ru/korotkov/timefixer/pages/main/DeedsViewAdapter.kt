package ru.korotkov.timefixer.pages.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.korotkov.timefixer.R
import ru.korotkov.timefixer.db.DeedsDbHelper
import ru.korotkov.timefixer.dto.Deed
import java.lang.ref.WeakReference

class DeedsViewAdapter(
    private val context: Context,
    layoutInflater: LayoutInflater,
    private val dbHelper: DeedsDbHelper,
) : RecyclerView.Adapter<DeedViewHolder>() {
    private val mInflater: WeakReference<LayoutInflater> = WeakReference(layoutInflater)
    private var allDeeds: List<Deed> = emptyList()

    init {
        reloadDeeds()
    }

    fun reloadDeeds() {
        this.allDeeds = dbHelper.getAllDeeds()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeedViewHolder {
        val inflater = mInflater.get()
        return if (inflater != null) {
            DeedViewHolder(inflater.inflate(R.layout.deed_view, parent, false))
        } else {
            throw RuntimeException("Oooops, looks like activity is dead")
        }
    }

    override fun onBindViewHolder(holder: DeedViewHolder, position: Int) {
        val deed = allDeeds[position]
        holder.setDeed(deed) {
            DeedsDbHelper.getInstance(context).addDeedDate(deed.id)
            allDeeds = dbHelper.getAllDeeds()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = allDeeds.size
}
