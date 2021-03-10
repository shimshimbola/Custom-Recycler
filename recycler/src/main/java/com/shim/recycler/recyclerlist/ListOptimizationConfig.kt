package com.shim.recycler.recyclerlist

import android.widget.RelativeLayout

data class ListOptimizationConfig(
        val optHandler: ViewOptHandler,
        val relativeLayout: RelativeLayout,
        val noOfCols: Int = 1)