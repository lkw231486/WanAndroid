package com.example.wanandroid.app.network.stateCallback

import java.util.*
import kotlin.collections.ArrayList

/**
 *@ author: lkw
 *created on:2020/7/6 16:37
 *description: 列表数据状态类
 *email:lkw@mantoo.com.cn
 */
data class ListDataUiState<T>(
    //是否请求成功
    val isSuccess: Boolean,
    //错误消息 isSuccess为false才会有
    val errorMsg: String = "",
    //是否刷新
    val isRefresh: Boolean = false,
    //是否为空
    val isEmpty: Boolean = false,
    //是否还有更多
    val hasMore: Boolean = false,
    //是第一页且没有数据
    val isFirstEmpty: Boolean = false,
    //列表数据
    val listDate: ArrayList<T> = arrayListOf()
)