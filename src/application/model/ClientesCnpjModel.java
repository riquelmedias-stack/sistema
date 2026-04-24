package application.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.conexao;
import application.controller.LoginController;
import javafx.scene.control.Alert;

public class ClientesCnpjModel {
	private int id;
	private String nome,email,cnpj,telefone,cep,atividade;

    // CONSTRUTOR
    public ClientesCnpjModel(int id, String nome, String email, String cnpj, String telefone, String cep,String atividade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.cep = cep;
        this.atividade = atividade;
    }

    // GETTERS 
    public int getId() { return this.id; }
    public String getNome() { return this.nome; }
    public String getEmail() { return this.email; }
    public String getCnpj() { return this.cnpj; }
    public String getTelefone() { return this.telefone; }
    public String getCep() {return this.cep;}
    public String getAtividade() { return this.atividade; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setCep(String cep) { this.cep = cep; }
    public void setAtividade(String atividade) { this.atividade = atividade; }
    
 // Cadastro
    public void CadastroCnpj() {
        try (Connection conn = conexao.getConnection();
             PreparedStatement insert = conn.prepareStatement(
                 "INSERT INTO clientes_cnpj (nome, email, cnpj, telefone, cep, atividade) VALUES (?,?,?,?,?,?)")) {
                insert.setString(1, this.nome);
                insert.setString(2, this.email);
                insert.setString(3, this.cnpj);
                insert.setString(4, this.telefone);
                insert.setString(5, this.cep);
                insert.setString(6, this.atividade);
                insert.executeUpdate();

                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Cliente Cadastrado!");
                mensagem.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // BUSCAR
    public void buscarCnpj(String valor) {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement(
                 "SELECT * FROM clientes_cnpj WHERE nome LIKE ? OR email LIKE ? OR cnpj LIKE ? OR telefone LIKE ? OR cep LIKE ?")) {

            consulta.setString(1, "%" + valor + "%");
            consulta.setString(2, "%" + valor + "%");
            consulta.setString(3, "%" + valor + "%");
            consulta.setString(4, "%" + valor + "%");
            consulta.setString(5, "%" + valor + "%");


            ResultSet resultado = consulta.executeQuery();
            if (resultado.next()) {
                this.id = resultado.getInt("id");
                this.nome = resultado.getString("nome");
                this.email = resultado.getString("email");
                this.cnpj = resultado.getString("cnpj");
                this.cep = resultado.getString("cep");
            } else {
                Alert mensagem = new Alert(Alert.AlertType.ERROR);
                mensagem.setContentText("Cliente não encontrado!");
                mensagem.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // EXCLUIR
    public void excluir() {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement("DELETE FROM clientes_cnpj WHERE id=?")) {

            if (this.id > 0) {
                consulta.setInt(1, this.id);
                consulta.executeUpdate();

                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Cliente Excluído!");
                mensagem.showAndWait();
            } else {
                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Cliente Não Localizado!");
                mensagem.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
