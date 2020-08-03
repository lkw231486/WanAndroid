package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/21 11:13
 *description: 热搜索实体类
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class SearchResponse(
    var id: Int,
    var link: String,
    var name: String,
    var order: Int,
    var visible: Int
) : Parcelable