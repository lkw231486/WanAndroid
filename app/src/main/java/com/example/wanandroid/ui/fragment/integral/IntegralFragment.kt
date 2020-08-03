package com.example.wanandroid.ui.fragment.integral

import android.media.audiofx.DynamicsProcessing
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
import com.example.wanandroid.data.model.bean.BannerResponse
import com.example.wanandroid.data.model.bean.IntegralResponse
import com.example.wanandroid.databinding.FragmentIntegralBinding
import com.example.wanandroid.ui.adapter.IntegralAdapter
import com.example.wanandroid.viewmodel.request.RequestIntegralViewModel
import com.example.wanandroid.viewmodel.state.IntegralViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.fragment_integral.*
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getAppViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.util.notNull
import me.hgj.jetpackmvvm.ext.view.gone
import me.hgj.jetpackmvvm.network.NetworkUtil.url

/**
 *@ author: lkw
 *created on:2020/7/20 11:12
 *description: 积分排行页面
 *email:lkw@mantoo.com.cn
 */
class IntegralFragment : BaseFragment<IntegralViewModel, FragmentIntegralBinding>() {
    private var rank: IntegralResponse? = null

    //适配器
    private lateinit var integralAdapter: IntegralAdapter

    //页面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //请求的ViewModel /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestIntegralViewModel: RequestIntegralViewModel by lazy { getAppViewModel<RequestIntegralViewModel>() }
    override fun layoutId(): Int = R.layout.fragment_integral
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        rank = arguments?.getParcelable("rank")
        rank.notNull({
            mViewModel.rank.set(rank)
        }, {
            integral_cardview.gone()
        })
        integralAdapter = IntegralAdapter(arrayListOf(), rank?.rank ?: -1)
        toolbar.run {
            inflateMenu(R.menu.integral_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.integral_guize -> {
                        nav().navigate(R.id.action_integralFragment_to_webFragment, Bundle().apply {
                            putParcelable(
                                "bannerData", BannerResponse(
                                    title = "积分规则",
                                    url = "https://www.wanandroid.com/blog/show/2653"
                                )
                            )
                        })
                    }
                    R.id.integral_history -> {
                        nav().navigate(R.id.action_integralFragment_to_integralHistoryFragment)
                    }
                }
                true
            }
            initClose("积分排行") {
                nav().navigateUp()
            }
        }

        //状态也配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击从试触发操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestIntegralViewModel.getIntegralData(true)
        }
        //初始化recyclerView
        recyclerView.init(LinearLayoutManager(context), integralAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                //触发加载更多时请求数据
                requestIntegralViewModel.getIntegralData(false)
            })
            //初始化FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化SwipeRefreshLayout
        swipeRefresh.init {
            //触发监听时请求数据
            requestIntegralViewModel.getIntegralData(true)
        }
        shareViewModel.appColor.value.let {
            setUiTheme(it, integral_remark, integral_name, integral_meCount)
        }
    }

    override fun lazyLoadData() {
        requestIntegralViewModel.getIntegralData(true)
    }

    override fun createObserver() {
        requestIntegralViewModel.integralDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //成功
                when {
                    //第一页没有数据
                    it.isFirstEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    //是第一页
                    it.isRefresh -> {
                        loadSir.showSuccess()
                        integralAdapter.setNewInstance(it.listDate)
                    }
                    //不是第一页
                    else -> {
                        loadSir.showSuccess()
                        integralAdapter.addData(it.listDate)
                    }
                }
            } else {
                //失败
                if (it.isRefresh) {
                    loadSir.setErrorMsg(it.errorMsg)
                    loadSir.showCallback(ErrorCallback::class.java)
                } else {
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })
    }

}