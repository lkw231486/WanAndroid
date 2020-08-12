package com.example.wanandroid

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.example.wanandroid.app.base.BaseActivity
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.utils.StatusBarUtil
import com.example.wanandroid.databinding.ActivityMainBinding
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.network.manager.NetState

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {
    override fun layoutId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(SettingUtil.getColor(this)))
        StatusBarUtil.setColor(this, SettingUtil.getColor(this), 0)
        //进入首页可以做app检查更新，一般继承bugly在线升级与热更新成本较低

    }

    override fun createObserver() {
        shareViewModel.appColor.observe(this, Observer {
            it?.let {
                supportActionBar?.setBackgroundDrawable(ColorDrawable(it))
                StatusBarUtil.setColor(this, it, 0)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.main_navition).navigateUp()
    }

    /**
     *示例，在Activity/Fragment中如果想监听网络变化，可重写onNetworkStateChanged该方法
     */
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        if (netState.isSuccess) {
            ToastUtils.showShort("有网络")
        } else {
            ToastUtils.showShort("没有网络")
        }
    }
}


//startActivity(
//Intent(
//Intent.ACTION_VIEW,
//Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=2314861690")
//)