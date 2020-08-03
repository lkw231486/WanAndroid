package com.example.wanandroid.data.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@ author: lkw
 *created on:2020/7/9 9:16
 *description:分享文章实体bean
 *email:lkw@mantoo.com.cn
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class ShareResponse(
    var coinInfo: CoinInfoResponse,
    var shareArticles: ApiPagerResponse<ArrayList<AriticleResponse>>
):Parcelable