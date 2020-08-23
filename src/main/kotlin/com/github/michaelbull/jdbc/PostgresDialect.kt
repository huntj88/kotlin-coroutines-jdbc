package com.github.michaelbull.jdbc

import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

fun Connection.postgresDialect(): PostgresConnection = PostgresConnection(this)

class PostgresConnection(private val connection: Connection): Connection by connection

fun <T> PostgresConnection.executeParameterizedQuery(
    @Language("PostgreSQL") query: String,
    setArgs: (PreparedStatement) -> Unit,
    handleResults: (ResultSet) -> T
): T = (this as Connection).executeParameterizedQuery(query, setArgs, handleResults)

fun PostgresConnection.executeParameterizedUpdate(
    @Language("PostgreSQL") query: String,
    setArgs: (PreparedStatement) -> Unit
): Int = (this as Connection).executeParameterizedUpdate(query, setArgs)

fun <T> PostgresConnection.executeParameterizedBatchUpdate(
    @Language("PostgreSQL") query: String,
    batchData: List<T>,
    setArgs: (T, PreparedStatement) -> Unit
): IntArray = (this as Connection).executeParameterizedBatchUpdate(query, batchData, setArgs)

fun PostgresConnection.executeCallableUpdate(
    @Language("PostgreSQL") query: String,
    setArgs: (PreparedStatement) -> Unit
): Int = (this as Connection).executeCallableUpdate(query, setArgs)
