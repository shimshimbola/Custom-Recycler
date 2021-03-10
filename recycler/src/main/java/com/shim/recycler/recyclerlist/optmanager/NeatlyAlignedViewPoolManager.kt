package com.shim.recycler.recyclerlist.optmanager

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import java.util.*

open class NeatlyAlignedViewPoolManager(private val viewGroup: RelativeLayout) {

    val context: Context = viewGroup.context
    private val viewMap = HashMap<Int, NeatImageParamBoolPair>()

    /**
     * Returns null if view already set
     */
    fun getView(imgParams: NeatlyAlignImageParams): ImageView? {

        val index = imgParams.imageIndex

        //Log.d("Child Count", viewGroup.childCount.toString())
        var imgView: ImageView? = null

        //Do we have one at index?
        if (viewMap.contains(index) && viewMap[index] != null && viewMap[index]!!.imageView != null) {
            if (viewMap[index]!!.isAvailable) {
                val viewBoolPair = viewMap[index]
                viewBoolPair?.isAvailable = false
                imgView = viewBoolPair?.imageView!!
                //Log.d("Cache", "Hit")
            } else {
                return null
            }
        } else {
            //Is there one available
            run loop@{
                viewMap.keys.forEach { key ->
                    if (viewMap[key] != null && viewMap[key]!!.imageView != null && viewMap[key]!!.isAvailable) {
                        val imgParamsBoolPair = viewMap[key]
                        imgView = imgParamsBoolPair?.imageView!!
                        viewMap.remove(key)
                        viewMap[index] = NeatImageParamBoolPair(imgParams, imgView, false)
                        //Log.d("Cache", "Hit")
                        return@loop
                    }
                }
            }
        }
        if (imgView == null) {
            //ErrorLog.e("Cache", "Miss")
            //Inflate
            //Create and init appearance of image view
            imgView = ImageView(context, null)
            imgView!!.layoutParams = RelativeLayout.LayoutParams(imgParams.finalWidth, imgParams.finalHeight)

            viewMap[index] = NeatImageParamBoolPair(imgParams, imgView, false)
            viewGroup.addView(imgView)
        }
        return imgView!!
    }

    fun getViewAt(index: Int): ImageView? {
        return viewMap[index]?.imageView
    }

    fun invalidateAt(index: Int) {
        if (viewMap.containsKey(index)) {
            viewMap[index]!!.isAvailable = true
        }
    }

    open fun clearData(index: Int) {
        //
    }

    fun release() {
        viewMap.keys.forEach { index->
            clearData(index)
        }
    }
}