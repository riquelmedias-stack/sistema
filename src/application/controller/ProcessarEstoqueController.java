package application.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.conexao;
import application.model.ProdutoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ProcessarEstoqueController {

	@FXML private Button btnAtualizar;
    @FXML private Button btnBuscar;
    @FXML private Button btnHistorico;
    @FXML private Button btnMovimentacao;
    @FXML private Button btnProcessar;
    @FXML private TableView<ProdutoModel> tabProdutos;
    @FXML private TableColumn<ProdutoModel, Integer> colID;
    @FXML private TableColumn<ProdutoModel, String> colNome;
    @FXML private TableColumn<ProdutoModel, String> colCod;
    @FXML private TableColumn<ProdutoModel, String> colDescricao;
    @FXML private TableColumn<ProdutoModel, String> colCategoria;
    @FXML private TableColumn<ProdutoModel, Integer> colQtd;
    @FXML private ToggleGroup operacao;
    @FXML private RadioButton rbEntrada;
    @FXML private RadioButton rbSaida;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCod;
    @FXML private TextField txtEstoque;
    @FXML private TextField txtId;
    @FXML private TextField txtNome;
    private ObservableList<ProdutoModel> listaProdutos;@FXML
	private MenuItem itemClientes;
	@FXML
	private MenuItem itemProcessaEstoque;
	@FXML
	private MenuItem itemFrente;
	@FXML
	private MenuItem itemProdutos;
	@FXML
	private MenuItem itemSair;
  
    //CRIANDO O OBJETO
    ProdutoModel produto = new ProdutoModel(0, null, null, null, 0, 0, 0,null);
    
    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCod.setCellValueFactory(new PropertyValueFactory<>("cod"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        ListarProdutosTab(null);

        //Seleciona item na tabela de produtos 
        tabProdutos.getSelectionModel().selectedItemProperty().addListener(
            (obs, selecao , novaSelecao) -> {
                if (novaSelecao != null) {
                    produto = novaSelecao;
                    txtId.setText(String.format("%06d", produto.getId()));
                    txtNome.setText(produto.getNome());
                    txtCod.setText("" + produto.getCod());
                    txtEstoque.setText("0");
                }
            });

        btnProcessar.setOnAction(e -> {
            produto.setEstoque(Integer.parseInt(txtEstoque.getText()));
            RadioButton op = (RadioButton) operacao.getSelectedToggle();
            produto.ProcessaEstoque(op.getText());
            txtBuscar.clear();
            txtId.clear();
            txtCod.clear();
            txtNome.clear();
            txtEstoque.setText("0");
            ListarProdutosTab(null);
        });

        btnBuscar.setOnAction(e -> { Pesquisar(); });
        btnHistorico.setOnAction(e -> { Historico(); });
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
   //metodo para abrir tela de historico 
    public void Historico() {
    	//Data atual
    	LocalDate hoje = LocalDate.now();
    	//Primeiro dia do Mês
    	LocalDate primeiroDia=hoje.withDayOfMonth(1);
    	//Ultimo dia do Mês
    	LocalDate ultimoDia = hoje.withDayOfMonth(hoje.lengthOfMonth());
    	try {
    		FXMLLoader loader = new FXMLLoader(
    				getClass().getResource("/application/view/HistoricoProcessamento.fxml"));
    		Parent root = loader.load();
    		HistoricoController controller = loader.getController();
    		controller.carregarHistoricoCompleto(primeiroDia, ultimoDia);
    		Stage stage= new Stage();
    		stage.setScene(new Scene (root));
    		stage.show();
        } catch(Exception e) {
    		e.printStackTrace();
    	}
    }
  //METODO PARA BUSCAR O CADASTRO DO PRODUTO
    public void Pesquisar() {
    	//VERIFICA SE TEM TEXTO NO CAMPO DE BUSCA
    	//PARA PESQUISAR DE ACORDO COM O TEXTO DIGITADO
    	if(!txtBuscar.getText().isEmpty()) {    		
    		//executa o metodo de buscar
    		produto.Buscar(txtBuscar.getText());
    		ListarProdutosTab(txtBuscar.getText());
    		//informar os Valores nos campos do formulario
    		txtId.setText(String.format("%06d", produto.getId()));//Usa o Get Para buscar informação
    		txtNome.setText(produto.getNome()); 
    		txtCod.setText(""+produto.getCod());
    		txtEstoque.setText("0");   		
    	} else { // SENÃO BUSCA TODOS OS PRODUTOS
    		//BUSCA TODOS OS PRODUTOS
    		ListarProdutosTab(null);
    	}
    }
    
    public List<ProdutoModel> ListarProdutos(String Valor) {
		List <ProdutoModel> produtos = new ArrayList<ProdutoModel>();
		try(Connection conn = conexao.getConnection();
			PreparedStatement consulta = conn.prepareStatement(
					"select * from produto");
			PreparedStatement consultaWhere = conn.prepareStatement(
					"select * from produto where nome like ?  or descricao like?"
					+ " or categoria like ?")){
			ResultSet resultado=null;
			if(Valor == null) {
				 resultado=consulta.executeQuery();
			} else {
				consultaWhere.setString(1, "%"+Valor+"%");
				consultaWhere.setString(2, "%"+Valor+"%");
				consultaWhere.setString(3, "%"+Valor+"%");
				resultado=consultaWhere.executeQuery();
			}					
			
			while (resultado.next()) {
				ProdutoModel p = new ProdutoModel(
						resultado.getInt("id"),
						resultado.getString("nome"),
						resultado.getString("descricao"),
						resultado.getString("categoria"),
						resultado.getDouble("preco_custo"),
						resultado.getDouble("preco_venda"),
						resultado.getInt("estoque"), 
						resultado.getString("cod_barras")				
						);
				produtos.add(p);
			}	
		}catch(Exception e) {e.printStackTrace();}
		return produtos;
	}
    
    public void ListarProdutosTab(String Valor) {
    	List <ProdutoModel> produtos = produto.ListarProdutos(Valor);
    	listaProdutos=FXCollections.observableArrayList(produtos);
    	tabProdutos.setItems(listaProdutos);
    	
    }
}