package application.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.model.ClientesCpfModel;
import application.model.UsuarioModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class CadastroClientesCpfController {

    @FXML private Button btnCadastrar;
    @FXML private RadioButton rbCnpj;
    @FXML private RadioButton rbCpf;
    @FXML private ToggleGroup rbtipo;
    @FXML private TextField txtCpf;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCep;
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone; 
    @FXML private DatePicker dpData;@FXML
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
    ClientesCpfModel cliente = new ClientesCpfModel(0, null, null, null, null, null, null, null);
    public void Cadastrar() {
    	LocalDate dataSelecionada = dpData.getValue();
		String Data = dataSelecionada.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	if(txtCpf.getText().isEmpty() || txtNome.getText().isEmpty() || txtTelefone.getText().isEmpty()|| Data.isEmpty()|| txtCep.getText().isEmpty()|| txtEmail.getText().isEmpty()) {
    		String erro="";
    		if(txtCpf.getText().isEmpty()) {erro=erro+"\nCampo CPF em Branco!";}
    		if(txtNome.getText().isEmpty()) {erro=erro+"\nCampo Nome em Branco!";}
    		if(txtTelefone.getText().isEmpty()) {erro=erro+"\nCampo Telefone em Branco!";}
    		if(txtEmail.getText().isEmpty()) {erro=erro+"\nCampo Email em Branco!";}
    		if(Data.isEmpty()) {erro=erro+"\nCampo Data de Nascimento em Branco!";}
    		if(txtCep.getText().isEmpty()) {erro=erro+"\nCampo CEP em Branco!";}
    		Alert mensagem = new Alert(Alert.AlertType.INFORMATION);
			mensagem.setContentText("Preenchaos campos:"+erro);
			mensagem.showAndWait();
    	} else {
    		//INCLUIR AS INFORMAÇÕES NO OBJETO PRODUTO
        	cliente.setNome(txtNome.getText());
        	cliente.setCpf(txtCpf.getText());
        	cliente.setEmail(txtEmail.getText());
        	cliente.setCep(txtCep.getText());
        	cliente.setData(Data);
        	cliente.setTelefone(txtTelefone.getText());
        	cliente.setAtividade("Ativo");
        	cliente.CadastroCpf();

    		txtCpf.setText("");
    		txtNome.setText("");
    		txtEmail.setText("");
    		txtCep.setText("");
    		txtTelefone.setText("");
    		dpData.setValue(null);
    	}    	
    }
    public void TelaCnpj() {
    	try {
    	txtNome.getScene().getWindow().hide();
        Parent root = FXMLLoader.load(getClass().getResource("/application/view/CadastroCnpjClientes.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    	}catch (Exception e) {
            e.printStackTrace();
        }
    }
}
