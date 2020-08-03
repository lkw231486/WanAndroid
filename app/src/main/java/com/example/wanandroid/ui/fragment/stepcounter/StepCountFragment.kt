package com.example.wanandroid.ui.fragment.stepcounter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.View
import androidx.core.content.ContextCompat.startForegroundService
import com.example.wanandroid.R
import com.example.wanandroid.app.App
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.databinding.FragmentStepCountBinding
import com.example.wanandroid.ui.fragment.stepcounter.Utils.TimeUtil
import com.example.wanandroid.ui.fragment.stepcounter.constant.ConstantData
import com.example.wanandroid.ui.fragment.stepcounter.dao.StepDataDao
import com.example.wanandroid.ui.fragment.stepcounter.model.StepEntity
import com.example.wanandroid.ui.fragment.stepcounter.service.StepService
import com.example.wanandroid.ui.fragment.stepcounter.view.BeforeOrAfterCalendarView
import com.example.wanandroid.viewmodel.state.StepCountViewModel
import com.fyspring.stepcounter.utils.StepCountCheckUtil
import kotlinx.android.synthetic.main.fragment_step_count.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.nav
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 *@ author: lkw
 *created on:2020/7/28 13:43
 *description: 计步页面
 *email:lkw@mantoo.com.cn
 */
class StepCountFragment : BaseFragment<StepCountViewModel, FragmentStepCountBinding>(),
    Handler.Callback {
    private var calenderView: BeforeOrAfterCalendarView? = null
    private var curSelDate: String = ""
    private val df = DecimalFormat("#.##")
    private val stepEntityList: MutableList<StepEntity> = ArrayList()
    private var stepDataDao: StepDataDao? = null
    private var isBind = false
    private val mGetReplyMessenger = Messenger(Handler(this))
    private var messenger: Messenger? = null
    override fun layoutId(): Int = R.layout.fragment_step_count

    /**
     * 定时任务
     */
    private var timerTask: TimerTask? = null
    private var timer: Timer? = null
    override fun initView(savedInstanceState: Bundle?) {
        //初始化标题
        toolbar.initClose("今日步数") {
            nav().navigateUp()
        }.setBackgroundColor(SettingUtil.getColor(App.instant))

        //获取当前时间
        curSelDate = TimeUtil.getCurrentDate()
        //初始化日历选择
        calenderView = BeforeOrAfterCalendarView(App.instant)
        movement_records_calender_ll!!.addView(calenderView)
        /**
         * 这里判断当前设备是否支持计步
         */
        if (StepCountCheckUtil.isSupportStepCountSensor(App.instant)) {
            getRecordList()
            is_support_tv.visibility = View.GONE
            setDatas()
            setupService()
        } else {
            movement_total_steps_tv.text = "0"
            is_support_tv!!.visibility = View.VISIBLE
        }
        initListener()
        refresh_date.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                curSelDate = TimeUtil.getCurrentDate()
                stepDataDao = StepDataDao(App.instant)
                val stepEntity = stepDataDao!!.getCurDataByDate(curSelDate)
                if (stepEntity == null) {
                    showMessage("今天为获取到你的步数，记得多运动小可爱")
                } else {
                    movement_total_steps_tv.text = stepEntity?.steps
                }
            }
        })
    }

    fun initListener() {
        calenderView!!.setOnBoaCalenderClickListener(object :
            BeforeOrAfterCalendarView.BoaCalenderClickListener {
            override fun onClickToRefresh(position: Int, curDate: String) {
                //获取当前选中的时间
                curSelDate = curDate
                //根据日期去取数据
                setDatas()
            }
        })
    }

    /**
     * 开启计步服务
     */
    private fun setupService() {
        val intent = Intent(context, StepService::class.java)
        isBind = context!!.bindService(intent, conn, Context.BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startForegroundService(context!!, intent)
        else context!!.startService(intent)
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    private val conn = object : ServiceConnection {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            timerTask = object : TimerTask() {
                override fun run() {
                    try {
                        messenger = Messenger(service)
                        val msg = Message.obtain(null, ConstantData.MSG_FROM_CLIENT)
                        msg.replyTo = mGetReplyMessenger
                        messenger!!.send(msg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
            timer = Timer()
            timer!!.schedule(timerTask, 0, 500)
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    /**
     * 设置记录数据
     */
    private fun setDatas() {
        val stepEntity = stepDataDao!!.getCurDataByDate(curSelDate)

        if (stepEntity != null) {
            val steps = stepEntity.steps?.let { Integer.parseInt(it) }
            //获取全局的步数
            movement_total_steps_tv.text = steps.toString()
            //计算总公里数
            movement_total_km_tv.text = steps?.let { countTotalKM(it) }
        } else {
            //获取全局的步数
            movement_total_steps_tv.text = "0"
            //计算总公里数
            movement_total_km_tv.text = "0"
        }

        //设置时间
        val time = TimeUtil.getWeekStr(curSelDate)
        movement_total_km_time_tv.text = time
        movement_total_steps_time_tv.text = time
    }

    /**
     * 简易计算公里数，假设一步大约有0.7米
     *
     * @param steps 用户当前步数
     * @return
     */
    private fun countTotalKM(steps: Int): String {
        val totalMeters = steps * 0.7
        //保留两位有效数字
        return df.format(totalMeters / 1000)
    }

    /**
     * 获取全部运动历史纪录
     */
    private fun getRecordList() {
        //获取数据库
        stepDataDao = StepDataDao(App.instant)
        stepEntityList.clear()
        stepEntityList.addAll(stepDataDao!!.getAllDatas())
        if (stepEntityList.size > 7) {
            //在这里获取历史记录条数，当条数达到7条以上时，就开始删除第七天之前的数据
            for (entity in stepEntityList) {
                if (TimeUtil.isDateOutDate(entity.curDate!!)) {
                    stepDataDao?.deleteCurData(entity.curDate!!)
                }
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            //这里用来获取到Service发来的数据
            ConstantData.MSG_FROM_SERVER ->
                //如果是今天则更新数据
                if (curSelDate == TimeUtil.getCurrentDate()) {
                    //记录运动步数
                    val steps = msg.data.getInt("steps")
                    //设置的步数
                    movement_total_steps_tv.text = steps.toString()
                    //计算总公里数
                    movement_total_km_tv.text = countTotalKM(steps)
                }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //记得解绑Service，不然多次绑定Service会异常
        if (isBind) context?.unbindService(conn)
    }

    override fun lazyLoadData() {

    }

    override fun createObserver() {
        shareViewModel.appColor.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            toolbar.setBackgroundColor(it)
        })
    }
}