package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import android.provider.ContactsContract
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/2 14:58
 *description:
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class UserInfo(
    var admin: Boolean = false,
    var chapterTops: List<String> = listOf(),
    var collectIds: MutableList<String> = mutableListOf(),
    var email: String = "",
    var icon: String = "",
    var id: String = "",
    var nickname: String = "",
    var password: String = "",
    var token: String = "",
    var type: Int = 0,
    var username: String = ""
) : Parcelable