package com.example.wanandroid.ui.fragment.login

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.databinding.FragmentRegisterBinding
import com.example.wanandroid.viewmodel.request.RequestLoginRegisterViewModel
import com.example.wanandroid.viewmodel.state.LoginRegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState

/**
 *@ author: lkw
 *created on:2020/7/7 11:43
 *description: 注册页面fragment
 *email:lkw@mantoo.com.cn
 */
class RegisterFragment : BaseFragment<LoginRegisterViewModel, FragmentRegisterBinding>() {

    private val requestLoginRegisterViewModel: RequestLoginRegisterViewModel by lazy { getViewModel<RequestLoginRegisterViewModel>() }
    override fun layoutId(): Int = R.layout.fragment_register

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewModel = mViewModel
        mDatabind.click = ProxyClick()
        toolbar.initClose("注册") {
            nav().navigateUp()
        }
        //设置颜色跟猪蹄颜色一致
        shareViewModel.appColor.value?.let {
            SettingUtil.setShapColor(registerSub, it)
            toolbar.setBackgroundColor(it)
        }
    }


    /**
     * 取网络请求返回的数据
     */
    override fun createObserver() {
        requestLoginRegisterViewModel.loginResult.observe(
            viewLifecycleOwner,
            Observer { resultState->
             parseState(resultState,{
              shareViewModel.isLogin.postValue(true)
                 CacheUtil.setUser(it)
                 shareViewModel.userInfo.postValue(it)
                 nav().navigate(R.id.action_registerFragment_to_mainFragment)
             },{
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
     * 代理点击事件
     */
    inner class ProxyClick {

        //清空账号点击事件
        fun clear() {
            mViewModel.username.postValue("")
        }

        //注册点击事件
        fun register() {
            when {
                mViewModel.username.value.isEmpty() -> showMessage("请填写账号")
                mViewModel.password.value.isEmpty() -> showMessage("请填写密码")
                mViewModel.password2.value.isEmpty() -> showMessage("请填写确认密码")
                mViewModel.password.value.length < 6 -> showMessage("最少要六位数密码")
                mViewModel.password.value != mViewModel.password2.value -> showMessage("请输入一致密码")
                else -> requestLoginRegisterViewModel.registerAndLogin(
                    mViewModel.username.value,
                    mViewModel.password.value,
                    mViewModel.password2.value
                )
            }
        }

        //密码显示隐藏开关
        var onCheckChangeListener1 = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            mViewModel.isShowPwd.postValue(isChecked)
        }

        //确认密码显示隐藏开关
        var onCheckChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            mViewModel.isShowPwd2.postValue(isChecked)
        }
    }
}