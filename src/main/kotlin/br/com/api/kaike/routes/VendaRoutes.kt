package br.com.api.kaike.routes

import br.com.api.kaike.models.VendaRequest
import br.com.api.kaike.repositories.VendaRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable // 1. IMPORT ADICIONADO

// 2. CLASSE DE RESPOSTA ADICIONADA AQUI
@Serializable
data class VendaResponse(val id: Int, val message: String)

fun Routing.configureVendaRoutes() {
    val vendaRepository = VendaRepository()

    route("/vendas") {

        post {
            println("--- ROTA POST /vendas ACIONADA ---")
            try {
                // 1. Lemos o corpo da requisição como texto puro.
                val requestBodyAsText = call.receiveText()
                println(">>> JSON Recebido: $requestBodyAsText")

                // 2. Criamos o nosso próprio parser de JSON, ignorando o do Ktor.
                val jsonParser = Json { ignoreUnknownKeys = true }

                // 3. Forçamos a conversão do texto para o nosso objeto VendaRequest.
                val vendaRequest = jsonParser.decodeFromString<VendaRequest>(requestBodyAsText)
                println(">>> Objeto Deserializado com Sucesso: $vendaRequest")

                // 4. Verificamos os dados e chamamos o repositório.
                if (vendaRequest.venda.cliente_id <= 0 || vendaRequest.itens.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, "ID do cliente e lista de itens são obrigatórios")
                    return@post
                }

                val id = vendaRepository.create(vendaRequest.venda, vendaRequest.itens)
                println("--- VENDA CRIADA COM SUCESSO, ID: $id ---")

                // 3. LINHA PROBLEMÁTICA SUBSTITUÍDA POR ESTAS DUAS
                val response = VendaResponse(id = id, message = "Venda criada com sucesso")
                call.respond(HttpStatusCode.Created, response)

            } catch (e: Exception) {
                // Se algo falhar, imprimimos o erro detalhado na consola.
                println("!!!!!!!!!!!!!!!!! ERRO !!!!!!!!!!!!!!!!!!")
                e.printStackTrace()
                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                call.respond(HttpStatusCode.InternalServerError, "Erro detalhado no servidor: ${e.message}")
            }
        }

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

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido")
                return@delete
            }
            try {
                val success = vendaRepository.delete(id)
                if (success) {
                    // A MESMA LÓGICA DE RESPOSTA PODE SER APLICADA AQUI SE QUISER
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