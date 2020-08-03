package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/27 16:45
 *description:
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class NavigationResponse(var articles: ArrayList<AriticleResponse>,
                              var cid: Int,
                              var name: String):Parcelable