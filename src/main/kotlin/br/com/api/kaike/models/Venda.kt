package br.com.api.kaike.models

import kotlinx.serialization.Serializable

@Serializable
data class Venda(
    val id: Int? = null,
    val data: String,
    val cliente: Int, // Mantido como no original
    val totalVenda: Double
)

@Serializable
data class ItemVenda(
    val id: Int? = null,
    val idVenda: Int,
    val idProduto: Int,
    val quantidade: Double,
    val precoUnitario: Double,
    val totalItem: Double
)

@Serializable
data class VendaCompleta(
    val venda: Venda,
    val itens: List<ItemVenda>,
    val cliente: Cliente
)

@Serializable
data class VendaRequest(
    val venda: Venda,
    val itens: List<ItemVenda>
)
