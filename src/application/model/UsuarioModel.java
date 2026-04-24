package application.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.conexao;
import application.controller.LoginController;
import javafx.scene.control.Alert;

public class UsuarioModel {
	private int id;
    private String login;
    private String senha;
    private String tipo;

    // CONSTRUTOR
    public UsuarioModel(int id, String login, String senha, String tipo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    // GETTERS 
    public int getId() { return this.id; }
    public String getLogin() { return this.login; }
    public String getSenha() { return this.senha; }
    public String getTipo() { return this.tipo; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setLogin(String login) { this.login = login; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    // Cadastro
    public void Cadastro() {
        try (Connection conn = conexao.getConnection();
             PreparedStatement insert = conn.prepareStatement(
                 "INSERT INTO usuarios (login, senha, tipo) VALUES (?,?,?)")) {
                insert.setString(1, this.login);
                insert.setString(2, this.senha);
                insert.setString(3, this.tipo);
                insert.executeUpdate();

                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Login Cadastrado!");
                mensagem.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // BUSCAR
    public void buscar(String valor) {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement(
                 "SELECT * FROM produto WHERE descricao LIKE ? OR categoria LIKE ? OR nome LIKE ?")) {

            consulta.setString(1, "%" + valor + "%");
            consulta.setString(2, "%" + valor + "%");
            consulta.setString(3, "%" + valor + "%");

            ResultSet resultado = consulta.executeQuery();
            if (resultado.next()) {
                this.id = resultado.getInt("id");
                this.login = resultado.getString("login");
                this.senha = resultado.getString("senha");
                this.tipo = resultado.getString("tipo");
            } else {
                Alert mensagem = new Alert(Alert.AlertType.ERROR);
                mensagem.setContentText("Usuario não encontrado!");
                mensagem.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // EXCLUIR
    public void excluir() {
        try (Connection conn = conexao.getConnection();
             PreparedStatement consulta = conn.prepareStatement("DELETE FROM produto WHERE id=?")) {

            if (this.id > 0) {
                consulta.setInt(1, this.id);
                consulta.executeUpdate();

                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Produto Excluído!");
                mensagem.showAndWait();
            } else {
                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
                mensagem.setContentText("Produto Não Localizado!");
                mensagem.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  //autenticar usuario
    public boolean autenticar(String usuario, String senha) {
        try {
            Connection conn = conexao.getConnection();
            String sql = "SELECT * FROM usuarios WHERE BINARY login=? AND BINARY senha=?";
            PreparedStatement query = conn.prepareStatement(sql);
            query.setString(1, usuario);
            query.setString(2, senha);

            ResultSet resultado = query.executeQuery();
            if (resultado.next()) {
            	LoginController.tipoUsuario = resultado.getString("tipo");
            	LoginController.idUsuario = resultado.getInt("id");
                return true; 
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
