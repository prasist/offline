package com.novoprasistoffline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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

public class HistoricoFinanc extends Activity {

public DataHelper dh;
public CheckBox ckInterno;
public String nome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historicofinanc);
        
        //Botao Pesquisar    
        final Button buView = (Button) findViewById(R.id.buPesquisarCliente);
        buView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                pesquisar();    
            }
        });        
    }
    
    public static boolean isNumeric (String s) {  
        try 
        {  
            Long.parseLong (s);   
            return true;  
        } catch (NumberFormatException ex) {  
            return false;  
        }  
    }  
  
        
    private void pesquisar()  {

        List<String[]> names2 = null ;
        String[] stg1;

        CheckBox ckPesqAvanc = (CheckBox)findViewById(R.id.ckPesqAvanc);
        EditText sProcurar = (EditText) findViewById(R.id.tbProcurarCliente);

        String sValor = sProcurar.getText().toString();
        String sWhere="";

        if (!"".equals(sValor))
        {           
            if (isNumeric(sValor)==true)
            {                
                if (sValor.length()>9) {
                	if (ckPesqAvanc.isChecked()) 
                	{
                		sWhere = " CNPJ LIKE '%" + sValor + "%'";	
                	}
                	else
                	{
                		sWhere = " CNPJ LIKE '" + sValor + "%'";
                	}
                    
                } else {
                    sWhere = " CODCLI = " + sValor + "";
                }                
            }
            else
            {
            	if (ckPesqAvanc.isChecked())
            	{
            		sWhere = " RAZSOC LIKE '%" + sValor + "%' OR CNPJ like '%" + sValor + "%'";	
            	} 
            	else 
            	{
            		sWhere = " RAZSOC LIKE '" + sValor + "%' OR CNPJ like '" + sValor + "%'";
            	}
                
            }
            
        }
        
        this.dh = new DataHelper(getApplicationContext());
        names2 = this.dh.BuscaClientes(sWhere);
        
        if (names2.size()<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pedidos OffLine");
            builder.setMessage("A tabela de CLIENTE está vazia.\n - Verifique se foi feita a importação de dados \n - Verifique se o vendedor tem clientes para a empresa selecionada ");
            builder.setPositiveButton("OK", null);
            builder.show();    
            return;
        }
        stg1=new String[(names2.size()+1)];
        int x=0;
        String stg;
        
        for (String[] name : names2) {
            
            if (x==0){
                stg = "Selecione um Cliente";
                stg1[x]=stg;
                x=1;
            }
            stg = Itens.preencheCom(name[0],"0",8,1)+" – "+name[1].trim()+" – "+name[3].trim();
            stg1[x]=stg;
            x++;
        }

        //Identifica o Spinner no layout
        Spinner spn1 = (Spinner) findViewById(R.id.cbcliente);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        //Método do Spinner para capturar o item selecionado        
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                                    
                    parent.getItemAtPosition(posicao).toString();
                    
                    String nome = parent.getItemAtPosition(posicao).toString();
	                    
	                if (!"Selecione um Cliente".equals(nome)) {
	                  	BuscarHistorico(nome.substring(0, 8));
	                }                   
                    
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });
        
        sProcurar.setText("");
    }

    @SuppressLint("SimpleDateFormat")
	private void BuscarHistorico(String sNome) {
    	
    	TextView lbLimite  = (TextView) findViewById(R.id.textView2);
    	TextView lbTotal   = (TextView) findViewById(R.id.textView4);
        TextView lbDebitos = (TextView) findViewById(R.id.textView6);
        TextView lbSaldo   = (TextView) findViewById(R.id.textView8);
        
    	try 
    	{
	        this.dh = new DataHelper(getApplicationContext());                                   
	        
	        List<String[]> arrayTitulos = null ;
	
	        ListView list = (ListView) findViewById(R.id.lsHistorico);
	        
	        //NUMDOC , NUMPAR , SERIE , STATUS , VLRDOC , SALDO , DTAVEN , DTAEMI , QTDPAR , ATRASO, LIMCRED, VLRDEB
	        arrayTitulos = this.dh.BuscaHistoricoFinanc(" H.CODCLI = " + Integer.parseInt(sNome));
	
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	        HashMap<String, String> map;
		        
	        String[] from = new String[] {"NUMDOC","NUMPAR","SERIE","STATUS", "VLRDOC", "SALDO", "DTAVEN","DTAEMI","ATRASO"};  
		        
	        int[] to = new int[] { R.id.numdoc, R.id.parcela, R.id.serie, R.id.status, R.id.valor, R.id.saldo, R.id.vencimento, R.id.emissao, R.id.atraso}; 
	
	        map = new HashMap<String, String>();
	        map.put("NUMDOC", "N. Doc.");	        	        
	        map.put("NUMPAR", "Parcela");
	        map.put("SERIE", "Série");
	        map.put("STATUS", "Status");
	        map.put("VLRDOC", "Valor");
	        map.put("SALDO", "Saldo");
	        map.put("DTAVEN", "Vencimento");
	        map.put("DTAEMI", "Emissão");
	        map.put("ATRASO", "Dias Atraso");
	        
	        
	        fillMaps.add(map);
	        
	        //Variaveis totalizadoras
	        BigDecimal cTotalTitulos   = new BigDecimal("0");
            BigDecimal cResultadoSaldo = new BigDecimal("0");
            //Variaveis valores
            BigDecimal cValorTitulo = new BigDecimal("0");
            BigDecimal cValorLimiteCredito = new BigDecimal("0");
            BigDecimal cValorDebitos = new BigDecimal("0");
	        
	        for (String[] name : arrayTitulos) 
	        {	   
	        	 cValorTitulo 	   	 = new BigDecimal(name[4]);
	        	 cValorLimiteCredito = new BigDecimal(name[10]);
	        	 cValorDebitos 		 = new BigDecimal(name[11]);
	 	        		
	        	 cTotalTitulos 	 	 = cTotalTitulos.add(cValorTitulo);
	        	 
	             map = new HashMap<String, String>();
	             map.put("NUMDOC", name[0]);	                         
	             map.put("NUMPAR", name[1]);
	             map.put("SERIE", name[2]);
	             map.put("STATUS", name[3]);
	             map.put("VLRDOC", name[4]); 
	             map.put("SALDO", name[5]);
	             map.put("DTAVEN", name[6]);
	             map.put("DTAEMI", name[7]);
	             map.put("ATRASO", name[9]);
	             
	             fillMaps.add(map);	             
	        }
	       
	        /*Exibe total geral*/
	        
	        
	        cResultadoSaldo = cValorLimiteCredito.subtract(cValorDebitos);
	        
	        lbLimite.setText(cValorLimiteCredito.toString());
	        lbTotal.setText(cTotalTitulos.toString());
	        lbDebitos.setText(cValorDebitos.toString());
	        lbSaldo.setText(cResultadoSaldo.toString());
	        
	        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.listagemhistorico,from, to);
	        list.setAdapter(adapter);        //listagem                            
    	}
    	catch (Exception ecx) 
    	{
    		lbTotal.setText(ecx.getMessage());
    	}
    }

	
}