package com.r.picturechargingedit.model

/**
 *
 * Author: romanvysotsky
 * Created: 16.06.21
 */

enum class Rotation(val angle: Int) {
    None(0),
    Rotation90(90),
    Rotation180(180),
    Rotation270(270);

    fun next(): Rotation {
        return when(this) {
            None -> Rotation90
            Rotation90 -> Rotation180
            Rotation180 -> Rotation270
            Rotation270 -> None
        }
    }
}
