package com.novoprasistoffline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

public class DataHelper {

   private static final String DATABASE_NAME = "bdados.db";
   private static final int DATABASE_VERSION = 1;
   private static final String TABLE_NAME = "produtos";

   private Context context;
   private SQLiteDatabase db;
   private SQLiteStatement insertStmt;
   private static final String INSERT = "insert into "
      + TABLE_NAME + "(id, nome, preco, unidade, saldo, percdesc, m_unidade, qtdcaixa, codemp_servmerc, codsec_servmerc) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   public DataHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT);
   }

  public long insert(String id, String nome, String preco, String unidade, String saldo, String percdesc, String m_unidade, String qtdcaixa, String codemp_servmerc, String codsec_servmerc) {      
      this.insertStmt.bindString(1, id.trim());
      this.insertStmt.bindString(2, nome);
      this.insertStmt.bindString(3, preco.trim());
      this.insertStmt.bindString(4, unidade.trim());
      this.insertStmt.bindString(5, saldo.trim());
      this.insertStmt.bindString(6, percdesc.trim());
      this.insertStmt.bindString(7, m_unidade.trim());
      this.insertStmt.bindString(8, qtdcaixa.trim());
      this.insertStmt.bindString(9, codemp_servmerc.trim());
      this.insertStmt.bindString(10, codsec_servmerc.trim());
      return this.insertStmt.executeInsert();
   }

  public void GravaPedido(String sQuery){
	  try 
	  {
		  db.execSQL(sQuery);              
	  } catch (SQLException serror) {
		  serror.printStackTrace();
	  }       
   }
   
  public void deleteAll() {
      
	  //Cria diretorio para arquivos enviados
	  try 
	  {
		  File diretorio = new File(Environment.getExternalStorageDirectory() + "/enviar");
		  if (!diretorio.exists()) 
		  {  
			  diretorio.mkdir();
		  }           
	  }  
	  catch (Exception e ) {
		  e.printStackTrace();
	  }

	  this.db.delete("PEDIDOS", null, null); 
	  this.db.delete("NUMPED", null, null);  
	  this.db.delete("ARQUIVO", null, null);  
      
   }
   
  public List<String[]> BuscaPedidos (String sWhere) {
               
        String sSql = " SELECT DISTINCT id_pedido, datapedido,  PEDIDOS.codfrmpgt, CONDICOES_PAGTO.DESFRMPGT, PEDIDOS.codtipprz, CONDICOES_PAGTO.DESTIPPRZ, PEDIDOS.CODCLI, CLIENTES.RAZSOC, PEDIDOS.PRECO, PEDIDOS.QTD, PEDIDOS.DESC, PEDIDOS.Status FROM PEDIDOS ";
        sSql = sSql + " JOIN CONDICOES_PAGTO ON PEDIDOS.codfrmpgt = CONDICOES_PAGTO.CODFRMPGT AND PEDIDOS.codtipprz = CONDICOES_PAGTO.CODTIPPRZ";
        sSql = sSql + " JOIN CLIENTES ON PEDIDOS.codcli = CLIENTES.CODCLI ";
        
        if (sWhere!="")
        {
        	sSql = sSql + " WHERE " + sWhere; 
        }
        		
        sSql = sSql + " ORDER BY ID_PEDIDO";
                        
        List<String[]> list = new ArrayList<String[]>();  
        
        try {
        
            Cursor cursor = db.rawQuery(sSql,null);
        
            int x=0;
            if (cursor.moveToFirst()) {
               do {
                    String[] b1=new String[]{
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10), cursor.getString(11)};
                    list.add(b1);
                    x=x+1;
               } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
               cursor.close();
            }
            cursor.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
        
   }
  //TODO
  public List<String[]> BuscaHistoricoFinanc (String sWhere) {
      
	  //LimCred , VlrDeb 	  
	  String sSql = " SELECT H.NUMDOC , H.NUMPAR , H.SERIE , H.STATUS , H.VLRDOC , H.SALDO , H.DTAVEN , H.DTAEMI , H.QTDPAR , H.ATRASO, C.LimCred, C.VlrDeb FROM HISTORICO_FINANCEIRO H";      
      sSql = sSql + " JOIN CLIENTES C ON H.CODCLI = C.CODCLI ";
      
	  
      //String sSql = " SELECT H.NUMDOC , H.NUMPAR , H.SERIE , H.STATUS , H.VLRDOC , H.SALDO , H.DTAVEN , H.DTAEMI , H.QTDPAR , H.ATRASO FROM HISTORICO_FINANCEIRO H";      
      
      
      if (sWhere!="")
      {
      	sSql = sSql + " WHERE " + sWhere; 
      }
      		
      sSql = sSql + " ORDER BY H.NUMDOC";
                      
      List<String[]> list = new ArrayList<String[]>();  
      
      try {
      
          Cursor cursor = db.rawQuery(sSql,null);
      
          int x=0;
          if (cursor.moveToFirst()) {
             do {
                  String[] b1=new String[]
                		  {
		                      cursor.getString(0),
		                      cursor.getString(1),
		                      cursor.getString(2),
		                      cursor.getString(3),
		                      cursor.getString(4),
		                      cursor.getString(5),
		                      cursor.getString(6),
		                      cursor.getString(7),
		                      cursor.getString(8),
		                      cursor.getString(9),
		                      cursor.getString(10),
		                      cursor.getString(11)
                		  };
                  list.add(b1);
                  x=x+1;
             } while (cursor.moveToNext());
          }
          if (cursor != null && !cursor.isClosed()) {
             cursor.close();
          }
          cursor.close();
      
      } catch (SQLException e) {
          e.printStackTrace();
      }

      return list;
      
 }
   
  @SuppressWarnings("resource")
  public String BuscaURL(int Indice) {
    
        File arq;
        String lstrlinha;
        
        try
        { 
            arq = new File(Environment.getExternalStorageDirectory(), "/conf_offline.txt");
            BufferedReader br = new BufferedReader(new FileReader(arq));
         
            if ((lstrlinha = br.readLine()) != null)
            {
                String sRetorno[]= lstrlinha.split(";",-1);                
                return sRetorno[Indice];                
            } else {
               return ""; 
            }
           
        } catch (Exception e) {
            e.getMessage();
            return "";
        }     
    }
   
  public List<String[]> ListagemPedidos (String sWhere) {
       
        
        String sSql = "SELECT id_pedido, datapedido, PEDIDOS.codfrmpgt, PEDIDOS.codtipprz, PEDIDOS.CODCLI, PEDIDOS.PRODUTO, PEDIDOS.QTD, PEDIDOS.PRECO, PEDIDOS.DESC, PEDIDOS.OBS, univenda, CodTabPrc, CodTabPrz, CodTipMov, Status, CLIENTES.RAZSOC FROM PEDIDOS ";
        sSql = sSql + " JOIN CONDICOES_PAGTO ON PEDIDOS.codfrmpgt = CONDICOES_PAGTO.CODFRMPGT AND PEDIDOS.codtipprz = CONDICOES_PAGTO.CODTIPPRZ";
        sSql = sSql + " JOIN CLIENTES ON PEDIDOS.codcli = CLIENTES.CODCLI ";
        
        if (sWhere!="")
        {
        	sSql = sSql + " WHERE " + sWhere;	
        }
                
        sSql = sSql + " ORDER BY id_pedido";
                
        List<String[]> list = new ArrayList<String[]>();  
        
        try {
        
            Cursor cursor = db.rawQuery(sSql,null);
        
            int x=0;
            if (cursor.moveToFirst()) {
               do {
                    String[] b1=new String[]{
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15)};
                    list.add(b1);
                    x=x+1;
               } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
               cursor.close();
            }
            cursor.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
        
   }
  
  
 public String BuscaSecundario (String sCodigo)    {       
      
	  String sQuery = "SELECT CODSEC_SERVMERC FROM PRODUTOS WHERE ID = " + sCodigo + "";
      String sResult="";
      
	  Cursor cursor = db.rawQuery(sQuery, null);
	  
	  if (cursor.moveToFirst()) 
	  {		  
		  sResult = cursor.getString(0); 
		  		  		                 
	  }
	  cursor.close();
	  	  
	  return sResult;
      
 }
 
  public String ValidaVerbaMovimentacao (String sCodigo)    {       
      
	  String sQuery = "SELECT GERA_VERBA FROM MOVIMENTACOES WHERE CODIGO = " + sCodigo + "";
      String sResult="";
      
	  Cursor cursor = db.rawQuery(sQuery, null);
	  
	  if (cursor.moveToFirst()) 
	  {		  
		  sResult = cursor.getString(0); 
		  		  		                 
	  }
	  cursor.close();
	  	  
	  return sResult;
      
 }
  
  public String BuscaIDVendedor ()    {       
        
        Cursor cursor = db.query("VENDEDOR", new String[] { "CODVEN"}, "", null, null, null, "");
        String b1="";
        
        if (cursor.moveToFirst()) {           
           b1 = cursor.getString(0);    
        }
        if (cursor != null && !cursor.isClosed()) {
           cursor.close();
        }
        cursor.close();
        return b1;
        
   }
  
  public String BuscaTabelaCliente (String sCliente)    {       
  	  
	  String sQuery = "SELECT TAB_PADRAO FROM CLIENTES WHERE CODCLI = " + sCliente + "";
      String sResult="";
      
	  Cursor cursor = db.rawQuery(sQuery, null);
	  
	  if (cursor.moveToFirst()) 
	  {		  
		  sResult = cursor.getString(0); 
		  		  		                 
	  }
	  cursor.close();
	  	  
	  return sResult;  
	  	          
   }
   
  public String BuscaDataHoraVerba ()    {       
       
       Cursor cursor = db.query("VERBAS", new String[] { "DATA_HORA"}, "", null, null, null, "");
       String b1="";
       
       if (cursor.moveToFirst()) {           
          b1 = cursor.getString(0);    
       }
       if (cursor != null && !cursor.isClosed()) {
          cursor.close();
       }
       cursor.close();
       return b1;       
  }
   
  public String[] BuscaValorVerba ()    {       
      
	  String sSql = "SELECT VERBALIBERADA, PercDescMaxVerbaPalm, VERBA_ORIGINAL  FROM VERBAS";
	          
	  String[] b1=new String[2]; 
	  
	  //Padrão Zerado
	  b1=new String[]{"0","0","0"};
      
      try {
      
          Cursor cursor = db.rawQuery(sSql,null);
      
          if (cursor.moveToFirst()) 
          {             
        	  b1=new String[]
        			  {
        			  cursor.getString(0), cursor.getString(1), cursor.getString(2) 
        			  };

          }
          
          if (cursor != null && !cursor.isClosed()) 
          {
             cursor.close();
          }
          cursor.close();
      
      } catch (SQLException e) 
      {
          e.printStackTrace();
      }

      return b1;       
 }
  
  public String BuscaValorDescontoItem (String sProduto, String sPedido)    {       
	  	  
	  String sQuery = "SELECT (PRECO * QTD) AS TOTAL_ITEM, DESC  FROM PEDIDOS WHERE produto = '" + sProduto + "' AND id_pedido = '" + sPedido + "'";
      float cValor=0;
      
	  Cursor cursor = db.rawQuery(sQuery, null);
	  
	  if (cursor.moveToFirst()) 
	  {		  
		  if (!"".equals(cursor.getString(1)))
		  {
			  cValor = Float.parseFloat(cursor.getString(0)) * Float.parseFloat(cursor.getString(1)) / 100;  
		  }
		  		                 
	  }
	  cursor.close();
	  
	  if (cValor>0) 
	  {
		  return String.valueOf(cValor);  
	  } else 
	  {
		  return "0";  
	  }
	          
   }
   
  public List<String[]> BuscaTotalPedidos (String sPedido)    {       
       
	  BigDecimal cPrecoUnit   = new BigDecimal("0");
	  BigDecimal cQtd 		  = new BigDecimal("0");
	  BigDecimal cDesc 		  = new BigDecimal("0");
	  BigDecimal cTotalPedido = new BigDecimal("0");
	  BigDecimal cTotalDesc   = new BigDecimal("0");
	  
	   //String sSql = "SELECT SUM((PRECO * QTD) - ((PRECO * QTD) * DESC / 100)) AS TOTAL_PEDIDO, SUM(DESC) AS TOTAL_DESCONTO FROM PEDIDOS ";
	   String sSql = "SELECT PRECO, QTD, DESC FROM PEDIDOS ";
	 
	   if (!"".equals(sPedido)) 
	   {
		   sSql = sSql + " WHERE id_pedido = '" + sPedido + "'";
	   }
               
       List<String[]> list = new ArrayList<String[]>();  
       
       try {
       
           Cursor cursor = db.rawQuery(sSql,null);
       
           if (cursor.moveToFirst()) 
           {
              do 
              {            	  
            	  cPrecoUnit = new BigDecimal(cursor.getString(0));
            	  cQtd 		 = new BigDecimal(cursor.getString(1));
            	  cDesc 	 = new BigDecimal("0");
            	  
            	  if (cursor.getString(2).length()>0) 
            	  {
            		  cDesc = new BigDecimal(cursor.getString(2));  
            	  }
            	  
            	  BigDecimal cValorDesconto = new BigDecimal("0");
            	  
            	  if (cursor.getString(2).length()>0) 
            	  {
            		  cValorDesconto = cPrecoUnit.multiply(cDesc).divide(new BigDecimal("100"), MathContext.DECIMAL32);  
            	  }
            	  
            	  cValorDesconto = cValorDesconto.setScale(2, BigDecimal.ROUND_FLOOR);
            	  cPrecoUnit 	 = cPrecoUnit.setScale(2, BigDecimal.ROUND_FLOOR);
            	  
            	  cTotalPedido 	= cTotalPedido.add(cPrecoUnit.subtract(cValorDesconto).multiply(cQtd));            	  
            	  cTotalDesc 	= cTotalDesc.add(cValorDesconto.multiply(cQtd));
            	  
            	  cTotalPedido  = cTotalPedido.setScale(2, BigDecimal.ROUND_FLOOR);
            	  cTotalDesc 	= cTotalDesc.setScale(2, BigDecimal.ROUND_FLOOR);
            	  /*
            	  String[] b1=new String[]
            			  {
            			  cursor.getString(0),
            			  cursor.getString(1),
            			  };
            	  list.add(b1);
            	  */
            	  
              } while (cursor.moveToNext());
           }
          
          String[] b1=new String[] {cTotalPedido.toString(),cTotalDesc.toString()};
     	  list.add(b1);
     	  
     	  
           if (cursor != null && !cursor.isClosed()) 
           {
              cursor.close();
           }
           cursor.close();
       
       } catch (SQLException e) 
       {
           e.printStackTrace();
       }

       return list;       
  }
   
  public String BuscaStatusCliente (String sCodCli)    {       

	  String sSql;
	  String b1="";

	  sSql = "SELECT STATUSCLIENTE FROM CLIENTES ";        
	  sSql = sSql + " WHERE CODCLI = " + sCodCli + "";

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) 
	  {
		  b1 = cursor.getString(0);
	  }
	  cursor.close();
	  return b1;

   }
     
  public String BuscaCampo (String sQuery) {       

	  String b1="";

	  Cursor cursor = db.rawQuery(sQuery, null);

	  if (cursor.moveToFirst()) {
		  b1 = cursor.getString(0);
	  }
	  cursor.close();
	  return b1;
        
   }
    
  public String BuscaPromocao (String sProduto, String cPreco)    {       
        
	  String sSql;
	  String b1="";
	  sSql = "SELECT PRECOPROMOCAO, PERCPROMOCAO FROM PROMOCOES ";        
	  sSql = sSql + " WHERE CODSERVMERC = " + sProduto + "";
	  sSql = sSql + " AND (DTAINI <= DATE('now') AND DTAFIM >= DATE('now'))";

	  try {
		  Cursor cursor = db.rawQuery(sSql, null);

		  if (cursor.moveToFirst()) {

			  if (!"0.00".equals(cursor.getString(1).toString())) 
			  { //Verifica se eh percentual
				  float cResultado = (Float.valueOf(cPreco) - (Float.valueOf(cPreco) * Float.valueOf(cursor.getString(1)) / 100));    
				  b1 = String.valueOf(cResultado);
			  } else {
				  b1 = cursor.getString(0);    
			  }

		  }
		  if (cursor != null && !cursor.isClosed()) {
			  cursor.close();
		  }
		  cursor.close();

	  } catch (Exception exc) {
		  exc.printStackTrace();
	  }

	  return b1;        
   }

  public boolean bTabelaLivre()    {       
        
	  String sSql;
	  boolean b1=false;

	  sSql = "SELECT CondicaoTabLivreWeb FROM PARAMETROS ";        

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) {
		  if ("0".equals(cursor.getString(0))) {
			  b1 = false;
		  } else if ("1".equals(cursor.getString(0))){
			  b1 = true;
		  }            
	  }        
	  cursor.close();
	  return b1;
        
   }
  
  public boolean bTodosProdutos(String sEmpresa)    {       
      
	  String sSql;
	  boolean b1=false;

	  sSql = "SELECT MostraTodosProdutos FROM PARAMETROS WHERE CODEMP = " + sEmpresa;        

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) {
		  if ("S".equals(cursor.getString(0))) {
			  b1 = true;
		  } else if ("1".equals(cursor.getString(0))){
			  b1 = false;
		  }            
	  }        
	  cursor.close();
	  return b1;
        
   }
    
  public boolean bRazaoSocial() {       
        
	  String sSql;
	  boolean b1=false;

	  sSql = "SELECT ExibirRazaoSocial FROM PARAMETROS ";        

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) {
		  if ("0".equals(cursor.getString(0))) {
			  b1 = false;
		  } else if ("1".equals(cursor.getString(0))){
			  b1 = true;
		  }            
	  }        
	  cursor.close();
	  return b1;        
   }
    
  public String sLayoutCombo()    {       
      
	  String sSql;
	  String b1="0";

	  sSql = "SELECT LayoutCombo FROM PARAMETROS ";        

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) {
		  b1 = cursor.getString(0);            
	  }        
	  cursor.close();
	  return b1;        
   }

  public String BuscaParametros ()    {       
        
	  String sSql;
	  String b1="";

	  sSql = "SELECT * FROM PARAMETROS ";        

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) 
	  {
		  if ("0".equals(cursor.getString(0))) {
			  b1 = "Menor Unidade";
		  } else if ("2".equals(cursor.getString(0))){
			  b1 = "Maior Unidade";
		  }            
	  }

	  cursor.close();
	  return b1;
        
   }
  
  public String BuscaIp ()    {       

	  
	  String b1="";

	  Cursor cursor = db.rawQuery("SELECT IPExterno FROM CONFIGURACOES", null);

	  if (cursor.moveToFirst()) {
		  b1 = cursor.getString(0);
	  }

	  cursor.close();
	  return b1;

   }
  
  public void IncluirLog (String sTexto) 
  {
	  db.execSQL("INSERT INTO LOG_IMPORTACAO (Mensagem) VALUES ('"+ sTexto +"')");
  }
  
  public void ExcluirPedido (String sPedido) {

      float cResultadoVerba=0;
      String sValidaVerbaMovimentacao="";
      
	  Cursor cursor = db.rawQuery("SELECT qtd, preco, desc, CodTipMov FROM PEDIDOS WHERE ID_PEDIDO = " + sPedido , null);

	  int x=0;
	  if (cursor.moveToFirst()) {
		  do {              
			  
			  sValidaVerbaMovimentacao = ValidaVerbaMovimentacao(cursor.getString(3));
				
			  if ("S".equals(sValidaVerbaMovimentacao)) //Se houver verba para controlar 
			  {
				  if (cursor.getString(2).length()>0) {
					  cResultadoVerba = cResultadoVerba + ((Float.parseFloat(cursor.getString(0)) * Float.parseFloat(cursor.getString(1)))) * Float.parseFloat(cursor.getString(2)) / 100;
				  }
			  }
				
			  x=x+1;
		  } while (cursor.moveToNext());
	  }


	  cursor.close();
	  
	  if (cResultadoVerba!=0){
		  db.execSQL("UPDATE VERBAS SET VERBALIBERADA = VERBALIBERADA + '" + cResultadoVerba + "'");
	  }
	  
	  db.execSQL("DELETE FROM PEDIDOS WHERE ID_PEDIDO = " + sPedido);
  }
  
  public void ExcluirLog () 
  {
	  db.execSQL("DELETE FROM LOG_IMPORTACAO ");
  }
  
  public String BuscarLog () {       

	  String b1="";

	  Cursor cursor = db.rawQuery("SELECT Mensagem FROM LOG_IMPORTACAO ", null);

	  if (cursor.moveToFirst()) {
		  b1 = cursor.getString(0);
	  }

	  cursor.close();
	  return b1;
   }   
  
  public String BuscaPrazoTabela (String iCodTipPrc, String iCodTipPrz)    {       

	  String sSql;
	  String b1="";

	  sSql = "SELECT PRAZOTAB FROM CONDICOES_PAGTO ";        
	  sSql = sSql + " WHERE CODFRMPGT = " + iCodTipPrc + "";
	  sSql = sSql + " AND CODTIPPRZ = " + iCodTipPrz + "";

	  Cursor cursor = db.rawQuery(sSql, null);

	  if (cursor.moveToFirst()) {
		  b1 = cursor.getString(0);
	  }

	  cursor.close();
	  return b1;

   }
   
  public String GravaNomeArquivoTxt(String sNome) {       
       
       if (!"".equalsIgnoreCase(sNome))
       {                  
           db.execSQL("INSERT OR REPLACE INTO ARQUIVO (NOME) VALUES ('" + sNome + "')");   
       }
              
       Cursor cursor = db.query("ARQUIVO", new String[] { "NOME"}, "", null, null, null, ""); 
       String b1="";
        
       if (cursor.moveToFirst()) {           
          b1 = cursor.getString(0);                           
       }
   
       cursor.close();
       return b1;
       
   }
    
  public int ProximoPedido ()    {    
	  try {
		  int NumeroPedido = 0;
		  Cursor cursor = db.query("NUMPED", new String[] { "ID_PEDIDO"}, "", null, null, null, "");

		  if (cursor.moveToLast()) {
			  NumeroPedido = cursor.getInt(0);               
			  db.execSQL("INSERT INTO NUMPED (rowid) VALUES ("+(NumeroPedido+1)+")");
		  }

		  if (cursor != null && !cursor.isClosed()) {                               
			  //nothing
			  if (NumeroPedido==0) {
				  db.execSQL("INSERT INTO NUMPED (rowid) VALUES (1)");  
			  }

		  }else {
			  db.execSQL("INSERT INTO NUMPED (rowid) VALUES (1)");                
		  }

		  cursor.close();
		  return (NumeroPedido+1);

	  } catch (SQLException e) {
		  e.printStackTrace();
		  return 1;
	  }        
   }
   
  public List<String[]> BuscaMovimentacoes (String sWhere)    {       
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.query("MOVIMENTACOES", new String[] { "CODIGO","DESCRICAO", "TIPO", "UF_EMPRESA", "GERA_VERBA"}, sWhere, null, null, null, "DESCRICAO asc");
        int x=0;
        if (cursor.moveToFirst()) {
           do {
                String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)};
                list.add(b1);
                x=x+1;
           } while (cursor.moveToNext());
        }
        
        cursor.close();
        return list;
        
   }
  
  public List<String[]> RetornaParametros (String sWhere)    {       
	  List<String[]> list = new ArrayList<String[]>();

	  /*51849 - Acrescentado Empresa*/
	  String sQuery = "SELECT IdVendedor , NomeVendedor , IPExterno , IPInterno , HostFtp , FtpUsuario , FtpSenha , PastaServidor, CodEmp  FROM CONFIGURACOES ";

	  Cursor cursor = db.rawQuery(sQuery,null);

	  int x=0;
	  if (cursor.moveToFirst()) {
		  do {                
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }

	  cursor.close();
	  return list;
        
   }

  public List<String[]> BuscaUsuario (String sUsuario, String sEmpresa)    {       
	  List<String[]> list = new ArrayList<String[]>();

	  String sQuery = "SELECT CODEMP, CODUSU, SENHA FROM USUARIOS WHERE CODUSU = " + sUsuario + " AND CODEMP = " + sEmpresa;

	  Cursor cursor = db.rawQuery(sQuery,null);

	  int x=0;
	  if (cursor.moveToFirst()) {
		  do {                
			  String[] b1=new String[] 
					  {
					  	cursor.getString(0),
					  	cursor.getString(1),
					  	cursor.getString(2)
					  };
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }

	  cursor.close();
	  return list;

   }
     
  public List<String[]> BuscaCondicoes (String cliente, String sWhere)    {   
       
	  List<String[]> list = new ArrayList<String[]>();
	  String procura;

	  if (!"".equals(cliente)) 
	  {
		  procura = this.BuscaCampo(" SELECT CODCLI FROM CONDICOES_CLIENTE WHERE CODCLI = " + Integer.parseInt(cliente)  + "");
	  } else {
		  procura="";
	  }

	  Cursor cursor;

	  if (!"".equals(procura)) {
		  String sQuery = " SELECT DISTINCT CONDICOES_PAGTO.CODFRMPGT, DESFRMPGT, CONDICOES_PAGTO.CODTIPPRZ, DESTIPPRZ, '' AS PRAZOTAB ";
		  sQuery = sQuery + " FROM CONDICOES_PAGTO JOIN CONDICOES_CLIENTE ";
		  sQuery = sQuery + " ON  CONDICOES_CLIENTE.CODFRMPGT = CONDICOES_PAGTO.CODFRMPGT";
		  sQuery = sQuery + " AND CONDICOES_CLIENTE.CODTIPPRZ = CONDICOES_PAGTO.CODTIPPRZ";
		  sQuery = sQuery + " WHERE CONDICOES_CLIENTE.CODCLI = " + Integer.parseInt(cliente) + "";
		  
		  if (sWhere!="") {
			  sQuery = sQuery + " AND " + sWhere; 
		  }

		  cursor  = db.rawQuery(sQuery,null);

	  } else {
		  cursor  = db.query("CONDICOES_PAGTO", new String[] { "CODFRMPGT","DESFRMPGT", "CODTIPPRZ","DESTIPPRZ", "PRAZOTAB"}, sWhere, null, null, null, "CODFRMPGT asc");                
	  }

	  int x=0;
	  if (cursor.moveToFirst()) {
		  do {
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }
	  if (cursor != null && !cursor.isClosed()) {
		  cursor.close();
	  }
	  cursor.close();
        return list; 
       
   }

  public List<String[]> BuscaPrazosTabela (String sWhere)    {       
	  
	  List<String[]> list = new ArrayList<String[]>();        
	  String sQuery = "SELECT DISTINCT CODTIPPRZ, DESTIPPRZ FROM TABPRECO ";
	  if (!"".equals(sWhere)) 
	  {
		  sQuery = sQuery + " WHERE 1=1 AND " + sWhere;
	  }
	  sQuery = sQuery + " ORDER BY CODTIPPRC, CODTIPPRZ";

	  Cursor cursor = db.rawQuery(sQuery,null);

	  int x=0;
	  if (cursor.moveToFirst()) 
	  {
		  do 
		  {
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }

	  cursor.close();
	  return list;
        
  }
  
 public List<String[]> BuscaPrecoProdutos (String sWhere, String iCodTipPrc, String iCodTipPrz)    {       
	  
	 List<String[]> list = new ArrayList<String[]>();        
	  String sSql;

	  //vs 4.0.3 - Acrescentado Saldo
	  sSql = "SELECT DISTINCT id, nome, T.preco, unidade, qtdcaixa, T.CODTIPPRC, T.CODTIPPRZ, C.DESFRMPGT, C.DESTIPPRZ, P.SALDO FROM PRODUTOS P ";
	  sSql = sSql + " JOIN TABPRECO T ON T.CODSERVMERC = P.ID ";
	  sSql = sSql + " JOIN CONDICOES_PAGTO C ON T.CODTIPPRZ = C.CODTIPPRZ"; //retirado CODTIPPRC CODFRMPGT
	  
	  sSql = sSql + " WHERE 1=1 ";
	  
	  if (!"".equals(iCodTipPrc)) {
		  sSql = sSql + " AND T.CODTIPPRC = " + iCodTipPrc + "";
	  }
	  
	  if (!"".equals(iCodTipPrz)) {
		  sSql = sSql + " AND T.CODTIPPRZ = " + iCodTipPrz + "";
	  }

	  if (!"".equals(sWhere)) 
	  {
		  sSql = sSql + " AND (" + sWhere + ")";
	  }

	  sSql = sSql + " ORDER BY NOME ASC";

	  try {
		  Cursor cursor = db.rawQuery(sSql, null);

		  int x=0;
		  if (cursor.moveToFirst()) 
		  {
			  do {

				  //id, nome, T.preco, unidade, qtdcaixa, T.CODTIPPRC, T.CODTIPPRZ, C.DESFRMPGT, C.DESTIPPRZ  
				  String[] b1=new String[]{
						  cursor.getString(0),
						  cursor.getString(1),
						  cursor.getString(2),
						  cursor.getString(3),
						  cursor.getString(4),
						  cursor.getString(5),
						  cursor.getString(6),
						  cursor.getString(7),
						  cursor.getString(8),
						  cursor.getString(9)};                
				  list.add(b1);
				  x=x+1;
			  } while (cursor.moveToNext());
		  }


	  } catch (Exception exc1) {
		  exc1.toString();
	  }


	  try {
		  return list;
	  } catch (Exception erro){
		  erro.toString();
		  return list;
	  }
        
   }
        
 public List<String[]> BuscaTabelas (String sWhere)    {       
	  
	  List<String[]> list = new ArrayList<String[]>();
	  
	  String sQuery = "SELECT DISTINCT CODTIPPRC, DESTIPPRC, CODTIPPRZ, DESTIPPRZ FROM TABPRECO ";
	  if (!"".equals(sWhere)) 
	  {
		  sQuery = sQuery + " WHERE 1=1 AND " + sWhere;
	  }
	  sQuery = sQuery + " ORDER BY CODTIPPRC, CODTIPPRZ";

	  Cursor cursor = db.rawQuery(sQuery,null);

	  int x=0;
	  if (cursor.moveToFirst()) 
	  {
		  do 
		  {
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }
	  
	  cursor.close();
	  return list;
        
   }
   
 public List<String[]> BuscaClientes (String sWhere)    {
      
	  List<String[]> list = new ArrayList<String[]>();
	  Cursor cursor = db.query("CLIENTES", new String[] { "CODCLI","RAZSOC", "STATUSCLIENTE","UF"}, sWhere, null, null, null, "RAZSOC asc");
	  int x=0;
	  if (cursor.moveToFirst()) 
	  {
		  do {
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }

	  cursor.close();
	  return list;        
   }
   
 public List<String[]> ListaProdutos(String sWhere) {
      
	  List<String[]> list = new ArrayList<String[]>();        
	  String sSql;

	  sSql = "SELECT DISTINCT id, nome, '0' as preco, unidade, saldo, percdesc, m_unidade, qtdcaixa FROM PRODUTOS P ";
		  
	  sSql = sSql + " WHERE 1=1 ";
	  	
	  if (!"".equals(sWhere)) 
	  {
		  sSql = sSql + " AND (" + sWhere + ")";
	  }

	  sSql = sSql + " ORDER BY NOME ASC";

	  try {
		  Cursor cursor = db.rawQuery(sSql, null);

		  int x=0;
		  if (cursor.moveToFirst()) 
		  {
			  do {

				  String[] b1=new String[]{cursor.getString(0),
						  cursor.getString(1),
						  cursor.getString(2),
						  cursor.getString(3),
						  cursor.getString(4),
						  cursor.getString(5),
						  cursor.getString(6),
						  cursor.getString(7)};                
				  list.add(b1);
				  x=x+1;
			  } while (cursor.moveToNext());
		  }


	  } catch (Exception exc1) {
		  exc1.toString();
	  }

	  try {
		  return list;
	  } catch (Exception erro){
		  erro.toString();
		  return list;
	  }
             
   }
  
 public List<String[]> selectAll(String sWhere, String iCodTipPrc, String iCodTipPrz) {
       
	  List<String[]> list = new ArrayList<String[]>();        
	  String sSql;

	  sSql = "SELECT DISTINCT id, nome, T.preco, unidade, saldo, percdesc, m_unidade, qtdcaixa, T.CODTIPPRC, T.CODTIPPRZ FROM PRODUTOS P ";
	  sSql = sSql + " JOIN TABPRECO T ON T.CODSERVMERC = P.ID ";
	  
	  sSql = sSql + " WHERE 1=1 ";
	  
	  if (!"".equals(iCodTipPrc)) {
		  sSql = sSql + " AND T.CODTIPPRC = " + iCodTipPrc + "";
	  }
	  
	  if (!"".equals(iCodTipPrz)) {
		  sSql = sSql + " AND T.CODTIPPRZ = " + iCodTipPrz + "";
	  }

	  if (!"".equals(sWhere)) 
	  {
		  sSql = sSql + " AND (" + sWhere + ")";
	  }

	  sSql = sSql + " ORDER BY NOME ASC";

	  try {
		  Cursor cursor = db.rawQuery(sSql, null);

		  int x=0;
		  if (cursor.moveToFirst()) 
		  {
			  do {

				  String[] b1=new String[]{cursor.getString(0),
						  cursor.getString(1),
						  cursor.getString(2),
						  cursor.getString(3),
						  cursor.getString(4),
						  cursor.getString(5),
						  cursor.getString(6),
						  cursor.getString(7)};                
				  list.add(b1);
				  x=x+1;
			  } while (cursor.moveToNext());
		  }


	  } catch (Exception exc1) {
		  exc1.toString();
	  }


	  try {
		  return list;
	  } catch (Exception erro){
		  erro.toString();
		  return list;
	  }
             
   }
   
 public boolean VerificaExistePedido(){
   
	  boolean bExiste = false;
	  Cursor cursor = db.query("PEDIDOS", new String[] { "produto", "descricao", "qtd","preco" }, "status = 'Liberado'", null, null, null, "produto asc");

	  if (cursor.moveToFirst()) {
		  do {
			  bExiste = true;
			  break;
		  } while (cursor.moveToNext());
	  }

	  return bExiste;

   }
   
 public List<String[]> LePedido(String sWhere) {
    
	  List<String[]> list = new ArrayList<String[]>();
	  Cursor cursor = db.query("PEDIDOS", new String[] { "produto", "descricao", "qtd", "preco", "desc", "obs", "univenda", "qtdcaixa", "codtabprc", "codtabprz", "CodTipMov" }, sWhere, null, null, null, "produto asc");
	  int x=0;
	  if (cursor.moveToFirst()) 
	  {
		  do 
		  {
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }

	  cursor.close();
	  return list;
   }

 public List<String[]> LePedidoCompleto(String sWhere) {
  
	  List<String[]> list = new ArrayList<String[]>();
	  Cursor cursor = db.query("PEDIDOS", new String[] { "produto", "descricao", "qtd", "preco", "desc", "obs", "univenda", "qtdcaixa", "codtabprc", "codtabprz", "CodTipMov", "codfrmpgt", "codtipprz" }, sWhere, null, null, null, "produto asc");
	  int x=0;
	  if (cursor.moveToFirst()) 
	  {
		  do 
		  {
			  String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12)};
			  list.add(b1);
			  x=x+1;
		  } while (cursor.moveToNext());
	  }

	  cursor.close();
	  return list;
   }

  
 public void ExecutarSql(String sQuery) {
	  try {
		  db.execSQL(sQuery);               
	  } catch (SQLException e) {
		  e.toString();
	  }
 }
     
  private static class OpenHelper extends SQLiteOpenHelper {

      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      private void CriarTabelas(SQLiteDatabase db) {
    	  /*51849 - Acrescentado Empresa*/
    	  
          //TABELA DE PRODUTOS PARA SE TRABALHAR OFF LINE 
         db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INT PRIMARY KEY, nome TEXT, preco TEXT, unidade TEXT, saldo TEXT, percdesc TEXT, m_unidade TEXT, qtdcaixa TEXT, codemp_servmerc INT, codsec_servmerc TEXT)");
         
         //Cria tabela clientes
         db.execSQL("CREATE TABLE CLIENTES (CODCLI INT PRIMARY KEY, RAZSOC TEXT, STATUSCLIENTE TEXT, UF TEXT, CNPJ TEXT, TAB_PADRAO INT, CodEmp INT, LimCred TEXT, VlrDeb TEXT)");
         db.execSQL("CREATE UNIQUE INDEX idx_cliente ON CLIENTES(CODCLI);");
                  
         //Cria tabela condicoes de pagamento
         db.execSQL("CREATE TABLE CONDICOES_PAGTO (CODFRMPGT INT, CODTIPPRZ INT, DESFRMPGT TEXT, DESTIPPRZ TEXT, PRAZOTAB INT, CodEmp INT)");
         db.execSQL("CREATE UNIQUE INDEX idx_condicoes ON CONDICOES_PAGTO(CODFRMPGT,CODTIPPRZ);");
         
         //TABELA DE PEDIDOS QUE SERÃO ENVIADOS AO TXT
         db.execSQL("CREATE TABLE PEDIDOS (id_pedido INT NOT NULL, CodTipMov INT, CodTabPrc INT, CodTabPrz INT, datapedido TEXT, codfrmpgt INT, codtipprz INT, codcli int , arquivo TEXT, produto TEXT NOT NULL, descricao TEXT, qtd TEXT, preco TEXT, desc TEXT, obs TEXT, univenda TEXT, qtdcaixa TEXT, CodEmp INT, Status TEXT, PRIMARY KEY(id_pedido, produto) );");
         db.execSQL("CREATE UNIQUE INDEX data_idx ON PEDIDOS(id_pedido, produto);");
         
         //Guarda numeracoes pedido
         db.execSQL("CREATE TABLE NUMPED (ID_Pedido INTEGER PRIMARY KEY);");
         
         //Guarda VENDEDOR logado
         db.execSQL("CREATE TABLE VENDEDOR (CODVEN INT PRIMARY KEY);");
         
         db.execSQL("CREATE TABLE ARQUIVO (NOME TEXT PRIMARY KEY);");
         
         db.execSQL("CREATE TABLE USUARIOS (CODEMP INT, CODUSU INT, SENHA TEXT);");
         
         db.execSQL("CREATE TABLE TABPRECO (CODSERVMERC INT, CODVEN INT, CODTIPPRC INT, CODTIPPRZ INT, DESTIPPRC TEXT, DESTIPPRZ TEXT, PRECO TEXT, PERCMAX TEXT, CodEmp INT)");
         
         db.execSQL("CREATE TABLE MOVIMENTACOES (CODIGO INT, DESCRICAO TEXT, TIPO TEXT, UF_EMPRESA TEXT, CODEMP INT, GERA_VERBA TEXT)");
         
         db.execSQL("CREATE TABLE CONDICOES_CLIENTE (CODCLI INT, CodFrmPgt INT, CodTipPrz INT, CodEmp INT)");
         
         db.execSQL("CREATE TABLE PROMOCOES (CODSERVMERC INT, DTAFIM TEXT, DTAINI TEXT, PERCPROMOCAO TEXT, PRECOPROMOCAO TEXT, CodEmp INT)");
         
         db.execSQL("CREATE TABLE PARAMETROS (UNIDADE_VENDA INT, CondicaoTabLivreWeb INT , ExibirRazaoSocial INT, LayoutCombo TEXT, CodEmp INT, MostraTodosProdutos TEXT)");
                  
         db.execSQL("CREATE TABLE CONFIGURACOES (IdVendedor INT, NomeVendedor TEXT, IPExterno TEXT, IPInterno TEXT, HostFtp TEXT, FtpUsuario TEXT, FtpSenha TEXT, PastaServidor TEXT, CodEmp INT)");
         
         db.execSQL("CREATE TABLE VERBAS (PercDescMaxVerbaPalm TEXT, VERBALIBERADA TEXT, DATA_HORA TEXT, CodEmp INT, VERBA_ORIGINAL TEXT);");
         
         db.execSQL("CREATE TABLE LOG_IMPORTACAO (Mensagem TEXT);");
         
         db.execSQL("CREATE TABLE HISTORICO_FINANCEIRO (CODEMP INT, CODCLI INT, NUMDOC TEXT, NUMPAR INT, SERIE TEXT, STATUS TEXT, VLRDOC TEXT, SALDO TEXT, DTAVEN TEXT, DTAEMI TEXT, QTDPAR INT, ATRASO INT)");
         
      }
      
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         CriarTabelas(db);                 
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w("Upgrade", "Atualização BD");
         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS LOG_IMPORTACAO");
         db.execSQL("DROP TABLE IF EXISTS PEDIDOS");
         db.execSQL("DROP TABLE IF EXISTS CLIENTES");
         db.execSQL("DROP TABLE IF EXISTS CONDICOES_PAGTO");
         db.execSQL("DROP TABLE IF EXISTS MOVIMENTACOES");
         db.execSQL("DROP TABLE IF EXISTS CONDICOES_CLIENTE");
         db.execSQL("DROP TABLE IF EXISTS PROMOCOES");
         db.execSQL("DROP TABLE IF EXISTS TABPRECO");
         db.execSQL("DROP TABLE IF EXISTS USUARIOS");
         db.execSQL("DROP TABLE IF EXISTS PARAMETROS");
         db.execSQL("DROP TABLE IF EXISTS CONFIGURACOES");
         db.execSQL("DROP TABLE IF EXISTS VERBAS");
                  
         onCreate(db);
         
         CriarTabelas(db);
      }
   }
}