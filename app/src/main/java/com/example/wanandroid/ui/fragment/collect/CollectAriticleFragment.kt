package com.example.wanandroid.ui.fragment.collect

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.customview.CollectView
import com.example.wanandroid.app.weight.loadCallBack.EmptyCallback
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.data.model.bean.CollectBus
import com.example.wanandroid.data.model.bean.CollectResponse
import com.example.wanandroid.databinding.IncludeListBinding
import com.example.wanandroid.ui.adapter.CollectAdapter
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.kingja.loadsir.core.LoadService
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/9 14:53
 *description: 文章fragment
 *email:lkw@mantoo.com.cn
 */
class CollectAriticleFragment : BaseFragment<RequestCollectViewModel, IncludeListBinding>() {


    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>
    private val articleAdapter: CollectAdapter by lazy { CollectAdapter(arrayListOf()) }
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }
    override fun layoutId(): Int = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击从试出发操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestCollectViewModel.getCollectArticleData(true)
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(activity), articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                requestCollectViewModel.getCollectArticleData(false)
            })
            //初始化FloatingActionBar
            it.initFloatBtn(floatbtn)
        }
        //初始化SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新时间请求
            requestCollectViewModel.getCollectArticleData(true)
        }
        articleAdapter.run {
            setOnCollectViewClickListener(object : CollectAdapter.OnCollectViewClickListener {
                override fun onClick(item: CollectResponse, v: CollectView, position: Int) {
                    if (v.isChecked) {
                        requestCollectViewModel.unCollect(item.originId)
                    } else {
                        requestCollectViewModel.collect(item.originId)
                    }
                }
            })
            setNbOnItemClickListener { _, view, position ->
                nav().navigate(R.id.action_collectFragment_to_webFragment, Bundle().apply {
                    putParcelable("collect", articleAdapter.data[position])
                })
            }
        }

    }

    override fun lazyLoadData() {
        requestCollectViewModel.getCollectArticleData(true)
    }

    override fun createObserver() {
        requestCollectViewModel.ariticleDateState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //成功
                when {
                    it.isFirstEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    //第一页
                    it.isRefresh -> {
                        loadSir.showSuccess()
                        articleAdapter.setNewInstance(it.listDate)
                    }
                    //不是第一页
                    else -> {
                        loadSir.showSuccess()
                        articleAdapter.addData(it.listDate)
                    }
                }
            } else {
                if (it.isRefresh) {
                    //如果是第一页就提示错误页面
                    loadSir.setErrorMsg(it.errorMsg)
                    loadSir.showCallback(ErrorCallback::class.java)
                } else {
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })
        requestCollectViewModel.collectUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                //收藏或取消收藏操作成功，发送全局收藏消息
                eventViewModel.collect.postValue(CollectBus(it.id, it.collect))
            } else {
                showMessage(it.errorMsg)
                for (index in articleAdapter.data.indices) {
                    if (articleAdapter.data[index].originId == it.id) {
                        articleAdapter.notifyItemChanged(index)
                        break
                    }
                }
            }
        })

        eventViewModel.run {
            //监听全局的收藏信息 收藏的Id跟本列表的数据id匹配则 需要删除他 否则则请求最新收藏数据
            collect.observe(viewLifecycleOwner, Observer {
                for (index in articleAdapter.data.indices) {
                    if (articleAdapter.data[index].originId == it.id) {
                        articleAdapter.remove(index)
                        if (articleAdapter.data.size == 0) {
                            loadSir.showCallback(EmptyCallback::class.java)
                        }
                        return@Observer
                    }
                }
                mViewModel.getCollectArticleData(true)
            })
        }
    }

}