package com.github.michaelbull.jdbc

import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

fun Connection.mysqlDialect(): MySQLConnection = MySQLConnection(this)

class MySQLConnection(private val connection: Connection): Connection by connection

fun <T> MySQLConnection.executeParameterizedQuery(
    @Language("MySQL") query: String,
    setArgs: (PreparedStatement) -> Unit,
    handleResults: (ResultSet) -> T
): T = (this as Connection).executeParameterizedQuery(query, setArgs, handleResults)

fun MySQLConnection.executeParameterizedUpdate(
    @Language("MySQL") query: String,
    setArgs: (PreparedStatement) -> Unit
): Int = (this as Connection).executeParameterizedUpdate(query, setArgs)

fun <T> MySQLConnection.executeParameterizedBatchUpdate(
    @Language("MySQL") query: String,
    batchData: List<T>,
    setArgs: (T, PreparedStatement) -> Unit
): IntArray = (this as Connection).executeParameterizedBatchUpdate(query, batchData, setArgs)

fun MySQLConnection.executeCallableUpdate(
    @Language("MySQL") query: String,
    setArgs: (PreparedStatement) -> Unit
): Int = (this as Connection).executeCallableUpdate(query, setArgs)
