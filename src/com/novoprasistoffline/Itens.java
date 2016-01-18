package com.novoprasistoffline;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class Itens extends Activity {
    
    public Itens clsItens;   
    public  DataHelper dh;      
    public  String nome;
    public  TextView txtCond;
    public  TextView txtCliente;
    public  EditText preco;
    public  EditText qtd;
    public  EditText desc;    
    private Spinner spn1;
    public static  String preco_ant;
    public File arq;
    public TextView saldo;
    public static float desconto_max;
    public TextView nPedido;
    public TextView tvPrecoLiq;
    public static String observacao;
    public static String posicao_combo_condicao;
    public static BigDecimal iQtdCaixa; 
    private static int iProduto 	= 0;
    private static int iDescricao 	= 1;
    private static int iQtd 		= 2;
    private static int iPreco 		= 3;
    private static int iDesc 		= 4;
    private static int iObs 		= 5;
    public String sValorVerba[];
    public static String iCodTipPrc; 
    public static String iCodTipMov;
    public static String iCodTipPrz; 
    public static String sPrazoTabela="";
    public static String sGuardaPrazoPrincipal="";
    public BigDecimal cVerbaLiberada;
    public BigDecimal cVerbaOriginal;
    public BigDecimal cPercMaximoVerba;
    public String sEmpresa;/*51849*/
    public String lePedido;
    public CheckBox ckInterno;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itens);

        final Date datapedido = new Date();  
        final SimpleDateFormat formatador = new SimpleDateFormat("ddMMyyyy");  
        
        nPedido     = (TextView) findViewById(R.id.tbPedido);                
        txtCond     = (TextView) findViewById(R.id.tbCondicao);
        txtCliente  = (TextView) findViewById(R.id.tbCliente);
        tvPrecoLiq  = (TextView) findViewById(R.id.tvPrecoLiq);
        
        txtCliente.setText("");
        txtCond.setText("");
                
        Intent intent   = getIntent();
        lePedido = intent.getStringExtra("recebe");
        
        this.dh = new DataHelper(getApplicationContext());                           

        /*51849 - Acrescentado Empresa*/
		/*Busca Empresa parametrizada, pois importará os dados somente da empresa informada.*/		
		List<String[]> sParametros;
		sParametros 		= this.dh.RetornaParametros("");
 	   
 	    for (String[] name : sParametros) 
 	    { 		   
 		   sEmpresa		  = name[8].toString(); /*51849 - Acrescentado Empresa*/
 	    }
 	     	    
        BuscaValorAtualizadoVerba();
        
        Spinner cbUnidadeVenda 	= (Spinner) findViewById(R.id.cbUnidade); //Instancia o objeto
        String sUnidadeVenda 	= dh.BuscaParametros(); //Busca qual unidade de venda será utilizada
        int index=0;
        
        //Procura no spinner o indice desejado
        for (int i=0;i < cbUnidadeVenda.getCount();i++)
        { 
            if (cbUnidadeVenda.getItemAtPosition(i).toString().equals(sUnidadeVenda))
            {
                index = i;
            }
        }
        
        Spinner cbPrazosTab = (Spinner) findViewById(R.id.cbPrazo); //Instancia o objeto
                        
        if (dh.bTabelaLivre()==true)
        {
            CarregarPrazos("");
            cbPrazosTab.setEnabled(true);
        } else 
        {
            cbPrazosTab.setEnabled(false);
        }
        
        //Seta o indice encontrado
        cbUnidadeVenda.setSelection(index);
                
        //Se foi passado id na pesquisa, busca pedido especifico
        if (lePedido!=null)
        {   
            //Pega pedido
            String pega_idpedido[]  = lePedido.toString().split("=");    
            String pega_idpedido2[] = pega_idpedido[1].toString().split(",");
            
            //Cliente
            String pega_cliente[] 	= pega_idpedido[2].toString().split(",");
            
            //Pega forma pagto.
            //String pega_condpagto[] = pega_idpedido[5].toString().split(",");
            
            //Pega Prazo Pagto
            String pega_prazoPagto[] = pega_idpedido[3].toString().split(",");
            
            listar_itens(false,pega_idpedido2[0].toString());
            
            nPedido.setText(pega_idpedido2[0].toString());
            txtCliente.setText(pega_cliente[0].toString().substring(0, 20));
           //txtCond.setText(pega_idpedido[5].replace("}", "").toString() + " ==> " + pega_condpagto[0].replace("}", "").toString());
            txtCond.setText(pega_idpedido[6].replace("}", "").toString() + " ==> " + pega_prazoPagto[0].toString());
            
            // = pega_prazoPagto[0].toString().trim().substring(0, 3);
            
            //this.dh = new DataHelper(getApplicationContext());                           
            List<String[]> names2 =null;
            names2 = dh.LePedido(pega_idpedido2[0].toString());

            for (String[] name : names2) 
            {
                iCodTipPrc = name[8];
                iCodTipPrz = name[9];
                iCodTipMov = name[10];
                sGuardaPrazoPrincipal = iCodTipPrz;
            }
        
        } 
        else 
        {            
            PegaDadosPrePedido();            
            listar_itens(false,"");
        }        
        
        //botao pesquisar    
        final Button buView = (Button) findViewById(R.id.buPesquisar);
        buView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pesquisar();                       
            }
        });

        //botao adicionar item
        final Button buAdd = (Button) findViewById(R.id.buAdd);
        buAdd.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                //se passar na validacao de campos , adiciona item  
                if (validacao_campos()){
                    adicionar_item();     
                }
            }
        });
        
        //Somente gera nova numeracao se nao for pesquisa...
        if (lePedido==null) {
            GeraNumPed();
        }

        //botao gravar    
        final Button buGerar = (Button) findViewById(R.id.buGerar);
        buGerar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                
                listar_itens(true, nPedido.getText().toString());                                 
            }
        });
        
        final EditText preco_novo = (EditText) findViewById(R.id.tbPreco);
        preco_novo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {            
            if (hasFocus==false){ //quando perde o foco
                if (!"".equals(preco_novo.getText().toString()))
                {
                    calcular_desconto(preco_novo.getText().toString());
                } 
                else 
                {
                    if (preco_ant !=null)
                    {
                    	if (preco_ant != "") 
                    	{
                    		preco.setText(preco_ant.toString());
                    	}
                    }
                }                                
            }                         
        }
        });
        
        final ListView list = (ListView) findViewById(R.id.lsItens);        
        
        AdapterView.OnItemClickListener onItemClick_List = new AdapterView.OnItemClickListener() {
            
            @SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView arg0, View view, int position, long index) {            
                if (position>0){ 
                    excluir_item(arg0.getItemAtPosition(position).toString(), "ped_" + nPedido.getText().toString() + "_" + formatador.format(datapedido));                              
                }
            }
        };

        list.setOnItemClickListener(onItemClick_List);
        
        final EditText desconto = (EditText) findViewById(R.id.tbDesconto);
        desconto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {            
            if (hasFocus==false){ //quando perde o foco
                if (!"".equals(desconto.getText().toString())) {
                    calcular_desconto(desconto.getText().toString());                    
                } else {
                	
                	 if (preco_ant !=null)
                     {
                     	if (preco_ant != "") 
                     	{
                     		preco.setText(preco_ant);
                            tvPrecoLiq.setText(preco_ant);
                     	}
                     }
                    
                }                
            }                         
        }
        });     

    }
    
    private void BuscaValorAtualizadoVerba()
    {
    	sValorVerba 		= this.dh.BuscaValorVerba(); /*Verifica se tem valor de verba para o vendedor*/
        cVerbaLiberada 		= new BigDecimal(sValorVerba[0]);
        
        if (sValorVerba[1].length()>0) 
        {
        	cPercMaximoVerba 	= new BigDecimal(sValorVerba[1]);	
        } else {
        	cPercMaximoVerba 	= new BigDecimal("0");
        }
        	
    }
    
    
    private Boolean AtualizaVerbaFinal() 
    {
        /*
    	 * sTipoOperacao : - (Subtraindo, significa que subtrai da verba disponivel do cliente e soma no total de descontos)
    	 * 				   + (Somando, significa que está excluindo o item e retornando o valor a verba, subtraindo do total de descontos o valor) 
         */
    	
    	BuscaValorAtualizadoVerba();
    	if (sValorVerba[2].length()>0)
    	{
    		if (Float.parseFloat(sValorVerba[0].toString())<=0)
    		{
    			return false;
    		}
    	}
    	
    	TextView totaldesconto 	= (TextView) findViewById(R.id.lbDescTotal);
    	TextView totalpedido 	= (TextView) findViewById(R.id.txTotal);
    	Boolean bRetorno 		= true;
    	
    	BigDecimal cTotalPedido 	= new BigDecimal(totalpedido.getText().toString());
    	BigDecimal cValorPassado 	= new BigDecimal(totaldesconto.getText().toString());
    	BigDecimal sGuardaDesconto 	= new BigDecimal(totaldesconto.getText().toString());
    	BigDecimal cTotalDesconto 	= new BigDecimal(totaldesconto.getText().toString());
    	BigDecimal cResultadoVerba 	= new BigDecimal("0");
    	    	
    		
		/*Validar se tem saldo de verba e se somando com o desconto atual ainda terá*/
		/*
		 * signum :
		 * quando > 0 	=  1
		 * quando 0 	=  0
		 * quando < 0 	= -1
	     */
		cResultadoVerba = cVerbaLiberada;
		
		if (cResultadoVerba.subtract(cValorPassado).signum()!=-1) 
		{              	 
			/*Verifica se total desconto não ultrapassa limite em percentual*/
			//BigDecimal cPercentualProporcional = (Float.parseFloat(totaldesconto.getText().toString().replace(",", "."))) / cTotalPedido * 100;
			BigDecimal cPercentualProporcional = cTotalDesconto.divide(cTotalPedido,RoundingMode.HALF_EVEN);
			cPercentualProporcional = cPercentualProporcional.multiply(new BigDecimal("100"));
			
			int res = cPercentualProporcional.compareTo(cPercMaximoVerba);
			
		    if (res==1) /*compara percentual proporcional com o percentual máximo*/
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Pedidos OffLine");
				builder.setMessage("Desconto " + cPercentualProporcional.toString() + " superior ao percentual permitido " + cPercMaximoVerba.toString() + ".");
				builder.setPositiveButton("OK", null);
				builder.show();		
				totaldesconto.setText(sGuardaDesconto.toString()); /*Deixa o total de desconto como estava*/
				bRetorno=false;
			}				
		}
		/*
		else 
		{
			cResultadoVerba = cResultadoVerba.setScale(2, BigDecimal.ROUND_FLOOR);
			cValorPassado 	= cValorPassado.setScale(2, BigDecimal.ROUND_FLOOR);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pedidos OffLine");
			builder.setMessage("Verba não disponível. O Saldo da verba é de : " + cResultadoVerba.toString() + " e o desconto total desse ítem totalizará : " + cValorPassado.toString() + "");
			builder.setPositiveButton("OK", null);
			builder.show();		
			totaldesconto.setText(sGuardaDesconto.toString()); //Deixa o total de desconto como estava
			bRetorno=false;	
		}
		*/
    	return bRetorno;
    }
    
    
    private Boolean AtualizaVerba(String sValor, String sTipoOperacao, float cValorTotalItem) 
    {
        /*
    	* sTipoOperacao : - (Subtraindo, significa que subtrai da verba disponivel do cliente e soma no total de descontos)
    	* 				   + (Somando, significa que está excluindo o item e retornando o valor a verba, subtraindo do total de descontos o valor) 
        */
    	
    	BuscaValorAtualizadoVerba();
    	if (sValorVerba[2].length()>0)
    	{
    		if (Float.parseFloat(sValorVerba[0].toString())<=0)
    		{
    			return false;
    		}
    	}
    	
    	TextView totaldesconto 	= (TextView) findViewById(R.id.lbDescTotal);
      //TextView totalpedido 	= (TextView) findViewById(R.id.txTotal);
    	Boolean bRetorno 		= true;
    	
    	BigDecimal cResult;
      //BigDecimal cTotalPedido 	= new BigDecimal(totalpedido.getText().toString());
    	BigDecimal cValorPassado 	= new BigDecimal(sValor);
    	BigDecimal sGuardaDesconto 	= new BigDecimal(totaldesconto.getText().toString());
    	BigDecimal cTotalDesconto 	= new BigDecimal(totaldesconto.getText().toString());
    	BigDecimal cResultadoVerba 	= new BigDecimal("0");
    	
    	/*Verifica se soma ou subtrai desconto no total geral de descontos*/
    	if (("+").equals(sTipoOperacao)) /*Estorno, subtrai desconto do total*/
    	{    		
    		//cResult = Float.parseFloat(totaldesconto.getText().toString().replace(",", ".")) - Float.parseFloat(sValor.replace(",", "."));    		
    		cResult = cTotalDesconto.subtract(cValorPassado);
    	} 
    	else /*Dando desconto*/
    	{
    		//cResult = Float.parseFloat(totaldesconto.getText().toString().replace(",", ".")) + Float.parseFloat(sValor.replace(",", "."));	
    		cResult = cTotalDesconto.add(cValorPassado);
    	}
    	
    	totaldesconto.setText(cResult.toString()); /*Exibe total descontos na label*/
    	    	
    	if (("-").equals(sTipoOperacao))
    	{ /*Se estiver dando desconto...*/
    		
    		/*Validar se tem saldo de verba e se somando com o desconto atual ainda terá*/
    		/*
    		 * signum :
    		 * quando > 0 	=  1
    		 * quando 0 	=  0
    		 * quando < 0 	= -1
    	     */
    		cResultadoVerba = cVerbaLiberada;
    		
    		if (cResultadoVerba.subtract(cValorPassado).signum()!=-1) 
    		{    		
    			/*51907 - Fazer a validação da verba somente no final, quando o usuario gravar o pedido*/
    			/*
    			//Valida se já tem valor de itens adicionados e se a somatoria dos descontos dados não ultrapassao o % máximo em relação ao total do pedido
    			if ((Float.parseFloat(totalpedido.getText().toString().replace(",", ".")) > 0 )) Já tem valor total de pedido... 
    			{
    				//cTotalPedido = Float.parseFloat(totalpedido.getText().toString().replace(",", "."));
    			} else //SE não tiver, significa que é o primeiro item adicionado 
    			{
    				cTotalPedido = new BigDecimal(Float.toString(cValorTotalItem));
    			}
    			           	 
    			Verifica se total desconto não ultrapassa limite em percentual
				//BigDecimal cPercentualProporcional = (Float.parseFloat(totaldesconto.getText().toString().replace(",", "."))) / cTotalPedido * 100;
				BigDecimal cPercentualProporcional = cTotalDesconto.divide(cTotalPedido,MathContext.DECIMAL32);
				cPercentualProporcional = cPercentualProporcional.multiply(new BigDecimal("100"));
				
				//if (cPercentualProporcional > Float.parseFloat(sValorVerba[1].replace(",", "."))) //compara percentual proporcional com o percentual máximo
				int res = cPercentualProporcional.compareTo(cPercMaximoVerba);
				
			    if (res==1) //compara percentual proporcional com o percentual máximo
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Pedidos OffLine");
					builder.setMessage("Desconto " + cPercentualProporcional.toString() + " superior ao percentual permitido " + cPercMaximoVerba.toString() + ".");
					builder.setPositiveButton("OK", null);
					builder.show();		
					totaldesconto.setText(sGuardaDesconto.toString()); //Deixa o total de desconto como estava
					bRetorno=false;
				}			
			    */	
    		}
    		else 
    		{
    			cResultadoVerba = cResultadoVerba.setScale(2, BigDecimal.ROUND_FLOOR);
    			cValorPassado 	= cValorPassado.setScale(2, BigDecimal.ROUND_FLOOR);
    			
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("Pedidos OffLine");
    			builder.setMessage("Verba não disponível. O Saldo da verba é de : " + cResultadoVerba.toString() + " e o desconto total desse ítem totalizará : " + cValorPassado.toString() + "");
    			builder.setPositiveButton("OK", null);
    			builder.show();		
    			totaldesconto.setText(sGuardaDesconto.toString()); /*Deixa o total de desconto como estava*/
    			bRetorno=false;	
    		}
    	}
    	    	
    	if (bRetorno==true) /*Só atualiza a verba se não houver impedimentos*/
    	{
    		if (cValorPassado.toString()!="0") 
    		{
    			if (("+").equals(sTipoOperacao))
            	{
            		this.dh.ExecutarSql("UPDATE VERBAS SET VERBALIBERADA = VERBALIBERADA + '" + cValorPassado.toString() + "'");	
            	} 
            	else
            	{
            		this.dh.ExecutarSql("UPDATE VERBAS SET VERBALIBERADA = VERBALIBERADA - '" + cValorPassado.toString() + "'");
            	}	
    		}    			
    	}
    	    	
    	return bRetorno;
    }
    
    private void PegaDadosPrePedido() {
        //Pega condicao de pagamento passada na aba anterior...        
        try 
        {
            Intent i = getParent().getIntent();

            posicao_combo_condicao = i.getStringExtra("posicao_condicao");
            
            if (!"".equals(i.getStringExtra("obs")))
            {            
               observacao = i.getStringExtra("obs");
            } else
            {
               observacao="";
            }
            
            if (!"".equals(i.getStringExtra("movimentacao")))
            {
                if ("Selecione uma Movimentação".equals(i.getStringExtra("movimentacao")) || i.getStringExtra("movimentacao")==null){            
                    
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);  

                    alert.setTitle("Pedidos OffLine");
                    alert.setMessage("Selecione uma Movimentação");                    
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
                        public void onClick(DialogInterface dialog,  int whichButton) {                                  
                            Intent in = new Intent(Itens.this, PrasistAndroidOffLine.class);                       
                            finish();
                            startActivity(in);
                        }  
                    });  

                    alert.show();  
                    
                } else 
                {
                    iCodTipMov = i.getStringExtra("movimentacao").substring(0,3);            
                }
            }
            
            if (!"".equals(i.getStringExtra("tabela")))
            {            
                String tabelas[] = i.getStringExtra("tabela").toString().split("==>");
                iCodTipPrc = tabelas[0].toString().trim().substring(0, 3);
                iCodTipPrz = tabelas[1].toString().trim().substring(0, 3);                
                sGuardaPrazoPrincipal = tabelas[1].toString().trim().substring(0, 3);                
            }
                    
            if (!"".equals(i.getStringExtra("condicao")))
            {            
                if ("Selecione uma Condição".equals(i.getStringExtra("condicao")) || i.getStringExtra("condicao")==null){
                    
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);  
  
                    alert.setTitle("Pedidos OffLine");
                    alert.setMessage("Selecione uma Condição de Pagamento");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
                        public void onClick(DialogInterface dialog,  int whichButton) {                                  
                            Intent in = new Intent(Itens.this, PrasistAndroidOffLine.class);                       
                            finish();
                            startActivity(in);
                        }  
                    });  

                    alert.show();  
                    
                } else {
                    txtCond.setText(i.getStringExtra("condicao"));            
                }   
            }  
            
            
            Spinner cbPrazosTab = (Spinner) findViewById(R.id.cbPrazo); //Instancia o objeto
            
            /*51907 - Trazer a combo do prazo da tabela de preço conforme tela anterior*/
            if (dh.bTabelaLivre()==true) {
            	int index=0;
                
                //Procura no spinner o indice desejado
                for (int x=0;x < cbPrazosTab.getCount();x++)
                { 
                	if (x>0) 
                	{
                		if (cbPrazosTab.getItemAtPosition(x).toString().substring(0, 3).equals(sGuardaPrazoPrincipal))
                        {
                            index = x;
                        }	
                	}                
                }

               	//Seta o indice encontrado
                cbPrazosTab.setSelection(index);	
            }
            
            
            if (!"".equals(i.getStringExtra("cliente")))
            {            
                if ("Selecione um Cliente".equals(i.getStringExtra("cliente")) || i.getStringExtra("cliente")==null){            
                    
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);  

                    alert.setTitle("Pedidos OffLine");
                    alert.setMessage("Selecione um Cliente");                    
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
                        public void onClick(DialogInterface dialog,  int whichButton) {                                  
                            Intent in = new Intent(Itens.this, PrasistAndroidOffLine.class);                       
                            finish();
                            startActivity(in);
                        }  
                    });  

                    alert.show();  
                    
                } else {
                    txtCliente.setText(i.getStringExtra("cliente"));            
                }
            } 
            
            //Recalcular itens se houve alteração da condição / tabela
            String sNovaCondicao="";
            String sNovaTabela="";
            sNovaCondicao = i.getStringExtra("nova_condicao");                                    
            sNovaTabela  = i.getStringExtra("nova_tabela");
            
            if ((sNovaCondicao!="" && sNovaCondicao!=null) || (sNovaTabela!="" && sNovaTabela!=null)) 
            {            	
            	RecalculaItens();
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();            
        }
    }
    
    private void RecalculaItens() {
    	
    	/*--------------------------------------------VARIÁVEIS--------------------------------------------*/
    	ListView lvItens;    	
    	List<String[]> names2 		= null ;
    	String[] sAdicionaProduto 	= new String[] {"produto", "qtd", "preco", "desc", "total"};
    	BigDecimal total 		  	= new BigDecimal("0");
        BigDecimal percdesc		  	= new BigDecimal("0");
        BigDecimal totalDesc	  	= new BigDecimal("0");        
        BigDecimal cPrecoUnitario 	= new BigDecimal("0");
        BigDecimal cDesconto 	  	= new BigDecimal("0");
        BigDecimal cQuantidade 	  	= new BigDecimal("0");        
        //List<String> listItems2 	= new ArrayList<String>();
    	Intent i 					= getParent().getIntent();    	
    	lvItens  					= (ListView)findViewById(R.id.lsItens);
    	/*--------------------------------------------VARIÁVEIS--------------------------------------------*/
    	
    	//Pega Tipo preco e prazo da nova tabela
    	if (!"".equals(i.getStringExtra("tabela")))
        {            
            String tabelas[] = i.getStringExtra("tabela").toString().split("==>");
            iCodTipPrc 		 = tabelas[0].toString().trim().substring(0, 3);
            iCodTipPrz 	     = tabelas[1].toString().trim().substring(0, 3);
        }
    	
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
         
        String[] from = new String[] {"produto", "qtd", "preco", "desc", "total"};
        int[] to = new int[] { R.id.produto, R.id.qtd, R.id.preco, R.id.desc, R.id.total };

        //ArrayAdapter<String> listagem2 = new ArrayAdapter<String>(this,
          //          android.R.layout.simple_gallery_item, listItems2);
        
        map = new HashMap<String, String>();
        map.put("produto", "PRODUTO");
        map.put("qtd", "QTD");
        map.put("preco", "PREÇO");
        map.put("desc", "% DESC.");
        map.put("total", "TOTAL");        
        fillMaps.add(map);        
        
    	//Percorre os itens adicionados para regravar os valores de preco
    	for (int iCont = 0; iCont < lvItens.getCount(); iCont++) {

    		if (iCont > 0) 
    		{	            
    			/*
    			 * sProdutoSelecionado[] = 0 (total), 1 (Preco), 2 (Desc), 3 (Produto), 4 (Qtd)    			 
    			 * sProdutoSelecionadoDetalhes[] = 0 (Descricao), 1 (Conteudo)
    			 * 
    			 * */
    			String sProdutoSelecionado[]  		 		= lvItens.getItemAtPosition(iCont).toString().split(",");    
	            //String sProdutoSelecionadoDetalhesPreco[] 	= sProdutoSelecionado[1].toString().split("=");
	            String sProdutoSelecionadoDetalhesDesc[] 	= sProdutoSelecionado[2].toString().split("=");
	            String sProdutoSelecionadoDetalhesProduto[] = sProdutoSelecionado[3].toString().split("=");
	            String sProdutoSelecionadoDetalhesQtd[] 	= sProdutoSelecionado[4].toString().split("=");
	            	            
	   			//Busca Preco da nova tabela 
	   			names2 = this.dh.BuscaPrecoProdutos(" id = " + sProdutoSelecionadoDetalhesProduto[1].substring(0,9), iCodTipPrc, iCodTipPrz);        
	   	     
	   	        //ID, NOME, T.PRECO, UNIDADE, QTDCAIXA, T.CODTIPPRC, T.CODTIPPRZ, C.DESFRMPGT, C.DESTIPPRZ F
	   	        for (String[] name : names2) 
	   	        {
	   	        	cPrecoUnitario = new BigDecimal(name[2]);
	   	        	cQuantidade    = new BigDecimal(sProdutoSelecionadoDetalhesQtd[1].replace("}", ""));
	   	        	cDesconto 	   = new BigDecimal(sProdutoSelecionadoDetalhesDesc[1]);
	   	        	
	   	        	cPrecoUnitario 	= cPrecoUnitario.setScale(2, BigDecimal.ROUND_FLOOR);
	   	        	cDesconto 		= cDesconto.setScale(2, BigDecimal.ROUND_FLOOR);
	   	        	
	   	        	if (sProdutoSelecionadoDetalhesDesc[1].length()>0) 
	   	            {   
	   	                percdesc = cPrecoUnitario.multiply(cDesconto).divide(new BigDecimal("100"));
	   	            }
	   	        	
	   	        	total = total.add(cPrecoUnitario.subtract(percdesc).multiply(cQuantidade));
	   	        	totalDesc 		= totalDesc.add(percdesc.multiply(cQuantidade));
	   	        	
	   	        	sAdicionaProduto[0] = preencheCom(name[0],"0",9,1) + " - " + name[1]; 	  		//produto
	   	        	sAdicionaProduto[1] = sProdutoSelecionadoDetalhesQtd[1].replace("}", ""); 	//qtd
	   	        	sAdicionaProduto[2] = cPrecoUnitario.toString(); //name[2]; 						  		//preco
	   	        	sAdicionaProduto[3] = cDesconto.toString();//sProdutoSelecionadoDetalhesDesc[1]; 	//desconto
	   	        	sAdicionaProduto[4] = total.toString();						//total
	   	        	
	   	        	
	   	        	map = new HashMap<String, String>();
	   	            
	   	            map.put("produto", sAdicionaProduto[0]);
	   	            map.put("qtd", sAdicionaProduto[1]);
	   	            map.put("preco", sAdicionaProduto[2]);
	   	            
	   	            if (!"".equals(name[iDesc])) {
	   	                map.put("desc", sAdicionaProduto[3]);
	   	            } 
	   	            else
	   	            {
	   	                map.put("desc", "0.00");
	   	            }
	   	            
	   	            map.put("total", cPrecoUnitario.subtract(percdesc).multiply(cQuantidade).toString());
	   	            fillMaps.add(map);
	   	            
	   	            String condicoes[] 		= txtCond.getText().toString().split("==>");
	   	            final Date datapedido 	= new Date();
	   	            final SimpleDateFormat formatador = new SimpleDateFormat("ddMMyyyy");
	   	            String sRegistro 		= "ped_" + nPedido.getText().toString()  + "_" + formatador.format(datapedido);
	   	            Spinner univenda 		= (Spinner) findViewById(R.id.cbUnidade);        
	   	            String sUniVend  		= univenda.getSelectedItem().toString();
	   	         
	   	            //Grava Item do Pedido
					this.dh.GravaPedido("INSERT OR REPLACE INTO PEDIDOS (Status, CodEmp, id_pedido, CodTabPrc, CodTabPrz, codcli, codfrmpgt, codtipprz, datapedido, arquivo, produto, descricao, qtd, preco, desc, obs, univenda, qtdcaixa, CodTipMov) VALUES " + 
							"('Bloqueado', " + 
							sEmpresa + ", " + 
							nPedido.getText().toString() + ", " + 
							iCodTipPrc + ", " + 
							iCodTipPrz + ", " + 
							txtCliente.getText().toString().trim().substring(0, 8) + "," + 
							condicoes[0].toString().trim().substring(0, 3) + ", " + 
							condicoes[1].toString().trim().substring(0, 3) + ", '" + 
							formatador.format(datapedido) + "', '" + 
							sRegistro + "', '" + 
							preencheCom(name[0],"0",9,1) + "', '" + 
							name[1] + "', '" + 
							sAdicionaProduto[1] + "', '" +  
							sAdicionaProduto[2] + "', '" + 
							sAdicionaProduto[3] + "', '" + 
							observacao + "', '" + 
							sUniVend + "', '" + 
							iQtdCaixa.toString() + "', " + 
							iCodTipMov + ")");
					
	   	        }
    		}   			
   		}
    	       
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.listviewprodutos,from, to);
                
        lvItens.setAdapter(adapter);        //listagem
        lvItens.refreshDrawableState();

        TextView totalpedido   = (TextView) findViewById(R.id.txTotal);        
        total 	= total.setScale(2, BigDecimal.ROUND_FLOOR);
        totalpedido.setText(total.toString());
        
        TextView totalDesconto = (TextView) findViewById(R.id.lbDescTotal);
        totalDesc 	= totalDesc.setScale(2, BigDecimal.ROUND_FLOOR);
        totalDesconto.setText(totalDesc.toString());
        
    }
        
    @Override
    public void onResume()
    {
    	super.onResume();
    	PegaDadosPrePedido();
    }
    
    private void GeraNumPed()
    {       
        String iPedido = Integer.toString(this.dh.ProximoPedido());
        nPedido.setText(iPedido.toString());
    }
    
    private void excluir_item(final String produto, final String sArquivo) 
    {
        
         if ("".equals(produto) || "PRODUTO".equals(produto))
         {
             return;
         }
         
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         
         int res = produto.indexOf("produto=");
         final String sProduto = produto.substring((res+8), (res+9+8));
                   
         builder.setMessage("Confirma a Exclusão do Ítem : " + sProduto + " ?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    
                	if (sValorVerba[2].length() > 0) /*Se houver verba para controlar*/ 
                    {
                		if (Float.parseFloat(sValorVerba[0].toString())>0) 
                		{                			
                			/*Busca qual o valor de desconto do item para estornar na verba utilizada*/
                			String sValorDesconto = dh.BuscaValorDescontoItem(sProduto.toString(),nPedido.getText().toString());

                			if (Float.parseFloat(sValorDesconto) > 0) /*Se encontrou valor maior que zero*/
                			{
                				AtualizaVerba(sValorDesconto, "+",0); /*Estorna valor da verba*/	
                			}
                		}
                    }
                	
                	/*Exclui item do pedido*/
                	dh.GravaPedido("DELETE FROM PEDIDOS WHERE id_pedido = '" + nPedido.getText().toString() + "' and produto = '" + sProduto.toString() + "'");
                    
                	listar_itens(false, nPedido.getText().toString()); //apenas lista os itens                                        
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
        
    }
    
	private void calcular_desconto(String valor) {
	    
		try 
		{       		
			desc = (EditText) findViewById(R.id.tbDesconto);
			
			BigDecimal result		 = new BigDecimal("0");
			BigDecimal cPreco 		 = new BigDecimal(preco.getText().toString());
			BigDecimal cDesc 		 = new BigDecimal("0");
			BigDecimal cValorPassado = new BigDecimal(valor);
	        
			if (desc.length()>0)  /*Se foi informado desconto*/
			{
				cDesc = new BigDecimal(desc.getText().toString());	
			}
			    		
	        if (desc.length()>0) //se foi dado desconto em percentual
	        { 		            
	            BigDecimal cPerc;
	            cPerc = cPreco.multiply(cDesc).divide(new BigDecimal("100"));
	            	     
	            //Arredondamento
	            cPerc = cPerc.setScale(2, BigDecimal.ROUND_CEILING); //arredonda para cima
	            
	            result = new BigDecimal(preco_ant);
	            result = result.subtract(cPerc);
	            
	            result = result.setScale(2, BigDecimal.ROUND_FLOOR);
	
	            tvPrecoLiq.setText(result.toString());
	        } 
	        else 
	        { //se foi alterado valor...
	
	            if (preco_ant.toString() == null ? valor != null : !preco_ant.toString().equals(valor)){
	                //result = 100 - (Float.valueOf(valor) * 100) / Float.valueOf(preco_ant.toString().replace(",", "."));
	                result =  cValorPassado.multiply(new BigDecimal("100")).divide(new BigDecimal(preco_ant),MathContext.DECIMAL32);
	                result = result.subtract(new BigDecimal("100"));
	                
	                //acrescimo	                
	                if (result.signum()==-1)	/* Maior que zero*/
	                {
	                	result = result.setScale(2, BigDecimal.ROUND_FLOOR);
	                	desc.setText(String.valueOf(result.abs().toString()));
	                    preco.setText(preco_ant.toString());
	
	                    //result = Float.valueOf(preco.getText().toString().replace(",", ".")) * Float.valueOf(desc.getText().toString().replace(",", ".")) /100;
	                    result = cPreco.multiply(cDesc).divide(new BigDecimal("100"));
	
	                    //String cValor = String.valueOf(Float.valueOf(preco.getText().toString().replace(",", "."))-result);
	                    result = cPreco.subtract(result);
	
	                    result = result.setScale(2, BigDecimal.ROUND_FLOOR);
	                    tvPrecoLiq.setText(result.toString());
	                }
	                else
	                {	                	
	                    preco.setText(cValorPassado.toString());	                    
	                    tvPrecoLiq.setText(cValorPassado.toString());
	                }                
	            }
	        }
	        
	        String sValidaVerbaMovimentacao = this.dh.ValidaVerbaMovimentacao(iCodTipMov);
	        
	        if (desc.length()>0) /*Se foi dado desconto*/ 
	        {		        	
	        	/*Se não houver verba valida limite de desconto do item, caso contrário deixa qualquer desconto*/
	        	if (Float.parseFloat(sValorVerba[2].toString())<=0 || "N".equals(sValidaVerbaMovimentacao)) 
	        	{	            	
	        		int res = cDesc.compareTo(new BigDecimal(desconto_max));
	
	        		if (res==1) /*compara percentual proporcional com o percentual máximo*/
	        		{
	        			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        			builder.setTitle("Pedidos OffLine");
	        			builder.setMessage("Desconto " + cDesc.toString() + "% superior ao limite permitido para o produto " + desconto_max + "%. Valor alterado para preço de tabela");
	        			builder.setPositiveButton("OK", null);
	        			builder.show();    
	        			desc.setText("");
	        			preco.setText(String.valueOf(preco_ant));
	        			tvPrecoLiq.setText(String.valueOf(preco_ant));
	        		}
	        	}
	        }
	
	        
		} catch (Exception exp) {    		          
			txtCond.setText("erro " + exp.getMessage());
		}
		    	
	}

    private boolean validacao_campos () {

        String sMsg="";

        qtd = (EditText) findViewById(R.id.tbQtd);
        if ("".equals(qtd.getText().toString()))
        {
            sMsg = "Informe a quantidade";   
            qtd.requestFocus();
        }

        preco = (EditText) findViewById(R.id.tbPreco);
        if ("".equals(preco.getText().toString()))
        {
            sMsg = "Informe o Preço";
            preco.requestFocus();
        }

        if (!"".equals(sMsg)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pedidos OffLine");
            builder.setMessage(sMsg);
            builder.setPositiveButton("OK", null);
            builder.show();    
            return false;
        } else {
            return true;
        }

    }
    
	private void adicionar_item() {
       	
		calcular_desconto(preco.getText().toString());

		try
		{
			qtd  = (EditText) findViewById(R.id.tbQtd);
			desc = (EditText) findViewById(R.id.tbDesconto); 

			Spinner univenda = (Spinner) findViewById(R.id.cbUnidade);        
			String sUniVend  = univenda.getSelectedItem().toString();

			if (sUniVend.equals("Maior Unidade"))
			{
				BigDecimal cResult = new BigDecimal(qtd.getText().toString()); 
				cResult.multiply(iQtdCaixa);// (Float.parseFloat(qtd.getText().toString()) * iQtdCaixa);
				qtd.setText(cResult.toString());
			}

			if ("Selecione uma mercadoria".equals(nome))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Pedidos OffLine");
				builder.setMessage("Selecione um Mercadoria.");
				builder.setPositiveButton("OK", null);
				builder.show();
				return;
			}

			//ID Pedido
			TextView IDPedido = (TextView) findViewById(R.id.tbPedido);

			//ID Cliente
			TextView IDCliente = (TextView) findViewById(R.id.tbCliente);

			//Data Atual
			Date datapedido = new Date();  
			SimpleDateFormat formatador = new SimpleDateFormat("ddMMyyyy");  

			//Pega condicao de pagamento
			//txtCond = (TextView) findViewById(R.id.tbCondicao);                        
			String condicoes[] = txtCond.getText().toString().split("==>");        

			String sRegistro = "ped_" + IDPedido.getText() + "_" + formatador.format(datapedido);

			String sValidaVerbaMovimentacao = this.dh.ValidaVerbaMovimentacao(iCodTipMov);
			
			/*Verifica se o item já existe para primeiro remover a verba anterior*/			
			if (sValorVerba[2].length() > 0 && "S".equals(sValidaVerbaMovimentacao)) //Se houver verba para controlar 
			{	
				if (Float.parseFloat(sValorVerba[0].toString())>0) 
				{
					//Busca qual o valor de desconto do item para estornar na verba utilizada
					String sValorDesconto = dh.BuscaValorDescontoItem(nome.substring(0, 9), IDPedido.getText().toString());

					if (sValorDesconto.length() > 0) //Se encontrou valor
					{					
						if (sValorDesconto.toString()!="0") 
						{
							AtualizaVerba(sValorDesconto, "+",0); //Estorna valor da verba
						}
					}
				}
			}
			
			Boolean bJaAdicionou=false;
			
			//Se houver tratamento para verbas
			if (sValorVerba[2].length() > 0 && "S".equals(sValidaVerbaMovimentacao))
			{
				if (Float.parseFloat(sValorVerba[0].toString())>0)
				{
					float cValor = 0;

					if (desc.getText().toString().length()>0)
					{
						if (Float.parseFloat(desc.getText().toString().replace(",", "."))>0) 
						{
							cValor = (Float.parseFloat(qtd.getText().toString()) * Float.parseFloat(preco.getText().toString().replace(",", "."))) * Float.parseFloat(desc.getText().toString().replace(",", ".")) / 100;

							float cValorTotalItem = Float.parseFloat(qtd.getText().toString()) * Float.parseFloat(preco.getText().toString().replace(",", "."));

							if (cValor>0)
							{
								if (AtualizaVerba(String.valueOf(cValor),  "-", cValorTotalItem)==true)
								{ //Subtrai do valor da verba disponivel do vendedor
									//grava item do pedido
									this.dh.GravaPedido("INSERT OR REPLACE INTO PEDIDOS (Status, CodEmp, id_pedido, CodTabPrc, CodTabPrz, codcli, codfrmpgt, codtipprz, datapedido, arquivo, produto, descricao, qtd, preco, desc, obs, univenda, qtdcaixa, CodTipMov) VALUES " + "('Bloqueado', " + sEmpresa + ", " + IDPedido.getText().toString() + ", " + iCodTipPrc + ", " + iCodTipPrz + ", " + IDCliente.getText().toString().trim().substring(0, 8) + "," + condicoes[0].toString().trim().substring(0, 3) + ", " + condicoes[1].toString().trim().substring(0, 3) + ", '" +  formatador.format(datapedido) + "', '" + sRegistro + "', '" + nome.substring(0, 9) + "', '" + nome.substring(10, nome.toString().length()) + "', '" + qtd.getText().toString() + "', '" + preco.getText().toString().replace(",", ".") + "', '" + desc.getText().toString().replace(",", ".") + "', '" + observacao + "', '" + sUniVend + "', '" + iQtdCaixa.toString() + "', " + iCodTipMov + ")");
									
									//Grava somente uma condicao e cliente para os itens
									this.dh.ExecutarSql("UPDATE PEDIDOS SET codcli = " + IDCliente.getText().toString().trim().substring(0, 8) + ", codfrmpgt = " + condicoes[0].toString().trim().substring(0, 3) + ", codtipprz = " + condicoes[1].toString().trim().substring(0, 3) + " WHERE id_pedido = " + IDPedido.getText().toString() + "");
									
									bJaAdicionou=true;
								} else {
									bJaAdicionou=true; //Não deixa adicionar, pois não foi permitido
								}
							}
						}
					}
				}
			}
			
			
			if (bJaAdicionou==false)
			{
				//Grava Item do Pedido
				this.dh.GravaPedido("INSERT OR REPLACE INTO PEDIDOS (Status, CodEmp, id_pedido, CodTabPrc, CodTabPrz, codcli, codfrmpgt, codtipprz, datapedido, arquivo, produto, descricao, qtd, preco, desc, obs, univenda, qtdcaixa, CodTipMov) VALUES " + "('Bloqueado', " + sEmpresa + ", " + IDPedido.getText().toString() + ", " + iCodTipPrc + ", " + iCodTipPrz + ", " + IDCliente.getText().toString().trim().substring(0, 8) + "," + condicoes[0].toString().trim().substring(0, 3) + ", " + condicoes[1].toString().trim().substring(0, 3) + ", '" +  formatador.format(datapedido) + "', '" + sRegistro + "', '" + nome.substring(0, 9) + "', '" + nome.substring(10, nome.toString().length()) + "', '" + qtd.getText().toString() + "', '" + preco.getText().toString().replace(",", ".") + "', '" + desc.getText().toString().replace(",", ".") + "', '" + observacao + "', '" + sUniVend + "', '" + iQtdCaixa.toString() + "', " + iCodTipMov + ")");
				
				//Grava somente uma condicao e cliente para os itens
				this.dh.ExecutarSql("UPDATE PEDIDOS SET codcli = " + IDCliente.getText().toString().trim().substring(0, 8) + ", codfrmpgt = " + condicoes[0].toString().trim().substring(0, 3) + ", codtipprz = " + condicoes[1].toString().trim().substring(0, 3) + " WHERE id_pedido = " + IDPedido.getText().toString() + "");
				
				if (lePedido==null) {
					Intent i = getParent().getIntent();                    
                	i.putExtra("adicionou_itens", posicao_combo_condicao );
				}
                
			}

			//Limpa Campos
			qtd.setText("");
			preco.setText("");
			desc.setText("");
			observacao="";
			tvPrecoLiq.setText("0.00");
			Spinner prod = (Spinner) findViewById(R.id.cbClientes);
			prod.setSelection(ArrayAdapter.NO_SELECTION);

			String sUnidadeVenda = this.dh.BuscaParametros(); //Busca qual unidade de venda será utilizada

			if ("Menor Unidade".equals(sUnidadeVenda)) 
			{
				univenda.setSelection(0);
			} else {
				univenda.setSelection(1);
			}
			
			listar_itens(false, IDPedido.getText().toString());//apenas lista os itens

		} catch (Exception e)
		{
			txtCliente.setText(e.getMessage());
		}

    }        
   
    //Quando passar parametro true, gerar arquivo txt
    private void listar_itens(Boolean bGravarArquivo, String sArquivo){

        EditText procura       = (EditText) findViewById(R.id.tbProcurar);
        TextView totalpedido   = (TextView) findViewById(R.id.txTotal);        
        TextView totalDesconto = (TextView) findViewById(R.id.lbDescTotal);
        ListView list          = (ListView) findViewById(R.id.lsItens); 
        List<String> listItems = new ArrayList<String>();
        List<String[]> names2  = null ;

        if (!"".equals(sArquivo))
        {
        	names2 = this.dh.LePedido(" id_pedido = " + sArquivo);        
        } 
        else 
        {
        	names2 = this.dh.LePedido(" id_pedido = 99999");        
        }            
        
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
         
        String[] from = new String[] {"produto", "qtd", "preco", "desc", "total"};

        int[] to = new int[] { R.id.produto, R.id.qtd, R.id.preco, R.id.desc, R.id.total };

        ArrayAdapter<String> listagem = new ArrayAdapter<String>(this,
                    android.R.layout.simple_gallery_item, listItems);
        
        BigDecimal total 		  = new BigDecimal("0");
        BigDecimal percdesc		  = new BigDecimal("0");
        BigDecimal totalDesc	  = new BigDecimal("0");        
        BigDecimal cPrecoUnitario = new BigDecimal("0");
        BigDecimal cDesconto 	  = new BigDecimal("0");
        BigDecimal cQuantidade 	  = new BigDecimal("0");
        
        map = new HashMap<String, String>();
        map.put("produto", "PRODUTO");
        map.put("qtd", "QTD");
        map.put("preco", "PREÇO");
        map.put("desc", "% DESC.");
        map.put("total", "TOTAL");        
        fillMaps.add(map);

        for (String[] name : names2) 
        {
            map = new HashMap<String, String>();
            
            cPrecoUnitario = new BigDecimal(name[iPreco]);
            
            if (name[iDesc].length()>0) /*Se houve Desconto*/ 
            {
            	cDesconto 	   = new BigDecimal(name[iDesc]);	
            }
            
            cQuantidade    = new BigDecimal(name[iQtd]);
            
            if (name[iDesc].length()>0) 
            {   
                percdesc = cPrecoUnitario.multiply(cDesconto).divide(new BigDecimal("100"));
            }
            
            cPrecoUnitario 	= cPrecoUnitario.setScale(2, BigDecimal.ROUND_FLOOR);
            percdesc 		= percdesc.setScale(2, BigDecimal.ROUND_FLOOR);
            total 			= total.add(cPrecoUnitario.subtract(percdesc).multiply(cQuantidade));
            totalDesc 		= totalDesc.add(percdesc.multiply(cQuantidade));
            
            map.put("produto", name[iProduto] + " - " + name[iDescricao]);
            map.put("qtd", name[iQtd]);
            map.put("preco", cPrecoUnitario.toString());
            
            if (!"".equals(name[iDesc])) {
                map.put("desc", cDesconto.toString());
            } 
            else
            {
                map.put("desc", "0.00");
            }
            
            map.put("total", cPrecoUnitario.subtract(percdesc).multiply(cQuantidade).toString());
            fillMaps.add(map);
            observacao = name[iObs];
            percdesc	= new BigDecimal("0");
            cDesconto	= new BigDecimal("0");
            
        }
       
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.listviewprodutos,from, to);

        list.setAdapter(adapter);        //listagem
        String sValidaVerbaMovimentacao = this.dh.ValidaVerbaMovimentacao(iCodTipMov);
                
        if (bGravarArquivo) /*Clicado botão GRAVAR*/
        {
        	/*51907 - Atualizar verba somente no final do pedido*/        	
        	/*Se houver tratamento para verbas*/
        	
        	if (lePedido==null){
        		Intent iTenta = getParent().getIntent();
                
                if (!"".equals(iTenta.getStringExtra("obs")))
                {            
                   if (iTenta.getStringExtra("obs")!=null){
                	   observacao = iTenta.getStringExtra("obs");                	   
                	   this.dh.ExecutarSql("UPDATE PEDIDOS SET obs = '" + observacao + "' WHERE id_pedido = " + sArquivo);                	   
                   }
                }                  
            }
        	 
        	
			if (sValorVerba[2].length() > 0 && "S".equals(sValidaVerbaMovimentacao))
			{
				if (Float.parseFloat(sValorVerba[0].toString())>0)
				{
					/*Se teve descontos*/
					if (Float.valueOf(totalDesconto.getText().toString())>0)
					{						
						if (AtualizaVerbaFinal()==false) 
						{
							this.dh.ExecutarSql("UPDATE PEDIDOS SET Status = 'Bloqueado' WHERE id_pedido = " + sArquivo);
							return;
						} 
						else /*Tudo certo, atualiza verba disponível*/ 
						{
							//this.dh.ExecutarSql("UPDATE VERBAS SET VERBALIBERADA = '" + totalDesconto.getText().toString() + "'");														
						}
					}
				} 
			}
			
			this.dh.ExecutarSql("UPDATE PEDIDOS SET Status = 'Liberado' WHERE id_pedido = " + sArquivo);
						        	
            qtd 	= (EditText) findViewById(R.id.tbQtd);
            preco 	= (EditText) findViewById(R.id.tbPreco);
            desc 	= (EditText) findViewById(R.id.tbDesconto);
            saldo 	= (TextView) findViewById(R.id.txValorSaldo);
            
            total= new BigDecimal("0");
            qtd.setText("");
            preco.setText("");
            desc.setText("");                
            totalpedido.setText("0");
            listagem.clear();   
            
            saldo.setText("");            
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Pedido gravado com sucesso!")
            .setTitle("Gravação Pedido OffLine")    
            .setCancelable(false)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {                                                                       
                   
            	   if (lePedido==null) {
            		   Intent i = getParent().getIntent();                    
            		   i.putExtra("adicionou_itens","");
            	   }
                   
            	   Intent in = new Intent(Itens.this, PrasistAndroidOffLine.class);                       
                   finish();
                   startActivity(in);                       
               }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        
        procura.requestFocus();
        total 		= total.setScale(2, BigDecimal.ROUND_FLOOR);
        totalDesc 	= totalDesc.setScale(2, BigDecimal.ROUND_FLOOR);
        totalpedido.setText(total.toString());
        totalDesconto.setText(totalDesc.toString());
        
    }
        
    @SuppressLint("DefaultLocale")
	private void pesquisar() {

        List<String[]> names2 =null ;
        String[] stg1;
                
        ckInterno = (CheckBox)findViewById(R.id.ckPesq);
        EditText sProcurar = (EditText) findViewById(R.id.tbProcurar);        
        String sValor = sProcurar.getText().toString();
        String sWhere="";

        String radioButtonSelected = "";

        try  
        {  
           Integer.parseInt(sValor);     
           radioButtonSelected = " id = '" + preencheCom(sValor,"0",9,1) + "'";
        }  
        catch (NumberFormatException ex)
        {  
        	if (ckInterno.isChecked()) 
        	{
        		radioButtonSelected = " nome like '%" + sValor + "%'";
			} else 
			{
				radioButtonSelected = " nome like '" + sValor.toUpperCase().trim() + "%'";
			}
        }  
                
        if (!"".equals(sValor))
        {               
            sWhere = radioButtonSelected;
        }
        
        names2 = this.dh.selectAll(sWhere, iCodTipPrc, iCodTipPrz);

        if (names2.size()<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pedidos OffLine");
            builder.setMessage("Mercadoria não encontrada ou importação de dados não efetuada.");
            builder.setPositiveButton("OK", null);
            builder.show();    
            return;
        }
        stg1=new String[(names2.size()+1)];
        int x=0;
        String stg;
        
        String sLayout = dh.sLayoutCombo();

        
        /* LAYOUT COMBO
         * Mercadoria                                         = 0
           Mercadoria + Unidade                               = 1
           Mercadoria + Preço                                 = 2
           Mercadoria + Qtd. Emb/Cx. Emb                      = 3
           Mercadoria + Qtd. Emb/Cx. Emb + Unidade + Preço    = 4
         */
        
        for (String[] name : names2) 
        {
            
            if (x==0)
            {
                stg = "Selecione uma mercadoria";
                stg1[x]=stg;
                x=1;
            }
            
            if ("".equals(name[2].trim()))
            {
                name[2] = "0";
            }
            
            BigDecimal cPrecoUnit = new BigDecimal(name[2].trim());
            cPrecoUnit = cPrecoUnit.setScale(2, BigDecimal.ROUND_FLOOR);
            
            stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() +" – "+name[3].trim() +" – "+ cPrecoUnit.toString();
            
            if ("0".equals(sLayout))
            {/*Somente código e descrição mercadoria*/
            	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim(); 
            } else if ("1".equals(sLayout)) 
            {/*código, descrição e unidade*/
            	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() + " - " + name[3].trim(); 
            }else if ("2".equals(sLayout)) 
            {/*código, descrição e preco*/
            	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() + " - " + cPrecoUnit.toString();
            }else if ("3".equals(sLayout)) 
            {
            	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim();
            }else if ("4".equals(sLayout)) 
            {
            	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() +" – "+name[3].trim() +" – "+ cPrecoUnit.toString();	
            }
            
            stg1[x]=stg;
            x++;
        }

        //Identifica o Spinner no layout
        spn1 = (Spinner) findViewById(R.id.cbClientes);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        //simple_spinner_dropdown_item
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);

        //Método do Spinner para capturar o item selecionado
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) 
                {
                        //pega nome pela posição
                        nome = parent.getItemAtPosition(posicao).toString();

                        //imprime um Toast na tela com o nome que foi selecionado
                        if (!"".equals(nome.trim()))
                        {
                            buscaProduto(nome);
                        }
                        EditText qtd = (EditText) findViewById(R.id.tbQtd);
                        qtd.requestFocus();
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });

        sProcurar.setText("");
    }

    public static String preencheCom(String linha_a_preencher, String letra, int tamanho, int direcao){

        //Checa se Linha a preencher é nula ou branco
        if (linha_a_preencher == null || "".equals(linha_a_preencher.trim()) ) {linha_a_preencher = "";}      

        //Retira caracteres estranhos
        linha_a_preencher = linha_a_preencher.replace("."," ");
        
        StringBuilder sb = new StringBuilder(linha_a_preencher);

        if (direcao==1){ //a Esquerda

            for (int i=sb.length() ; i<tamanho ; i++){
                sb.insert(0,letra);
            }

        } else if (direcao==2) {//a Direita

            for (int i=sb.length() ; i<tamanho ; i++){
                sb.append(letra);
            }
        }
        return sb.toString();
    }
  
    private void buscaProduto(String sNome) {
        String sWhere="";
        
        try {
            if ("Selecione uma mercadoria".equals(sNome)){
                return;
            }
            
            if ("".equals(sNome)){
                return;
            }

            //Variáveis
            List<String[]> names2 = null;

            int x=0;
            //String preco_unitario;
            BigDecimal preco_unitario;
            String stg;

            //Codigo produto
            sWhere = " id = " + sNome.substring(0, 9) + "";
                        
            //Busca dados mercadoria e preco conforme tabela de preco selecionada na tela anterior
            //Retorna :
            //0 = ID, 1 = NOME, 2 = PRECO, 3 = UNIDADE, 4 = SALDO, 5 = PERC DESC, 6 = MAIOR UNIDADE, 7 = QTD. CAIXA
            
            //Se selecionou prazo diferente da original (Prazo por item)
            if (!"".equals(sPrazoTabela.trim())) {                
                iCodTipPrz = sPrazoTabela.substring(0,3);
            } else //Pega prazo da tela anterior (Cabeçalho pedido)
            {
                iCodTipPrz = sGuardaPrazoPrincipal;
            }
            
            names2 = this.dh.selectAll(sWhere, iCodTipPrc, iCodTipPrz);

            //Pega saldo
            saldo = (TextView) findViewById(R.id.txValorSaldo);

            String[] stg1; //array onde irao campos da query mercadorias
            stg1=new String[names2.size()]; //Tamanho 7

            //Retorna mercadorias consultadas
            for (String[] name : names2) {

                //Verificar se há preço promocional
                String cPreco = this.dh.BuscaPromocao(name[0].toString(),name[2].toString());

                //Se vier em branco não tem promoção
                if ("".equals(cPreco)) 
                {
                    //preco_unitario = df.format(Float.valueOf(name[2]));
                    preco_unitario = new BigDecimal(name[2]);
                } else {
                    //preco_unitario = df.format(Float.valueOf(cPreco)); //Preço da promoção
                	preco_unitario = new BigDecimal(cPreco);
                }

                preco_unitario = preco_unitario.setScale(2, BigDecimal.ROUND_FLOOR);
                
                stg     = preco_unitario.toString(); //Preço unitário
                stg1[x] = stg;
                saldo.setText(name[4]);
                desconto_max = Float.parseFloat(name[5]);

                if (!"".equals(name[7])) {

                    //Instancia a combo da unidade de venda
                    Spinner univenda = (Spinner) findViewById(R.id.cbUnidade);        
                    String sUniVend = univenda.getSelectedItem().toString();

                    //Verifica se é a maior unidade selecionada
                    if (sUniVend.equals("Maior Unidade"))
                    {
                    	iQtdCaixa = new BigDecimal(name[7].replace(",", "."));
                    	
                        //iQtdCaixa = Float.parseFloat(name[7].replace(",", "."));
                    } else {
                        //iQtdCaixa = 1;
                        iQtdCaixa = new BigDecimal("1");
                    }

                } else {
                    //iQtdCaixa = 1;
                	iQtdCaixa = new BigDecimal("1");
                }
                x++;
            }

                preco = (EditText) findViewById(R.id.tbPreco);
                
                if (stg1[0].length()!=0)
                {
                    //51114
                    preco.setText(stg1[0]);
                    preco_ant = stg1[0].replace(",", ".");
                    
                    //float cResult = (Float.parseFloat(stg1[0].replace(",", ".")) * iQtdCaixa);                                                
                    BigDecimal cResult = new BigDecimal(stg1[0]);
                    cResult = cResult.multiply(iQtdCaixa);
                    tvPrecoLiq.setText(cResult.toString());                                        
                }
                
       }
        catch (Exception e)
       {
             txtCond = (TextView) findViewById(R.id.tbCondicao);          
             txtCond.setText("erro " + e.getMessage());
       }
    }
    
    private void CarregarPrazos (String sWhere)    {
        List<String[]> names2 =null ;
        String[] stg1;

        //this.dh = new DataHelper(getApplicationContext());
        
        names2 = this.dh.BuscaPrazosTabela(sWhere);
        
        stg1=new String[(names2.size()+1)];
        int x=0;
        String stg;
        
        if (names2.size()<=0)
        {
            stg = "";
            stg1[x]=stg;
            x=1;
        } 
        
        for (String[] name : names2) {
            
            if (x==0) {
                stg = "";
                stg1[x]=stg;
                x=1;
            }
            stg = Itens.preencheCom(name[0],"0",3,1) + " – " + name[1].trim();             
            stg1[x]=stg;
            x++;
        }

        //Identifica o Spinner no layout
        spn1 = (Spinner) findViewById(R.id.cbPrazo);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        //Método do Spinner para capturar o item selecionado        
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {                                    
                                        
                    if (!"".equals(parent.getItemAtPosition(posicao).toString())) {
                        sPrazoTabela = parent.getItemAtPosition(posicao).toString();
                    }
                    
                    if (nome!=null){
                        if (!"".equals(nome.trim())){
                            buscaProduto(nome);
                        }
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });        
    }
}