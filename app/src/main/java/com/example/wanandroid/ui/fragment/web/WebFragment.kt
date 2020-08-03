package com.example.wanandroid.ui.fragment.web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.hideSoftKetBoard
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.data.model.bean.*
import com.example.wanandroid.data.model.enums.CollectType
import com.example.wanandroid.databinding.FragmentWebBinding
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.example.wanandroid.viewmodel.state.WebViewModel
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/8 11:36
 *description: webView fragment
 *email:lkw@mantoo.com.cn
 */
class WebFragment : BaseFragment<WebViewModel, FragmentWebBinding>() {

    private var mAgentWeb: AgentWeb? = null

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_web

    override fun initView(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        arguments?.run {
            //文章点进来的
            getParcelable<AriticleResponse>("articleData")?.let {
                mViewModel.articleId = it.id
                mViewModel.showTitle = it.title
                mViewModel.collect = it.collect
                mViewModel.url = it.link
                mViewModel.collectType = CollectType.Article.type
            }
            //轮播图点击进来
            getParcelable<BannerResponse>("bannerData")?.let {
                mViewModel.articleId = it.id
                mViewModel.showTitle = it.title
                //从轮播图过来没法确定是否收没收藏，所以这里默认false
                mViewModel.collect = false
                mViewModel.url = it.url
                mViewModel.collectType = CollectType.Url.type
            }
            //从收藏文章列表进来
            getParcelable<CollectResponse>("collectData")?.let {
                mViewModel.articleId = it.id
                mViewModel.showTitle = it.title
                //收藏列表进来默认都是收藏
                mViewModel.collect = true
                mViewModel.url = it.link
                mViewModel.collectType = CollectType.Article.type
            }
            //点击收藏网址列表进来
            getParcelable<CollectUrlResponse>("collectUrlData")?.let {
                mViewModel.articleId = it.id
                mViewModel.showTitle = it.name
                //从收藏列表过来的，肯定 是 true 了
                mViewModel.collect = true
                mViewModel.url = it.link
                mViewModel.collectType = CollectType.Url.type
            }
        }
        toolbar.run {
            //设置menu关键代码
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            initClose(mViewModel.showTitle) {
                hideSoftKetBoard(activity)
                nav().navigateUp()
            }
        }
    }

    override fun lazyLoadData() {
        //加载网页
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(webcontent, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(mViewModel.url)
        //添加返回逻辑
        val callBack: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mAgentWeb?.let {
                    if (it.webCreator.webView.canGoBack()) {
                        it.webCreator.webView.goBack()
                    } else {
                        nav().navigateUp()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }

    override fun createObserver() {
        requestCollectViewModel.collectUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                mViewModel.collect = it.collect
                eventViewModel.collect.postValue(CollectBus(it.id, it.collect))
                //刷新一下menu
                activity?.window?.invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL)
                activity?.invalidateOptionsMenu()
            } else {
                showMessage(it.errorMsg)
            }
        })
        requestCollectViewModel.collectUrlUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                mViewModel.collect = it.collect
                eventViewModel.collect.postValue(CollectBus(it.id, it.collect))
                //刷新一下menu
                activity?.window?.invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL)
                activity?.invalidateOptionsMenu()
            } else {
                showMessage(it.errorMsg)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.web_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //如果收藏了，右上角的图标相对应改变
        context?.let {
            if (mViewModel.collect) {
                menu.findItem(R.id.web_collect).icon =
                    ContextCompat.getDrawable(it, R.drawable.ic_collected)
            } else {
                menu.findItem(R.id.web_collect).icon =
                    ContextCompat.getDrawable(it, R.drawable.ic_collect)
            }
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.web_share -> {
                //分享
                startActivity(Intent.createChooser(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${mViewModel.showTitle}:${mViewModel.url}")
                    type = "text/plain"
                }, "分享到"))
            }

            R.id.web_refresh -> {
                //刷新网页
                mAgentWeb?.urlLoader?.reload()
            }

            R.id.web_collect -> {
                //点击收藏
                //是否已经登录，没有登录跳转到登录页去
                if (shareViewModel.isLogin.value) {
                    //是否已经收藏
                    if (mViewModel.collect) {
                        if (mViewModel.collectType == CollectType.Url.type) {
                            //取消网址收藏
                            requestCollectViewModel.unCollectUrl(mViewModel.articleId)
                        } else {
                            //取消文章收藏
                            requestCollectViewModel.unCollect(mViewModel.articleId)
                        }
                    } else {
                        if (mViewModel.collectType == CollectType.Url.type) {
                            //取消网址收藏
                            requestCollectViewModel.collectUrl(mViewModel.showTitle, mViewModel.url)
                        } else {
                            //取消文章收藏
                            requestCollectViewModel.collect(mViewModel.articleId)
                        }
                    }
                } else {
                    //跳转到登录页
                    nav().navigate(R.id.action_webFragment_to_loginFragment)
                }
            }

            R.id.web_liulanqi -> {
                //用浏览器打开
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mViewModel.url)))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        (activity as? AppCompatActivity)?.setSupportActionBar(null)
        super.onDestroy()
    }
}