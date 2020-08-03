package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/27 16:42
 *description: 体系数据
 *email:lkw@mantoo.com.cn
 */

@SuppressLint("ParcelCreator")
@Parcelize

data class SystemResponse(var children: ArrayList<ClassifyResponse>,
                          var courseId: Int,
                          var id: Int,
                          var name: String,
                          var order: Int,
                          var parentChapterId: Int,
                          var userControlSetTop: Boolean,
                          var visible: Int):Parcelable
//data class SystemResponse(
//    var children: ArrayList<ClassifyResponse>,
//    var courseId: Int,
//    var id: Int,
//    var name: String,
//    var order: Int,
//    var parentChapterId: Int,
//    var userControlSetTop: Boolean,
//    var visible: Int
//) : Parcelable