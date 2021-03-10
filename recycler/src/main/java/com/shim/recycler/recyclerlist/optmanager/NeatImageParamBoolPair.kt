package com.shim.recycler.recyclerlist.optmanager

import android.widget.ImageView


data class NeatImageParamBoolPair(
    var neatlyAlignImageParams: NeatlyAlignImageParams,
    var imageView: ImageView?,
    var isAvailable: Boolean
)

data class NeatlyAlignImageParams(
    val imageIndex: Int,
    val transY: Int,
    val finalWidth: Int,
    val finalHeight: Int
)