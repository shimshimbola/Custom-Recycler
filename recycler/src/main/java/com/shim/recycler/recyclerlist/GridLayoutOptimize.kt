package util.performance

import android.view.View
import android.view.ViewParent
import androidx.databinding.BindingAdapter
import androidx.core.widget.NestedScrollView
import android.widget.RelativeLayout
import com.shim.recycler.recyclerlist.GridOptimizationObject
import com.shim.recycler.recyclerlist.ViewOptHandler
import com.shim.recycler.recyclerlist.GridOptimize

/**
 * Grid Must contain one child having height equal to cell height
 */
@BindingAdapter("optimizeGrid", "optHandler")
fun optimizeGrid(relativeLayout: RelativeLayout, noOfCols: Int?, optHandler: ViewOptHandler) {

    val nestedScrollView = relativeLayout.getParentNestedScrollView() ?: return
    val optObject = GridOptimizationObject(relativeLayout, noOfCols ?: 1, optHandler)
    nestedScrollView.post {
        GridOptimize.runScroll(optObject)
    }
    nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
        GridOptimize.runScroll(optObject)
    })
}

fun View.getParentNestedScrollView(): NestedScrollView? {
    var parentView: ViewParent? = parent
    var nestedScrollView: NestedScrollView? = null

    while (parentView != null) {
        if ((parentView as View) is NestedScrollView) {
            nestedScrollView = parentView as NestedScrollView?
            break
        }
        parentView = (parentView as ViewParent).parent
    }

    return nestedScrollView
}