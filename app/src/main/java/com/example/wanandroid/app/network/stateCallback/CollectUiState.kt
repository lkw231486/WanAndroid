package com.example.wanandroid.app.network.stateCallback

/**
 *@ author: lkw
 *created on:2020/7/6 16:28
 *description: 收藏数据状态类
 *email:lkw@mantoo.com.cn
 */
data class CollectUiState(
    //请求是否成功
    var isSuccess: Boolean = true,
    //收藏
    var collect: Boolean = false,
    //收藏id
    var id: Int = -1,
    //请求失败的错误信息
    var errorMsg: String = ""
)