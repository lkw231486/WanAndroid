package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.app.network.stateCallback.UpdateUiState
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState
import java.text.FieldPosition

/**
 *@ author: lkw
 *created on:2020/7/24 13:46
 *description: 分享文章
 *email:lkw@mantoo.com.cn
 */
class RequestShareArticleViewModel(application: Application) : BaseViewModel(application) {

    var pageNo = 0

    var addData = MutableLiveData<ResultState<Any?>>()

    //分享列表集合数据
    var shareDataState = MutableLiveData<ListDataUiState<AriticleResponse>>()

    //删除分享文章回调数据
    var delDataState = MutableLiveData<UpdateUiState<Int>>()

    fun addArticle(shareTitle: String, shareUrl: String) {
        request(
            { HttpRequestManger.instance.addArticle(shareTitle, shareUrl) },
            addData,
            true,
            "正在分享文章..."
        )
    }

    fun getShareData(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        request({ HttpRequestManger.instance.getShareArticle(pageNo) }, {
            //请求成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.shareArticles.isEmpty(),
                hasMore = it.shareArticles.hasMore(),
                isFirstEmpty = it.shareArticles.isEmpty() && isRefresh,
                listDate = it.shareArticles.datas
            )
            shareDataState.postValue(listDataUiState)
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<AriticleResponse>()
            )
            shareDataState.postValue(listDataUiState)
        })
    }

    fun deletShareData(id: Int, position: Int) {
        request({ HttpRequestManger.instance.delShareArticle(id) }, {
            val updateUiState = UpdateUiState(
                isSuccess = true,
                data = position
            )
            delDataState.postValue(updateUiState)
        }, {
            val updateUiState =
                UpdateUiState(isSuccess = false, data = position, errorMsg = it.errorMsg)
            delDataState.postValue(updateUiState)
        })
    }
}