package com.example.wanandroid.ui.fragment.stepcounter.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 *@ author: lkw
 *created on:2020/7/28 9:17
 *description: 创建步数存数数据库
 *email:lkw@mantoo.com.cn
 */
class DBOpenHelper(mContext: Context) : SQLiteOpenHelper(mContext, "StepCounter.dp", null, 1) {

    private val DB_NAME = "StepCounter.dp"//数据库名称
    private val DB_VERSION = 1 //数据库版本，大于零

    //用于创建Banner表
    private val CREATE_BANNER =
        ("create table step(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "curDate TEXT," + "totalSteps TEXT)")

    override fun onCreate(db: SQLiteDatabase) {
      db.execSQL(CREATE_BANNER)//执行可更改的SQL语句
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}