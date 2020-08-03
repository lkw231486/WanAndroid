package com.example.wanandroid.ui.fragment.stepcounter.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.wanandroid.ui.fragment.stepcounter.model.StepEntity

/**
 *@ author: lkw
 *created on:2020/7/28 9:37
 *description: 对步数数据库进行增删改查操作
 *email:lkw@mantoo.com.cn
 */
class StepDataDao(mContext: Context) {
    private var stepHelper: DBOpenHelper = DBOpenHelper(mContext)
    private var stepDb: SQLiteDatabase? = null

    /**
     * 添加一条新纪录
     */
    fun addNewData(stepEntity: StepEntity) {
        stepDb = stepHelper.readableDatabase
        val values = ContentValues()
        values.put("curDate", stepEntity.curDate)
        values.put("totalSteps", stepEntity.steps)
        stepDb!!.insert("step", null, values)
        stepDb!!.close()
    }

    /**
     * 根据日期查询记录
     */
    fun getCurDataByDate(curDate: String): StepEntity? {
        stepDb = stepHelper.readableDatabase
        var stepEntity: StepEntity? = null
        val cursor = stepDb!!.query("step", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow("curDate"))
            if (curDate == date) {
                val steps = cursor.getString(cursor.getColumnIndexOrThrow("totalSteps"))
                stepEntity = StepEntity(date, steps)
                //跳出循环
                break
            }
        }
        //关闭
        stepDb!!.close()
        cursor.close()
        return stepEntity
    }

    /**
     * 查询所有的步数记录
     */
    fun getAllDatas(): List<StepEntity> {
        val dataList: MutableList<StepEntity> = ArrayList()
        stepDb = stepHelper.readableDatabase
        val cursor = stepDb!!.rawQuery("select*from step", null)
        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow("curDate"))
            val steps = cursor.getString(cursor.getColumnIndexOrThrow("totalSteps"))
            val stepEntity = StepEntity(date, steps)
            dataList.add(stepEntity)
        }
        stepDb!!.close()
        cursor.close()
        return dataList
    }

    /**
     *更新数据
     */
    fun updateCurData(stepEntity: StepEntity) {
        stepDb = stepHelper.readableDatabase
        val values = ContentValues()
        values.put("curDate", stepEntity.curDate)
        values.put("totalSteps", stepEntity.steps)
        stepDb!!.update("step", values, "curDate=?", arrayOf(stepEntity.curDate))
        stepDb!!.close()
    }

    /**
     * 根据时间删除步数
     */
    fun deleteCurData(curDate: String) {
        stepDb = stepHelper.readableDatabase
        if (stepDb!!.isOpen) {
            stepDb!!.delete("step", "curDate", arrayOf(curDate))
            stepDb!!.close()
        }
    }

}