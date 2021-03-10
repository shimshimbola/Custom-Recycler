package com.shim.recycler.recyclerlist

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * Carbon copy of OptimizedScrollView, TODO: consider DRY
 */
class OptimizedNestedScrollView(mContext: Context, attributeSet: AttributeSet) : NestedScrollView(mContext, attributeSet) {
    //Init from view
    var config: ListOptimizationConfig? = null
    var multiConfigs: List<ListOptimizationConfig>? = null


    var optObj: GridOptimizationObject? = null
    var optObjects: ArrayList<GridOptimizationObject>? = null
    var onScrollChanged : (()->Unit)? = null

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
        onScrollChanged?.invoke()
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
}