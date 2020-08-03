package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/6 9:35
 *description: 首页文章实体类
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
class AriticleResponse(
    var apklink:String,
var author: String,//作者
var chapterId: Int,
var chapterName: String,
var collect: Boolean,//是否收藏
var courseId: Int,
var desc: String,
var envelopePic: String,
var fresh: Boolean,
var id: Int,
var link: String,
var niceDate: String,
var origin: String,
var prefix: String,
var projectLink: String,
var publishTime: Long,
var superChapterId: Int,
var superChapterName: String,
var shareUser: String,
var tags: List<TagsResponse>,
var title: String,
var type: Int,
var userId: Int,
var visible: Int,
var zan: Int) : Parcelable