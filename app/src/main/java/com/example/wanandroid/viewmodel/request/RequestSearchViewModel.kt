package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.data.model.bean.ApiPagerResponse
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.SearchResponse
import com.example.wanandroid.data.repository.local.LocalDataManger
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 *@ author: lkw
 *created on:2020/7/21 11:36
 *description: 搜索请求
 *email:lkw@mantoo.com.cn
 */
class RequestSearchViewModel(application: Application) : BaseViewModel(application) {
    var pageNo = 0

    //搜索热词数据
    var hotData: MutableLiveData<ResultState<ArrayList<SearchResponse>>> = MutableLiveData()

    //搜索结果数据
    val searchResultData: MutableLiveData<ResultState<ApiPagerResponse<ArrayList<AriticleResponse>>>> =
        MutableLiveData()

    //搜索热词数据
    var historyData: MutableLiveData<ArrayList<String>> = MutableLiveData()

    /**
     * 获得热词数据
     */
    fun getHotData() {
        request({ HttpRequestManger.instance.getHotData() }, hotData)
    }

    /**
     * 获取历史搜索数据
     */
    fun getHistoryData() {
        historyData.postValue(LocalDataManger.instance.getHistoryData())
    }

    /**
     * 根据热词搜索结果
     */
    fun getSearchResultData(searchKey: String, isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        request(
            { HttpRequestManger.instance.getSearchResultData(pageNo, searchKey) },
            searchResultData
        )
    }
}