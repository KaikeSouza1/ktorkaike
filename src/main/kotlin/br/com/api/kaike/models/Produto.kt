package br.com.api.kaike.models

import kotlinx.serialization.Serializable

@Serializable
data class Produto(
    val id: Int? = null,
    val nome: String,
    val unidade: String,
    val quantidade: Double,
    val preco: Double
)
