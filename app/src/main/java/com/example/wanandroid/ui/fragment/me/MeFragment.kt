package com.example.wanandroid.ui.fragment.me

import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.data.model.bean.BannerResponse
import com.example.wanandroid.data.model.bean.IntegralResponse
import com.example.wanandroid.databinding.FragmentMeBinding
import com.example.wanandroid.viewmodel.request.RequestMeViewModel
import com.example.wanandroid.viewmodel.state.MeViewModel
import com.zhpan.idea.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_me.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.view.notNull

/**
 *@ author: lkw
 *created on:2020/7/9 10:47
 *description: 我的页面
 *email:lkw@mantoo.com.cn
 */
class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>() {

    private var rank: IntegralResponse? = null

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestMeViewModel: RequestMeViewModel by lazy { getViewModel<RequestMeViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_me

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewModel = mViewModel
        mDatabind.click = ProxyClick()
        shareViewModel.appColor.value.let { setUiTheme(it, me_linear, me_integral) }
        shareViewModel.userInfo.value?.let { mViewModel.name.postValue(if (it.nickname.isEmpty()) it.username else it.nickname) }
        //初始化刷新事件
        me_swipe.init {
            requestMeViewModel.getUserInfo()
        }
    }

    /**
     * 懒加载
     */
    override fun lazyLoadData() {
        shareViewModel.userInfo.value?.run {
            me_swipe.isRefreshing = true
            requestMeViewModel.getUserInfo()
        }
    }

    override fun createObserver() {
        requestMeViewModel.meData.observe(viewLifecycleOwner, Observer { resultState ->
            me_swipe.isRefreshing = false
            parseState(resultState, {
                rank = it
                mViewModel.info.postValue("id:${it.userId} 排名:${it.rank}")
                mViewModel.integral.postValue(it.coinCount)
            }, {
                showMessage(it.errorMsg)
            })
        })

        shareViewModel.run {
            appColor.observe(viewLifecycleOwner, Observer {
                setUiTheme(it, me_linear, me_swipe, me_integral)
            })
            userInfo.observe(viewLifecycleOwner, Observer {
                it.notNull({
                    me_swipe.isRefreshing = true
                    mViewModel.name.postValue(if (it.nickname.isEmpty()) it.username else it.nickname)
                    requestMeViewModel.getUserInfo()
                }, {
                    mViewModel.name.postValue("请先登录")
                    mViewModel.info.postValue("id：--　排名：--")
                    mViewModel.integral.postValue(0)
                })
            })
        }
    }

    inner class ProxyClick {
        /**
         * 登录
         */
        fun login() {
            nav().jumpByLogin {}
        }

        /**
         * 收藏
         */
        fun collect() {
            nav().jumpByLogin {
                it.navigate(R.id.action_mainFragment_to_collectFragment)
            }
        }

        /**
         * 积分
         */
        fun integral() {
            nav().jumpByLogin {
                it.navigate(R.id.action_mainFragment_to_integralFragment, Bundle().apply {
                    putParcelable("rank", rank)
                })
            }
        }

        /**
         * 文章
         */
        fun article() {
            nav().jumpByLogin {
                it.navigate(R.id.action_mainFragment_to_articleFragment)
            }
        }

        /**
         * TODO
         */
        fun toDo() {
         nav().navigate(R.id.action_mainFragment_to_stepCountFragment)
        }

        /**
         * 玩转安卓开源网站
         */
        fun about() {
            nav().navigate(R.id.action_mainFragment_to_webFragment, Bundle().apply {
                putParcelable(
                    "bannerData",
                    BannerResponse(
                        title = "玩Android网站",
                        url = "https://www.wanandroid.com/"
                    )
                )
            })
        }

        /**
         * 加入我们
         */
        fun join() {
            joinQQGroup("2314861690")
        }

        /**
         * demo示列模拟异常
         */
        fun demo() {
            var str: Int
            str = 1 / 0
        }

        /**
         * 设置
         */
        fun setting() {
            nav().navigate(R.id.action_mainFragment_to_settingFragment)
        }

    }
}