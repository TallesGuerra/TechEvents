package com.example.techevents.utils

import android.text.Editable
import android.text.TextWatcher

class DateMaskWatcher : TextWatcher {
    private var isUpdating = false
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        if (isUpdating || s == null) return
        isUpdating = true
        val digits = s.filter { it.isDigit() }.take(8)
        val result = StringBuilder()
        digits.forEachIndexed { i, c ->
            if (i == 2 || i == 4) result.append('/')
            result.append(c)
        }
        s.replace(0, s.length, result)
        isUpdating = false
    }
}

class TimeMaskWatcher : TextWatcher {
    private var isUpdating = false
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        if (isUpdating || s == null) return
        isUpdating = true
        val digits = s.filter { it.isDigit() }.take(4)
        val result = StringBuilder()
        digits.forEachIndexed { i, c ->
            if (i == 2) result.append(':')
            result.append(c)
        }
        s.replace(0, s.length, result)
        isUpdating = false
    }
}

fun String.toApiDate(): String {
    val p = split("/")
    return if (p.size == 3) "${p[2]}-${p[0]}-${p[1]}" else this
}

fun String.toDisplayDate(): String {
    val p = split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else this
}