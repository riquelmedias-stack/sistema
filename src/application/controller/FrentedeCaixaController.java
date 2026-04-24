package application.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import application.conexao;
import application.model.ClientesCpfModel;
import application.model.ProdutoModel;
import application.model.VendaModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class FrentedeCaixaController implements Initializable {

    // --- ELEMENTOS DO FXML ---
    @FXML private TextField txtNomeCliente;
    @FXML private Button btnBuscarCliente;
    
    @FXML private TextField txtNomeProduto;
    @FXML private Button btnBuscarProduto;
    @FXML private TextField txtQtdProduto;
    @FXML private Button btnAdicionarItem;
    @FXML private Button btnCancelarCompra;

    @FXML private TableView<VendaModel> tabelaCarrinho;
    @FXML private TableColumn<VendaModel, String> colCodigo;
    @FXML private TableColumn<VendaModel, String> colDescricao;
    @FXML private TableColumn<VendaModel, Integer> colQtd;
    @FXML private TableColumn<VendaModel, Double> colPrecoUnitario;
    @FXML private TableColumn<VendaModel, Double> colSubtotal;

    @FXML private ComboBox<String> cbPagamento1;
    @FXML private TextField txtValorPag1;
    @FXML private ComboBox<String> cbPagamento2;
    @FXML private TextField txtValorPag2;

    @FXML private TextField txtCodVendedor;
    @FXML private TextField txtDesconto;
    @FXML private Label lblTotalPedido;
    @FXML private Button btnFinalizarVenda;@FXML
	private MenuItem itemClientes;
	@FXML
	private MenuItem itemProcessaEstoque;
	@FXML
	private MenuItem itemFrente;
	@FXML
	private MenuItem itemProdutos;
	@FXML
	private MenuItem itemSair;

    // --- VARIÁVEIS GLOBAIS DO CAIXA ---
    private ObservableList<VendaModel> carrinho = FXCollections.observableArrayList();
    private ProdutoModel produtoSelecionado = null;
    private ClientesCpfModel clienteSelecionado = null;
    private double valorTotalFinal = 0.0; // Guarda o valor total com desconto para o cupom
    private double descontoAplicado = 0.0; // Guarda o desconto já autorizado
    private boolean validandoDesconto = false; // TRAVA DE SEGURANÇA: Evita loop infinito de janelas

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configurar as colunas da Tabela
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("cod_barras"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tabelaCarrinho.setItems(carrinho);

        // 2. Preencher os ComboBoxes de Pagamento
        ObservableList<String> formasPagamento = FXCollections.observableArrayList(
            "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "PIX"
        );
        cbPagamento1.setItems(formasPagamento);
        cbPagamento2.setItems(formasPagamento);

        // 3. Configurar as ações dos Botões
        btnBuscarCliente.setOnAction(e -> buscarCliente());
        btnBuscarProduto.setOnAction(e -> buscarProduto());
        btnAdicionarItem.setOnAction(e -> adicionarItem());
        btnFinalizarVenda.setOnAction(e -> finalizarVenda());
        btnCancelarCompra.setOnAction(e -> {
            // 1. Abre a janela pedindo o ID da venda
            TextInputDialog dialogId = new TextInputDialog();
            dialogId.setTitle("Cancelar Venda");
            dialogId.setHeaderText("Informe o ID da venda que deseja cancelar:");
            dialogId.setContentText("ID da Venda:");

            Optional<String> resultId = dialogId.showAndWait();
            
            // 2. Verifica se o usuário digitou algo e clicou em OK
            if (resultId.isPresent() && !resultId.get().trim().isEmpty()) {
                try {
                    // Tenta converter o que o usuário digitou para número inteiro
                    int idVenda = Integer.parseInt(resultId.get().trim());

                    // 3. Abre uma segunda janela pedindo o Motivo (necessário para a sua Regra de Negócio 03)
                    TextInputDialog dialogMotivo = new TextInputDialog();
                    dialogMotivo.setTitle("Motivo do Cancelamento");
                    dialogMotivo.setHeaderText("Por que a venda #" + idVenda + " está sendo cancelada?");
                    dialogMotivo.setContentText("Motivo:");

                    Optional<String> resultMotivo = dialogMotivo.showAndWait();
                    
                    // Se o usuário não digitar o motivo, coloca um padrão
                    String motivo = resultMotivo.isPresent() && !resultMotivo.get().trim().isEmpty() 
                                    ? resultMotivo.get() 
                                    : "Motivo não informado";

                    // 4. Finalmente, chama o seu método passando o ID e o Motivo!
                    cancelarVenda(idVenda, motivo);

                } catch (NumberFormatException ex) {
                    // Se o usuário digitar letras no lugar do ID, mostra um erro
                    mostrarAlerta(Alert.AlertType.ERROR, "ID inválido! Digite apenas números inteiros.");
                }
            }
        });        
        // 4. Configurar ações do Desconto (Evita loop infinito)
        txtDesconto.setOnAction(e -> validarEComputarDesconto()); // Dispara ao apertar ENTER
        txtDesconto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Dispara apenas quando o campo PERDE o foco
                validarEComputarDesconto();
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

    public void cancelarVenda(int idVendaParaCancelar, String motivoCancelamento) {
        String sqlBuscaItens = "SELECT produto_id, quantidade FROM itens_venda_cpf WHERE venda_id = ?";
        String sqlEstornoEstoque = "UPDATE produtos SET estoque = estoque + ? WHERE id = ?";
        String sqlRegistraMotivo = "UPDATE vendas_cpf SET status = 'CANCELADA', motivo_cancelamento = ? WHERE id = ?";
        
        try (Connection conn = conexao.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação segura

            try (PreparedStatement pstBusca = conn.prepareStatement(sqlBuscaItens);
                 PreparedStatement pstEstorno = conn.prepareStatement(sqlEstornoEstoque);
                 PreparedStatement pstMotivo = conn.prepareStatement(sqlRegistraMotivo)) {
                
                // 1. Registra o motivo do cancelamento na Venda
                pstMotivo.setString(1, motivoCancelamento);
                pstMotivo.setInt(2, idVendaParaCancelar);
                pstMotivo.executeUpdate();

                // 2. Busca os itens da venda
                pstBusca.setInt(1, idVendaParaCancelar);
                ResultSet rs = pstBusca.executeQuery();

                // 3. Devolve cada quantidade ao estoque (RN03)
                while (rs.next()) {
                    pstEstorno.setInt(1, rs.getInt("quantidade"));
                    pstEstorno.setInt(2, rs.getInt("produto_id"));
                    pstEstorno.executeUpdate();
                }

                conn.commit(); // Efetiva o cancelamento e a devolução no banco
                mostrarAlerta(Alert.AlertType.INFORMATION, "Venda cancelada e estoque estornado com sucesso!");

            } catch (Exception e) {
                conn.rollback(); // Se der erro, desfaz tudo e o estoque não bagunça
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cancelar venda: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void buscarCliente() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Cliente");
        dialog.setHeaderText("Digite o CPF, Nome ou Email do cliente:");
        dialog.setContentText("Busca:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            ClientesCpfModel busca = new ClientesCpfModel(0, "", "", "", "", "", "", "");
            busca.buscarCpf(result.get());

            if (busca.getId() > 0) {
                this.clienteSelecionado = busca;
                txtNomeCliente.setText(clienteSelecionado.getNome());
            } else {
                this.clienteSelecionado = null;
                txtNomeCliente.setText("");
            }
        }
    }

    private void buscarProduto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Produto");
        dialog.setHeaderText("Digite o Código de Barras ou Nome:");
        dialog.setContentText("Busca:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            ProdutoModel produto = new ProdutoModel(0, null, null, null, 0, 0, 0, null); 
            produto.Buscar(result.get()); 

            if (produto.getId() > 0) {
                this.produtoSelecionado = produto;
                txtNomeProduto.setText(produto.getNome());
                txtQtdProduto.setText("1");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Produto não encontrado!");
                this.produtoSelecionado = null;
                txtNomeProduto.setText("");
            }
        }
    }

    private void adicionarItem() {
        try {
            if (produtoSelecionado == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Por favor, busque um produto antes de adicionar!");
                return;
            }

            int quantidadeSolicitada = Integer.parseInt(txtQtdProduto.getText());
            int estoqueAtual = produtoSelecionado.getEstoque(); // Pegando o estoque que o seu model carregou

            // Calcula quantos itens desse mesmo produto já estão no carrinho
            int qtdJaNoCarrinho = 0;
            for (VendaModel item : carrinho) {
                if (item.getCod_barras().equals(produtoSelecionado.getCod())) {
                    qtdJaNoCarrinho += item.getQuantidade();
                }
            }

            // --- VALIDAÇÃO RN02: BLOQUEIO SEM ESTOQUE ---
            if (estoqueAtual <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Produto esgotado! Estoque atual é zero.");
                return; // Bloqueia!
            }

            if ((qtdJaNoCarrinho + quantidadeSolicitada) > estoqueAtual) {
                mostrarAlerta(Alert.AlertType.ERROR, "Quantidade indisponível! \nEstoque na loja: " + estoqueAtual + 
                                                     "\nJá no carrinho: " + qtdJaNoCarrinho);
                return; // Bloqueia!
            }
            // --- FIM VALIDAÇÃO RN02 ---

            int idProduto = produtoSelecionado.getId(); 
            String codBarras = produtoSelecionado.getCod(); 
            String descricao = produtoSelecionado.getNome();
            double precoUnitario = produtoSelecionado.getPrecovenda(); 
            double subtotal = precoUnitario * quantidadeSolicitada;

            VendaModel item = new VendaModel(idProduto, codBarras, descricao, precoUnitario, quantidadeSolicitada, subtotal);
            carrinho.add(item);

            atualizarTotal();

            produtoSelecionado = null;
            txtNomeProduto.setText("");
            txtQtdProduto.setText("1");

        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Quantidade inválida. Digite apenas números.");
        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao adicionar item: " + ex.getMessage());
        }
    }

    private void validarEComputarDesconto() {
        // SE JÁ ESTIVER VALIDANDO, ABORTA (Isso quebra o loop infinito!)
        if (validandoDesconto) {
            return;
        }

        validandoDesconto = true; // Trava a execução

        try {
            double descontoDigitado = 0.0;
            try {
                if (!txtDesconto.getText().trim().isEmpty()) {
                    descontoDigitado = Double.parseDouble(txtDesconto.getText().replace(",", "."));
                }
            } catch (NumberFormatException e) {
                descontoDigitado = 0.0;
            }

            // Só pede a senha se o desconto for maior que 5 E se for diferente do que já está aplicado
            if (descontoDigitado > 5.0 && descontoDigitado != descontoAplicado) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Autorização de Gerente");
                dialog.setHeaderText("Desconto de " + descontoDigitado + "% exige senha de gerente.");
                dialog.setContentText("Senha:");

                Optional<String> result = dialog.showAndWait();
                
                // A SENHA ESTÁ MOCKADA COMO 'admin123' PARA EXEMPLO
                if (result.isPresent() && result.get().equals("1234")) {
                    descontoAplicado = descontoDigitado; // Autorizado!
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Senha incorreta ou cancelada. Desconto revertido para 5%.");
                    descontoAplicado = 5.0;
                    txtDesconto.setText("5");
                }
            } else if (descontoDigitado <= 5.0) {
                descontoAplicado = descontoDigitado; // Até 5% passa direto
            }

            // Após validar, chama a atualização visual
            atualizarTotal();

        } finally {
            validandoDesconto = false; // Destrava a execução no final de tudo, aconteça o que acontecer
        }
    }

    private void atualizarTotal() {
        double totalBruto = 0.0;
        
        // Soma os subtotais do carrinho
        for (VendaModel item : carrinho) {
            totalBruto += item.getSubtotal();
        }

        // Aplica o desconto autorizado
        valorTotalFinal = totalBruto - (totalBruto * (descontoAplicado / 100));

        // Formata e exibe na Label e na sugestão de Pagamento 1
        lblTotalPedido.setText(String.format("R$ %.2f", valorTotalFinal));
        txtValorPag1.setText(String.format("%.2f", valorTotalFinal).replace(",", "."));
    }

    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "O carrinho está vazio!");
            return;
        }

        // --- CÁLCULO DE PAGAMENTOS E TROCO ---
        double valorPag1 = lerValorCampo(txtValorPag1);
        double valorPag2 = lerValorCampo(txtValorPag2);
        double totalPago = valorPag1 + valorPag2;

        if (totalPago < valorTotalFinal) {
            mostrarAlerta(Alert.AlertType.ERROR, String.format("Valor pago (R$ %.2f) é menor que o total da venda (R$ %.2f)!", totalPago, valorTotalFinal));
            return;
        }

        double troco = totalPago - valorTotalFinal;
        
        // Verifica se gerou troco mas não tem dinheiro envolvido
        if (troco > 0) {
            boolean temDinheiro = "Dinheiro".equals(cbPagamento1.getValue()) || "Dinheiro".equals(cbPagamento2.getValue());
            if (!temDinheiro) {
                mostrarAlerta(Alert.AlertType.WARNING, "Há troco a ser devolvido, mas nenhuma das formas de pagamento é Dinheiro. Verifique os valores.");
            }
        }

        int idVendaGerada = 0;
        int idCliente = (clienteSelecionado != null) ? clienteSelecionado.getId() : 1; 
        int idUsuario = 1; 

        // --- SALVA A VENDA NO BANCO ---
        String sqlVenda = "INSERT INTO vendas_cpf (cliente_id, usuario_id, valor_total) VALUES (?, ?, ?)";
        
        try (Connection conn = conexao.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idUsuario);
            pstmt.setDouble(3, valorTotalFinal);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                idVendaGerada = rs.getInt(1);
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao registrar a venda: " + e.getMessage());
            e.printStackTrace();
            return; 
        }

     // --- SALVA OS ITENS, GERA COMPROVANTE E DÁ BAIXA NO ESTOQUE ---
        if (idVendaGerada > 0) {
            for (VendaModel item : carrinho) {
                item.inserirItemVenda(idVendaGerada); 

                // --- APLICAÇÃO DA RN01 ---
                // Cria um produto apenas para acionar o seu método de baixa
                ProdutoModel produtoBaixa = new ProdutoModel(0, null, null, null, 0, 0, 0, null);
                produtoBaixa.setId(item.getProdutoId());
                produtoBaixa.setNome(item.getDescricao());
                // O SEU método ProcessaEstoque usa o 'this.estoque' como a QUANTIDADE a ser alterada
                produtoBaixa.setEstoque(item.getQuantidade()); 
                
                produtoBaixa.ProcessaEstoque("Saida"); // Faz o update no banco e gera a MovimentacaoEstoqueModel!
            }

            // Gera o Cupom Não Fiscal na tela
            gerarCupomNaoFiscal(idVendaGerada, troco, valorPag1, valorPag2);
            // ... (resto do seu código de limpar tela)

            // Limpa a tela e reseta as variáveis
            carrinho.clear();
            clienteSelecionado = null;
            txtNomeCliente.setText("");
            txtDesconto.setText("0");
            descontoAplicado = 0.0;
            txtValorPag1.setText("");
            txtValorPag2.setText("");
            cbPagamento1.getSelectionModel().clearSelection();
            cbPagamento2.getSelectionModel().clearSelection();
            atualizarTotal();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro: Não foi possível gerar o ID da venda.");
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private double lerValorCampo(TextField campo) {
        if (campo == null || campo.getText().trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(campo.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.showAndWait();
    }

    private void gerarCupomNaoFiscal(int idVenda, double troco, double valorPag1, double valorPag2) {
        StringBuilder cupom = new StringBuilder();
        cupom.append("--------------------------------------------------\n");
        cupom.append("              CUPOM NÃO FISCAL              \n");
        cupom.append("--------------------------------------------------\n");
        cupom.append(String.format("Venda Nº: %06d\n", idVenda));
        cupom.append("Cliente: ").append(clienteSelecionado != null ? clienteSelecionado.getNome() : "Consumidor Final").append("\n");
        cupom.append("--------------------------------------------------\n");
        cupom.append(String.format("%-4s | %-20s | %-5s | %-8s\n", "QTD", "DESCRIÇÃO", "UN(R$)", "SUB(R$)"));
        
        double subtotalBruto = 0.0;
        for (VendaModel item : carrinho) {
            cupom.append(String.format("%-4d | %-20s | %-5.2f | %-8.2f\n", 
                item.getQuantidade(), 
                cortarString(item.getDescricao(), 20), 
                item.getPrecoUnitario(), 
                item.getSubtotal()));
            subtotalBruto += item.getSubtotal();
        }
        
        cupom.append("--------------------------------------------------\n");
        cupom.append(String.format("SUBTOTAL: R$ %.2f\n", subtotalBruto));
        
        double descontoCalc = subtotalBruto - valorTotalFinal;
        if (descontoCalc > 0) {
            cupom.append(String.format("DESCONTO: R$ %.2f\n", descontoCalc));
        }
        
        cupom.append(String.format("TOTAL A PAGAR: R$ %.2f\n", valorTotalFinal));
        cupom.append("--------------------------------------------------\n");
        
        if (cbPagamento1.getValue() != null && valorPag1 > 0) {
            cupom.append(String.format("PAG 1 (%s): R$ %.2f\n", cbPagamento1.getValue(), valorPag1));
        }
        if (cbPagamento2.getValue() != null && valorPag2 > 0) {
            cupom.append(String.format("PAG 2 (%s): R$ %.2f\n", cbPagamento2.getValue(), valorPag2));
        }
        
        cupom.append(String.format("TROCO: R$ %.2f\n", troco));
        cupom.append("--------------------------------------------------\n");
        cupom.append("           OBRIGADO PELA PREFERÊNCIA!             \n");

        // Mostra o cupom em um Alert customizado com TextArea
        Alert alertCupom = new Alert(Alert.AlertType.INFORMATION);
        alertCupom.setTitle("Venda Finalizada");
        alertCupom.setHeaderText("Venda registrada com sucesso!");

        TextArea textArea = new TextArea(cupom.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'monospaced';"); // Mantém o alinhamento da string

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alertCupom.getDialogPane().setContent(expContent);
        alertCupom.showAndWait();
    }
    
    // Corta strings longas para não quebrar a formatação do cupom
    private String cortarString(String texto, int tamanho) {
        if (texto.length() <= tamanho) return texto;
        return texto.substring(0, tamanho);
    }
}