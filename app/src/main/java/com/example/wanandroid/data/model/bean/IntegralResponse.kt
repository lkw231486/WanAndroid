package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/9 13:35
 *description: 我的页面
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class IntegralResponse(
    var coinCount: Int,//当前积分
    var rank: Int,
    var userId: Int,
    var username: String
) : Parcelable