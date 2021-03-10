package com.shim.recycler.recyclerlist

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class OptimizedScrollView(mContext: Context, attributeSet: AttributeSet) : ScrollView(mContext, attributeSet) {

    //Init from view
    var config: ListOptimizationConfig? = null
    var multiConfigs: List<ListOptimizationConfig>? = null

    var optObj: GridOptimizationObject? = null
    var optObjects: ArrayList<GridOptimizationObject>? = null
    var onScrollChanged: (() -> Unit)? = null
    var triggerOnTop: (() -> Unit)? = null
    var isScrollingActive = false
    var shouldScroll = true

    init {
        post {
            instantiateOptObjs()
        }
    }

    fun onTop(callback: () -> Unit) {
        triggerOnTop = callback
    }

    private fun instantiateOptObjs() {
        if (config != null) {
            optObj = GridOptimizationObject(config!!)
        }
        if (multiConfigs != null) {
            optObjects = arrayListOf()
            multiConfigs!!.forEach { obj ->
                optObjects!!.add(GridOptimizationObject(obj))
            }
        }
    }

    public override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        run()
        onScrollChanged?.invoke()
        if (t != 0) {
            isScrollingActive = true
        }
        if (t == 0 && isScrollingActive) {
            triggerOnTop?.invoke()
        }
    }

    fun run() {
        when {
            optObj != null -> {
                GridOptimize.runScroll(optObj!!)
            }
            optObjects != null -> optObjects!!.forEach { obj ->
                GridOptimize.runScroll(obj)
            }
            else -> instantiateOptObjs()
        }
    }

    fun pauseScroll() {
        shouldScroll = false
        requestDisallowInterceptTouchEvent(true)
    }

    fun resumeScroll() {
        shouldScroll = true
        requestDisallowInterceptTouchEvent(false)
    }
}