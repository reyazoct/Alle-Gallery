package com.reyaz.allegallery.util

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache

fun getImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .respectCacheHeaders(false)
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("imageCacheDirectory"))
                .build()
        }
        .build()
}
