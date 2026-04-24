package application.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import application.conexao;
import application.controller.LoginController;
import javafx.scene.control.Alert;

public class ProdutoModel {
		private int id;
	    private String nome;
	    private String descricao;
	    private String categoria;
	    private double preco_custo;
	    private double preco_venda;
	    private int estoque;
	    private String cod_barras;

	    // CONSTRUTOR
	    public ProdutoModel(int id, String nome, String descricao, String categoria,double preco_custo,double preco_venda,int estoque,String cod_barras) {
	        this.id = id;
	        this.nome = nome;
	        this.descricao = descricao;
	        this.categoria = categoria;
	        this.preco_custo = preco_custo;
	        this.preco_venda = preco_venda;
	        this.estoque = estoque;
	        this.cod_barras = cod_barras;
	    }

	    // GETTERS 
	    public int getId() { return this.id; }
	    public String getNome() { return this.nome; }
	    public String getDescricao() { return this.descricao; }
	    public String getCategoria() { return this.categoria; }
	    public double getPrecocusto() { return this.preco_custo; }
	    public double getPrecovenda() { return this.preco_venda; }
	    public int getEstoque() { return this.estoque; }
	    public String getCod() {return this.cod_barras;}

	    // SETTERS
	    public void setId(int id) { this.id = id; }
	    public void setNome(String nome) { this.nome = nome; }
	    public void setDescricao(String descricao) { this.descricao = descricao; }
	    public void setCategoria(String categoria) { this.categoria = categoria; }
	    public void setPrecocusto(double preco_custo) { this.preco_custo = preco_custo; }
	    public void setPrecovenda(double preco_venda) { this.preco_venda = preco_venda; }
	    public void setEstoque(int estoque) { this.estoque = estoque; }
	    public void setCod(String cod_barras) { this.cod_barras = cod_barras; }
	    
	 // Cadastro
	    public void CadastroProduto() {
	        try (Connection conn = conexao.getConnection();
	             PreparedStatement insert = conn.prepareStatement(
	                 "INSERT INTO produtos (nome, descricao, categoria, preco_custo, preco_venda, estoque, cod_barras) VALUES (?,?,?,?,?,?,?)")) {
	                insert.setString(1, this.nome);
	                insert.setString(2, this.descricao);
	                insert.setString(3, this.categoria);
	                insert.setDouble(4, this.preco_custo);
	                insert.setDouble(5, this.preco_venda);
	                insert.setInt(6, this.estoque);
	                insert.setString(7, this.cod_barras);
	                insert.executeUpdate();

	                Alert mensagem = new Alert(Alert.AlertType.CONFIRMATION);
	                mensagem.setContentText("Produto Cadastrado!");
	                mensagem.showAndWait();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public void ProcessaEstoque(String operacao) {		
			MovimentacaoEstoqueModel movimentacao= new MovimentacaoEstoqueModel(
					0, this.id,LoginController.idUsuario, this.nome, null, this.estoque, operacao);
			if(this.id>0) {
				String sql = "update produtos set estoque = estoque + ? where id=?";
				if (operacao.equals("Saida")) {
					sql = "update produtos set estoque = estoque - ? where id=?";
				}
				
			try(Connection conn = conexao.getConnection();
					PreparedStatement consulta = conn.prepareStatement(sql);
				){
				consulta.setInt(1, this.estoque);
				consulta.setInt(2, this.id);
				consulta.execute();
				movimentacao.InsereMovimentacao();
				
			}catch(Exception e) {e.printStackTrace();}
			}
		}
	    
	    public void Buscar(String Valor) {
			try(Connection conn = conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement("select * from produtos where descricao like ? or categoria like ? or nome like ?");){
				//COLOCA INFORMAÇÕES NOS PARAMETROS DA CONSULTA SQL REPRESENTADA POR ?
				consulta.setString(1,"%"+Valor+"%");
				consulta.setString(2,"%"+Valor+"%");
				consulta.setString(3,"%"+Valor+"%");
				//GUARDA O RESULTADO EM UMA VARIAVEL DO TIPO RESULTSET (TIPO DE DADOS SQL)
				ResultSet resultado= consulta.executeQuery();
				//VERIFICA SE RETORNOU DADOS NA CONSULTA
				if(resultado.next()) {
					this.id=resultado.getInt("id");
					this.nome=resultado.getString("nome");
					this.cod_barras=resultado.getString("cod_barras");
					this.descricao=resultado.getString("descricao");
					this.categoria=resultado.getString("categoria");
					this.estoque=resultado.getInt("estoque");
					this.preco_custo=resultado.getDouble("preco_custo");
					this.preco_venda=resultado.getDouble("preco_venda");
				} else {
					Alert mensagem = new Alert(Alert.AlertType.ERROR);
					mensagem.setContentText("Produto não encontrado!");
					mensagem.showAndWait();
				}
			} catch(Exception e) { e.printStackTrace();}
		}
	    
	    public List<ProdutoModel> ListarProdutos(String Valor) {
			List <ProdutoModel> produtos = new ArrayList<ProdutoModel>();
			try(Connection conn = conexao.getConnection();
				PreparedStatement consulta = conn.prepareStatement("select * from produtos");
				PreparedStatement consultaWhere = conn.prepareStatement("select * from produtos where nome like ?  or descricao like? or categoria like ?")){
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
}
