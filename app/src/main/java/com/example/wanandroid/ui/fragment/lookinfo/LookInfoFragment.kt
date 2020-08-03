package com.example.wanandroid.ui.fragment.lookinfo

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
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.CollectBus
import com.example.wanandroid.databinding.FragmentLookinfoBinding
import com.example.wanandroid.ui.adapter.ArticleAdapter
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.example.wanandroid.viewmodel.request.RequestLookInfoViewModel
import com.example.wanandroid.viewmodel.state.LookInfoViewModel
import com.example.wanandroid.viewmodel.state.MainViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.fragment_lookinfo.*
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/8 15:18
 *description:看作者主页
 *email:lkw@mantoo.com.cn
 */
class LookInfoFragment : BaseFragment<LookInfoViewModel, FragmentLookinfoBinding>() {

    //对方的id
    private var shareId = 0

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //适配器
    private val articleAdapter: ArticleAdapter by lazy { ArticleAdapter(arrayListOf(), true) }

    //收藏viewmodel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    //收藏viewmodel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestLookInfoViewModel: RequestLookInfoViewModel by lazy { getViewModel<RequestLookInfoViewModel>() }
    override fun layoutId(): Int = R.layout.fragment_lookinfo

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            shareId = it.getInt("id")
        }
        mDatabind.vm = mViewModel
        shareViewModel.appColor.value.let { share_layout.setBackgroundColor(it) }
        toolbar.initClose("他的信息") {
            nav().navigateUp()
        }
        loadSir = LoadServiceInit(share_linear) {
            loadSir.showCallback(LoadingCallback::class.java)
            //接口请求，拿去作者信息以及文章数据
            requestLookInfoViewModel.getLookInfo(shareId, true)
        }
        recyclerView.init(LinearLayoutManager(context), articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                //接口请求，拿去作者信息以及文章数据
                requestLookInfoViewModel.getLookInfo(shareId, false)
            })
            it.initFloatBtn(floatbtn)
        }
        swipeRefresh.init {
            //接口请求，拿去作者信息以及文章数据
            requestLookInfoViewModel.getLookInfo(shareId, true)
        }
        articleAdapter.run {
            setOnCollectViewClickListener(object : ArticleAdapter.OnCollectViewClickListener {
                override fun onClick(item: AriticleResponse, v: CollectView, position: Int) {
                    if (shareViewModel.isLogin.value) {
                        if (v.isChecked) {
                            requestCollectViewModel.unCollect(item.id)
                        } else {
                            requestCollectViewModel.collect(item.id)
                        }
                    } else {
                        v.isChecked = true
                        nav().navigate(R.id.action_lookInfoFragment_to_loginFragment)
                    }
                }
            })
            setNbOnItemClickListener { adapter, view, position ->
                nav().navigate(R.id.action_lookInfoFragment_to_webFragment, Bundle().apply {
                    putParcelable(
                        "articleData",
                        articleAdapter.data[position - recyclerView.headerCount]
                    )
                })
            }
        }
    }

    override fun lazyLoadData() {
        //接口请求，拿去作者信息以及文章数据
        requestLookInfoViewModel.getLookInfo(shareId, true)
    }

    override fun createObserver() {
        requestLookInfoViewModel.shareResponse.observe(viewLifecycleOwner, Observer {
            mViewModel.name.postValue(it.coinInfo.username)
            mViewModel.info.postValue("积分 : ${it.coinInfo.coinCount} 排名 : ${it.coinInfo.rank}")
        })
        requestLookInfoViewModel.shareListDataUiState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    //是第一页
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
                //失败
                if (it.isRefresh) {
                    //如果是第一页，则显示错误界面，并提示错误信息
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
                    if (articleAdapter.data[index].id == it.id) {
                        articleAdapter.data[index].collect = it.collect
                        articleAdapter.notifyItemChanged(index)
                        break
                    }
                }
            }
        })
        shareViewModel.run {
            //监听账户信息是否改变 有值时(登录)将相关的数据设置为已收藏，为空时(退出登录)，将已收藏的数据变为未收藏
            userInfo.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    it.collectIds.forEach { id ->
                        for (item in articleAdapter.data) {
                            if (id.toInt() == item.id) {
                                item.collect = true
                                break
                            }
                        }
                    }
                } else {
                    //没有登录去掉item收藏的小红心
                    for (item in articleAdapter.data) {
                        item.collect = false
                    }
                }
                //刷新列表
                articleAdapter.notifyDataSetChanged()
            })
            //监听全局的收藏信息 收藏的Id跟本列表的数据id匹配则需要更新
            eventViewModel.collect.observe(viewLifecycleOwner, Observer {
                for (index in articleAdapter.data.indices) {
                    if (articleAdapter.data[index].id == it.id) {
                        articleAdapter.data[index].collect = it.collect
                        articleAdapter.notifyItemChanged(index)
                        break
                    }
                }
            })
        }
    }
}