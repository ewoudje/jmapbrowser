package com.ewoudje.jmapbrowser

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform