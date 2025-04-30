package com.droneyee.calcuhost.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

class DesktopDatabaseDriverFactory : DatabaseDriverFactory {

    override fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + "calcuhost.db")
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DesktopDatabaseDriverFactory()