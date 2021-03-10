package com.shim.recycler.recyclerlist

import android.graphics.Rect
import android.widget.RelativeLayout

data class GridOptimizationObject(
        val viewGroup: RelativeLayout,
        val noOfCols: Int,
        val optHandler: ViewOptHandler
) {

    constructor(optimizationConfig: ListOptimizationConfig) : this(
            viewGroup = optimizationConfig.relativeLayout,
            noOfCols = optimizationConfig.noOfCols,
            optHandler = optimizationConfig.optHandler
    )
    //find the child gridlayout

    val numberOfColumns = noOfCols

    var middle_row_visible = -1
    var top_row_visible = -1
    var bottom_row_visible = -1
    val container_visible_rectf = Rect()
    val container_location_on_screen = IntArray(2)
    val first_child_location_on_screen = IntArray(2)
}