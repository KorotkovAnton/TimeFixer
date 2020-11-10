package ru.korotkov.timefixer.dto

class Deed(
    val id: Int,
    val name: String,
    val maxCount: Int,
    val dates: MutableList<Long> = mutableListOf()
) {
    fun addDate(date: Long) {
        this.dates.add(date)
    }
}