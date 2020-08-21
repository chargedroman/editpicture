package com.r.picturechargingedit.util

/**
 * to minimize object allocations during onDraw
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

class IntArrayBuffer {

    private val map = mutableMapOf<Int, IntArray>()

    fun get(size: Int): IntArray {
        val array = map[size]

        if(array == null) {
            val a = IntArray(size)
            map[size] = a
            return a
        }

        return array
    }

}
