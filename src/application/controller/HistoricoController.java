package application.controller;

import java.time.LocalDate;
import java.util.List;

import application.model.MovimentacaoEstoqueModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class HistoricoController {

    @FXML private Button btnBuscar;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colData;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colID;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colUsuario;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colIdProd;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colNome;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colQuantidade;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colTipo;
    @FXML private DatePicker dataFinal;
    @FXML private DatePicker dataInicio;
    @FXML private Label lblProd;
    @FXML private TableView<MovimentacaoEstoqueModel> tabHistorico;
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
    private ObservableList<MovimentacaoEstoqueModel> listaMovimentacao;
    private LocalDate hoje, primeiroDia, ultimoDia;

    private MovimentacaoEstoqueModel movimentacao = new MovimentacaoEstoqueModel(
            0, 0, 0, null, null, 0, null);

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdProd.setCellValueFactory(new PropertyValueFactory<>("idProduto"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeProd"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        hoje = LocalDate.now();
        primeiroDia = hoje.withDayOfMonth(1);
        ultimoDia = hoje.withDayOfMonth(hoje.lengthOfMonth());

        dataInicio.setValue(primeiroDia);
        dataFinal.setValue(ultimoDia);

        // ✅ Carrega todo o histórico ao abrir a tela
        carregarHistoricoCompleto(primeiroDia, ultimoDia);

        // Botão buscar aplica filtro por produto
        btnBuscar.setOnAction(e -> carregarHistoricoCompleto(primeiroDia, ultimoDia)
        );
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

    public void carregarHistoricoCompleto(LocalDate inicio, LocalDate fim) {
        List<MovimentacaoEstoqueModel> listaHistorico =
                movimentacao.HistoricoMovimentacaoTodos(inicio, fim);

        listaMovimentacao = FXCollections.observableArrayList(listaHistorico);
        tabHistorico.setItems(listaMovimentacao);

        if (listaHistorico != null && !listaHistorico.isEmpty()) {
            lblProd.setText("Histórico completo (" + listaHistorico.size() + " registros)");
        } else {
            lblProd.setText("Nenhum registro encontrado");
        }
    }
}
