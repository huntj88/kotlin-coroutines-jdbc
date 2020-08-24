package com.github.michaelbull.jdbc

import com.github.michaelbull.jdbc.context.CoroutineDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

suspend fun <T> withRepositoryContext(
    dataSource: DataSource,
    block: suspend CoroutineScope.() -> T
): T = withContext(
    context = Dispatchers.IO + CoroutineDataSource(dataSource),
    block = { transaction { block() } }
)

fun <T> Connection.executeParameterizedQuery(
    @Language("SQL") query: String,
    setArgs: (PreparedStatement) -> Unit,
    handleResults: (ResultSet) -> T
): T = prepareStatement(query).use {
    setArgs(it)
    handleResults(it.executeQuery())
}

fun Connection.executeParameterizedUpdate(
    @Language("SQL") query: String,
    setArgs: (PreparedStatement) -> Unit
): Int = prepareStatement(query).use {
    setArgs(it)
    it.executeUpdate()
}

fun <T> Connection.executeParameterizedBatchUpdate(
    @Language("SQL") query: String,
    batchData: List<T>,
    setArgs: (T, PreparedStatement) -> Unit
): IntArray = prepareStatement(query).use { statement ->
    batchData.forEach {
        setArgs(it, statement)
        statement.addBatch()
    }
    statement.executeBatch()
}

fun Connection.executeCallableUpdate(
    @Language("SQL") query: String,
    setArgs: (PreparedStatement) -> Unit
): Int = prepareCall(query).use {
    setArgs(it)
    it.executeUpdate()
}

fun <T> ResultSet.map(parseRow: (ResultSet) -> T): List<T> = this.mapNotNull { parseRow(it) }

fun <T> ResultSet.mapNotNull(parseRow: (ResultSet) -> T?): List<T> {
    val list = mutableListOf<T>()
    while (this.next()) {
        parseRow(this)?.also { list.add(it) }
    }
    return list
}

fun ResultSet.getNullableDouble(columnLabel: String): Double? {
    return this.getObject(columnLabel) as Double?
}

fun ResultSet.getNullableFloat(columnLabel: String): Float? {
    return this.getNullableDouble(columnLabel)?.toFloat()
}

fun ResultSet.getNullableLong(columnLabel: String): Long? {
    return this.getObject(columnLabel) as Long?
}
