package com.droneyee.calcuhost.database

import app.cash.sqldelight.db.SqlDriver

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

expect fun getDatabaseDriverFactory(): DatabaseDriverFactory
