/*Ultima atualização 4.0.3 - Listar saldo na consulta de tabela de preços*/
/*Atualização teste pelo github*/
package com.novoprasistoffline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BuscarDados extends Activity {

	public  DataHelper dh;
	static final String KEY_ITEM 	  		= "Table"; 
	static final String KEY_ID 		  		= "CODSERVMERC";
	static final String KEY_NAME 	  		= "DESSERVMERC";
	static final String KEY_COST 	  		= "Preco";
	static final String KEY_DESC 	  		= "description";
	static final String KEY_SALDO 	  		= "SALDO";
	static final String KEY_UNIDADE   		= "UNIDADE";
	static final String KEY_PERCDESC  		= "PERCDESC";
	static final String KEY_M_UNIDADE 		= "M_UNIDADE";
	static final String KEY_QTDCAIXA  		= "QTDCAIXA";	
	static final String KEY_CODEMP    		= "CODEMP_SERVMERC";
	static final String KEY_CODSEC_SERVMERC = "CodSec_ServMerc";
	public TextView txtSalvar;
	public TextView txtHost;
	public String   sPath;
	public String   sEmpresa; /*51849*/
	public String   sMensagemLog="Log Importação Arquivos : \n";
	
	Button btnStartProgress;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress);

		this.dh = new DataHelper(getApplicationContext());

		Bundle bundle = getIntent().getExtras();

		if(bundle.getString("recebe")!= null)
		{
			txtSalvar = (TextView) findViewById(R.id.tbRecebe);        
			txtSalvar.setText(bundle.getString("recebe"));  
		}

		if(bundle.getString("host")!= null)
		{
			txtHost = (TextView) findViewById(R.id.tbHost);        
			txtHost.setText(bundle.getString("host"));  
		}

		/*51849 - Acrescentado Empresa*/
		/*Busca Empresa parametrizada, pois importará os dados somente da empresa informada.*/		
		List<String[]> sParametros;
		sParametros 		= this.dh.RetornaParametros("");
 	   
 	    for (String[] name : sParametros) 
 	    { 		   
 		   sEmpresa		  = name[8].toString(); /*51849 - Acrescentado Empresa*/
 	    }
 	   
	   /*Dispara rotina de importação*/
 	   addListenerOnButton();

	}    
	
	public void addListenerOnButton() {
				
		progressBar = new ProgressDialog(this);
		progressBar.setCancelable(false);
		progressBar.setMessage("Importando Dados. Por favor, aguarde...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		progressBarStatus = 0;
		
		new Thread(new Runnable() {
						
			public void run() {
				
				while (progressBarStatus < 100) 
				{
					progressBarStatus = doSomeTasks();					
				}

				if (progressBarStatus >= 100) 
				{
					try 
					{                                          
						Thread.sleep(1000);
						progressBar.dismiss();						
						Intent in = new Intent(BuscarDados.this, PrasistAndroidOffLine.class);                                       
		                finish();
		                startActivity(in);
					} 
					catch (InterruptedException ex) 
					{
						Logger.getLogger(BuscarDados.class.getName()).log(Level.SEVERE, null, ex);
					}			
				}
			}

		}).start();

	}

	private int doSomeTasks() {

		progressBarStatus=progressBarStatus+1;
		progressBar.setProgress(progressBarStatus);

		TextView lbMsg = (TextView) findViewById(R.id.txArq);
		
		if (isOnline()==false)
		{			
			lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
			android.os.SystemClock.sleep(4000);
			return 100;
		} 
		else 
		{             
			txtHost = (TextView) findViewById(R.id.tbHost);

			if (!"".equals(txtHost.getText().toString()))
			{
				sPath = txtHost.getText().toString();
			} else {
				sPath = this.dh.BuscaURL(0);
			}

			this.dh.ExcluirLog(); /*Excluir log anterior*/
			
			//TODO
			ImportarHistoricoFin();
			ImportaClientes();
			ImportaProdutos();
			ImportarCondicoes();									
			ImportarMovimentacoes();						
			ImportarConfiguracoes();					
			ImportarUsuarios();				
			ImportarCondicoesCliente();
			ImportarPromocoes();			
			ImportarVerbas();							
			ImportarParametros();
			ImportarTabelas();			
			
			
			/*Gravar log atual*/
			this.dh.IncluirLog(sMensagemLog);
			
		}

		return 100;         
	}

	private void ImportarVerbas () {

		XMLParser parser = new XMLParser();

		try
		{
			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{                
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			TextView ID_Vendedor = (TextView) findViewById(R.id.tbRecebe);

			if ("".equals(sPath))
			{
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			try {
				String xml = parser.getXmlFromUrl(sPath + "/xml/" + ID_Vendedor.getText() + "verbas.xml"); // getting XML

				Document doc = parser.getDomElement(xml); // getting DOM element
				NodeList nl = doc.getElementsByTagName(KEY_ITEM);

				//se encontrou xml
				if (nl.getLength() >0)
				{                      
					//this.dh.ExecutarSql("DELETE FROM VERBAS");                    
					progressBar.setMax(nl.getLength());                        
					progressBarStatus=0;
					progressBar.setProgress(progressBarStatus);                            
				}      

				int iCont=0; /*Conta registros importados*/
				
				// grava dados xml no banco
				for (int i = 0; i < nl.getLength(); i++) 
				{
					Element e = (Element) nl.item(i);		

					/*Busca data e hora da última atualização*/
					String sDataHora = this.dh.BuscaDataHoraVerba();

					if (("").equals(sDataHora)) /*Se estiver vazio é a primeira importação*/
					{
						/*51849*/
						/*Só importa os dados da empresa configurada*/
						if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
							this.dh.ExecutarSql("INSERT INTO VERBAS (PercDescMaxVerbaPalm, VERBALIBERADA, DATA_HORA, CODEMP, VERBA_ORIGINAL) "
									+ "VALUES ('" + parser.getValue(e,"PercDescMaxVerbaPalm") + "', '" + parser.getValue(e,"VERBALIBERADA") + "', '" + parser.getValue(e,"DATA_HORA").trim() + "' , " + parser.getValue(e,"CODEMP").trim() + ", '" + parser.getValue(e,"VERBALIBERADA") + "')");
							iCont++;
						}							
					}
					else
					{                    	
						if (!(sDataHora.trim()).equals(parser.getValue(e,"DATA_HORA").trim())) /*Verifica se a data hora do xml é diferente da gravada*/ 
						{
							/*51849*/
							/*Só importa os dados da empresa configurada*/
							if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
								/*Se for diferente atualiza o valor da nova verba, caso contrário não faz nada.*/
								this.dh.ExecutarSql("UPDATE VERBAS SET PercDescMaxVerbaPalm = '" + parser.getValue(e,"PercDescMaxVerbaPalm") + "', VERBALIBERADA = '" + parser.getValue(e,"VERBALIBERADA") + "', DATA_HORA = '" + parser.getValue(e,"DATA_HORA").trim() + "' WHERE CODEMP = " + parser.getValue(e,"CODEMP").trim() + "");
								iCont++;
							}	
						}
					}

					progressBarStatus = progressBarStatus+1;
					progressBar.setProgress(progressBarStatus);
					
				}
								
				sMensagemLog = sMensagemLog  + "\n VERBAS : " + iCont + " registro(s).";
				
			}
			catch (Exception exc) 
			{
				exc.getMessage();
			}

		} 
		catch (Exception e) 
		{
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    
	}
		
	private void ImportarHistoricoFin () {

		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			
			if (isOnline()==false)
			{				
				lbMsg.setText("Sem Conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Importação Histórico financeiro");
				builder.setMessage("Arquivo de Configura��o conf_offline.txt n�o encontrado no diret�rio raiz.");
				builder.setPositiveButton("OK", null);
				builder.show();    
				return;
			}

			TextView ID_Vendedor = (TextView) findViewById(R.id.tbRecebe);
			String xml = parser.getXmlFromUrl(sPath + "/xml/" + ID_Vendedor.getText() + "titulos.xml"); // getting XML
			

			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                                
				this.dh.ExecutarSql("DELETE FROM HISTORICO_FINANCEIRO");				
				
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                                        
			}      

			int iCont=0;
			
			// grava dados xml no banco
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		
				
				/*51849*/
				/*Só importa os dados da empresa configurada*/
				//CodEmp, CodCli, NumDoc, NumPar, Serie, Status, VlrDoc, Saldo, DtaVen, DtaEmi, QtdPar, Obs, atraso
				if (sEmpresa.equals(parser.getValue(e,"CodEmp"))) {
					this.dh.ExecutarSql("INSERT INTO HISTORICO_FINANCEIRO (CODEMP, CODCLI, NUMDOC, NUMPAR, SERIE, STATUS, VLRDOC, SALDO, DTAVEN, DTAEMI, QTDPAR, ATRASO) VALUES (" + parser.getValue(e,"CodEmp") + ", " + parser.getValue(e,"CodCli") + ", '" + parser.getValue(e,"NumDoc") + "', " + parser.getValue(e,"NumPar") + ", '" + parser.getValue(e,"Serie") + "', '" + parser.getValue(e,"Status") + "', '" + parser.getValue(e,"VlrDoc") + "', '" + parser.getValue(e,"Saldo") + "',  '" + parser.getValue(e,"DtaVen") + "', '" + parser.getValue(e,"DtaEmi") + "', " + parser.getValue(e,"QtdPar") + ", " + parser.getValue(e,"atraso") + ")"); 
					iCont++;
				}

				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
			/*Se importou registros*/
			
			sMensagemLog = sMensagemLog  + "\n HISTÓRICO FINANC. : " + iCont + " registro(s).";
			
		} catch (Exception e) {
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}
		
	}
		
	private void ImportarUsuarios() {

		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false){                
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");     
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/tabvend.xml"); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                      
				this.dh.ExecutarSql("DELETE FROM USUARIOS");                    
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                            
			}      

			int iCont=0;
			
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		

				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
					this.dh.ExecutarSql("INSERT INTO USUARIOS (CODEMP, CODUSU, SENHA) VALUES (" + parser.getValue(e,"CODEMP") + "," + parser.getValue(e,"CODUSU") + ", '" + parser.getValue(e,"SENHA") + "')");
					iCont++;
				}
				
				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
			/*Se importou registros*/
			
			if (iCont>0) {				
				sMensagemLog = sMensagemLog  + "\n USUÁRIOS : " + iCont + " registro(s).";
			} else {
				sMensagemLog = sMensagemLog  + "\n ATENÇÃO !!! USUÁRIOS não encontrados. Dados Obrigatórios.";
			}

		} catch (Exception e) {

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    

	}

	private void ImportarTabelas () {
		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			TextView ID_Vendedor = (TextView) findViewById(R.id.tbRecebe);

			if ("".equals(sPath)) {
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			try {
				
				String xml = parser.getXmlFromUrl(sPath + "/xml/" + ID_Vendedor.getText() + "_" + sEmpresa + "_" + "tabpreco.xml"); // getting XML
				
				Document doc = parser.getDomElement(xml); // getting DOM element
				NodeList nl = doc.getElementsByTagName(KEY_ITEM);

				//se encontrou xml
				if (nl.getLength() >0)
				{                      
					this.dh.ExecutarSql("DELETE FROM TABPRECO");                    
					progressBar.setMax(nl.getLength());                        
					progressBarStatus=0;
					progressBar.setProgress(progressBarStatus);                            
				}      

				int iCont=0;
				
				// grava dados xml no banco
				for (int i = 0; i < nl.getLength(); i++) 
				{
					Element e = (Element) nl.item(i);		

					/*51849*/
					/*Só importa os dados da empresa configurada*/
					if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
						this.dh.ExecutarSql("INSERT INTO TABPRECO (CODSERVMERC, CODVEN, CODTIPPRC, CODTIPPRZ, DESTIPPRC, DESTIPPRZ, PRECO, PERCMAX, CODEMP) "
								+ "VALUES (" + parser.getValue(e,"CODSERVMERC") + ", " + parser.getValue(e,"CODVEND") + ", " + parser.getValue(e,"CODTIPPRC") + ", " + parser.getValue(e,"CODTIPPRZ") + ", '" + parser.getValue(e,"DESTIPPRC").trim() + "', '" + parser.getValue(e,"DESTIPPRZ").trim() + "', '" + parser.getValue(e,"PRECO") + "', '" + parser.getValue(e,"PERCMAX") + "' , " + parser.getValue(e,"CODEMP") + ")");
						iCont++;
					}
					
					progressBarStatus=progressBarStatus+1;
					progressBar.setProgress(progressBarStatus);                       
				}
				
				/*Se importou registros*/
				
				if (iCont>0) {				
					sMensagemLog = sMensagemLog  + "\n TABELA DE PREÇO : " + iCont + " registro(s).";
				} else {
					sMensagemLog = sMensagemLog  + "\n ATENÇÃO !!! TABELA DE PREÇO não encontrada. Dados Obrigatórios.";
				}

			}
			catch (Exception exc) {
				exc.getMessage();
			}


		} catch (Exception e) {
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    
	}        

	private void ImportarMovimentacoes () {
		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{                
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) 
			{
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);                    
				//finish();                    
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/movimentacoes.xml"); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                      
				this.dh.ExecutarSql("DELETE FROM MOVIMENTACOES");                    
				progressBar.setMax(nl.getLength());                        
				//progressBarStatus=progressBarStatus-1;
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                            
			}      

			int iCont=0;
			
			// grava dados xml no banco
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		

				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
					this.dh.ExecutarSql("INSERT INTO MOVIMENTACOES (CODEMP, CODIGO, DESCRICAO, TIPO, UF_EMPRESA, GERA_VERBA) VALUES (" + parser.getValue(e,"CODEMP") + ", " + parser.getValue(e,"CODIGO") + ", '" + parser.getValue(e,"DESCRICAO") + "', '" + parser.getValue(e,"TIPO") + "', '" + parser.getValue(e,"UF_EMPRESA") + "', '" + parser.getValue(e,"GERA_VERBA") + "')");
					iCont++;
				}

				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
			/*Se importou registros*/			
			if (iCont>0) 
			{				
				sMensagemLog = sMensagemLog  + "\n TIPOS DE MOVIMENTAÇÃO : " + iCont + " registro(s).";
			} else {
				sMensagemLog = sMensagemLog  + "\n ATENÇÃO !!! TIPO DE MOVIMENTAÇÃO não encontrado. Dados Obrigatórios.";
			}

		} catch (Exception e) {
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    
	}    

	private  void ImportarPromocoes() {

		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{   
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/promocao.xml"); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                      
				this.dh.ExecutarSql("DELETE FROM PROMOCOES");                    
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                            
			}      

			int iCont=0;
			
			// grava dados xml no banco
			//CODSERVMERC INT, DTAFIM TEXT, DTAINI TEXT, PERCPROMOCAO TEXT, PRECOPROMOCAO 
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		

				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
					this.dh.ExecutarSql("INSERT INTO PROMOCOES (CODSERVMERC, DTAFIM, DTAINI, PERCPROMOCAO, PRECOPROMOCAO, CODEMP) VALUES (" + parser.getValue(e,"CODSERVMERC") + ", '" + parser.getValue(e,"DtaFim") + "', '" + parser.getValue(e,"DtaIni") + "', '" + parser.getValue(e,"PercProm") + "', '" + parser.getValue(e,"PrecoProm") + ", " + parser.getValue(e,"CodEmp") + ")");
					iCont++;
				}
				
				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
										
			sMensagemLog = sMensagemLog  + "\n PROMOÇÃO DE MERCADORIAS : " + iCont + " registro(s).";


		} catch (Exception e) {

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    

	}   

	private  void ImportarParametros() {

		/*
         * "Mercadoria"                                         = 0
           "Mercadoria + Unidade"                               = 1
           "Mercadoria + Preço"                                 = 2
           "Mercadoria + Qtd. Emb/Cx. Emb"                      = 3
           "Mercadoria + Qtd. Emb/Cx. Emb + Unidade + Preço"    = 4
         */
		
		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{                
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/parametros.xml"); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                      
				this.dh.ExecutarSql("DELETE FROM PARAMETROS");                    
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                            
			}      

			int iCont=0;
			
			// grava dados xml no banco
			//UNIDADE_VENDA
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		

				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CodEmp"))) {
					this.dh.ExecutarSql("INSERT INTO PARAMETROS (UNIDADE_VENDA, CondicaoTabLivreWeb, ExibirRazaoSocial, LayoutCombo, CodEmp, MostraTodosProdutos) VALUES (" + parser.getValue(e,"PARA_UNIDADEVENDA") + ", " + parser.getValue(e,"CondicaoTabLivreWeb") + ", " + parser.getValue(e,"ExibirRazaoSocial") + ", '" + parser.getValue(e,"LayoutCombo") + "', " + parser.getValue(e,"CodEmp") + ", '" + parser.getValue(e,"MostraTodosProdutos") + "')");
					iCont++;
				}

				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
							
			sMensagemLog = sMensagemLog  + "\n PARÂMETROS : " + iCont + " registro(s).";
			
			
		} catch (Exception e) {

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    

	}

	private  void ImportarConfiguracoes() {

		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			/*Verifica se há conexão de dados*/
			if (isOnline()==false)
			{                
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/parametros.xml"); // buscando XML
			Document doc = parser.getDomElement(xml); // DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                      
				//this.dh.ExecutarSql("DELETE FROM CONFIGURACOES");                    
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                            
			}      
			String sQuery="";

			int iCont=0;
			
			// grava dados xml no banco
			//UNIDADE_VENDA
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		
				/*
                    sQuery = "INSERT OR REPLACE INTO CONFIGURACOES (IPExterno,IPInterno, HostFtp, FtpUsuario, FtpSenha, PastaServidor ) VALUES (";
                    sQuery = sQuery + " '" + parser.getValue(e,"IPExterno") + "'";
                    sQuery = sQuery + " , '" + parser.getValue(e,"IPInterno") + "'";
                    sQuery = sQuery + " , '" + parser.getValue(e,"HostFtp") + "'";
                    sQuery = sQuery + " , '" + parser.getValue(e,"FtpUsuario") + "'";
                    sQuery = sQuery + " , '" + parser.getValue(e,"FtpSenha") + "'";
                    sQuery = sQuery + " , '" + parser.getValue(e,"PastaServidor") + "')";
				 */

				sQuery = "UPDATE CONFIGURACOES ";
				sQuery = sQuery + " SET IPExterno = '" + parser.getValue(e,"IPExterno") + "'";
				sQuery = sQuery + " , IPInterno = '" + parser.getValue(e,"IPInterno") + "'";
				sQuery = sQuery + " , HostFtp = '" + parser.getValue(e,"HostFtp") + "'";
				sQuery = sQuery + " , FtpUsuario = '" + parser.getValue(e,"FtpUsuario") + "'";
				sQuery = sQuery + " , FtpSenha = '" + parser.getValue(e,"FtpSenha") + "'";
				sQuery = sQuery + " , PastaServidor  = '" + parser.getValue(e,"PastaServidor") + "'";
				sQuery = sQuery + " WHERE CodEmp = " + sEmpresa; /*51849 - Acrescentado Empresa*/
				iCont++;

				this.dh.ExecutarSql(sQuery);

				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
			sMensagemLog = sMensagemLog + "\n CONFIGURAÇÕES : " + iCont + " registro(s).";				
			

		} catch (Exception e) {

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    

	}
 
	private void ImportarCondicoes () {
		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{                
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) 
			{
				progressBar.dismiss();
				lbMsg.setText(Environment.getExternalStorageDirectory() + " AVISO ! O arquivo conf_offline.txt não encontrado no diretório raiz.");              
				android.os.SystemClock.sleep(6000);
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/condpagto.xml"); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                      
				this.dh.ExecutarSql("DELETE FROM CONDICOES_PAGTO");                                        
				progressBar.setTitle("Importando condições de pagamento...");                                                            
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                            
			}      

			int iCont=0;
			
			// grava dados xml no banco
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		

				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
					this.dh.ExecutarSql("INSERT INTO CONDICOES_PAGTO (CODFRMPGT, DESFRMPGT, CODTIPPRZ, DESTIPPRZ, PRAZOTAB, CODEMP) VALUES (" + parser.getValue(e,"CODFRMPGT") + ", '" + parser.getValue(e,"DESFRMPGT") + "', " + parser.getValue(e,"CODTIPPRZ") + ", '" + parser.getValue(e,"DESTIPPRZ") + "', " + parser.getValue(e,"PRAZOTAB") + ", " + parser.getValue(e,"CODEMP") + ")");
					iCont++;
				}
				
				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
			/*Se importou registros*/
			
			if (iCont>0) {				
				sMensagemLog = sMensagemLog  + "\n COND. DE PAGTO : " + iCont + " registro(s).";
			} else {
				sMensagemLog = sMensagemLog  + "\n ATENÇÃO !!! CONDIÇÕES DE PAGAMENTO não encontradas. Dados Obrigatórios.";
			}
			
		} catch (Exception e) {
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}                    
	}    

	private void ImportaClientes() {

		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			
			if (isOnline()==false)
			{				
				lbMsg.setText("Sem Conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Importação de clientes");
				builder.setMessage("Arquivo de Configura��o conf_offline.txt n�o encontrado no diret�rio raiz.");
				builder.setPositiveButton("OK", null);
				builder.show();    
				return;
			}

			TextView ID_Vendedor = (TextView) findViewById(R.id.tbRecebe);
			String xml = parser.getXmlFromUrl(sPath + "/xml/" + ID_Vendedor.getText() + "clientes.xml"); // getting XML

			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                                
				this.dh.ExecutarSql("DELETE FROM CLIENTES");
				this.dh.ExecutarSql("DELETE FROM VENDEDOR");
				this.dh.ExecutarSql("INSERT INTO VENDEDOR (CODVEN) VALUES (" + ID_Vendedor.getText() + ")");
				this.dh.deleteAll();

				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                                        
			}      

			int iCont=0;
			
			// grava dados xml no banco
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		
				
				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
					this.dh.ExecutarSql("INSERT INTO CLIENTES (CODCLI, RAZSOC, STATUSCLIENTE, UF, CNPJ, TAB_PADRAO, CODEMP, LimCred, VlrDeb) VALUES (" + parser.getValue(e,"CODCLI") + ", '" + parser.getValue(e,"RAZSOC") + "', '" + parser.getValue(e,"STATUSCLIENTE") + "', '" + parser.getValue(e,"UF") + "', '" + parser.getValue(e,"CNPJ") + "', " + parser.getValue(e,"TAB_PADRAO") + ", " + parser.getValue(e,"CODEMP") + ", '" + parser.getValue(e,"LimCred") + "', '" + parser.getValue(e,"VlrDeb") + "')");
					iCont++;
				}

				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
			
			/*Se importou registros*/
			
			if (iCont>0) {				
				sMensagemLog = sMensagemLog  + "\n CLIENTES : " + iCont + " registro(s).";
			} else {
				sMensagemLog = sMensagemLog  + "\n ATENÇÃO !!! CLIENTES não encontrados. Dados Obrigatórios.";
			}
			
		} catch (Exception e) {
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}

	}        

	private void ImportarCondicoesCliente() {

		XMLParser parser = new XMLParser();
		try{

			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			
			if (isOnline()==false)
			{				
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Importação condições de pagamento do cliente");
				builder.setMessage("Arquivo de Configuração conf_offline.txt não encontrado no diretório raiz.");
				builder.setPositiveButton("OK", null);
				builder.show();    
				return;
			}

			TextView ID_Vendedor = (TextView) findViewById(R.id.tbRecebe);
			String xml = parser.getXmlFromUrl(sPath + "/xml/" + ID_Vendedor.getText() + "condcli.xml"); // getting XML

			Document doc = parser.getDomElement(xml); // getting DOM element

			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{                                
				this.dh.ExecutarSql("DELETE FROM CONDICOES_CLIENTE");                                                            
				progressBar.setMax(nl.getLength());                        
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);                                        
			}      

			int iCont=0;
			
			// grava dados xml no banco
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);		

				/*51849*/
				/*Só importa os dados da empresa configurada*/
				if (sEmpresa.equals(parser.getValue(e,"CODEMP"))) {
					this.dh.ExecutarSql("INSERT INTO CONDICOES_CLIENTE (CODCLI, CodFrmPgt, CodTipPrz, CodEmp) VALUES (" + parser.getValue(e,"CodCli") + ", " + parser.getValue(e,"CodFrmPgt") + ", " + parser.getValue(e,"CodTipPrz") + ", " + parser.getValue(e,"CodEmp") + ")");
					iCont++;
				}
				
				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);                       
			}
						
			sMensagemLog = sMensagemLog  + "\n COND. PAGTO. CLIENTES : " + iCont + " registro(s).";
			
			
		} catch (Exception e) {
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}

	}        

	private void ImportaProdutos () {

		XMLParser parser = new XMLParser();
		try{
			
			TextView lbMsg = (TextView) findViewById(R.id.txArq);

			if (isOnline()==false)
			{				
				lbMsg.setText("Não há conexão. Verifique e tente mais tarde");             
				android.os.SystemClock.sleep(6000); 
				return;
			} 

			progressBarStatus=0;    

			if ("".equals(sPath)) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Importação de Produtos");
				builder.setMessage("Arquivo de Configuração conf_offline.txt não encontrado no diretório raiz.");
				builder.setPositiveButton("OK", null);
				builder.show();    
				return;
			}

			String xml = parser.getXmlFromUrl(sPath + "/xml/produtos.xml"); // getting XML

			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			//se encontrou xml
			if (nl.getLength() >0)
			{
				this.dh.deleteAll();
				this.dh.ExecutarSql("DELETE FROM produtos");

				progressBar.setMax(nl.getLength());
				progressBarStatus=0;
				progressBar.setProgress(progressBarStatus);
			}      

			int iCont=0;
			
			/*Grava dados xml no banco*/
			for (int i = 0; i < nl.getLength(); i++) 
			{
				Element e = (Element) nl.item(i);
				
				/*Se estiver marcado para ler todos produtos de todas as empresas*/
				if (this.dh.bTodosProdutos(sEmpresa)) {
					this.dh.insert(
							parser.getValue(e, KEY_ID), 
							parser.getValue(e, KEY_NAME), 
							parser.getValue(e, KEY_COST), 
							parser.getValue(e, KEY_UNIDADE), 
							parser.getValue(e, KEY_SALDO), 
							parser.getValue(e, KEY_PERCDESC), 
							parser.getValue(e, KEY_M_UNIDADE), 
							parser.getValue(e, KEY_QTDCAIXA),
							parser.getValue(e, KEY_CODEMP),
							parser.getValue(e, KEY_CODSEC_SERVMERC));
					iCont++;
					
				} 
				else /*Le somente produtos da empresa selecionada*/
				{
					/*Só importa os dados da empresa configurada*/
					if (sEmpresa.equals(parser.getValue(e,KEY_CODEMP))) {
						this.dh.insert(
								parser.getValue(e, KEY_ID), 
								parser.getValue(e, KEY_NAME), 
								parser.getValue(e, KEY_COST), 
								parser.getValue(e, KEY_UNIDADE), 
								parser.getValue(e, KEY_SALDO), 
								parser.getValue(e, KEY_PERCDESC), 
								parser.getValue(e, KEY_M_UNIDADE), 
								parser.getValue(e, KEY_QTDCAIXA),
								parser.getValue(e, KEY_CODEMP),
								parser.getValue(e, KEY_CODSEC_SERVMERC));
						iCont++;
					}
				}
				                     
				progressBarStatus=progressBarStatus+1;
				progressBar.setProgress(progressBarStatus);  
				
			}
			
			/*Se importou registros*/			
			if (iCont > 0) {				
				sMensagemLog = sMensagemLog  + "\n PRODUTOS : " + iCont + " registro(s).";
			} else {
				sMensagemLog = sMensagemLog  + "\n ATENÇÃO !!! PRODUTOS não encontrados. Dados Obrigatórios.";
			}
			
		} 
		catch (Exception e) 
		{
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText(e.getMessage());             
		}
	}

	public boolean isOnline() {
		boolean bRetorno=false;

		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();

			if (netInfo != null && netInfo.isConnectedOrConnecting()) 
			{
				bRetorno = true;
			} else {
				bRetorno = false;
			}        
		} catch (Exception e){                
			TextView lbMsg = (TextView) findViewById(R.id.txArq);
			lbMsg.setText("Não há conexão. Verifique e tente mais tarde - " + e.getMessage());             
		}
		return bRetorno;
	}    
}
