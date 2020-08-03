package com.example.wanandroid.app.utils

import android.annotation.SuppressLint
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.FormatStyle
import java.util.*

/**
 *@ author: lkw
 *created on:2020/7/20 16:52
 *description: 时间工具类
 *email:lkw@mantoo.com.cn
 */
object DatetimeUtil {

    val DATE_PATTERN = "yyyy-MM-dd"
    val DATE_PATTERN_SS = "yyyy-MM-dd HH:mm:ss"
    val DATE_PATTERN_MM = "yyyy-MM-dd HH:mm"

    /**
     * 获取现在时刻
     */
    val now: Date
        get() = Date(Date().time)

    /**
     *获取现在时刻
     */
    val nows: Date
        get() = formatDate(DATE_PATTERN, now)

    /**
     * Date to String
     */
    @SuppressLint("SimpleDateFormat")
    fun formatDate(date: Date?, formatStyle: String): String {
        return if (date != null) {
            val sdf = SimpleDateFormat(formatStyle)
            sdf.format(date)
        } else {
            ""
        }
    }

    /**
     *Date to String
     */
    @SuppressLint("SimpleDateFormat")
    fun formatDate(date: Long, formatStyle: String): String {
        val sdf = SimpleDateFormat(formatStyle)
        return sdf.format(Date(date))
    }

    fun formatDate(formatStyle: String, formatStr: String): Date {
        val format = SimpleDateFormat(formatStyle, Locale.CHINA)
        return try {
            val date = Date()
            date.time = format.parse(formatStr).time
            date
        } catch (e: Exception) {
            println(e.message)
            nows
        }
    }

    /**
     *Date to Date
     */
    @SuppressLint("SimpleDateFormat")
    fun formatDate(formatStyle: String, date: Date?): Date {
        if (date != null) {
            val sdf = SimpleDateFormat(formatStyle)
            val formatDate = sdf.format(date)
            try {
                return sdf.parse(formatDate)
            } catch (e: Exception) {
                e.printStackTrace()
                return Date()
            }
        } else {
            return Date()
        }
    }

    /**
     *将时间戳转换成时间
     */
    fun stampToDate(s: String): Date {
        val lt = s.toLong()
        return Date(lt)
    }

    /**
     *获取指定的时间与日期
     */
    fun getCustomTime(dataStr: String): Date {
        return formatDate(DATE_PATTERN, dataStr)
    }
}