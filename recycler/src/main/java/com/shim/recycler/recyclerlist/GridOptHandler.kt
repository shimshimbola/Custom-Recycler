package com.shim.recycler.recyclerlist

import android.view.View
import android.view.ViewGroup
import com.shim.recycler.recyclerlist.optmanager.GridViewPoolManager
import com.shim.recycler.recyclerlist.optmanager.UiCell
import kotlin.math.roundToInt

open class GridOptHandler(
    override val normalizedPeakHeight: Float = 0.3f,
    override val cells: List<UiCell>,
    val container: ViewGroup,
    val referenceView : Int = 0,
    containerDimen: Int = Utils.getDeviceScreenWidth(),
    spacing: Float = 0f,
    noOfCols: Int = 1,
    isHorizontal: Boolean = false,
    val onHeightSet: (() -> Unit)? = null) : GridViewPoolManager(
        viewGroup = container,
        containerWidth = containerDimen,
        uiCells = cells,
        spacing = spacing,
        cols = noOfCols,
        isHorizontal = isHorizontal), ViewOptHandler {

    override fun onStableViewPeak(index: Int) {
        //Expected implementation in child
    }

    override fun getUiCellHeight(index: Int): Float {
        return getCellHeight(index)
    }

    override val visible_cells = HashSet<Int>()

    override fun onViewVisible(index: Int, shouldAnimate: Boolean) {
        //Expected implementation in child
    }

    override fun onViewNotVisible(index: Int) {
        clearData(getViewAt(index))
        invalidateAt(index)
    }

    //Default to height of first cell
    override fun getCellHeight(index: Int): Float {
        return getCellAt(index).cellDimen
    }

    open fun handleDataReady(
            dataSize: Int,
            initialLoadCount: Int = getInitialLoadCount(),
            shouldAnimate: Boolean = true) {
        //Adjust container height
        val blownHeight = (getBlownHeight(dataSize)).toInt()
        setLayoutHeight(blownHeight)

        //Load the first cells
        (0 until initialLoadCount).forEach { index ->
            onViewVisible(index, shouldAnimate)
        }
    }

    //TODO: This function is fishy
    private fun getInitialLoadCount(): Int {
        return if (uiCells.size == 1) {
            (Utils.getDeviceScreenHeight() / uiCells[0].cellDimen).toInt() * cols + cols
        } else {
            2
        }
    }

    fun setHeight(dataSize: Int) {
        val blownHeight = (getBlownHeight(dataSize)).toInt() + Utils.pxFromDp(container.context, 36f).toInt()
        setLayoutHeight(blownHeight)
    }

    open fun resizeTo(dataSize: Int) {

        val blownHeight = (getBlownHeight(dataSize)).roundToInt()
        if (isHorizontal) {
            container.layoutParams.width = blownHeight
            //For some reason, a request layout is needed because horizontal scroll view does not update its width
            container.requestLayout()
        } else {
            //Set Reference height
            if (uiCells.size == 1) {
                container.findViewById<View?>(referenceView)?.layoutParams?.height = uiCells[0].cellDimen.roundToInt()
            }
            setLayoutHeight(blownHeight)
            container.requestLayout()
        }
    }

    open fun getBlownHeight(dataSize: Int): Float {
        return getTotalHeight(dataSize)
    }

    internal fun getTotalHeight(dataSize: Int): Float {
        return if (uiCells.size > 1) {
            var height = 0f
            (0 until dataSize).forEach { index ->
                height += getCellHeight(index)
            }
            height
        } else {
            ((dataSize + cols - 1) / cols) * uiCells[0].cellDimen
        }
    }

    private fun setLayoutHeight(blownHeight: Int) {
        container.layoutParams.height = blownHeight
        onHeightSet?.invoke()
    }

    /**
     * Min load count is needed for cases where visible cells does not have on edge case
     * the corner cells
     */
    protected fun refreshVisible(listSize: Int, unscrolledCount: Int, minLoadCount: Int? = null) {
        //ErrorLog.e(EmojiTags.App, "visible_cells size (refreshVisible): ${visible_cells.size}")
        if (visible_cells.size == 0) {
            (0 until unscrolledCount).forEach { index ->
                if (listSize > index) {
                    onViewVisible(index, false)
                } else {
                    removeAtIfExists(index)
                }
            }
        } else if (minLoadCount != null && visible_cells.size < minLoadCount) {
            (0 until minLoadCount).forEach { index ->
                if (listSize > index) {
                    onViewVisible(index, false)
                } else {
                    removeAtIfExists(index)
                }
            }
        } else {
            visible_cells.forEach { cellIndex ->
                if (listSize > cellIndex) {
                    onViewVisible(cellIndex, false)
                } else {
                    removeAtIfExists(cellIndex)
                }
            }
        }
        resizeTo(listSize)
    }
}