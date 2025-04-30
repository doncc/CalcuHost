package com.droneyee.calcuhost

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

