package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *@ author: lkw
 *created on:2020/7/7 15:09
 *description: 分页数据的基类
 *email:lkw@mantoo.com.cn
 */
data class ApiPagerResponse<T>(
    var datas: T,
    var curPage: Int,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) : Serializable {
    /**
     * 数据是否为空
     */
    fun isEmpty(): Boolean {
        return (datas as List<*>).size == 0
    }

    /**
     *是否为刷新
     */
    fun isRefresh(): Boolean {
        return offset == 0
    }

    /**
     * 是否还有更多数据
     */
    fun hasMore(): Boolean {
        return !over
    }
}