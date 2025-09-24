package br.com.api.kaike.repositories

import br.com.api.kaike.database.DatabaseFactory
import br.com.api.kaike.models.Produto
import java.sql.SQLException
import java.sql.Statement

class ProdutoRepository {

    fun getAll(): List<Produto> {
        val produtos = mutableListOf<Produto>()
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM produtos ORDER BY id").use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        produtos.add(Produto(
                            id = rs.getInt("id"),
                            nome = rs.getString("nome"),
                            unidade = rs.getString("unidade"),
                            quantidade = rs.getDouble("quantidade"),
                            preco = rs.getDouble("preco")
                        ))
                    }
                }
            }
        }
        return produtos
    }

    fun getById(id: Int): Produto? {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM produtos WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return Produto(
                            id = rs.getInt("id"),
                            nome = rs.getString("nome"),
                            unidade = rs.getString("unidade"),
                            quantidade = rs.getDouble("quantidade"),
                            preco = rs.getDouble("preco")
                        )
                    }
                }
            }
        }
        return null
    }

    fun create(produto: Produto): Int {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(
                "INSERT INTO produtos (nome, unidade, quantidade, preco) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { stmt ->
                stmt.setString(1, produto.nome)
                stmt.setString(2, produto.unidade)
                stmt.setDouble(3, produto.quantidade)
                stmt.setDouble(4, produto.preco)
                stmt.executeUpdate()

                stmt.generatedKeys.use { rs ->
                    if (rs.next()) {
                        return rs.getInt(1)
                    }
                }
            }
        }
        throw SQLException("Falha ao obter ID do produto criado.")
    }

    fun update(id: Int, produto: Produto): Boolean {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(
                "UPDATE produtos SET nome = ?, unidade = ?, quantidade = ?, preco = ? WHERE id = ?"
            ).use { stmt ->
                stmt.setString(1, produto.nome)
                stmt.setString(2, produto.unidade)
                stmt.setDouble(3, produto.quantidade)
                stmt.setDouble(4, produto.preco)
                stmt.setInt(5, id)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun delete(id: Int): Boolean {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("DELETE FROM produtos WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                return stmt.executeUpdate() > 0
            }
        }
    }
}
