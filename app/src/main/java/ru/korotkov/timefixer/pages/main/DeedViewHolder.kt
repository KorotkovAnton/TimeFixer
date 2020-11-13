package ru.korotkov.timefixer.pages.main

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.korotkov.timefixer.R
import ru.korotkov.timefixer.dto.Deed
import ru.korotkov.timefixer.customViews.SeekArc
import java.text.SimpleDateFormat
import java.util.*

class DeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var deedId: Int = -1
    private val name: TextView = itemView.findViewById(R.id.name_title)
    private val lastDate: TextView = itemView.findViewById(R.id.lastDate)
    private val progressBar: SeekArc = itemView.findViewById(R.id.deed_progress)
    private val progressText: TextView = itemView.findViewById(R.id.progress_text)
    private val addDate: ImageButton = itemView.findViewById(R.id.addDate)

    fun setDeed(deed: Deed, onAddClickListener: View.OnClickListener) {
        this.deedId = deed.id
        this.name.text = deed.name
        if (deed.dates.isNotEmpty()) {
            lastDate.text =
                "Last deed: " + SimpleDateFormat("HH:mm - dd.MM.yy", Locale.getDefault()).format(
                    deed.dates.maxOrNull()
                )
        } else {
            this.lastDate.text = "There is no last date"
        }
        progressText.text = "${deed.dates.size} / ${deed.maxCount}"

        progressBar.progressColor = calcColor(deed)
        progressBar.max = deed.maxCount
        progressBar.progress = deed.dates.size
        addDate.setOnClickListener(onAddClickListener)
    }

    private fun calcColor(deed: Deed): Int {
        return when {
            deed.dates.size > deed.maxCount -> Color.BLACK
            else -> ArgbEvaluator().evaluate(
                (deed.dates.size.toFloat() / deed.maxCount.toFloat()),
                Color.GREEN,
                Color.RED
            ) as Int
        }
    }
}
