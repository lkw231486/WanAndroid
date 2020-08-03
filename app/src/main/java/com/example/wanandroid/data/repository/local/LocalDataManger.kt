package com.example.wanandroid.data.repository.local

import com.example.wanandroid.app.utils.CacheUtil

/**
 *@ author: lkw
 *created on:2020/7/21 13:27
 *description: 从本地获取的数据，可以从数据库，Sp等等中获取
 *email:lkw@mantoo.com.cn
 */
class LocalDataManger {
    //单例
    companion object {
        val instance: LocalDataManger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LocalDataManger()
        }
    }

    fun getHistoryData():ArrayList<String>{
      return CacheUtil.getSearchHistoryData()
    }
}