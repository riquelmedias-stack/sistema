package application.controller;

import application.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtLogin;      
    @FXML private PasswordField txtSenha;   
    @FXML private Button btnEntrar;   
    public static String tipoUsuario;
    public static int idUsuario;
    UsuarioModel user=new UsuarioModel(0, null, null, null);

    public void sair() {
        System.exit(0);
    }

    @FXML
    public void entrar() {
        try {
            String usuario = txtLogin.getText();
            String senha = txtSenha.getText();
            String tipo = null;
            
           
            if (user.autenticar(usuario,senha)) {
                Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
                aviso.setTitle("Confirmação");
                aviso.setHeaderText(null);
                aviso.setContentText("Bem-vindo ao Sistema " + usuario);
                aviso.showAndWait();
                txtLogin.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("/application/view/menu.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                Alert aviso = new Alert(Alert.AlertType.ERROR);
                aviso.setTitle("Erro");
                aviso.setHeaderText(null);
                aviso.setContentText("Usuário ou senha incorretos");
                aviso.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
