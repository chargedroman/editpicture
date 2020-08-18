package com.r.picturechargingedit.model

import android.net.Uri

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureResultArgs(
    val picture: Uri,
    val onSuccess: (Uri) -> Unit,
    val onError: (Throwable) -> Unit
)
