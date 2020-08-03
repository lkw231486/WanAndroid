package com.example.wanandroid.app.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.ContactsContract
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.Utils
import com.example.wanandroid.R
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.tencent.mmkv.MMKV

/**
 *@ author: lkw
 *created on:2020/7/2 15:42
 *description: 设置缓存工具类
 *email:lkw@mantoo.com.cn
 */
object SettingUtil {

    /**
     * 获取当前主题的颜色
     */
    fun getColor(context: Context): Int {
        val setting = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val color = setting.getInt("color", defaultColor)
        return if (color != 0 && Color.alpha(color) != 255) {
            defaultColor
        } else {
            color
        }
    }

    /**
     * 设置app主题颜色
     */
    fun setColor(context: Context, color: Int) {
        val setting = PreferenceManager.getDefaultSharedPreferences(context)
        setting.edit().putInt("color", color).apply()//忘记写apply导致主题颜色保存失败
    }

    /**
     * 设置列表动画
     */
    fun setListMode(mode: Int) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("mode", mode)
    }

    /**
     * 获取列表动画
     */
    fun getListMode(): Int {
        val kv = MMKV.mmkvWithID("app")
        //0.关闭动画，1.渐显，2.缩放，3.从上到下，4.从左到右，5.从右到左
        return kv.getInt("mode", 2)
    }


    fun getColorStateList(context: Context): ColorStateList {
        val colors = intArrayOf(getColor(context), ContextCompat.getColor(context, R.color.colorGray))
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_checked, android.R.attr.state_checked)
        states[1] = intArrayOf()
        return ColorStateList(states, colors)
    }

    fun getColorStateList(color: Int): ColorStateList {
        val colors = intArrayOf(color, ContextCompat.getColor(Utils.getApp(), R.color.colorGray))
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_checked, android.R.attr.state_checked)
        states[1] = intArrayOf()
        return ColorStateList(states, colors)
    }


    fun getOneColorStateList(context: Context): ColorStateList {
        val colors = intArrayOf(getColor(context))
        val states = arrayOfNulls<IntArray>(1)
        states[0] = intArrayOf()
        return ColorStateList(states, colors)
    }

    fun getOneColorStateList(color: Int): ColorStateList {
        val colors = intArrayOf(color)
        val states = arrayOfNulls<IntArray>(1)
        states[0] = intArrayOf()
        return ColorStateList(states, colors)
    }

    /**
     * 设置loading颜色和加载布局
     */
    fun setLoadingColor(color: Int, loadSir: LoadService<Any>) {
        loadSir.setCallBack(LoadingCallback::class.java) { _, view ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.findViewById<ProgressBar>(R.id.loading_progress).indeterminateTintMode =
                    PorterDuff.Mode.SRC_ATOP
                view.findViewById<ProgressBar>(R.id.loading_progress).indeterminateTintList =
                    getOneColorStateList(color)
            }
        }
    }

    /**
     * 设置shap文件的颜色
     *
     * @param view
     * @param color
     */
    fun setShapColor(view: View, color: Int) {
        val drawable = view.background as GradientDrawable
        drawable.setColor(color)
    }

    /**
     * 设置shap的渐变颜色
     */
    fun setShapColor(view: View, color: IntArray, orientation: GradientDrawable.Orientation) {
        val drawable = view.background as GradientDrawable
        drawable.orientation = orientation
        drawable.colors = color
    }
}