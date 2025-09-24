package br.com.api.kaike.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null

    fun init() {
        if (dataSource == null) {
            val config = HikariConfig().apply {
                jdbcUrl = DatabaseConfig.JDBC_URL
                username = DatabaseConfig.USERNAME
                password = DatabaseConfig.PASSWORD
                maximumPoolSize = DatabaseConfig.MAX_POOL_SIZE
                driverClassName = "org.postgresql.Driver"
                validate()
            }
            dataSource = HikariDataSource(config)
        }
    }

    fun getConnection(): Connection {
        return dataSource?.connection ?: throw IllegalStateException("O banco de dados não foi inicializado.")
    }
}