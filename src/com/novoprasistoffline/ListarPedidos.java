package com.novoprasistoffline;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class ListarPedidos extends Activity { 
   
    public  DataHelper dh;      
    public Itens clsItens;
    public String sNumPedido;
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_pedidos); 
        
        try 
        {
            BuscaPedidos();
        } catch (ParseException ex) {
            Logger.getLogger(ListarPedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        final CharSequence[] items={"Alterar","Excluir"};

        final ListView list = (ListView) findViewById(R.id.lsPedidos);        
        
        AdapterView.OnItemClickListener onItemClick_List = 
        		new AdapterView.OnItemClickListener()  
        {
            @SuppressWarnings("rawtypes")
			public void onItemClick(final AdapterView arg0, View view, final int position, long index) 
            {            
                if (position>0)
                {              
                	
                	AlertDialog.Builder builder3=new AlertDialog.Builder(ListarPedidos.this);
                	  builder3.setTitle("Selecione uma opção").setItems(items, new DialogInterface.OnClickListener() {

                	  public void onClick(DialogInterface dialog, int which) {

                		  //TODO Auto-generated method stub	                	  
                		  //Toast.makeText(getApplicationContext(), "U clicked "+items[which], Toast.LENGTH_LONG).show();
                		  
                		  if (items[which]=="Alterar") {
                			  AbrirPedido(arg0.getItemAtPosition(position).toString());                              
                		  }
                	  
                		  if (items[which]=="Excluir") {
                			  
                			  String pega_idpedido[]  = arg0.getItemAtPosition(position).toString().split("=");    
                	          String pega_idpedido2[] = pega_idpedido[1].toString().split(",");
                	            
                			  ExcluirPedido(pega_idpedido2[0]);          
                		  }
                	  
                	  }
                	  
                	});

                	  builder3.show();
                                    
                }
            }                   
        };
      
        list.setOnItemClickListener(onItemClick_List);
    }
   	    
    
    private void AbrirPedido(String sPedido) 
    {    
        if (!"".equals(sPedido.toString())) 
        {   
            Intent i = new Intent(ListarPedidos.this, Itens.class);
            i.putExtra("recebe",sPedido.toString());                                     
            startActivity(i);
            finish();
        }                    
    }
           
    private void ExcluirPedido (String sPedido) {
    	      	 
    	sNumPedido = sPedido;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                     
           builder.setMessage("Confirma a Exclusão do Pedido : " + sNumPedido + " ?")
              .setCancelable(false)
              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                	  Exclusao(true);                                        
                  }
              })
              .setNegativeButton("No", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                          dialog.cancel();
                          Exclusao(false);
                  }
              });
          AlertDialog alert = builder.create();
          alert.show();
    	 
    }
    
    public void Exclusao(Boolean bExcluir)
    {
    	if (bExcluir==true){
    		this.dh = new DataHelper(getApplicationContext());
  	  		this.dh.ExcluirPedido(sNumPedido);
  	  		finish();
    	}
    	
    	sNumPedido="";
    }
        
    
    @SuppressLint("SimpleDateFormat")
	private void BuscaPedidos() throws ParseException {

        
    	TextView lbTotalPed = (TextView) findViewById(R.id.lbValorTotal);
        TextView lbSaldoVerba = (TextView) findViewById(R.id.lbSaldoVerba);
        TextView lbTotalDesc = (TextView) findViewById(R.id.lbTotalDesc);
        
    	try 
    	{
	        this.dh = new DataHelper(getApplicationContext());                                   
	        
	        List<String[]> arrayPedidos = null ;
	        List<String[]> arrayTotais  = null ;
	
	        ListView list = (ListView) findViewById(R.id.lsPedidos);
	        
	        String[] sValorVerba = this.dh.BuscaValorVerba();        
	        
	        BigDecimal a = new BigDecimal(sValorVerba[0]);
	        
	        a = a.setScale(2, BigDecimal.ROUND_FLOOR);
	        lbSaldoVerba.setText(a.toString());
	        
	        arrayPedidos = this.dh.BuscaPedidos("");
	
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	        HashMap<String, String> map;
	
	        String[] from = new String[] {"id_pedido","codfrmpgt","codtipprz","codcli", "valor", "status"};  //,"CodTabPrc","CodTabPrz"
		        
	        int[] to = new int[] { R.id.pedido, R.id.forma, R.id.prazo, R.id.cliente, R.id.valor, R.id.status}; //, R.id.codtipprc, R.id.codtipprz
	
	        map = new HashMap<String, String>();
	        map.put("id_pedido", "PEDIDO");	        	        
	        map.put("codfrmpgt", "FORMA PAGTO.");
	        map.put("codtipprz", "PRAZO");
	        map.put("codcli", "CLIENTE");
	        map.put("valor", "VALOR");
	        map.put("status", "STATUS");
	        
	        fillMaps.add(map);
	        
	        BigDecimal cTotalDesc 		= new BigDecimal("0");
	        BigDecimal cTotalGeralDesc 	= new BigDecimal("0");
	        BigDecimal cTotalGeral 		= new BigDecimal("0");	        
            BigDecimal cTotalPedido		= new BigDecimal("0");
            
	        String sPedido="";
	        
	        for (String[] name : arrayPedidos) 
	        {	            	        	
	        	/*Busca Valor Total do pedido*/
	        	arrayTotais = this.dh.BuscaTotalPedidos(name[0]);
	            
	 	        for (String[] exibir : arrayTotais) 
	 	        {
	 	        	cTotalPedido = new BigDecimal(exibir[0]); 
	 	        	cTotalDesc   = new BigDecimal(exibir[1]);
	 	        }
	 	        	   
	 	         cTotalPedido = cTotalPedido.setScale(2, BigDecimal.ROUND_FLOOR);
	 	       
	             map = new HashMap<String, String>();
	             map.put("id_pedido", name[0]);	                         
	             map.put("codfrmpgt", Itens.preencheCom(name[2],"0",3,1) + " - " + name[3]);
	             map.put("codtipprz", Itens.preencheCom(name[4],"0",3,1) + " - " + name[5]);
	             map.put("codcli", Itens.preencheCom(name[6],"0",8,1) + " - " + name[7]);
	             map.put("valor", cTotalPedido.toString()); 
	             map.put("status", name[11]);
	             
	             /*Artifício para não exibir duplicações na listagem*/
	             if (!sPedido.equals(name[0]))
	             {
	            	 fillMaps.add(map);
	            	 sPedido = name[0];
	            	 
	            	 cTotalGeral = cTotalGeral.add(cTotalPedido);
	                 cTotalGeralDesc = cTotalGeralDesc.add(cTotalDesc);
	             }	                          
	             	                       
                 cTotalDesc 	= new BigDecimal("0");
                 cTotalPedido	= new BigDecimal("0");
	        }
	       
	        /*Exibe total geral*/
	        
	        //cTotalDesc = cTotalDesc.setScale(2, BigDecimal.ROUND_FLOOR);
	        cTotalGeral = cTotalGeral.setScale(2, BigDecimal.ROUND_FLOOR);
	        lbTotalPed.setText(cTotalGeral.toString());
	        
	        if (cTotalGeralDesc.toString()!="")
	        {
	        	lbTotalDesc.setText(cTotalGeralDesc.toString());	
	        }
	        
	        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.listagem_pedidos,from, to);
	        list.setAdapter(adapter);        //listagem                            
    	}
    	catch (Exception ecx) 
    	{
    		lbTotalPed.setText(ecx.getMessage());
    	}
    }

	

	
    
}
