package com.shim.recycler.recyclerlist

import android.util.Log
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

class GridOptimize {

    companion object {

        private var globalScrollPosition = 0f
        var global_scroll_velocity = 0f
            private set
        private const val MIN_STABLE_SCROLL_VELOCITY_FOR_PEAK = 2f

        fun runScroll(optObject: GridOptimizationObject, isHorizontal: Boolean = false) {
            if (optObject.viewGroup.childCount == 0) return
            val firstChild = optObject.viewGroup.getChildAt(0) ?: return
            if (firstChild.measuredHeight == 0 && !isHorizontal) return
            if (firstChild.measuredWidth == 0 && isHorizontal) return

            /*UiUtils.debugCoords(firstChild)
                Log.d("Grid Layout", "GRRIIIDDDLAYOUT")
                UiUtils.debugCoords(viewGroup)*/

            optObject.viewGroup.getGlobalVisibleRect(optObject.container_visible_rectf)
            firstChild.getLocationOnScreen(optObject.first_child_location_on_screen)

            optObject.viewGroup.getLocationOnScreen(optObject.container_location_on_screen)

            val top_of_container = if (!isHorizontal) optObject.container_visible_rectf.top else optObject.container_visible_rectf.left
            val y_of_first = if (!isHorizontal) optObject.first_child_location_on_screen[1] else optObject.first_child_location_on_screen[0]
            val view_height = if (!isHorizontal) {
                optObject.container_visible_rectf.bottom - optObject.container_visible_rectf.top
            } else {
                optObject.container_visible_rectf.right - optObject.container_visible_rectf.left
            }
            val abs_visible_center = view_height / 2
            val abs_visible_peak = (view_height * (1f - optObject.optHandler.normalizedPeakHeight)).toInt()

            if (top_of_container - y_of_first < 0) {
                return
            }

            val y_of_first_dp = Utils.dpFromPx(optObject.viewGroup.context, y_of_first.toFloat())
            global_scroll_velocity = abs(globalScrollPosition - y_of_first_dp)
            //ErrorLog.e(EmojiTags.App, "Scroll Velocity: $global_scroll_velocity")
            globalScrollPosition = y_of_first_dp

            //Is there variance in cells?
            val singularViewDimention = (if (!isHorizontal) firstChild.measuredHeight else firstChild.measuredWidth)
            val isVariableCellType = optObject.optHandler.cells.size > 1
            val total_height_till_bottom = ((top_of_container + view_height) - y_of_first)


            val startTime = System.currentTimeMillis()
            val new_bottom_row_visible = if (isVariableCellType) {
                var heightSoFar = 0f
                var cellIndex = 0
                while (heightSoFar < total_height_till_bottom) {
                    heightSoFar += optObject.optHandler.getCellHeight(cellIndex)
                    cellIndex++
                }
                cellIndex - 1
            } else {
                total_height_till_bottom / singularViewDimention
            }

            var new_top_row_visible = if (isVariableCellType) {
                var heightSoFar = 0f
                var cellIndex = 0
                while (heightSoFar < total_height_till_bottom - view_height) {
                    heightSoFar += optObject.optHandler.getCellHeight(cellIndex)
                    cellIndex++
                }
                cellIndex - 1
            } else {
                val no_of_rows_on_screen = ceil(view_height.toFloat() / singularViewDimention.toFloat()).toInt()
                new_bottom_row_visible - no_of_rows_on_screen
            }
            val bottom_row_for_peak = if (isVariableCellType) {
                var heightSoFar = 0f
                var cellIndex = 0
                while (heightSoFar < (total_height_till_bottom + view_height * optObject.optHandler.normalizedPeakHeight)) {
                    heightSoFar += optObject.optHandler.getCellHeight(cellIndex)
                    cellIndex++
                }
                (cellIndex - 1).toFloat()
            } else {
                (total_height_till_bottom + view_height * optObject.optHandler.normalizedPeakHeight) / singularViewDimention
            }
            val new_middle_row_visible = if (isVariableCellType) {
                var heightSoFar = 0f
                var cellIndex = 0
                while (heightSoFar < total_height_till_bottom - view_height / 2) {
                    heightSoFar += optObject.optHandler.getCellHeight(cellIndex)
                    cellIndex++
                }
                cellIndex - 1
            } else {
                ((new_top_row_visible + bottom_row_for_peak) / 2.0f).roundToInt()
            }

            //Log.e(EmojiTags.App, "Perf: ${System.currentTimeMillis() - startTime}")

            if (new_top_row_visible < 0) {
                new_top_row_visible = 0
            }

            //Stable Peak row
            if (global_scroll_velocity < MIN_STABLE_SCROLL_VELOCITY_FOR_PEAK) {
                if (new_middle_row_visible != optObject.middle_row_visible) {
                    (0 until optObject.numberOfColumns).forEach { columnIndex ->
                        val key = new_middle_row_visible * optObject.numberOfColumns + columnIndex
                        optObject.optHandler.onStableViewPeak(key)
                    }
                    optObject.middle_row_visible = new_middle_row_visible
                }
            }

            if (new_top_row_visible != optObject.top_row_visible || new_bottom_row_visible != optObject.bottom_row_visible) {


                //Add visible index
                (optObject.top_row_visible..optObject.bottom_row_visible).forEach { row ->
                    if (row < new_top_row_visible || row > new_bottom_row_visible) {
                        (0 until optObject.numberOfColumns).forEach { columnIndex ->
                            val key = row * optObject.numberOfColumns + columnIndex
                            if (optObject.optHandler.visible_cells.contains(key)) {
                                optObject.optHandler.visible_cells.remove(key)
                                //On Added
                                optObject.optHandler.onViewNotVisible(key)
                            }
                        }
                    }
                }

                optObject.top_row_visible = new_top_row_visible
                optObject.bottom_row_visible = new_bottom_row_visible


                //Add visible index
                (optObject.top_row_visible..optObject.bottom_row_visible).forEach { row ->
                    (0 until optObject.numberOfColumns).forEach { columnIndex ->
                        val key = row * optObject.numberOfColumns + columnIndex
                        if (!optObject.optHandler.visible_cells.contains(key)) {
                            optObject.optHandler.visible_cells.add(key)
                            //On Added
                            optObject.optHandler.onViewVisible(key)
                        }
                    }
                }

                if (false) {

                    Log.d("----", "-------------------------------------")
                    Log.d("top_of_container     ", top_of_container.toString())
                    Log.d("y_of_first           ", y_of_first.toString())
                    Log.d("abs_visible_center   ", abs_visible_center.toString())

                    Log.d("Top Row Visible       :", " " + optObject.top_row_visible.toString())
                    Log.d("Middle Row Visible    :", " " + optObject.middle_row_visible.toString())
                    Log.d("Bottom Row Visible    :", " " + optObject.bottom_row_visible.toString())

                    var indexSetString = ""
                    optObject.optHandler.visible_cells.forEach { key: Int ->
                        indexSetString += "$key, "
                    }
                    Log.d("Index Set             :", indexSetString)
                }
            }
        }
    }
}