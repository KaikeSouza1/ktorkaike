package br.com.api.kaike.routes

import br.com.api.kaike.models.VendaRequest
import br.com.api.kaike.repositories.VendaRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException

fun Routing.configureVendaRoutes() {
    val vendaRepository = VendaRepository()

    route("/vendas") {
        get {
            try {
                val vendas = vendaRepository.getAll()
                call.respond(vendas)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar vendas: ${e.message}")
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            try {
                val venda = vendaRepository.getById(id)
                if (venda == null) {
                    call.respond(HttpStatusCode.NotFound, "Venda não encontrada")
                } else {
                    call.respond(venda)
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar venda: ${e.message}")
            }
        }

        get("/completa/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            try {
                val vendaCompleta = vendaRepository.getVendaCompleta(id)
                if (vendaCompleta == null) {
                    call.respond(HttpStatusCode.NotFound, "Venda não encontrada")
                } else {
                    call.respond(vendaCompleta)
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar detalhes da venda: ${e.message}")
            }
        }

        post {
            try {
                val vendaRequest = call.receive<VendaRequest>()
                if (vendaRequest.venda.cliente <= 0 || vendaRequest.itens.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "ID do cliente e lista de itens são obrigatórios")
                    return@post
                }

                val id = vendaRepository.create(vendaRequest.venda, vendaRequest.itens)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Venda criada com sucesso"))
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Formato de JSON inválido: ${e.message}")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao criar venda: ${e.message}")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }

            try {
                val success = vendaRepository.delete(id)
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Venda deletada com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Venda não encontrada")
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar venda: ${e.message}")
            }
        }
    }
}
