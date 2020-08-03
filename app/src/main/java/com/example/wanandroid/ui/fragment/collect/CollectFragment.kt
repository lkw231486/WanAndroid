package com.example.wanandroid.ui.fragment.collect

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.bindViewPager2
import com.example.wanandroid.app.ext.init
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.databinding.FragmentCollectBinding
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import kotlinx.android.synthetic.main.fragment_collect.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/9 14:33
 *description: 收藏
 *email:lkw@mantoo.com.cn
 */
class CollectFragment : BaseFragment<RequestCollectViewModel, FragmentCollectBinding>() {
    var titleData = arrayListOf("文章", "网址")
    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.add(CollectAriticleFragment())
        fragments.add(CollectUrlFragment())
    }

    override fun layoutId(): Int = R.layout.fragment_collect

    override fun initView(savedInstanceState: Bundle?) {
        //初始化设置主题颜色
        shareViewModel.appColor.value.let {
            collect_viewpager_line.setBackgroundColor(it)
        }
        //初始化viewpager2
       collect_view_pager.init(this,fragments)
        //初始化magic_indicator
        collect_magic_indicator.bindViewPager2(collect_view_pager, mStringList = titleData)
        toolbar.initClose {
            nav().navigateUp()
        }
    }

    override fun lazyLoadData() {

    }
}