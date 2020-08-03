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
 *created on:2020/7/21 16:37
 *description: 项目页面请求
 *email:lkw@mantoo.com.cn
 */
class RequestProjectViewModel(application: Application) : BaseViewModel(application) {
    //页码
    var pageNo = 1

    //标题数据源
    var titleData: MutableLiveData<ResultState<ArrayList<ClassifyResponse>>> = MutableLiveData()

    var projectDateState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    /**
     * 获取项目标题数据源
     */
    fun getProjectTitleData() {
        request({ HttpRequestManger.instance.getProjectTitle() }, titleData)
    }

    /**
     * 获取项目数据
     */
    fun getProjectData(isRefresh: Boolean, cid: Int, isNew: Boolean = false) {
        if (isRefresh) {
            pageNo = if (isNew) 0 else 1
        }
        request({ HttpRequestManger.instance.getProjectData(pageNo, cid, isNew) }, {
            //请求成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                hasMore = it.hasMore(),
                isFirstEmpty = it.isRefresh() && it.isEmpty(),
                listDate = it.datas
            )
            projectDateState.postValue(listDataUiState)
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<AriticleResponse>()
            )
            projectDateState.postValue(listDataUiState)
        })
    }
}