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

public class TabelaPrecos extends Activity {

public DataHelper dh;
public CheckBox ckInterno;
public static String iCodTipPrc; 
public static String iCodTipMov;
public static String iCodTipPrz; 
public static String sPrazoTabela="";
public static String sGuardaPrazoPrincipal="";
private Spinner spn1;
public  String nome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabela);
        
        iCodTipPrc = "";
        iCodTipPrz = "";
        
        //botao pesquisar    
        final Button buView = (Button) findViewById(R.id.buPesquisarCliente);
        buView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                pesquisar();                
                CarregarTabelas("");                
            }
        });
        
        //Botao Pesquisar Produtos 
        final Button buPesquisarProd = (Button) findViewById(R.id.buPesquisar3);
        buPesquisarProd.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                PesquisarProdutos();                
            }
        });
        
        //Botao Pesquisar Produtos 
        final Button buListar = (Button) findViewById(R.id.buListar);
        buListar.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                PesquisarPrecos();                
            }
        });
        
               
        CarregarTabelas("");
       
    }
    
    private void PesquisarPrecos () 
    {
    	
        ListView list        = (ListView) findViewById(R.id.lsProdutos); 
    
    	  String  sWhere = "";
    	  
    	  if (nome!=null){
    		  
    		  if (nome!="Selecione uma mercadoria") {
    			  if (nome.substring(0, 9)!="") {
    				  sWhere = "id = " + nome.substring(0, 9);
    			  }
    		  }    		  
    	  }
    	      	  
          List<String> listItems = new ArrayList<String>();
          List<String[]> names2 =null ;

          names2 = this.dh.BuscaPrecoProdutos(sWhere, iCodTipPrc, iCodTipPrz);        
       
          List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
          HashMap<String, String> map;
           
          String[] from = new String[] {"produto", "forma", "prazo", "valor", "saldo"};

          int[] to = new int[] { R.id.produto, R.id.forma, R.id.prazo, R.id.valor, R.id.saldo};

          ArrayAdapter<String> listagem = new ArrayAdapter<String>(this,
                      android.R.layout.simple_gallery_item, listItems);
          

          listagem.clear();
          
          map = new HashMap<String, String>();
          map.put("produto", "Produto");
          map.put("forma", "Forma Pagto.");
          map.put("prazo", "Prazo Pagto.");
          map.put("valor", "Valor");
          map.put("saldo", "Saldo");
          
          
          fillMaps.add(map);
          //id, nome, T.preco, unidade, qtdcaixa, T.CODTIPPRC, T.CODTIPPRZ, C.DESFRMPGT, C.DESTIPPRZ F
          for (String[] name : names2) 
          {
              map = new HashMap<String, String>();              
              
              map.put("produto", name[0] + " - " + name[1]);
              map.put("forma", name[7]);
              map.put("prazo", name[8]);              
              map.put("valor", name[2]);
              map.put("saldo", name[9]);
              
              fillMaps.add(map);
              
          }
         
          SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.listagemprecos,from, to);
          
          list.setAdapter(adapter);        //listagem
        
          
    }
    
    @SuppressLint("DefaultLocale")
	private void PesquisarProdutos()
    {
    	 List<String[]> names2 =null ;
         String[] stg1;
                 
         ckInterno = (CheckBox)findViewById(R.id.ckPesqAvanc);
         EditText sProcurar = (EditText) findViewById(R.id.tbProcurarProd);        
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
         
         names2 = this.dh.ListaProdutos(sWhere);

         if (names2.size()<=0) {
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
             
             stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() +" – "+name[3].trim();
             
             if ("0".equals(sLayout))
             {/*Somente código e descrição mercadoria*/
             	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim(); 
             } else if ("1".equals(sLayout)) 
             {/*código, descrição e unidade*/
             	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() + " - " + name[3].trim(); 
             }else if ("2".equals(sLayout)) 
             {/*código, descrição e preco*/
             	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim();
             }else if ("3".equals(sLayout)) 
             {
             	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim();
             }else if ("4".equals(sLayout)) 
             {
             	stg = preencheCom(name[0],"0",9,1) + " " + name[1].trim() +" – "+name[3].trim();	
             }
             
             stg1[x]=stg;
             x++;
         }

         //Identifica o Spinner no layout
         spn1 = (Spinner) findViewById(R.id.cbItens);

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
                 }

                 public void onNothingSelected(AdapterView<?> parent) {

                 }
         });

         sProcurar.setText("");	
    }
     
    private static String preencheCom(String linha_a_preencher, String letra, int tamanho, int direcao){

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
    
    public static boolean isNumeric (String s) {  
        try 
        {  
            Long.parseLong (s);   
            return true;  
        } catch (NumberFormatException ex) {  
            return false;  
        }  
    }  
  
    private void CarregarTabelas (String sWhere)   
    {
        List<String[]> names2;
        String[] stg1;

        this.dh = new DataHelper(getApplicationContext());
        
        names2 = this.dh.BuscaTabelas(sWhere);
        
        stg1=new String[(names2.size()+1)];
        int x=0;
        String stg;
        
        if (names2.size()<=0){
            stg = "Não encontrado : " + sWhere;
            stg1[x]=stg;
            x=1;
        } 
        
        for (String[] name : names2) {
            
            if (x==0){
                stg = "Selecione uma Tabela";
                stg1[x]=stg;
                x=1;
            }
            stg = Itens.preencheCom(name[0],"0",3,1) + " – " + name[1].trim() + " ==> " +  Itens.preencheCom(name[2],"0",3,1) + " – " + name[3].trim();            
            stg1[x]=stg;
            x++;
        }
        
        //Identifica o Spinner no layout
        Spinner spn1 = (Spinner) findViewById(R.id.cbTabelas);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        try {

            //Método do Spinner para capturar o item selecionado        
            spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {  
                    	
                    	 String nome = parent.getItemAtPosition(posicao).toString();
                                        
                    	 if (!"Selecione uma Tabela".equals(nome))
                    	 {
	                    	 String tabelas[] = nome.toString().split("==>");
	                         iCodTipPrc = tabelas[0].toString().trim().substring(0, 3);
	                         iCodTipPrz = tabelas[1].toString().trim().substring(0, 3);  
                    	 }
                         
                        /*
                        String nome = parent.getItemAtPosition(posicao).toString();
                        
                        Intent i = getParent().getIntent();                    
                        i.putExtra("tabela",nome.toString());                
                        
                        TabActivity ta = (TabActivity) TabelaPrecos.this.getParent();
                        ta.getTabHost().setCurrentTab(2);   
                        */                 
                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
            });

		} catch (Exception e) {
			e.printStackTrace();
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
                                    
                    String nome = parent.getItemAtPosition(posicao).toString();
                    
                    //Intent i = getParent().getIntent();                    
                    
                    /*
                    if (nome.toString().length()>35) 
                    {
                    	i.putExtra("cliente",nome.toString().substring(0, 35));
                    } else {
                    	i.putExtra("cliente",nome.toString());
                    }                                    
                    */
                    
                    if (!"Selecione um Cliente".equals(nome)) {
                        
                        String sTabPadrao = dh.BuscaTabelaCliente(nome.substring(0, 8));
                                                                 
                        if (!"0".equals(sTabPadrao.toString())) {
                        	CarregarTabelas(" CODTIPPRC = " + sTabPadrao);
                        } else {
                        	CarregarTabelas("");
                        }
                    }        
                                      
                    
                    //TabActivity ta = (TabActivity) TabelaPrecos.this.getParent();
                    //ta.getTabHost().setCurrentTab(2);                    
                    
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });
        
        sProcurar.setText("");
    }

}