package application.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import application.conexao;

public class VendaModel {
    private int produtoId; // ID real do produto no banco (Foreign Key)
    private String cod_barras; // Apenas para visualização na Tabela
    private String descricao;  // Apenas para visualização na Tabela
    private double precoUnitario;
    private int quantidade;
    private double subtotal;

    // CONSTRUTOR ATUALIZADO
    public VendaModel(int produtoId, String cod_barras, String descricao, double precoUnitario, int quantidade, double subtotal) {
        this.produtoId = produtoId;
        this.cod_barras = cod_barras;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
    }

    // GETTERS
    public int getProdutoId() { return produtoId; }
    public String getCod_barras() { return cod_barras; }
    public String getDescricao() { return descricao; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidade() { return quantidade; }
    public double getSubtotal() { return subtotal; }

    // SETTERS
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public void setCod_barras(String cod_barras) { this.cod_barras = cod_barras; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public void setQuantidade(int quantidade) { 
        this.quantidade = quantidade; 
        calcularSubtotal();
    }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public void calcularSubtotal() {
        this.subtotal = this.precoUnitario * this.quantidade;
    }

    // MÉTODO CORRIGIDO: Agora recebe o ID da venda pai e insere os dados certos
    public void inserirItemVenda(int vendaId) {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement(
                 "INSERT INTO itens_venda_cpf (venda_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?,?,?,?,?)")) {

            consulta.setInt(1, vendaId);
            consulta.setInt(2, this.produtoId); // Grava o INT, não a String
            consulta.setInt(3, this.quantidade);
            consulta.setDouble(4, this.precoUnitario);
            consulta.setDouble(5, this.subtotal);
            consulta.executeUpdate();

            // Removi o Alert de sucesso daqui, porque num carrinho com 10 itens, 
            // você receberia 10 pop-ups seguidos. Deixe o Alert apenas para o final da venda toda.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}