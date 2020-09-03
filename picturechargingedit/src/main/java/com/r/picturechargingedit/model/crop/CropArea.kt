package com.r.picturechargingedit.model.crop

import android.graphics.RectF

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

enum class CropArea(val add: (RectF, Float, Float) -> Unit) {

    NONE({ _, _, _ -> }),

    LEFT({ rect, dx, _ -> rect.left += dx }),
    RIGHT({ rect, dx, _ -> rect.right += dx }),
    TOP({ rect, _, dy -> rect.top += dy }),
    BOTTOM({ rect, _, dy -> rect.bottom += dy }),

    TOP_LEFT({ rect, dx, dy -> rect.top += dy; rect.left += dx }),
    TOP_RIGHT({ rect, dx, dy -> rect.top += dy; rect.right += dx }),
    BOTTOM_LEFT({ rect, dx, dy -> rect.bottom += dy; rect.left += dx }),
    BOTTOM_RIGHT({ rect, dx, dy -> rect.bottom += dy; rect.right += dx }),

    INSIDE({ rect, dx, dy -> rect.left += dx; rect.right += dx; rect.top += dy; rect.bottom += dy });

}
