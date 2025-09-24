package br.com.api.kaike

import br.com.api.kaike.database.DatabaseFactory
import br.com.api.kaike.routes.configureClienteRoutes
import br.com.api.kaike.routes.configureProdutoRoutes
import br.com.api.kaike.routes.configureVendaRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, "Erro interno no servidor: ${cause.localizedMessage}")
        }
    }

    routing {
        configureClienteRoutes()
        configureProdutoRoutes()
        configureVendaRoutes()
        get("/") {
            call.respond(mapOf("status" to "Funcionando!"))
        }
    }
}
