package application.controller;

import java.text.NumberFormat;
import java.util.Locale;

import application.model.ProdutoModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroProdutosController {
	private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

	@FXML
	private Button btnCadastrar;
	@FXML
	private ChoiceBox<String> cbCategoria;
	@FXML
	private TextField txtDescricao;
	@FXML
	private TextField txtEstoque;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtPrecoCusto;
	@FXML
	private TextField txtPrecoVenda;
	@FXML
	private TextField txtLucro;
	@FXML
	private TextField txtCod;
	@FXML
	private Label lblSugerido;
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

	public Double lucro;
	ProdutoModel produto = new ProdutoModel(0, null, null, null, 0, 0, 0, null);

	@FXML
	public void initialize() {
		cbCategoria.getItems().addAll("Eletrônicos", "Cabos", "Iluminação");
		txtLucro.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.trim().isEmpty()) {
				lblSugerido.setText("Digite uma margem de lucro...");
			} else {
				try {
					lucro = Double.parseDouble(newValue);
					ValorSugerido(lucro);
				} catch (NumberFormatException e) {
					lblSugerido.setText("Valor inválido");
				}
			}
		});
		txtPrecoCusto.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.trim().isEmpty()) {
				lblSugerido.setText("Digite um preço de custo...");
			} else {
				if (lucro != null) {
					ValorSugerido(lucro);
				}
			}
		});
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

	public void Cadastrar() {
		if (txtNome.getText().isEmpty() || txtDescricao.getText().isEmpty() || txtPrecoCusto.getText().isEmpty()
				|| txtPrecoVenda.getText().isEmpty() || txtEstoque.getText().isEmpty()|| txtCod.getText().isEmpty()) {

			String erro = "";
			if (txtNome.getText().isEmpty())
				erro += "\nCampo Nome em Branco!";
			if (txtDescricao.getText().isEmpty())
				erro += "\nCampo Descrição em Branco!";
			if (txtPrecoCusto.getText().isEmpty())
				erro += "\nCampo Preço Custo em Branco!";
			if (txtPrecoVenda.getText().isEmpty())
				erro += "\nCampo Preço Venda em Branco!";
			if (txtEstoque.getText().isEmpty())
				erro += "\nCampo Estoque em Branco!";
			if (txtCod.getText().isEmpty())
				erro += "\nCampo Código de barras em Branco!";

			Alert mensagem = new Alert(Alert.AlertType.INFORMATION);
			mensagem.setContentText("Preencha os campos:" + erro);
			mensagem.showAndWait();

		} else {
			produto.setNome(txtNome.getText());
			produto.setDescricao(txtDescricao.getText());

			try {
			    NumberFormat nfEntrada = NumberFormat.getInstance(new Locale("pt", "BR"));
			    produto.setPrecocusto(nfEntrada.parse(txtPrecoCusto.getText()).doubleValue());
			    produto.setPrecovenda(nfEntrada.parse(txtPrecoVenda.getText()).doubleValue());
			} catch (Exception e) {
			    Alert mensagem = new Alert(Alert.AlertType.ERROR);
			    mensagem.setContentText("Preço inválido. Digite no formato real(R$)");
			    mensagem.showAndWait();
			    return;
			}


			try {
			    String estoque = txtEstoque.getText().trim();
			    if(Integer.parseInt(estoque)<0) { 
			    	Alert mensagem = new Alert(Alert.AlertType.ERROR);
			    	mensagem.setContentText("Estoque inválido. Digite valores positivos.");
				    mensagem.showAndWait();
				    return;
			    } else {
			    produto.setEstoque(Integer.parseInt(estoque));
			    }
			} catch (NumberFormatException e) {
			    Alert mensagem = new Alert(Alert.AlertType.ERROR);
			    mensagem.setContentText("Estoque inválido. Digite apenas números inteiros.");
			    mensagem.showAndWait();
			    return;
			}
		
			produto.setCod(txtCod.getText());
			produto.setCategoria(cbCategoria.getValue());
			produto.CadastroProduto();

			txtNome.setText("");
			txtDescricao.setText("");
			txtPrecoCusto.setText("");
			txtPrecoVenda.setText("");
			txtEstoque.setText("0");
			txtCod.setText("");
			cbCategoria.setValue("Eletrônicos");
		}
	}

	private void ValorSugerido(Double Valor) {
	    try {
	        NumberFormat nfEntrada = NumberFormat.getInstance(new Locale("pt", "BR"));
	        Double precoCusto = nfEntrada.parse(txtPrecoCusto.getText()).doubleValue();
	        Double sugestao = precoCusto + (precoCusto * (Valor / 100));
	        lblSugerido.setText("Valor Sugerido: " + nf.format(sugestao));
	    } catch (Exception e) {
	        lblSugerido.setText("Valor inválido");
	    }
	}

}