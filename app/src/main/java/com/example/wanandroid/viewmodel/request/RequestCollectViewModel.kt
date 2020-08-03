package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.network.stateCallback.CollectUiState
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.data.model.bean.CollectResponse
import com.example.wanandroid.data.model.bean.CollectUrlResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

/**
 *@ author: lkw
 *created on:2020/7/6 16:25
 *description: 专门为了收藏而写的Viewmodel
 *email:lkw@mantoo.com.cn
 */
class RequestCollectViewModel(application: Application) : BaseViewModel(application) {

    private var pageNo = 0

    //收藏的文章
    val collectUiState: MutableLiveData<CollectUiState> = MutableLiveData()

    //收藏网址
    val collectUrlUiState: MutableLiveData<CollectUiState> = MutableLiveData()

    //收藏的文章数据
    var ariticleDateState: MutableLiveData<ListDataUiState<CollectResponse>> = MutableLiveData()

    //收藏的网址数据
    var urlDateState: MutableLiveData<ListDataUiState<CollectUrlResponse>> = MutableLiveData()

    /**
     *收藏文章
     *提醒一下，玩安卓的收藏 和取消收藏 成功后返回的data值为null，所以在CollectRepository中的返回值一定要加上 非空？
     * 不然会出错
     */
    fun collect(id: Int) {
        request({
            HttpRequestManger.instance.collect(id)
        }, {
            val uiState = CollectUiState(isSuccess = true, collect = true, id = id)
            collectUiState.postValue(uiState)
        }, {
            val uiState = CollectUiState(isSuccess = false, collect = false, id = id)
            collectUiState.postValue(uiState)
        })
    }

    /**
     * 取消收藏文章
     * 提醒一下，玩安卓的收藏 和取消收藏 成功后返回的data值为null，所以在CollectRepository中的返回值一定要加上 非空？
     * 不然会出错
     */
    fun unCollect(id: Int) {
        request({
            HttpRequestManger.instance.unCollect(id)
        }, {
            val uiState = CollectUiState(isSuccess = true, collect = false, id = id)
            collectUiState.postValue(uiState)
        }, {
            val uiState = CollectUiState(isSuccess = false, collect = true, id = id)
            collectUiState.postValue(uiState)
        })
    }

    /**
     * 收藏网址
     */
    fun collectUrl(name: String, link: String) {
        request({ HttpRequestManger.instance.collectUrl(name, link) }, {
            val uiState = CollectUiState(isSuccess = true, collect = true, id = it.id)
            collectUrlUiState.postValue(uiState)
        }, {
            val uiState =
                CollectUiState(isSuccess = false, collect = false, errorMsg = it.errorMsg, id = 0)
            collectUrlUiState.postValue(uiState)
        })
    }

    /**
     * 取消收藏网址
     */
    fun unCollectUrl(id: Int) {
        request({ HttpRequestManger.instance.unCollectUrl(id) }, {
            val uiState = CollectUiState(isSuccess = true, collect = false, id =id)
            collectUrlUiState.postValue(uiState)
        }, {
            val uiState =
                CollectUiState(isSuccess = false, collect = true, errorMsg = it.errorMsg, id = id)
            collectUrlUiState.postValue(uiState)
        })
    }

    /**
     * 获取用户收藏的的文章
     */
    fun getCollectArticleData(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        request({ HttpRequestManger.instance.collectArticleData(pageNo) }, {
            //请求成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                hasMore = it.hasMore(),
                isFirstEmpty = it.isEmpty() && it.isRefresh(),
                listDate = it.datas
            )
            ariticleDateState.postValue(listDataUiState)
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errorMsg = it.errorMsg,
                    listDate = arrayListOf<CollectResponse>()
                )
            ariticleDateState.postValue(listDataUiState)
        })
    }

    /**
     * 获取收藏url数据
     */
    fun getCollectUrlData() {
        request({ HttpRequestManger.instance.collectUrlData() }, {
            //请求成功
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = true,
                hasMore = false,
                isEmpty = it.isEmpty(),
                listDate = it
            )
            urlDateState.postValue(listDataUiState)
        }, {
            val listDataUiState=ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg,
                listDate = ArrayList<CollectUrlResponse>()//创建空对象
            )
            urlDateState.postValue(listDataUiState)
        })
    }
}