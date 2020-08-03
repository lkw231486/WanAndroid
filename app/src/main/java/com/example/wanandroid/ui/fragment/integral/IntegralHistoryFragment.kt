package com.example.wanandroid.ui.fragment.integral

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.loadCallBack.EmptyCallback
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.databinding.FragmentListBinding
import com.example.wanandroid.ui.adapter.IntegralHistoryAdapter
import com.example.wanandroid.viewmodel.request.RequestIntegralViewModel
import com.example.wanandroid.viewmodel.state.IntegralViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getAppViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/20 16:30
 *description: 积分获取历史记录
 *email:lkw@mantoo.com.cn
 */
class IntegralHistoryFragment : BaseFragment<IntegralViewModel, FragmentListBinding>() {

    //适配器
    private val integralHistoryAdapter: IntegralHistoryAdapter by lazy {
        IntegralHistoryAdapter(arrayListOf())
    }

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //请求的ViewModel /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestIntegralViewModel: RequestIntegralViewModel by lazy { getAppViewModel<RequestIntegralViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_list

    override fun initView(savedInstanceState: Bundle?) {
        toolbar.initClose("积分记录") {
            nav().navigateUp()
        }
        //状态页配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击从试
            loadSir.showCallback(LoadingCallback::class.java)
            requestIntegralViewModel.getIntegralHistoryData(true)
        }
        //初始化recycleview
        recyclerView.init(LinearLayoutManager(context), integralHistoryAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                //触发时加载更多数据
                requestIntegralViewModel.getIntegralHistoryData(false)
            })
            //初始化FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化swipeRefreshLayout
        swipeRefresh.init {
            //触发刷新时请求数据
            requestIntegralViewModel.getIntegralHistoryData(false)
        }
    }

    override fun lazyLoadData() {
        requestIntegralViewModel.getIntegralHistoryData(true)
    }

    override fun createObserver() {
        requestIntegralViewModel.integralHistoryDataUiState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //请求成功
                when {
                    it.isFirstEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    //是第一页
                    it.isRefresh -> {
                        loadSir.showSuccess()
                        integralHistoryAdapter.setNewInstance(it.listDate)
                    }
                    //不是第一页
                    else -> {
                        loadSir.showSuccess()
                        integralHistoryAdapter.addData(it.listDate)
                    }
                }
            } else {
                //请求失败
                if (it.isRefresh) {
                    //如果是第一页则显示错误页面，并且提示错误信息
                    loadSir.setErrorText(it.errorMsg)
                    loadSir.showCallback(ErrorCallback::class.java)
                } else {
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })
    }

}