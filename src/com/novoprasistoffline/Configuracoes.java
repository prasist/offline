package com.novoprasistoffline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;

public class Configuracoes extends Activity {

    public DataHelper dh;
            
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        
        //botao gravar
        final Button buAdd = (Button) findViewById(R.id.buGravar);
        buAdd.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                
                Gravar();                
            }
        });
        
        //botao sair
        final Button btSair = (Button) findViewById(R.id.buSair);
        btSair.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {                
                Intent in = new Intent(Configuracoes.this, PrasistAndroidOffLine.class);                                       
                finish();
                startActivity(in);
            }
        });
        
        List<String[]> sParametros;        
        this.dh = new DataHelper(getApplicationContext());           
        sParametros = dh.RetornaParametros("");
                
        for (String[] name : sParametros) {
             if (!"".equals(name[0])) {
                 EditText sCodigoVendedor    = (EditText) findViewById(R.id.tbId);
                 EditText sNomeVendedor      = (EditText) findViewById(R.id.tbNome);
                 EditText sIpExterno         = (EditText) findViewById(R.id.tbIpExterno);
                 EditText sIpLocal           = (EditText) findViewById(R.id.tbIpLocal);
                 EditText sFtpHost           = (EditText) findViewById(R.id.tbFtp);
                 EditText sFtpUser           = (EditText) findViewById(R.id.tbUser);
                 EditText sFtpPwd            = (EditText) findViewById(R.id.tbPwd);
                 EditText sPasta             = (EditText) findViewById(R.id.tbPasta);
                 EditText sEmpresa			 = (EditText) findViewById(R.id.tbEmpresa); /*51849 - Acrescentado Empresa*/
                 
                /* PARAMETROS              
                * 0  IdVendedor,
                * 1  NomeVendedor , 
                * 2  IPExterno, 
                * 3  IPInterno, 
                * 4  HostFtp, 
                * 5  FtpUsuario,
                * 6  FtpSenha, 
                * 7  PastaServidor 
                * 8  Empresa
                */
                 
                 sCodigoVendedor.setText(name[0]);
                 sNomeVendedor.setText(name[1]);
                 sIpExterno.setText(name[2]);
                 sIpLocal.setText(name[3]);
                 sFtpHost.setText(name[4]);
                 sFtpUser.setText(name[5]);
                 sFtpPwd.setText(name[6]);
                 sPasta.setText(name[7]);
                 sEmpresa.setText(name[8]); /*51849 - Acrescentado Empresa*/
             }
        }
   
    }
    
    private void Gravar(){
        
        try {
            
        	EditText sCodigoVendedor    = (EditText) findViewById(R.id.tbId);
            EditText sNomeVendedor      = (EditText) findViewById(R.id.tbNome);
            EditText sIpExterno         = (EditText) findViewById(R.id.tbIpExterno);
            EditText sIpLocal           = (EditText) findViewById(R.id.tbIpLocal);
            EditText sFtpHost           = (EditText) findViewById(R.id.tbFtp);
            EditText sFtpUser           = (EditText) findViewById(R.id.tbUser);
            EditText sFtpPwd            = (EditText) findViewById(R.id.tbPwd);
            EditText sPasta             = (EditText) findViewById(R.id.tbPasta);
            EditText sEmpresa           = (EditText) findViewById(R.id.tbEmpresa);  /*51849 - Acrescentado Empresa*/
            
            /*Validação campos obrigatórios*/
            
            /*ID Vendedor*/
            if ("".equals(sCodigoVendedor.getText())) {
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("Pedidos OffLine");
    			builder.setMessage("Informe o ID do vendedor.");
    			builder.setPositiveButton("OK", null);
    			builder.show();
    			return;
            }
            
            /*Código da Empresa*/
            if ("".equals(sEmpresa.getText())) {
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("Pedidos OffLine");
    			builder.setMessage("Informe o código da empresa.");
    			builder.setPositiveButton("OK", null);
    			builder.show();
    			return;
            }        					
			
            this.dh = new DataHelper(getApplicationContext());           
            
            String sUpdate;

            this.dh.ExecutarSql("DELETE FROM CONFIGURACOES");
            
            /*51849 - Acrescentado Empresa*/
            sUpdate = "INSERT INTO CONFIGURACOES (IdVendedor, NomeVendedor, IPExterno, IPInterno, HostFtp, FtpUsuario, FtpSenha, PastaServidor, CodEmp ) VALUES ( ";
            sUpdate = sUpdate + " " + sCodigoVendedor.getText().toString()  + "";            
            sUpdate = sUpdate + " , '"  + sNomeVendedor.getText().toString() + "'";            
            sUpdate = sUpdate + " , '"  + sIpExterno.getText().toString() + "'";
            sUpdate = sUpdate + " , '"  + sIpLocal.getText().toString() + "'";
            sUpdate = sUpdate + " , '"  + sFtpHost.getText().toString() + "'";
            sUpdate = sUpdate + " , '"  + sFtpUser.getText().toString() + "'";
            sUpdate = sUpdate + " , '"  + sFtpPwd.getText().toString() + "'";
            sUpdate = sUpdate + " , '"  + sPasta.getText().toString() + "'";
            sUpdate = sUpdate + " , "  + sEmpresa.getText().toString() + " ) "; /*51849 - Acrescentado Empresa*/

            this.dh.ExecutarSql(sUpdate);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Dados Atualizados com sucesso.")
            .setTitle("Configurações")    
            .setCancelable(false)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface dialog, int id) {                                                                       
                   Intent in = new Intent(Configuracoes.this, PrasistAndroidOffLine.class);                       
                   finish();
                   startActivity(in);
               }
            });

                    AlertDialog alert = builder.create();
                    alert.show();
        } catch (Exception e){
             Log.e("Home", "Erro " + e.getMessage());
        }
    }
}
