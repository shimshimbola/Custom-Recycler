package com.shim.recycler.recyclerlist.optmanager

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

data class UiCell(
        val layoutId: Int,
        val cellDimen: Float
)

abstract class GridViewPoolManager(
    private val viewGroup: ViewGroup,
    val containerWidth: Int,
    val uiCells: List<UiCell>,
    val spacing: Float,
    val cols: Int = 3,
    val isHorizontal: Boolean = false) {

    private val context: Context = viewGroup.context
    private val viewMap = HashMap<Int, GridView>()
    val cellWidth: Int = containerWidth / cols

    fun getViewAt(index: Int): View? {
        return viewMap[index]?.view
    }

    //Default to first
    open fun getCellAt(index: Int): UiCell {
        return uiCells[0]
    }

    /**
     * Returns null if view already set
     */
    fun getUnhandledView(index: Int, shouldAnimate: Boolean): View? {
        var returnView: View? = null
        val uiCell = getCellAt(index)
        //Do we have one at index?
        if (viewMap.contains(index) && viewMap[index] != null && viewMap[index]!!.view != null && uiCell.layoutId == viewMap[index]?.layoutId) {
            if (viewMap[index]!!.isAvailable) {
                val viewBoolPair = viewMap[index]
                viewBoolPair?.isAvailable = false
                returnView = viewBoolPair?.view!!
                //Log.d("Cache", "Hit")
            } else {
                return null
            }
        } else {
            if (uiCells.size > 1 && uiCell.layoutId != viewMap[index]?.layoutId) {
                removeAtIfExists(index)
            }
            //Is there one available
            run loop@{
                viewMap.keys.forEach { key ->
                    if (viewMap[key] != null && viewMap[key]!!.view != null && viewMap[key]!!.isAvailable && uiCell.layoutId == viewMap[key]!!.layoutId) {
                        val viewBoolPair = viewMap[key]
                        returnView = viewBoolPair?.view!!
                        viewMap.remove(key)
                        viewMap[index] = GridView(returnView, false, uiCell.layoutId)
                        //Log.d("Cache", "Hit")
                        return@loop
                    }
                }
            }
        }
        if (returnView == null) {
            //ErrorLog.e("Cache", "Miss")
            //Inflate
            returnView = LayoutInflater.from(viewGroup.context).inflate(uiCell.layoutId, viewGroup, false)
            onViewCreated(returnView!!, getUiCellHeight(index))
            viewMap[index] = GridView(returnView, false, uiCell.layoutId)
            viewGroup.addView(returnView)
        }

        val transY = getTransY(index)
        val transX = if (uiCells.size > 1) 0f else ((index % cols) * containerWidth.toFloat() / cols) + (if (!isHorizontal) spacing / 2 else 0f)

        //Only animate above 24
        if (shouldAnimate && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)) {
            returnView!!.scaleX = 0.8f
            returnView!!.scaleY = 0.8f
            returnView!!.animate().scaleX(1f).setDuration(200).start()
            returnView!!.animate().scaleY(1f).setDuration(200).start()
        }

        if (isHorizontal) {
            returnView!!.translationX = transY
            returnView!!.translationY = transX
        } else {
            returnView!!.translationY = transY
            returnView!!.translationX = transX
        }

        return returnView!!
    }

    abstract fun getUiCellHeight(index: Int): Float

    private fun getTransY(index: Int): Float {
        return if (uiCells.size > 1) {
            var heightSoFar = 0f
            (0 until index).forEach { ind ->
                heightSoFar += getUiCellHeight(ind)
            }
            heightSoFar
        } else {
            val row = index / cols
            row * getUiCellHeight(index)
        }
    }

    open fun onViewCreated(view: View, cellHeight: Float) {
        if (isHorizontal) {
            view.layoutParams.width = (cellHeight - spacing).toInt()
            view.layoutParams.height = (cellWidth)
        } else {
            view.layoutParams.width = (cellWidth - spacing).toInt()
            view.layoutParams.height = (cellHeight - spacing).toInt()
        }
    }

    fun invalidateAt(index: Int) {
        if (viewMap.containsKey(index)) {
            viewMap[index]!!.isAvailable = true
        }
    }

    fun invalidateAll() {
        viewMap.keys.forEach { key ->
            viewMap[key]!!.isAvailable = true
        }
    }

    fun removeAtIfExists(index: Int) {
        if (viewMap.containsKey(index) && viewMap[index]?.view != null) {
            viewGroup.removeView(viewMap[index]?.view)
            viewMap.remove(index)
        }
    }

    private fun getALLViews(): List<View> {
        val views = ArrayList<View>()
        viewMap.values.forEach { gridView ->
            if (gridView.view != null) {
                views.add(gridView.view!!)
            }
        }

        return views
    }

    fun clear() {
        val views = getALLViews()
        views.forEach { view ->
            clearData(view)
        }
    }

    open fun clearData(view: View?) {
        //Expected implementation
    }

    fun reset() {
        viewMap.keys.forEach { key ->
            viewMap[key]!!.isAvailable = true
        }
    }

    fun removeAllViewAndReset() {
        viewMap.clear()
        //Remove all view except first because first is used as a reference measure view
        viewGroup.removeViews(1, viewGroup.childCount - 1)
        if (!isHorizontal) {
            viewGroup.layoutParams.height = 0
        } else {
            viewGroup.layoutParams.width = 0
        }
    }
}