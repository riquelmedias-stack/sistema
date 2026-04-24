package application.controller;

import application.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuInicialController {

	@FXML
	private MenuItem itemClientes;
	@FXML
	private MenuItem itemProcessaEstoque;
	@FXML
	private MenuItem itemFrente;
	@FXML
	private MenuItem itemProdutos;
	@FXML
	private MenuItem itemSair;

	@FXML
	private void initialize() {
		itemProcessaEstoque.setOnAction(e -> {
			AbrirProcessaEstoque();
		});
		itemFrente.setOnAction(e -> {
			AbrirFrenteCaixa();
		});
	}

	public void Sair() {
		System.exit(0);
	}

	public void AbrirCadastroUsuario() {
		if (LoginController.tipoUsuario.equals("Gerente")) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/CadastroUsuarios.fxml"));
				Stage stage = new Stage();
				stage.setScene(new Scene(loader.load()));
				stage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert aviso = new Alert(Alert.AlertType.ERROR);
			aviso.setTitle("ERRO");
			aviso.setHeaderText(null);
			aviso.setContentText("Acesso Negado");
			aviso.showAndWait();
		}
	}

	public void AbrirCadastroClientes() {
		if (LoginController.tipoUsuario.equals("Gerente") || LoginController.tipoUsuario.equals("Vendedor")) {
			try {
				FXMLLoader loader = new FXMLLoader(
						getClass().getResource("/application/view/CadastroCpfClientes.fxml"));
				Stage stage = new Stage();
				stage.setScene(new Scene(loader.load()));
				stage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert aviso = new Alert(Alert.AlertType.ERROR);
			aviso.setTitle("ERRO");
			aviso.setHeaderText(null);
			aviso.setContentText("Acesso Negado");
			aviso.showAndWait();
		}
	}

	public void AbrirCadastroProdutos() {
		if (LoginController.tipoUsuario.equals("Gerente") || LoginController.tipoUsuario.equals("Vendedor")) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/CadastroProdutos.fxml"));
				Stage stage = new Stage();
				stage.setScene(new Scene(loader.load()));
				stage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void AbrirProcessaEstoque() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/ProcessarEstoque.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void AbrirFrenteCaixa() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Frentedecaixa.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}