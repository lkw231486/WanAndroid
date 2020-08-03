package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/9 17:20
 *description:项目
 *email:lkw@mantoo.com.cn
 */


@SuppressLint("ParcelCreator")
@Parcelize
data class ClassifyResponse(var children: List<String> = listOf(),
                            var courseId: Int = 0,
                            var id: Int = 0,
                            var name: String = "",
                            var order: Int = 0,
                            var parentChapterId: Int = 0,
                            var userControlSetTop: Boolean = false,
                            var visible: Int = 0):Parcelable
//data class ClassifyResponse(
//    var children: List<String> = listOf(),
//    var courseId: Int = 0,
//    var id: Int = 0,
//    var name: String = "",
//    var order: Int = 0,
//    var parentChapterId: Int = 0,
//    var userControlSetTop: Boolean = false,
//    var visible: Int = 0
//) : Parcelable