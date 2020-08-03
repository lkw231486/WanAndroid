package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.ShareResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

/**
 *@ author: lkw
 *created on:2020/7/8 17:28
 *description: 作者详情页面viewModel
 *email:lkw@mantoo.com.cn
 */
class RequestLookInfoViewModel(application: Application) : BaseViewModel(application) {

    var pageNo = 1
    var shareListDataUiState = MutableLiveData<ListDataUiState<AriticleResponse>>()
    var shareResponse = MutableLiveData<ShareResponse>()

    fun getLookInfo(id: Int, isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        request({ HttpRequestManger.instance.getLookInfoById(id, pageNo) }, {
            //请求成功
            pageNo++
            shareResponse.postValue(it)
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = it.shareArticles.isRefresh(),
                isEmpty = it.shareArticles.isEmpty(),
                hasMore = it.shareArticles.hasMore(),
                isFirstEmpty = isRefresh && it.shareArticles.isEmpty(),
                listDate = it.shareArticles.datas
            )
            shareListDataUiState.postValue(listDataUiState)
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg,
                isRefresh = isRefresh,
                listDate = arrayListOf<AriticleResponse>()
            )
            shareListDataUiState.postValue(listDataUiState)
        })
    }

}