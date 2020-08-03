package com.example.wanandroid.app

import android.os.Process
import androidx.multidex.MultiDex
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.example.wanandroid.app.ext.getProcessName
import com.example.wanandroid.app.weight.loadCallBack.EmptyCallback
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.ui.activity.ErrorActivity
import com.example.wanandroid.ui.activity.WelcomeActivity
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import com.zhpan.idea.utils.ConvertUtils
import me.hgj.jetpackmvvm.base.BaseApp

/**
 *@ author: lkw
 *created on:2020/7/1 15:40
 *description: 全局管理application
 *email:lkw@mantoo.com.cn
 */
class App : BaseApp() {

    //app单例
    companion object {
        lateinit var instant: App
    }

    override fun onCreate() {
        super.onCreate()
        instant = this
        MultiDex.install(this)
        MMKV.initialize(this.filesDir.absolutePath + "/mmkv")
        //界面加载管理器 初始化。
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())//加载
            .addCallback(EmptyCallback())//空数据
            .addCallback(ErrorCallback())//错误布局
            .setDefaultCallback(SuccessCallback::class.java)
            .commit()

        //获取上下文
        val context = applicationContext
        //获取当前包名
        val packName = context.packageName
        //获取当前进程名
        val processName = getProcessName(Process.myPid())
        //设置是否上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packName
        //防止项目崩溃，崩溃后打开错误界面
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(true)//是否启用CustomActivityOnCrash崩溃拦截机制 必须启用！不然集成这个库干啥？？？
            .showErrorDetails(false) //是否必须显示包含错误详细信息的按钮 default: true
            .showRestartButton(false) //是否必须显示“重新启动应用程序”按钮或“关闭应用程序”按钮default: true
            .logErrorOnRestart(false) //是否必须重新堆栈堆栈跟踪 default: true
            .trackActivities(true) //是否必须跟踪用户访问的活动及其生命周期调用 default: false
            .minTimeBetweenCrashesMs(2000)  //应用程序崩溃之间必须经过的时间 default: 3000
            .restartActivity(WelcomeActivity::class.java) // 重启的activity
            .errorActivity(ErrorActivity::class.java) //发生错误跳转的activity
            .eventListener(null)  //允许你指定事件侦听器，以便在库显示错误活动 default: null
            .apply()
    }


}