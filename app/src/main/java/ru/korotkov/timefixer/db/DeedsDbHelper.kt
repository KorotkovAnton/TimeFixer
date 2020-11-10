package ru.korotkov.timefixer.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.korotkov.timefixer.dto.Deed
import ru.korotkov.timefixer.utils.SingletonHolder

class DeedsDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object : SingletonHolder<DeedsDbHelper, Context>(::DeedsDbHelper)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_DEEDS_TABLE)
        db.execSQL(CREATE_DATES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getAllDeeds(): List<Deed> {
        val deedsList = getDeedsList()
        val datesList = getDatesList()
        mergeDeedsAndDates(deedsList, datesList)
        return deedsList.sortedByDescending { it.id }
    }

    private fun getDeedsList(): List<Deed> {
        val deedsList = mutableListOf<Deed>()
        val cursor = readableDatabase.query(
            TABLE_DEEDS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_MAX_COUNT),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val maxCount = cursor.getInt(cursor.getColumnIndex(COLUMN_MAX_COUNT))
                deedsList.add(Deed(id, name, maxCount))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return deedsList
    }

    // return list of pair<deedId, date>
    private fun getDatesList(): List<Pair<Int, Long>> {
        val datesList = mutableListOf<Pair<Int, Long>>()
        val cursor = readableDatabase.query(
            TABLE_DATES,
            arrayOf(COLUMN_DATE, COLUMN_DEED_ID),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val deedId = cursor.getInt(cursor.getColumnIndex(COLUMN_DEED_ID))
                val date = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))
                datesList.add(Pair(deedId, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return datesList
    }

    private fun mergeDeedsAndDates(deedsList: List<Deed>, datesList: List<Pair<Int, Long>>) {
        datesList.forEach { pair ->
            val deedId = pair.first
            val date = pair.second
            val deed = deedsList.find { deed -> deed.id == deedId }
                ?: error("There is no deed for this date")
            deed.addDate(date)
        }
    }

    fun addDeed(name: String, maxCount: Int) {
        writableDatabase.insert(
            TABLE_DEEDS,
            null,
            ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_MAX_COUNT, maxCount)
            }
        )
    }

    fun addDeedDate(deedId: Int) {
        val date = System.currentTimeMillis()
        writableDatabase.insert(
            TABLE_DATES,
            null,
            ContentValues().apply {
                put(COLUMN_DATE, date)
                put(COLUMN_DEED_ID, deedId)
            }
        )
    }

    fun removeDeed(deedId: Int) {
        writableDatabase.delete(
            TABLE_DEEDS,
            "$COLUMN_ID = ?",
            arrayOf(deedId.toString())
        )
        writableDatabase.delete(
            TABLE_DATES,
            "$COLUMN_DEED_ID = ?",
            arrayOf(deedId.toString())
        )
    }
}