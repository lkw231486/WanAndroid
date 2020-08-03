package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/6 17:19
 *description:
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BannerResponse(var desc: String = "",
                          var id: Int = 0,
                          var imagePath: String = "",
                          var isVisible: Int = 0,
                          var order: Int = 0,
                          var title: String = "",
                          var type: Int = 0,
                          var url: String = ""):Parcelable