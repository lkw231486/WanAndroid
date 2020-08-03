package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/9 9:18
 *description:分享人信息实体类
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class CoinInfoResponse(var coinCount: Int, var rank:Int, var userId: Int, var username:String):Parcelable