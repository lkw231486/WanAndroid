package com.example.wanandroid

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 *@ author: lkw
 *created on: 2020/8/12 11:02
 *description: kotlin函数学习
 *email:lkw@mantoo.com.cn
 */
fun function() {
    println("调用")
}

fun main() {
    //调用function函数
    function()
}

// 函数后面加  :类型   表示可返回函数类型
fun call(value: String): Int {
    return value.toInt()
}

fun call(value: Int = 100): String {
    return value.toString()
}

interface Calli {
    fun call(value: String): Int
}

fun maini() {
    //动态代理
    val call = Proxy.newProxyInstance(
        ClassLoader.getSystemClassLoader(),
        arrayOf(Calli::class.java),
        object : InvocationHandler {
            override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                // 拦截 call 方法,定义代码.其它方法都调用 InvocationHandler 的方法
                return if (method?.name == "call") {
                    (args!![0] as String).toInt()
                } else method?.invoke(this, args)
            }
        }) as Calli
    call.toString()
}