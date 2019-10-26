package com.graham.nofreeride.utils

fun formatUSD(value: Any) : String {
    when (value) {
        is Int -> return String.format("$%d")
        is Float -> return String.format("$%.2f",value)
        is String -> return String.format("$%s", value)
        else -> return "-"
    }
}

