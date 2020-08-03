package com.example.wanandroid.ui.fragment.publicnumber

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.data.model.bean.ClassifyResponse
import com.example.wanandroid.databinding.FragmentViewpagerBinding
import com.example.wanandroid.viewmodel.request.RequestPublicNumberViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.include_viewpager.*
import me.hgj.jetpackmvvm.ext.parseState

/**
 *@ author: lkw
 *created on: 2020/8/3 11:11
 *description: 公众号页面
 *email:lkw@mantoo.com.cn
 */
class PublicNumberFragment :
    BaseFragment<RequestPublicNumberViewModel, FragmentViewpagerBinding>() {

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //fragment集合
    private var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    private var titleList: ArrayList<ClassifyResponse> = arrayListOf()
    override fun layoutId(): Int = R.layout.fragment_viewpager

    override fun initView(savedInstanceState: Bundle?) {
        //状态也配置
        loadSir = LoadServiceInit(view_pager) {
            //点击重试时出发操作
            loadSir.showCallback(LoadingCallback::class.java)
            mViewModel.getPublicTitleData()
        }
        //初始化viewPager2
        view_pager.init(this, fragments)
        //初始化magic_indicator
        magic_indicator.bindViewPager2(view_pager, titleList)
        //初始化时设置顶部布局颜色
        shareViewModel.appColor.value.let {
            viewpager_linear.setBackgroundColor(it)
        }
    }

    /**
     * 懒加载
     */
    override fun lazyLoadData() {
        //请求标题数据
        mViewModel.getPublicTitleData()
    }

    override fun createObserver() {
        mViewModel.titleData.observe(viewLifecycleOwner, Observer { data ->
            parseState(data, {
                titleList.addAll(it)
                it.forEach { classify ->
                    fragments.add(PublicChildFragment.newInstance(classify.id))
                }
                magic_indicator.navigator.notifyDataSetChanged()
                view_pager.adapter?.notifyDataSetChanged()
                view_pager.offscreenPageLimit = fragments.size
                loadSir.showSuccess()
            }, {
                //请求项目标题失败
                loadSir.showCallback(ErrorCallback::class.java)
                loadSir.setErrorText(it.errorMsg)
            })
        })
        shareViewModel.appColor.observe(viewLifecycleOwner, Observer {
            setUiTheme(it, viewpager_linear, loadSir)
        })
    }
}