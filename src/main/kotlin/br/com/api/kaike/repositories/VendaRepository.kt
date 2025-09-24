package br.com.api.kaike.repositories

import br.com.api.kaike.database.DatabaseFactory
import br.com.api.kaike.models.ItemVenda
import br.com.api.kaike.models.Venda
import br.com.api.kaike.models.VendaCompleta
import java.sql.SQLException
import java.sql.Date // Importe a classe Date do SQL

class VendaRepository {

    fun getAll(): List<Venda> {
        val vendas = mutableListOf<Venda>()
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT id, data, cliente_id, total_venda FROM venda ORDER BY id").use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        vendas.add(Venda(
                            id = rs.getInt("id"),
                            data = rs.getString("data"),
                            cliente_id = rs.getInt("cliente_id"),
                            totalVenda = rs.getDouble("total_venda")
                        ))
                    }
                }
            }
        }
        return vendas
    }

    fun getById(id: Int): Venda? {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT id, data, cliente_id, total_venda FROM venda WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return Venda(
                            id = rs.getInt("id"),
                            data = rs.getString("data"),
                            cliente_id = rs.getInt("cliente_id"),
                            totalVenda = rs.getDouble("total_venda")
                        )
                    }
                }
            }
        }
        return null
    }

    fun create(venda: Venda, itens: List<ItemVenda>): Int {
        DatabaseFactory.getConnection().use { conn ->
            conn.autoCommit = false
            try {
                val vendaId = conn.prepareStatement(
                    "INSERT INTO venda (data, cliente_id, total_venda) VALUES (?, ?, ?) RETURNING id"
                ).use { stmt ->
                    stmt.setDate(1, Date.valueOf(venda.data))
                    stmt.setInt(2, venda.cliente_id)
                    stmt.setDouble(3, venda.totalVenda)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else throw SQLException("Falha ao criar venda")
                    }
                }

                itens.forEach { item ->
                    conn.prepareStatement(
                        "INSERT INTO itensvenda (id_venda, id_produto, quantidade, preco_unitario, total_item) VALUES (?, ?, ?, ?, ?)"
                    ).use { stmt ->
                        stmt.setInt(1, vendaId)
                        stmt.setInt(2, item.idProduto)
                        stmt.setDouble(3, item.quantidade)
                        stmt.setDouble(4, item.precoUnitario)
                        stmt.setDouble(5, item.totalItem)
                        stmt.executeUpdate()
                    }
                }

                conn.commit()
                return vendaId
            } catch (e: Exception) {
                conn.rollback()
                throw SQLException("Falha ao criar venda: ${e.message}")
            }
        }
    }

    fun delete(id: Int): Boolean {
        DatabaseFactory.getConnection().use { conn ->
            conn.autoCommit = false
            try {
                conn.prepareStatement("DELETE FROM itensvenda WHERE id_venda = ?").use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeUpdate()
                }
                val success = conn.prepareStatement("DELETE FROM venda WHERE id = ?").use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeUpdate() > 0
                }
                conn.commit()
                return success
            } catch(e: Exception) {
                conn.rollback()
                throw SQLException("Falha ao deletar venda: ${e.message}")
            }
        }
    }

    fun getItensByVendaId(vendaId: Int): List<ItemVenda> {
        val itens = mutableListOf<ItemVenda>()
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM itensvenda WHERE id_venda = ? ORDER BY id").use { stmt ->
                stmt.setInt(1, vendaId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        itens.add(ItemVenda(
                            id = rs.getInt("id"),
                            idVenda = rs.getInt("id_venda"),
                            idProduto = rs.getInt("id_produto"),
                            quantidade = rs.getDouble("quantidade"),
                            precoUnitario = rs.getDouble("preco_unitario"),
                            totalItem = rs.getDouble("total_item")
                        ))
                    }
                }
            }
        }
        return itens
    }

    fun getVendaCompleta(vendaId: Int): VendaCompleta? {
        val venda = getById(vendaId) ?: return null
        val itens = getItensByVendaId(vendaId)
        val cliente = ClienteRepository().getById(venda.cliente_id) ?: return null

        return VendaCompleta(venda, itens, cliente)
    }
}