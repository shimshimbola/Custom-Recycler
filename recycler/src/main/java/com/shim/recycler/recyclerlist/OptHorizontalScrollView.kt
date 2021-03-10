package com.shim.recycler.recyclerlist

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class OptHorizontalScrollView(mContext: Context, attributeSet: AttributeSet) : HorizontalScrollView(mContext, attributeSet) {

    //Init from view
    var config: ListOptimizationConfig? = null
    var multiConfigs: List<ListOptimizationConfig>? = null

    var optObj: GridOptimizationObject? = null
    var optObjects: ArrayList<GridOptimizationObject>? = null

    init {
        post {
            instantiateOptObjs()
        }
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
    }

    fun run() {
        when {
            optObj != null -> {
                GridOptimize.runScroll(optObj!!, true)
            }
            optObjects != null -> optObjects!!.forEach { obj ->
                GridOptimize.runScroll(obj, true)
            }
            else -> instantiateOptObjs()
        }
    }
}