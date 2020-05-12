package com.github.michaelbull.jdbc

import com.github.michaelbull.jdbc.context.CoroutineConnection
import com.github.michaelbull.jdbc.context.dataSource
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException
import kotlin.coroutines.coroutineContext

@PublishedApi
internal val logger = InlineLogger()

suspend inline fun <T> withConnection(crossinline block: suspend () -> T): T {
    val connection = coroutineContext[CoroutineConnection]

    return if (connection.isNullOrClosed()) {
        newConnection(block)
    } else {
        block()
    }
}

@PublishedApi
internal suspend inline fun <T> newConnection(crossinline block: suspend () -> T): T {
    val connection = coroutineContext.dataSource?.connection ?: error("No data source in context")

    return try {
        withContext(CoroutineConnection(connection)) {
            block()
        }
    } finally {
        connection.closeCatching()
    }
}

@PublishedApi
internal fun Connection?.isNullOrClosed(): Boolean {
    return if (this == null) {
        true
    } else try {
        isClosed
    } catch (ex: SQLException) {
        logger.warn(ex) { "Connection isNullOrClosed check failed, assuming closed:" }
        true
    }
}

@PublishedApi
internal fun Connection.rollbackCatching() {
    try {
        rollback()
    } catch (ex: Throwable) {
        logger.warn(ex) { "Failed to rollback transaction:" }
    }
}

@PublishedApi
internal fun Connection.closeCatching() {
    try {
        close()
    } catch (ex: SQLException) {
        logger.warn(ex) { "Failed to close database connection cleanly:" }
    }
}
