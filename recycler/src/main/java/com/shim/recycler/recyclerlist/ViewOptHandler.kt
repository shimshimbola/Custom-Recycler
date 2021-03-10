package com.shim.recycler.recyclerlist

import com.shim.recycler.recyclerlist.optmanager.UiCell

interface ViewOptHandler {
    val visible_cells : HashSet<Int>
    val normalizedPeakHeight : Float
    val cells : List<UiCell>
    fun onViewVisible(index: Int, shouldAnimate : Boolean = true)
    fun onViewNotVisible(index: Int)
    fun onStableViewPeak(index: Int)
    fun getCellHeight(index: Int) : Float
}