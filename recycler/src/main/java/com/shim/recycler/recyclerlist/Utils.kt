package com.shim.recycler.recyclerlist

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlin.math.atan2


object Utils {
//http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
//The above method results accurate method compared to below methods
//http://stackoverflow.com/questions/8309354/formula-px-to-dp-dp-to-px-android
//http://stackoverflow.com/questions/13751080/converting-pixels-to-dpi-for-mdpi-and-hdpi-screens


    fun convertPixelsToDp(px: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val dp = px / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

    fun convertDpToPixel(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    fun convertDpToPx(context: Context, dp: Int): Int {
        return Math.round(dp * (context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))

    }

    fun convertPxToDp(px: Int): Int {
        return Math.round(px / (Resources.getSystem().displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun pxFromSp(context: Context, sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }

    fun spFromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.scaledDensity
    }

    fun pxFromDp(context: Context?, dp: Float): Float {
        if (context == null) {
            return dp * 1.5f    //fail case, return hdpi
        }
        return dp * context.resources.displayMetrics.density
    }

    fun getDeviceScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getDeviceScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun getScreenDimens(context: Context): Point {
        val size = Point()
        val w = ContextCompat.getSystemService(context, WindowManager::class.java)!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.defaultDisplay.getSize(size)
            return size
        } else {
            val d = w.defaultDisplay
            return Point(d.width, d.height)
        }
    }

    inline fun distanceSqr(ax: Float, ay: Float, bx: Float, by: Float): Float {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by)
    }

    inline fun distance(ax: Float, ay: Float, bx: Float, by: Float): Float {
        return Math.sqrt(((ax - bx) * (ax - bx) + (ay - by) * (ay - by)).toDouble()).toFloat()
    }

    //https://gamedev.stackexchange.com/questions/28142/how-do-i-find-the-angle-between-two-vectors
    inline fun angleBetweenAB(aX: Float, bX: Float, aY: Float, bY: Float) =
            180.0 / Math.PI * atan2((bX - aX).toDouble(), (aY - bY).toDouble())

    inline fun angleBetweenAB(aX: Int, bX: Int, aY: Int, bY: Int) =
            180.0 / Math.PI * atan2((bX - aX).toDouble(), (aY - bY).toDouble())

}

