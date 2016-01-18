package com.novoprasistoffline;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


@SuppressWarnings("deprecation")
public class PrePedido extends Activity {
    
    public DataHelper dh;
    public Spinner spn1;
    public Itens clsItens;    
    public String sPrazo;
    public String sPreco;
    public String sTabPadrao="0";
    public CheckBox ckPesqAvanc;
    public boolean bAlterar=false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_pedido);  
         
        //botao pesquisar    
        final Button buView = (Button) findViewById(R.id.buPesquisarCliente);
        buView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                pesquisar(); 
                CarregarCondicoes("");
                CarregarTabelas("");                
            }
        });
        
        CarregarMovimentacoes();
        CarregarCondicoes("");
        CarregarTabelas("");
               
        final EditText txObs = (EditText) findViewById(R.id.tbObs);
        
        
        txObs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {            
            if (hasFocus==false){ //quando perde o foco
                if (!"".equals(txObs.getText().toString())){                    
                    Intent i = getParent().getIntent();                    
                    i.putExtra("obs",txObs.getText().toString());            
                }                                
            }                         
        }        
                
        });   
                                
    }
    
    public static boolean isNumeric (String s) {  
        try {  
            Long.parseLong (s);   
            return true;  
        } catch (NumberFormatException ex) {  
            return false;  
        }  
    }  
    
    private void CarregarCondicoes (String CodCliente)   
    {
        List<String[]> names2 =null ;
        String[] stg1;

        this.dh = new DataHelper(getApplicationContext());
        
        /*53190*/
        Spinner cbMoviments = (Spinner) findViewById(R.id.cbMov);
        String CodTipMov = cbMoviments.getSelectedItem().toString();
        
        String sMovimentacao ="";
        
        if (!"Selecione uma Movimentação".equals(CodTipMov)) {
        	sMovimentacao = CodTipMov.substring(0, 3);
        }
        names2 = this.dh.BuscaCondicoes(CodCliente, sMovimentacao);
        
        if (names2.size()<=0)
        {
            return;
        }
        
        stg1=new String[(names2.size()+1)];
        int x=0;
        String stg;
        
        for (String[] name : names2) 
        {            
            if (x==0)
            {
                stg = "Selecione uma Condição";
                stg1[x]=stg;
                x=1;
            }
            stg = Itens.preencheCom(name[0],"0",3,1) + " – " + name[1].trim() + " ==> " +  Itens.preencheCom(name[2],"0",3,1) + " – " + name[3].trim();
            stg1[x]=stg;
            x++;
        }

        //Identifica o Spinner no layout
        spn1 = (Spinner) findViewById(R.id.cbCond);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        //Método do Spinner para capturar o item selecionado        
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {                                    
                    
                	Intent i = getParent().getIntent();
                    String JaTemItens = i.getStringExtra("adicionou_itens");
                    boolean bAlterou=false;
                    
                    //pega condicao selecionada
                    String nome = parent.getItemAtPosition(posicao).toString();
                    
                    if (!"".equals(JaTemItens) && JaTemItens!=null) {
                    	//parent.setSelection(Integer.valueOf(JaTemItens));
                    	//return;
                    	TratarAlteracao();    
                    	bAlterou=true;
                    }
                         
                    if (!"Selecione uma Condição".equals(nome)) {
                        
                        //Pega tipo preco e prazo
                        String arrTemp[] = nome.toString().split("==>");
                        
                        sPreco = arrTemp[0].toString().substring(0, 4);
                        sPrazo = arrTemp[1].toString().substring(0, 4);
                        //Agora buscar o campo PRAZOTAB que vem na CONDICOES DE PAGAMENTO
                        sPrazo = Itens.preencheCom(dh.BuscaPrazoTabela(sPreco, sPrazo),"0",3,1);
                        
                        if (dh.bTabelaLivre()==false) 
                        {                            
                        	if (!"0".equals(sTabPadrao.toString())) 
                        	{
                            	CarregarTabelas(" CODTIPPRC = " + sTabPadrao + " AND CODTIPPRZ = " + sPrazo);
                            } else {
                            	CarregarTabelas(" CODTIPPRZ = " + sPrazo);	
                            }                        	
                        }
                        
                        Spinner spTabela = (Spinner) findViewById(R.id.cbTabelas);
                        
                        spTabela.setSelection(0);
                        //spTabela.setEnabled(true); 
                                    
                        for (int c=0; c < spTabela.getCount(); c++) {
                            if (!"Selecione uma Tabela".equals(spTabela.getItemAtPosition(c).toString())) {                                
                                
                                //pega prazo da combo tabela de preco para comparar com a variavel prazo
                                String arrTemp2[] = spTabela.getItemAtPosition(c).toString().split("==>");
                                        
                                if (arrTemp2[1].toString().substring(0, 4).trim().equals(sPrazo.toString())) {
                                    spTabela.setSelection(c);                                    
                                    break;   
                                }          
                            }
                        }
                    }
                    
                    i = getParent().getIntent();                    
                    i.putExtra("condicao",nome.toString());
                    
                    i = getParent().getIntent();
                    i.putExtra("posicao_condicao", String.valueOf(posicao));
                    
                    if (bAlterou==true) {
                    	i = getParent().getIntent();
                        i.putExtra("nova_condicao", nome.toString());
                    }
                    
                    TabActivity ta = (TabActivity) PrePedido.this.getParent();
                    ta.getTabHost().setCurrentTab(1);                    
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });
        
    }
    
    private void TratarAlteracao() {
    	
    	
    	TextView sStatus = (TextView) findViewById(R.id.textView3);
        String sSituacao = "Houve alteração da cond. pagamento / tab. preços, \n os preços poderão sofrer alterações.";
        sStatus.setText(sSituacao);
        sStatus.setTextColor(0xffff0000);

	        
    }

    private void CarregarMovimentacoes () {
        List<String[]> names2 =null ;
        String[] stg1;

        this.dh = new DataHelper(getApplicationContext());
        
        names2 = this.dh.BuscaMovimentacoes("");
        
        stg1=new String[(names2.size()+1)];
        int x=0;
        String stg;
        
        for (String[] name : names2) {
            
            if (x==0){
                stg = "Selecione uma Movimentação";
                stg1[x]=stg;
                x=1;
            }
            stg = Itens.preencheCom(name[0],"0",3,1) + " – " + name[1].trim();
            stg1[x]=stg;
            x++;
        }

        //Identifica o Spinner no layout
        spn1 = (Spinner) findViewById(R.id.cbMov);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        //Método do Spinner para capturar o item selecionado        
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {                                    
                    
                    String nome = parent.getItemAtPosition(posicao).toString();
                    
                    if (!"Selecione uma Movimentação".equals(nome)) 
                    {
                        if (!ValidaMovimentacao()) {
                            return;
                        }
                    }
                    
                    if (!"Selecione uma Movimentação".equals(nome)) 
                    {
                    	ValidarCondicoesMovimentacao();
                    }
                    
                    Intent i = getParent().getIntent();                    
                    i.putExtra("movimentacao",nome.toString());                
                    
                    TabActivity ta = (TabActivity) PrePedido.this.getParent();
                    ta.getTabHost().setCurrentTab(1);                    
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });
                  
    }
    
    private void ValidarCondicoesMovimentacao() {

    	CarregarCondicoes("");
    }
    
    private boolean ValidaMovimentacao() {
    
        String CodTipMov;
        String CodCli;
        List<String[]> clientes;
        List<String[]> movimentacoes;
        String sUFEmpresa = "";
        String sUFCliente = "";
        String sTipoMovimentacao = "";
        
        Spinner cbClientes = (Spinner) findViewById(R.id.cbcliente);        
        CodCli = cbClientes.getSelectedItem().toString();
        
        Spinner cbMoviments = (Spinner) findViewById(R.id.cbMov);        
        CodTipMov = cbMoviments.getSelectedItem().toString();
        
        if ("Selecione um Cliente".equals(CodCli) || "Selecione uma Movimentação".equals(CodTipMov)) {
            return false;
        }
        
        this.dh = new DataHelper(getApplicationContext());        
        clientes = this.dh.BuscaClientes("CODCLI = " + CodCli.substring(0,8)); //"CODCLI","RAZSOC", "STATUSCLIENTE","UF"
        movimentacoes = this.dh.BuscaMovimentacoes("CODIGO = " + CodTipMov.substring(0,3)); //"CODIGO","DESCRICAO", "TIPO", "UF_EMPRESA"
        
        if (clientes.size()>0 && movimentacoes.size()>0) {
            
            for (String[] ufCliente : clientes) {
                sUFCliente = ufCliente[3].toString();
            }
            
            for (String[] ufEmpresa : movimentacoes) {
                sUFEmpresa = ufEmpresa[3].toString();
                sTipoMovimentacao = ufEmpresa[2].toString();
            }
            
            //Se estado do cliente for o mesmo da empresa e a movimentacao escolhida for para fora do estado
            if (sUFCliente == null ? sUFEmpresa == null : sUFCliente.equals(sUFEmpresa) && !"D".equals(sTipoMovimentacao)) {                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pedidos OffLine");
                builder.setMessage("Movimentação inválida para vendas dentro do Estado.");
                builder.setPositiveButton("OK", null);
                builder.show();    
                cbMoviments.setSelection(0);
                return false;   
            }
            
            //Se estado do cliente for o mesmo da empresa e a movimentacao escolhida for para fora do estado
            if ((!sUFCliente.equals(sUFEmpresa)) && "D".equals(sTipoMovimentacao)) {                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pedidos OffLine");
                builder.setMessage("Movimentação inválida para vendas fora do Estado.");
                builder.setPositiveButton("OK", null);
                builder.show();    
                cbMoviments.setSelection(0);
                return false;   
            }
            
        }
        
        //CarregarCondicoes(CodCli);
        return true;
    }

    private void CarregarTabelas (String sWhere)    {
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
        spn1 = (Spinner) findViewById(R.id.cbTabelas);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        //Método do Spinner para capturar o item selecionado        
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {                                    
                    
                    String nome = parent.getItemAtPosition(posicao).toString();
                    
                    Intent i = getParent().getIntent();                    
                    i.putExtra("tabela",nome.toString());                
                    
                    TabActivity ta = (TabActivity) PrePedido.this.getParent();
                    ta.getTabHost().setCurrentTab(1);              
                    
                    String JaTemItens = i.getStringExtra("adicionou_itens");
                    boolean bAlterou=false;
                                        
                    if (!"".equals(JaTemItens) && JaTemItens!=null) {
                    	TratarAlteracao();    
                    	bAlterou=true;
                    }
                    
                    if (bAlterou==true) {
                    	i = getParent().getIntent();
                        i.putExtra("nova_tabela", nome.toString());
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });
        
    }
    
    private void pesquisar()    {

        List<String[]> names2 = null ;
        String[] stg1;

        ckPesqAvanc = (CheckBox)findViewById(R.id.ckPesqAvanc);
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
        spn1 = (Spinner) findViewById(R.id.cbcliente);

        //Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stg1); //simple_spinner_dropdown_item
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_spinner_dropdown_item
        spn1.setAdapter(spinnerArrayAdapter);
        
        //Método do Spinner para capturar o item selecionado        
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                                    
                    String nome = parent.getItemAtPosition(posicao).toString();
                    
                    Intent i = getParent().getIntent();                    
                    
                    if (nome.toString().length()>35) 
                    {
                    	i.putExtra("cliente",nome.toString().substring(0, 35));
                    } else {
                    	i.putExtra("cliente",nome.toString());
                    }                                    
                    
                    TextView sStatus = (TextView) findViewById(R.id.textView3);
                    String sSituacao = "";
                    sStatus.setText(sSituacao);
                    
                    if (!"Selecione um Cliente".equals(nome)) {
                        sSituacao = dh.BuscaStatusCliente(nome.substring(0, 8));
                        if (!"LB".equals(sSituacao.trim())) {
                            sSituacao = "Atenção !!! Cliente Bloqueado no SIGMA";
                        } else {
                            sSituacao = "";
                        }
                        sStatus.setTextColor(0xffff0000);
                        sStatus.setText(sSituacao);
                        
                        sTabPadrao = dh.BuscaTabelaCliente(nome.substring(0, 8));
                        
                        CarregarCondicoes(nome.substring(0, 8));
                        
                        if (!"0".equals(sTabPadrao.toString())) {
                        	CarregarTabelas(" CODTIPPRC = " + sTabPadrao);
                        } else {
                        	CarregarTabelas("");
                        }
                    }
        
                    if (!"Selecione um Cliente".equals(nome)) {
                        if (!ValidaMovimentacao()) {
                            return;
                        }
                    }
                    
                    TabActivity ta = (TabActivity) PrePedido.this.getParent();
                    ta.getTabHost().setCurrentTab(1);                    
                    
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
        });
        
        sProcurar.setText("");
    }

}