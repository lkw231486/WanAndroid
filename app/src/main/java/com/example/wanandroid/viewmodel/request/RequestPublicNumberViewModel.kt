package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.ClassifyResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 *@ author: lkw
 *created on: 2020/8/3 11:27
 *description:
 *email:lkw@mantoo.com.cn
 */
class RequestPublicNumberViewModel(application: Application) : BaseViewModel(application) {
    var pageNo = 1

    var titleData: MutableLiveData<ResultState<ArrayList<ClassifyResponse>>> = MutableLiveData()

    var publicDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    /**
     * 获取公众号导航标题
     */
    fun getPublicTitleData() {
        request({ HttpRequestManger.instance.getPublicTitle() }, titleData)
    }

    /**
     * 获取公众号数据源
     */
    fun getPublicData(isRefresh: Boolean, cid: Int) {
        if (isRefresh) {
            pageNo = 0
        }
        request({ HttpRequestManger.instance.getPublicData(pageNo, cid) }, {
            //成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isFirstEmpty = it.isRefresh() && it.isEmpty(),
                hasMore = it.hasMore(),
                listDate = it.datas
            )
            publicDataState.postValue(listDataUiState)
        }, {
            //失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<AriticleResponse>()
            )
            publicDataState.postValue(listDataUiState)
        })
    }
}