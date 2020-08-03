package com.example.wanandroid.app.base

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.wanandroid.R
import com.example.wanandroid.app.event.AppViewModel
import com.example.wanandroid.app.event.EventViewModel
import com.example.wanandroid.app.ext.hideSoftKetBoard
import com.example.wanandroid.app.utils.SettingUtil
import me.hgj.jetpackmvvm.base.fragment.BaseVmDbFragment
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.getAppViewModel

/**
 *@ author: lkw
 *created on:2020/7/2 16:17
 *description: 你项目中的Fragment基类，在这里实现显示弹窗，吐司，还有自己的需求操作 ，如果不想用Databind，请继承
 * BaseVmFragment例如
 * abstract class BaseFragment<VM : BaseViewModel> : BaseVmFragment<VM>() {
 *email:lkw@mantoo.com.cn
 */
abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbFragment<VM, DB>() {

    private var dialog: MaterialDialog? = null

    //Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
    val shareViewModel: AppViewModel by lazy { getAppViewModel<AppViewModel>() }

    //Application全局的ViewModel，用于发送全局通知操作
    val eventViewModel: EventViewModel by lazy { getAppViewModel<EventViewModel>() }

    /**
     *当前fragment绑定的视图布局
     */
    abstract override fun layoutId(): Int

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     *懒加载，只有当前fragment视图显示时才会触发该方法
     */
    abstract override fun lazyLoadData()

    /**
     *创建liveDate观察者，懒加载之后才会触发
     */
    override fun createObserver() {}

    /**
     * Fragment触发onCreated之后触发的初始化数据方法
     */
    override fun initData() {}

    /**
     *打开等待框
     */
    override fun showLoading(message: String) {
        if (dialog == null) {
            dialog = activity?.let {
                MaterialDialog(it)
                    .cancelable(true)
                    .cancelOnTouchOutside(false)
                    .cornerRadius(8f)
                    .customView(R.layout.layout_custom_progress_dialog_view)
                    .lifecycleOwner(this)
            }
            dialog?.getCustomView()?.run {
                this.findViewById<TextView>(R.id.loading_tips).text = message
                shareViewModel.appColor.value.let {
                    this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList =
                        SettingUtil.getOneColorStateList(it)
                }
            }
            dialog?.show()
        }
    }

    /**
     * 关闭等待窗
     */
    override fun dismissLoading() {
        dialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        //关闭软键盘
        hideSoftKetBoard(activity)
    }
}