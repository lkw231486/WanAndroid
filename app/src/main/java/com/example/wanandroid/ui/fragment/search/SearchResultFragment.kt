package com.example.wanandroid.ui.fragment.search

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
import com.example.wanandroid.databinding.FragmentListBinding
import com.example.wanandroid.ui.adapter.ArticleAdapter
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.example.wanandroid.viewmodel.request.RequestSearchViewModel
import com.example.wanandroid.viewmodel.state.SearchViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_list.view.*
import kotlinx.android.synthetic.main.include_list.view.floatbtn
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

/**
 *@ author: lkw
 *created on:2020/7/21 15:10
 *description: 搜索数据页面
 *email:lkw@mantoo.com.cn
 */
class SearchResultFragment : BaseFragment<SearchViewModel, FragmentListBinding>() {
    private var searchKey = ""

    //适配器
    private val articleAdapter: ArticleAdapter by lazy { ArticleAdapter(arrayListOf(), true) }

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //收藏viewModel 注意，lazy使用getViewModel一定要使用泛型，虽然他不提示报错，但是你不写不行
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestSearchViewModel: RequestSearchViewModel by lazy { getViewModel<RequestSearchViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_list

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let { arguments -> arguments.getString("searchKey")?.let { searchKey = it } }
        toolbar.initClose(searchKey) {
            nav().navigateUp()
        }
        //状态页配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击从试操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestSearchViewModel.getSearchResultData(searchKey, true)
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(context), articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                //触发请求更多数据操作
                requestSearchViewModel.getSearchResultData(searchKey, false)
            })
            //初始化FloatingBtn
            it.initFloatBtn(floatbtn)
        }
        //初始化swipRefreshLayout
        swipeRefresh.init {
            //触发监听事件请求数据
            requestSearchViewModel.getSearchResultData(searchKey, true)
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
                        nav().navigate(R.id.action_searchFragment_to_loginFragment)
                    }
                }
            })
            setNbOnItemClickListener { adapter, view, position ->
                nav().navigate(R.id.action_searchFragment_to_webFragment, Bundle().apply {
                    putParcelable("articleData", articleAdapter.data[position])
                })
            }
            addChildClickViewIds(R.id.item_home_author, R.id.item_project_author)
            setNbOnItemClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_home_author, R.id.item_project_author -> {
                        nav().navigate(
                            R.id.action_searchFragment_to_lookInfoFragment,
                            Bundle().apply {
                                putInt("id", articleAdapter.data[position].userId)
                            })
                    }
                }
            }
        }
    }

    override fun lazyLoadData() {
        requestSearchViewModel.getSearchResultData(searchKey, true)
    }

    override fun createObserver() {
        requestSearchViewModel.searchResultData.observe(
            viewLifecycleOwner,
            Observer { resultState ->
                parseState(resultState, {
                    swipeRefresh.isRefreshing = false
                    //请求成功，页码+1
                    requestSearchViewModel.pageNo++
                    if (it.isRefresh() && it.datas.size == 0) {
                        //如果是第一页，没有数据，显示空数据页面
                        loadSir.showCallback(EmptyCallback::class.java)
                    } else if (it.isRefresh()) {
                        //如果是刷新 ，有数据
                        loadSir.showSuccess()
                        articleAdapter.setNewInstance(it.datas)
                    } else {
                        //不是第一页
                        loadSir.showSuccess()
                        articleAdapter.addData(it.datas)
                    }
                    recyclerView.loadMoreFinish(it.isEmpty(), it.hasMore())
                }, {
                    //这里代表请求失败
                    swipeRefresh.isRefreshing = false
                    if (articleAdapter.data.size == 0) {
                        //如果适配器数据没有值，则显示错误界面，并提示错误信息
                        loadSir.showCallback(ErrorCallback::class.java)
                        loadSir.setErrorText(it.errorMsg)
                    } else {
                        recyclerView.loadMoreError(0, it.errorMsg)
                    }
                })
            })

        requestCollectViewModel.collectUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                //成功
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
                        for (item in articleAdapter.data)
                            if (id.toInt() == item.id) {
                                item.collect = true
                                break
                            }
                    }
                } else {
                    for (item in articleAdapter.data) {
                        item.collect = false
                    }
                }
                articleAdapter.notifyDataSetChanged()
            })
            //监听全局的收藏信息 收藏的Id跟本列表的数据id匹配则需要更新
            eventViewModel.collect.observe(viewLifecycleOwner, Observer {
                 for (index in articleAdapter.data.indices){
                      if (articleAdapter.data[index].id==it.id){
                          articleAdapter.data[index].collect=it.collect
                          articleAdapter.notifyItemChanged(index)
                          break
                      }
                 }
            })
        }
    }
}