package com.example.wanandroid.ui.activity

import android.content.ClipData
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.ToastUtils.showShort
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseActivity
import com.example.wanandroid.app.ext.init
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.utils.StatusBarUtil
import com.example.wanandroid.databinding.ActivityErrorBinding
import kotlinx.android.synthetic.main.activity_error.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.util.clipboardManager
import me.hgj.jetpackmvvm.ext.view.clickNoRepeat
import java.lang.Exception

/**
 *@ author: lkw
 *created on:2020/7/3 9:26
 *description: 错误页面
 *email:lkw@mantoo.com.cn
 */
class ErrorActivity : BaseActivity<BaseViewModel, ActivityErrorBinding>() {

    override fun layoutId(): Int = R.layout.activity_error


    override fun initView(savedInstanceState: Bundle?) {
        toolbar.init("发生错误")
        supportActionBar?.setBackgroundDrawable(ColorDrawable(SettingUtil.getColor(this)))
        StatusBarUtil.setColor(this, SettingUtil.getColor(this), 0)
        val config = CustomActivityOnCrash.getConfigFromIntent(intent)
        errorRestart.clickNoRepeat {
            config?.run {
                CustomActivityOnCrash.restartApplication(this@ErrorActivity, this)
            }
        }
        errorSendError.clickNoRepeat {
            CustomActivityOnCrash.getActivityLogFromIntent(intent)?.let {
                showMessage(it, "发现有Bug不去打作者脸？", "必须打", {
                    val mClipData = ClipData.newPlainText("errorLog", it)
                    //将mClipData内容放到系统剪贴板里。
                    clipboardManager?.setPrimaryClip(mClipData)
                    ToastUtils.showShort("已复制错误日志")
                    try {
                        val url = "mqqwpa://im/chat?chat_type=wpa&uin=2314861690"
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (e: Exception) {
                        ToastUtils.showShort("请先安装QQ")
                    }
                }, "我不敢")
            }
        }
    }
}