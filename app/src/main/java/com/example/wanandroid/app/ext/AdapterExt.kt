package com.example.wanandroid.app.ext

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 *@ author: lkw
 *created on:2020/7/8 11:21
 *description: 给adapter拓展的，防止重复点击item
 *email:lkw@mantoo.com.cn
 */
var adapterLastClickTime = 0L

fun BaseQuickAdapter<*, *>.setNbOnItemClickListener(
    interval: Long = 1000,
    action: (adapter: BaseQuickAdapter<*, *>, view: View, position: Int) -> Unit
) {
    setOnItemClickListener { adapter, view, position ->
        val currentTime = System.currentTimeMillis()
        if (adapterLastClickTime != 0L && (currentTime - adapterLastClickTime < interval)) {
            return@setOnItemClickListener
        }
        adapterLastClickTime = currentTime
        action(adapter, view, position)
    }
}


/**
 * 给adapter拓展的，防止重复点击item
 */
var adapterChildLastClickTime = 0L

fun BaseQuickAdapter<*, *>.setNbOnItemChildClickListener(
    interval: Long = 1000,
    action: (adapter: BaseQuickAdapter<*, *>, view: View, position: Int) -> Unit
) {
    setOnItemChildClickListener { adapter, view, position ->
        val currentTime = System.currentTimeMillis()
        if (adapterLastClickTime != 0L && (currentTime - adapterLastClickTime < interval)) {
            return@setOnItemChildClickListener
        }
        adapterLastClickTime = currentTime
        action(adapter, view, position)
    }
}
