package br.com.api.kaike.routes

import br.com.api.kaike.models.Cliente
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import br.com.api.kaike.repositories.ClienteRepository

// Classe de dados para respostas padronizadas
@Serializable
data class RespostaSimples(val id: Int? = null, val mensagem: String)

fun Routing.configureClienteRoutes() {
    val clienteRepository = ClienteRepository()

    route("/clientes") {
        get {
            try {
                val clientes = clienteRepository.getAll()
                call.respond(clientes)
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar clientes: ${e.message}")
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@get
            }

            try {
                val cliente = clienteRepository.getById(id)
                if (cliente == null) {
                    call.respond(HttpStatusCode.NotFound, "Cliente não encontrado")
                } else {
                    call.respond(cliente)
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar cliente: ${e.message}")
            }
        }

        get("/cpf/{cpf}") {
            val cpf = call.parameters["cpf"]
            if (cpf.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "CPF inválido")
                return@get
            }

            try {
                val cliente = clienteRepository.getByCpf(cpf)
                if (cliente == null) {
                    call.respond(HttpStatusCode.NotFound, "Cliente não encontrado")
                } else {
                    call.respond(cliente)
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao buscar cliente por CPF: ${e.message}")
            }
        }

        post {
            try {
                val cliente = call.receive<Cliente>()

                if (cliente.nome.isBlank() || cliente.cpf.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Nome e CPF são obrigatórios")
                    return@post
                }

                val clienteExistente = clienteRepository.getByCpf(cliente.cpf)
                if (clienteExistente != null) {
                    call.respond(HttpStatusCode.Conflict, "CPF já cadastrado")
                    return@post
                }

                val id = clienteRepository.create(cliente)
                // Usando a nova classe de resposta
                call.respond(HttpStatusCode.Created, RespostaSimples(id = id, mensagem = "Cliente criado com sucesso"))
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Formato de JSON inválido: ${e.message}")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao criar cliente: ${e.message}")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@put
            }

            try {
                val cliente = call.receive<Cliente>()
                if (cliente.nome.isBlank() || cliente.cpf.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Nome e CPF são obrigatórios")
                    return@put
                }

                val success = clienteRepository.update(id, cliente)
                if (success) {
                    // Usando a nova classe de resposta
                    call.respond(HttpStatusCode.OK, RespostaSimples(mensagem = "Cliente atualizado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Cliente não encontrado")
                }
            } catch (e: SerializationException) {
                call.respond(HttpStatusCode.BadRequest, "Formato de JSON inválido: ${e.message}")
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao atualizar cliente: ${e.message}")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }

            try {
                val success = clienteRepository.delete(id)
                if (success) {
                    // Usando a nova classe de resposta
                    call.respond(HttpStatusCode.OK, RespostaSimples(mensagem = "Cliente deletado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Cliente não encontrado")
                }
            } catch (e: Throwable) {
                call.respond(HttpStatusCode.InternalServerError, "Erro ao deletar cliente: ${e.message}")
            }
        }
    }
}

