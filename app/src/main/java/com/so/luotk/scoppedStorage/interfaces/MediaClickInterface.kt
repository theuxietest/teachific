package com.so.luotk.scoppedStorage.interfaces

import com.so.luotk.scoppedStorage.gallery.MediaModel


interface MediaClickInterface {
    fun onMediaClick(media: MediaModel)
    fun onMediaLongClick(media: MediaModel, intentFrom: String)
}