package com.example.wanandroid.ui.fragment.share

import android.os.Bundle
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.databinding.FragmentShareAriticleBinding
import com.example.wanandroid.viewmodel.request.RequestShareArticleViewModel
import com.example.wanandroid.viewmodel.state.AriticleViewModel
import kotlinx.android.synthetic.main.fragment_share_ariticle.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.view.clickNoRepeat

/**
 *@ author: lkw
 *created on:2020/7/24 16:40
 *description: 添加文章页面
 *email:lkw@mantoo.com.cn
 */
class AddArticleFragment : BaseFragment<AriticleViewModel, FragmentShareAriticleBinding>() {
    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestAriticleViewModel: RequestShareArticleViewModel by lazy { getViewModel<RequestShareArticleViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_share_ariticle

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        //设置分享人昵称
        shareViewModel.userInfo.value?.let {
            if (it.nickname.isEmpty()) mViewModel.shareName.set(it.username) else mViewModel.shareName.set(
                it.nickname
            )
        }
        //设置提交按钮颜色
        shareViewModel.appColor.value.let {
            SettingUtil.setShapColor(share_submit, it)
        }
        //设置toolbar
        toolbar.run {
            //设置toolbar标题
            initClose("分享文章") {
                //设置返回按钮关闭事件
                nav().navigateUp()
            }
            //设置分享菜单
            inflateMenu(R.menu.share_menu)
            //设置分享规则点击事件
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.share_guize -> {
                        activity?.let { activity ->
                            MaterialDialog(activity, BottomSheet())
                                .lifecycleOwner(viewLifecycleOwner)
                                .show {
                                    title(text = "温馨提示")
                                    customView(
                                        R.layout.customview,
                                        scrollable = true,
                                        horizontalPadding = true
                                    )
                                    positiveButton(text = "朕知道了")
                                    cornerRadius(16f)
                                    getActionButton(WhichButton.POSITIVE).updateTextColor(
                                        SettingUtil.getColor(context)
                                    )
                                    getActionButton(WhichButton.NEGATIVE).updateTextColor(
                                        SettingUtil.getColor(context)
                                    )
                                }
                        }
                    }
                }
                true
            }
        }
        share_submit.clickNoRepeat {
            when {
                mViewModel.shareTitle.get().isEmpty() -> {
                    showMessage("请输入文章标题")
                }
                mViewModel.shareUrl.get().isEmpty() -> {
                    showMessage("请输入分享链接")
                }
                else -> {
                    showMessage("确定分享吗?", positiveButtonText = "分享", postiveAction = {
                        requestAriticleViewModel.addArticle(
                            mViewModel.shareTitle.get(),
                            mViewModel.shareUrl.get()
                        )
                    }, negativeButtonText = "取消")
                }
            }
        }
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        requestAriticleViewModel.addData.observe(viewLifecycleOwner, Observer { resultState ->
            parseState(resultState, {
                eventViewModel.shareArticle.postValue(true)
                nav().navigateUp()
            }, {
                showMessage(it.errorMsg)
            })
        })
    }
}