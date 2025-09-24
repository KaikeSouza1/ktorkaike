package br.com.api.kaike.repositories

import br.com.api.kaike.database.DatabaseFactory
import br.com.api.kaike.models.Cliente
import java.sql.SQLException
import java.sql.Statement

class ClienteRepository {

    fun getAll(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM clientes ORDER BY id").use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        clientes.add(Cliente(
                            id = rs.getInt("id"),
                            cpf = rs.getString("cpf"),
                            nome = rs.getString("nome"),
                            rua = rs.getString("rua"),
                            bairro = rs.getString("bairro"),
                            cidade = rs.getString("cidade"),
                            estado = rs.getString("estado"),
                            uf = rs.getString("uf"),
                            telefone = rs.getString("telefone"),
                            email = rs.getString("email")
                        ))
                    }
                }
            }
        }
        return clientes
    }

    fun getById(id: Int): Cliente? {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM clientes WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return Cliente(
                            id = rs.getInt("id"),
                            cpf = rs.getString("cpf"),
                            nome = rs.getString("nome"),
                            rua = rs.getString("rua"),
                            bairro = rs.getString("bairro"),
                            cidade = rs.getString("cidade"),
                            estado = rs.getString("estado"),
                            uf = rs.getString("uf"),
                            telefone = rs.getString("telefone"),
                            email = rs.getString("email")
                        )
                    }
                }
            }
        }
        return null
    }

    fun create(cliente: Cliente): Int {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(
                "INSERT INTO clientes (cpf, nome, rua, bairro, cidade, estado, uf, telefone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { stmt ->
                stmt.setString(1, cliente.cpf)
                stmt.setString(2, cliente.nome)
                stmt.setString(3, cliente.rua)
                stmt.setString(4, cliente.bairro)
                stmt.setString(5, cliente.cidade)
                stmt.setString(6, cliente.estado)
                stmt.setString(7, cliente.uf)
                stmt.setString(8, cliente.telefone)
                stmt.setString(9, cliente.email)
                stmt.executeUpdate()

                stmt.generatedKeys.use { rs ->
                    if (rs.next()) {
                        return rs.getInt(1)
                    }
                }
            }
        }
        throw SQLException("Falha ao obter ID do cliente criado.")
    }

    fun update(id: Int, cliente: Cliente): Boolean {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement(
                "UPDATE clientes SET cpf = ?, nome = ?, rua = ?, bairro = ?, cidade = ?, estado = ?, uf = ?, telefone = ?, email = ? WHERE id = ?"
            ).use { stmt ->
                stmt.setString(1, cliente.cpf)
                stmt.setString(2, cliente.nome)
                stmt.setString(3, cliente.rua)
                stmt.setString(4, cliente.bairro)
                stmt.setString(5, cliente.cidade)
                stmt.setString(6, cliente.estado)
                stmt.setString(7, cliente.uf)
                stmt.setString(8, cliente.telefone)
                stmt.setString(9, cliente.email)
                stmt.setInt(10, id)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun delete(id: Int): Boolean {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("DELETE FROM clientes WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                return stmt.executeUpdate() > 0
            }
        }
    }

    fun getByCpf(cpf: String): Cliente? {
        DatabaseFactory.getConnection().use { conn ->
            conn.prepareStatement("SELECT * FROM clientes WHERE cpf = ?").use { stmt ->
                stmt.setString(1, cpf)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        return Cliente(
                            id = rs.getInt("id"),
                            cpf = rs.getString("cpf"),
                            nome = rs.getString("nome"),
                            rua = rs.getString("rua"),
                            bairro = rs.getString("bairro"),
                            cidade = rs.getString("cidade"),
                            estado = rs.getString("estado"),
                            uf = rs.getString("uf"),
                            telefone = rs.getString("telefone"),
                            email = rs.getString("email")
                        )
                    }
                }
            }
        }
        return null
    }
}
