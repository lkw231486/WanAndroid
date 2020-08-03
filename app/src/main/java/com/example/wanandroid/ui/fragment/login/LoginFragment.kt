package com.example.wanandroid.ui.fragment.login

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.hideSoftKetBoard
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.databinding.FragmentLoginBinding
import com.example.wanandroid.viewmodel.request.RequestLoginRegisterViewModel
import com.example.wanandroid.viewmodel.state.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

/**
 *@ author: lkw
 *created on:2020/7/6 17:53
 *description:登录页面
 *email:lkw@mantoo.com.cn
 */
class LoginFragment : BaseFragment<LoginRegisterViewModel, FragmentLoginBinding>() {

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestLoginRegisterViewModel: RequestLoginRegisterViewModel by lazy { getViewModel<RequestLoginRegisterViewModel>() }

    /**
     * 绑定布局文件
     */
    override fun layoutId(): Int = R.layout.fragment_login

    /**
     * 初始化数据
     */
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewModel = mViewModel
        mDatabind.click = ProxyClick()
        toolbar.initClose("登录") {
            nav().navigateUp()
        }
        //设置颜色跟主题颜色一致
        shareViewModel.appColor.value.let {
            SettingUtil.setShapColor(loginSub, it)
            loginRegister.setTextColor(it)
            toolbar.setBackgroundColor(it)
        }
    }

    override fun createObserver() {
        //监听请求结果
        requestLoginRegisterViewModel.loginResult.observe(
            viewLifecycleOwner,
            Observer { resultState ->
                parseState(resultState, {
                    //登录成功 通知账户数据发生改变
                    CacheUtil.setUser(it)
                    shareViewModel.isLogin.postValue(true)
                    shareViewModel.userInfo.postValue(it)
                    nav().navigateUp()
                }, {
                    //登陆失败
                    showMessage(it.errorMsg)
                })
            }
        )
    }

    /**
     * 懒加载
     */
    override fun lazyLoadData() {

    }

    /**
     * 点击事件代理
     */
    inner class ProxyClick {
        fun clear() {
            mViewModel.username.postValue("")
        }

        fun login() {
            when {
                mViewModel.username.value.isEmpty() -> showMessage("请填写账号")
                mViewModel.password.value.isEmpty() -> showMessage("请填写密码")
                else -> requestLoginRegisterViewModel.loginReq(
                    mViewModel.username.value,
                    mViewModel.password.value
                )
            }
        }

        fun goRegister() {
            hideSoftKetBoard(activity)
            nav().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        var onCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                mViewModel.isShowPwd.postValue(isChecked)
            }
    }


}