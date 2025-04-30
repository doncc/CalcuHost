package com.droneyee.calcuhost.database

import app.cash.sqldelight.db.SqlDriver

class DesktopDatabaseDriverFactory : DatabaseDriverFactory {

    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(CalcuHostDatabase.Schema, "calcuhost.db")
    }
}

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DesktopDatabaseDriverFactory()