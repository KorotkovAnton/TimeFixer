package ru.korotkov.timefixer.db

const val DB_NAME = "deeds"
const val DB_VERSION = 1

const val COLUMN_ID = "id"

const val TABLE_DEEDS = "DEEDS"
const val COLUMN_NAME = "name"
const val COLUMN_MAX_COUNT = "maxCount"

const val TABLE_DATES = "DATES"
const val COLUMN_DATE = "_date"
const val COLUMN_DEED_ID = "deedId"

const val CREATE_DEEDS_TABLE =
    "CREATE TABLE $TABLE_DEEDS(" +
            "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "$COLUMN_NAME TEXT," +
            "$COLUMN_MAX_COUNT INTEGER" +
            ")"

const val CREATE_DATES_TABLE =
    "CREATE TABLE $TABLE_DATES(" +
            "$COLUMN_ID INTEGER PRIMARY KEY," +
            "$COLUMN_DATE INTEGER," +
            "$COLUMN_DEED_ID INTEGER," +
            "FOREIGN KEY ($COLUMN_DEED_ID) REFERENCES DEEDS($COLUMN_ID)" +
            ")"