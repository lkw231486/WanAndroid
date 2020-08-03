package com.example.wanandroid.ui.fragment.stepcounter.model

/**
 *@ author: lkw
 *created on:2020/7/28 9:11
 *description: 计步实体类
 *email:lkw@mantoo.com.cn
 */
class StepEntity() {

    var curDate: String? = null//当天日期
    var steps: String? = null //当天步数

    constructor(curDate: String, steps: String) : this() {
        this.curDate = curDate
        this.steps = steps
    }

    override fun toString(): String {
        return "StepEntity{" +
                "curDate='" + curDate + '\'' +
                ", steps=" + steps +
                '}'
    }
}