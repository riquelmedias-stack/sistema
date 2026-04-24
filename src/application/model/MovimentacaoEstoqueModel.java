package application.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.conexao;
import javafx.scene.control.Alert;

public class MovimentacaoEstoqueModel {
    private int id;
    private int idProduto;
    private int idUsuario;
    private String nomeProd;
    private String data;
    private int quantidade;
    private String tipo;

    // Construtor
    public MovimentacaoEstoqueModel(int id, int idProduto, int idUsuario,
                                    String nomeProd, String data,
                                    int quantidade, String tipo) {
        this.id = id;
        this.idProduto = idProduto;
        this.idUsuario = idUsuario;
        this.nomeProd = nomeProd;
        this.data = data;
        this.quantidade = quantidade;
        this.tipo = tipo;
    }

    // Getters
    public int getId() { return id; }
    public int getIdProduto() { return idProduto; }
    public int getIdUsuario() { return idUsuario; }
    public String getNomeProd() { return nomeProd; }
    public String getData() { return data; }
    public int getQuantidade() { return quantidade; }
    public String getTipo() { return tipo; }

    // Histórico completo (sem filtro de produto)
    public List<MovimentacaoEstoqueModel> HistoricoMovimentacaoTodos(LocalDate dataInicio, LocalDate dataFim) {
        List<MovimentacaoEstoqueModel> movimentacao = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(m.dataHora,'%d/%m/%y') AS data, " +
                     "m.id, m.idProduto, m.idUsuario, m.nomeProd, m.quantidade, " +
                     "(CASE WHEN m.tipo=0 THEN 'Entrada' " +
                     "      WHEN m.tipo=1 THEN 'Saida' " +
                     "      ELSE 'Não Informado' END) AS tipo " +
                     "FROM movimentacaoEstoque m " +
                     "WHERE m.dataHora BETWEEN ? AND ?";

        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement(sql)) {

            consulta.setDate(1, java.sql.Date.valueOf(dataInicio));
            consulta.setDate(2, java.sql.Date.valueOf(dataFim));

            ResultSet resultado = consulta.executeQuery();
            while (resultado.next()) {
                MovimentacaoEstoqueModel m = new MovimentacaoEstoqueModel(
                    resultado.getInt("id"),
                    resultado.getInt("idProduto"),
                    resultado.getInt("idUsuario"),
                    resultado.getString("nomeProd"),
                    resultado.getString("data"),
                    resultado.getInt("quantidade"),
                    resultado.getString("tipo")
                );
                movimentacao.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movimentacao;
    }
 // Inserir movimentação
    public void InsereMovimentacao() {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement(
                 "INSERT INTO movimentacaoEstoque (idProduto, idUsuario, nomeProd, dataHora, quantidade, tipo) " +
                 "VALUES (?,?,?,NOW(),?,?)")) {

            int tipoInt = 0;
            if (this.tipo != null && this.tipo.equalsIgnoreCase("Saida")) {
                tipoInt = 1;
            }

            consulta.setInt(1, this.idProduto);
            consulta.setInt(2, this.idUsuario);
            consulta.setString(3, this.nomeProd);
            consulta.setInt(4, this.quantidade);
            consulta.setInt(5, tipoInt);
            consulta.executeUpdate();

            Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
            mensagem.setContentText("Estoque Processado!");
            mensagem.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
