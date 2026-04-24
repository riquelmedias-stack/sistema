package application.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.conexao;
import application.controller.LoginController;
import javafx.scene.control.Alert;

public class ClientesCpfModel {
	private int id;
	private String nome,email,cpf,atividade,telefone,cep,data_nascimento;

    // CONSTRUTOR
    public ClientesCpfModel(int id, String nome, String email, String cpf, String atividade, String telefone, String cep, String data_nascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.atividade = atividade;
        this.telefone = telefone;
        this.cep = cep;
        this.data_nascimento = data_nascimento;
    }

    // GETTERS 
    public int getId() { return this.id; }
    public String getNome() { return this.nome; }
    public String getEmail() { return this.email; }
    public String getCpf() { return this.cpf; }
    public String getAtividade() { return this.atividade; }
    public String getTelefone() { return this.telefone; }
    public String getCep() {return this.cep;}
    public String getData() {return this.data_nascimento;}

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setAtividade(String atividade) { this.atividade = atividade; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setCep(String cep) { this.cep = cep; }
    public void setData(String data_nascimento) { this.data_nascimento = data_nascimento; }
    
 // Cadastro
    public void CadastroCpf() {
        try (Connection conn = conexao.getConnection();
             PreparedStatement insert = conn.prepareStatement(
                 "INSERT INTO clientes_cpf (nome, email, cpf, atividade, telefone, cep, data_nascimento) VALUES (?,?,?,?,?,?,?)")) {
                insert.setString(1, this.nome);
                insert.setString(2, this.email);
                insert.setString(3, this.cpf);
                insert.setString(4, this.atividade);
                insert.setString(5, this.telefone);
                insert.setString(6, this.cep);
                insert.setString(7, this.data_nascimento);
                insert.executeUpdate();

                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Cliente Cadastrado!");
                mensagem.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // BUSCAR
    public void buscarCpf(String valor) {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement(
                 "SELECT * FROM clientes_cpf WHERE nome LIKE ? OR email LIKE ? OR cpf LIKE ? OR atividade LIKE ? OR telefone LIKE ? OR cep LIKE ? OR data_nascimento LIKE ?")) {

            consulta.setString(1, "%" + valor + "%");
            consulta.setString(2, "%" + valor + "%");
            consulta.setString(3, "%" + valor + "%");
            consulta.setString(4, "%" + valor + "%");
            consulta.setString(5, "%" + valor + "%");
            consulta.setString(6, "%" + valor + "%");
            consulta.setString(7, "%" + valor + "%");


            ResultSet resultado = consulta.executeQuery();
            if (resultado.next()) {
                this.id = resultado.getInt("id");
                this.nome = resultado.getString("nome");
                this.email = resultado.getString("email");
                this.cpf = resultado.getString("cpf");
                this.atividade = resultado.getString("atividade");
                this.telefone = resultado.getString("telefone"); 
                this.cep = resultado.getString("cep");
                this.data_nascimento = resultado.getString("data_nascimento");
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
             PreparedStatement consulta = conn.prepareStatement("DELETE FROM clientes_cpf WHERE id=?")) {

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
