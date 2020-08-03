package com.example.wanandroid.ui.fragment.share

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
import com.example.wanandroid.ui.adapter.ShareAdapter
import com.example.wanandroid.viewmodel.request.RequestShareArticleViewModel
import com.example.wanandroid.viewmodel.state.AriticleViewModel
import com.kingja.loadsir.core.LoadService
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/24 13:15
 *description: 我的文章页面
 *email:lkw@mantoo.com.cn
 */
class ArticleFragment : BaseFragment<AriticleViewModel, FragmentListBinding>() {
    //适配器
    private val shareAdapter: ShareAdapter by lazy { ShareAdapter(arrayListOf()) }

    //页面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //记得要写泛型，虽然在 by lazy中 提示不用写，但是你不写就会报错
    private val requestViewModel: RequestShareArticleViewModel by lazy { getViewModel<RequestShareArticleViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_list

    override fun initView(savedInstanceState: Bundle?) {
        toolbar.run {
            initClose("我分享的文章") {
                nav().navigateUp()
            }
            inflateMenu(R.menu.todo_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.todo_add -> {
                        nav().navigate(R.id.action_articleFragment_to_addArticleFragment)
                    }
                }
                true
            }
        }
        //状态页设置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击重试触发操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestViewModel.getShareData(true)
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(context), shareAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFooter(SwipeRecyclerView.LoadMoreListener {
                //触发加载更多操作
                requestViewModel.getShareData(false)
            })
            //初始化FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化swipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestViewModel.getShareData(true)
        }

        shareAdapter.run {
            setNbOnItemClickListener { adapter, view, position ->
                nav().navigate(R.id.action_articleFragment_to_webFragment, Bundle().apply {
                    putParcelable("articleData", shareAdapter.data[position])
                })
            }
            addChildClickViewIds(R.id.item_share_del)
            setNbOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_share_del -> {
                        showMessage("确认删除改文章吗？", positiveButtonText = "删除", postiveAction = {
                            requestViewModel.deletShareData(
                                shareAdapter.data[position].id,
                                position
                            )
                        }, negativeButtonText = "取消")
                    }
                }
            }
        }
    }

    override fun lazyLoadData() {
        requestViewModel.getShareData(true)
    }

    override fun createObserver() {
        requestViewModel.shareDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并且没数据，显示空布局
                    it.isFirstEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    //是第一页
                    it.isRefresh -> {
                        loadSir.showSuccess()
                        shareAdapter.setNewInstance(it.listDate)
                    }
                    //不是第一页
                    else -> {
                        loadSir.showSuccess()
                        shareAdapter.addData(it.listDate)
                    }
                }
            } else {
                //失败
                //是第一页
                if (it.isFirstEmpty) {
                    //是第一页
                    loadSir.setErrorText(it.errorMsg)
                    loadSir.showCallback(ErrorCallback::class.java)
                } else {
                    //不是第一页
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })
        requestViewModel.delDataState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                //删除数据成功，如果删除是最后一个，那摩直接把布局显示为空
                if (shareAdapter.data.size == 0) {
                    loadSir.showCallback(EmptyCallback::class.java)
                }
                shareAdapter.remove(it.data)
            } else {
                //删除失败
                showMessage(it.errorMsg)
            }
        })
        eventViewModel.shareArticle.observe(viewLifecycleOwner, Observer {
            //添加刷新动画，避免页面产生尴尬
            if (shareAdapter.data.size == 0) {
                loadSir.showCallback(LoadingCallback::class.java)
            } else {
                swipeRefresh.isRefreshing = true
            }
            requestViewModel.getShareData(true)
        })
    }

}