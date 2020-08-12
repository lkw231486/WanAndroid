package com.example.wanandroid.ui.activity

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.MainActivity
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseActivity
import com.example.wanandroid.app.ext.setPageListener
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.utils.StatusBarUtil
import com.example.wanandroid.app.weight.banner.WelcomeBannerViewHolder
import com.example.wanandroid.databinding.ActivityWelcomeBinding
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.activity_welcome.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.view.gone
import me.hgj.jetpackmvvm.ext.view.visible

/**
 *@ author: lkw
 *created on:2020/7/2 14:32
 *description: 欢迎引导界面
 *email:lkw@mantoo.com.cn
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class WelcomeActivity : BaseActivity<BaseViewModel, ActivityWelcomeBinding>() {

    private var resList = arrayOf("易", "大", "师")
    private lateinit var mViewPager: BannerViewPager<String, WelcomeBannerViewHolder>
    override fun layoutId(): Int = R.layout.activity_welcome

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarUtil.setColor(this,SettingUtil.getColor(this),0)
        //防止出现按Home键回到桌面时，再次点击重新进入该界面bug
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT !== 0) {
            finish()
            return
        }
        mDatabind.click = ProxyClick()
        welcome_baseview.setBackgroundColor(SettingUtil.getColor(this))
        mViewPager = findViewById(R.id.banner_view)
        if (shareViewModel.isFirst.value) {
            //是第一次打开app，打开引导页面
            welcome_image.gone()
            mViewPager.setHolderCreator {
                WelcomeBannerViewHolder()
            }.setIndicatorMargin(0, 0, 0, ConvertUtils.dp2px(24f))
                .setPageListener {
                    if (it == resList.size - 1) {
                        welcomeJoin.visible()
                    } else {
                        welcomeJoin.gone()
                    }
                }
            mViewPager.create(resList.toList())
        } else {
            //不是第一次打开app，0.3秒后自动跳转到app首页
            welcome_image.visible()
            mViewPager.postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                //带点渐变动画
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 300)
        }


    }

    inner class ProxyClick {
        fun toMain() {
            shareViewModel.isFirst.postValue(false)
            CacheUtil.setFirst(false)
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
            //带点渐变动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }


}