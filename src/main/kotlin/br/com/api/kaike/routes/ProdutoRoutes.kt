package br.com.api.kaike.routes

import br.com.api.kaike.models.Produto
import br.com.api.kaike.repositories.ProdutoRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException

fun Routing.configureProdutoRoutes() {
    val produtoRepository = ProdutoRepository()

    route("/produtos") {
        get {
            try {
                val produtos = produtoRepository.getAll()
                call.respond(produtos)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar produtos: ${e.message}")
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            try {
                val produto = produtoRepository.getById(id)
                if (produto == null) {
                    call.respond(HttpStatusCode.NotFound, "Produto não encontrado")
                } else {
                    call.respond(produto)
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar produto: ${e.message}")
            }
        }

        post {
            try {
                val produto = call.receive<Produto>()
                if (produto.nome.isBlank() || produto.unidade.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Nome e unidade são obrigatórios")
                    return@post
                }

                val id = produtoRepository.create(produto)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Produto criado com sucesso"))
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Formato de JSON inválido: ${e.message}")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao criar produto: ${e.message}")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }

            try {
                val produto = call.receive<Produto>()
                if (produto.nome.isBlank() || produto.unidade.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Nome e unidade são obrigatórios")
                    return@put
                }

                val success = produtoRepository.update(id, produto)
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Produto atualizado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Produto não encontrado")
                }
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Formato de JSON inválido: ${e.message}")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar produto: ${e.message}")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }

            try {
                val success = produtoRepository.delete(id)
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Produto deletado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Produto não encontrado")
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar produto: ${e.message}")
            }
        }
    }
}
