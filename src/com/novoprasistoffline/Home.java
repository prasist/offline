package com.novoprasistoffline;
/*
android:versionCode="364"
android:versionName="3.6.4">

3.5.2 - Correcao validacao verba zerada, erro ao adicionar item

3.5.1 - Correção seleção tabela de preco - 51907 
		Correção leitura das movimentacoes por empresa - 51909
		Criação campo status para os pedidos, onde como padrao são bloqueados e ao gravar não havendo inconsistencias ficará liberado 
		Validação do percentual da verba na gravação do pedido
		Validação do calculo de verba pelo tipo de movimentação  

3.5   - Implementação multi-empresa.
      - Requisitos para instalação : Desinstalar a versão anterior, pois a nova versão criará novos campos no banco de dados do offline.
	  - Estar aplicado a versão 51849 da Webpedidos
	  - Gerar arquivos XML na webpedidos
	  - Instalar a versão 3.4 e configurar inicialmente o ID do vendedor e o cód. da empresa
	  
3.3 = Novo Layout

3.4 = Correção Erro Seleção da Tabela de preço

3.5.5 = 52688


	1 - EM UM PEDIDO JA GRAVADO NO APLICATIVO OFFLINE, ADICIONAR MAIS TRES ITENS. SEGUNDO O CLIENTE NÃO É POSS�?VEL FAZÊ-LO DE UMA VEZ APENAS, DISSE QUE PRECISA ADICIONAR UM ITEM, GRAVAR O PEDIDO, PESQUISAR E INSERIR O PRÓXIMO.

	2-AS OBSERVAÇÕES DO OFFLINE NÃO TRANSMITEM
	
	4- NÃO TEM OPÇÃO DE EXCLUIR PEDIDO NO OFFLINE , APENAS EXCLUINDO PRODUTO A PRODUTO
	  
*/
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.http.util.ByteArrayBuffer;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;


@SuppressLint("SimpleDateFormat")
public class Home extends Activity {
    
    public DataHelper dh;  
    public Itens clsItens;
    public boolean bResposta=false;
    public BuscarDados busca;
    public String sNomeVendedor;
    public String sIpExterno;
    public String sIpInterno;
    public String sHostFtp;
    public String sFtpUsuario;
    public String sFtpSenha;
    public String sPastaServidor;
    public String sEmpresa;/*51849*/
    public String sCodEmp;
    public String sUsuario;
    public String sSenha;
    public CheckBox ckInterno;
    public int iTotalPedidos;
    public Handler mHandler;
                     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);        
        
       try 
       {
    	   
    		//Correcao para funcionar 4.0.1
			if (android.os.Build.VERSION.SDK_INT > 9) {
			    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			    StrictMode.setThreadPolicy(policy);
			}

			
    	   /*Exibir versão do aplicativo*/
    	   PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    	   TextView lbVersion = (TextView) findViewById(R.id.lbVersao); 
    	   lbVersion.setText("Versão : " + pInfo.versionName);

    	   List<String[]> sParametros;        
    	   this.dh 			= new DataHelper(getApplicationContext());           
    	   sParametros 		= dh.RetornaParametros("");
    	
    	   /*Le LOG da importação de arquivos*/
     	   TextView lbMensagem = (TextView) findViewById(R.id.lbMsg);
     	   lbMensagem.setText(dh.BuscarLog());
     	   
    	   sNomeVendedor   	= "";
    	   sIpExterno      	= "";
    	   sIpInterno      	= "";

    	   for (String[] name : sParametros) 
    	   {
    		   sNomeVendedor  = name[0].toString() + "   -   " + name[1].toString();             
    		   sIpExterno     = name[2].toString();
    		   sIpInterno     = name[3].toString();
    		   sHostFtp       = name[4].toString();
    		   sFtpUsuario    = name[5].toString();
    		   sFtpSenha      = name[6].toString();
    		   sPastaServidor = name[7].toString();
    		   sEmpresa		  = name[8].toString(); /*51849 - Acrescentado Empresa*/
    	   }

    	   if ("".equals(sNomeVendedor)) 
    	   {

    		   AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		   builder.setMessage("Essa é sua primeira utilização, favor configurar os parâmetros.")
    		   .setTitle("Configurações")    
    		   .setCancelable(false)
    		   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

    			   public void onClick(DialogInterface dialog, int id) 
    			   {                                                                       
    				   AbreTelaConfig();
    			   }
    		   });
    		   AlertDialog alert = builder.create();
    		   alert.show();

    	   } else 
    	   {
    		   TextView lbVend = (TextView) findViewById(R.id.lbVendedor);
    		   lbVend.setText(sNomeVendedor);
    		   /*51849 - Acrescentado Empresa*/
    		   TextView lbEmpresa = (TextView) findViewById(R.id.tvEmpresa);
    		   lbEmpresa.setText("Empresa : " + sEmpresa);
    	   }
       } catch (Exception e) {
    	   Log.e("Home", "Erro " + e.getMessage());
       }

             
       final Button buView = (Button) findViewById(R.id.buAtualizar);
       buView.setOnClickListener(new View.OnClickListener()
       {
    	   public void onClick(View v) {

    		   Intent in = new Intent(Home.this, BuscarDados.class);

    		   TextView salvar = (TextView) findViewById(R.id.lbVendedor);
    		   ckInterno = (CheckBox)findViewById(R.id.ckConexao);

    		   if (ValidarPedidos()==false) {

    			   if (!"".equals(salvar.getText().toString())){            
    				   in.putExtra("recebe",salvar.getText().toString().substring(0, 3).trim());

    				   String sQualIp="";

    				   if (ckInterno.isChecked())
    				   {
    					   sQualIp = sIpInterno;
    				   } else {
    					   sQualIp = sIpExterno;
    				   }

    				   if (!"".equals(sQualIp)){
    					   in.putExtra("host",sQualIp);
    				   }

    				   finish();
    				   startActivity(in);   
    				   
    			   } else {
    				   AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
    				   builder.setTitle("Pedidos OffLine");
    				   builder.setMessage("Informe o ID do vendedor");
    				   builder.setPositiveButton("OK", null);
    				   builder.show();    
    				   salvar.requestFocus();                                    
    			   }                                    
    		   } else {
    			   AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
    			   builder.setTitle("Pedidos OffLine");
    			   builder.setMessage("Existem Pedidos que ainda não foram enviados. Não será possível a importação dos dados antes do envio.");
    			   builder.setPositiveButton("OK", null);
    			   builder.show();    
    			   salvar.requestFocus();                                    
    		   }
    	   }                    
       });

       final Button buVisualisar = (Button) findViewById(R.id.buPreview);
       buVisualisar.setOnClickListener(new View.OnClickListener()
       {
    	   public void onClick(View v) {                        
    		   Intent in = new Intent(Home.this, ListarPedidos.class);                            
    		   startActivity(in);                               
    	   }                    
       });

       final Button buConfiguracoes = (Button) findViewById(R.id.buConfig);
       buConfiguracoes.setOnClickListener(new View.OnClickListener()
       {
    	   public void onClick(View v) {                        
    		   AbreTelaConfig();    
    	   }                    
       });

       final Button buEnviar = (Button) findViewById(R.id.buEnviar);
       buEnviar.setOnClickListener(new View.OnClickListener()
       {
    	   public void onClick(View v) 
    	   {                    	
    		   String sArquivoCriado;
    		      		   
    		   if (ValidarPedidos()==true) 
    		   {
    			   if (!"".equals(sHostFtp)) 
    			   {
    				   if (isOnline()==true)
    				   {    					   
    					   gravar_arquivo();                        
    					   sArquivoCriado = MoverArquivo();
    					       					   
    					   Esperar();
    					       					   
    					   envioFTP(sArquivoCriado);    
    					       					   
    				   } else {
    					   AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
    					   builder.setTitle("Envio Arquivo FTP");
    					   builder.setMessage("Não há conexão de dados disponível no momento, verifique se há sinal 3G/4G/Dados Móveis / Wi-Fi e tente novamente.");
    					   builder.setPositiveButton("OK", null);
    					   builder.show();  
    				   }
    			   } 
    			   else 
    			   {
    				   gravar_arquivo();                        
    				   sArquivoCriado = MoverArquivo();
    			   }

    		   } else {
    			   AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
    			   builder.setTitle("Pedidos OffLine");
    			   builder.setMessage("Nenhum Pedido Encontrado.");
    			   builder.setPositiveButton("OK", null);
    			   builder.show();                                        
    		   }

    	   }                    
       });

       final Button buSair = (Button) findViewById(R.id.buSair);
       buSair.setOnClickListener(new View.OnClickListener()
       {
    	   public void onClick(View v)
    	   {                                                   
    		   finish();
    	   }                    
       });

       mHandler = new Handler();            
       checkUpdate.start();

    }        
        
    private void Esperar()
    {
    	try {
	    	Thread.sleep(1000);
		} catch (InterruptedException e) {								
			e.printStackTrace();
		}
    }
    
    private Thread checkUpdate = new Thread() {
        public void run() {
            try {
            	
            	String sPath="";
            	
            	sPath = dh.BuscaIp();
                 
                if (sPath=="") 
                {
                 	dh.BuscaURL(1);	
                }
            	
                URL updateURL 				= new URL(sPath + "/offline.txt");                
                URLConnection conn 			= updateURL.openConnection(); 
                InputStream is 				= conn.getInputStream();
                BufferedInputStream bis 	= new BufferedInputStream(is);
                ByteArrayBuffer baf 		= new ByteArrayBuffer(50);
                
                int current = 0;
                while((current = bis.read()) != -1)
                {
                     baf.append((byte)current);
                }
                
                final String s = new String(baf.toByteArray());         
                
                int curVersion = getPackageManager().getPackageInfo("com.novoprasistoffline", 0).versionCode;
                int newVersion = Integer.valueOf(s);
                
                if (newVersion > curVersion) 
                {                
                    mHandler.post(showUpdate);
                }                
            } catch (Exception e) 
            {
            	e.printStackTrace();
            }
        }
    };
 
    private Runnable showUpdate = new Runnable()
    {
           public void run(){
            new AlertDialog.Builder(Home.this)
            .setIcon(R.drawable.icon)
            .setTitle("Atualização Disponível")
            .setMessage("Existe uma atualização disponível! Deseja instalar agora ?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {                            
            		String sPath="";

            		sPath = dh.BuscaIp();

            		if (sPath=="") 
            		{
            			dh.BuscaURL(1);	
            		}
            		
            		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sPath + "/instalar.apk"));
            		startActivity(intent);
            		finish();
            		
            	}
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                            
                    }
            })
            .show();
           }
    };
    
    private void envioFTP(String nomeArquivo) {
       String sTexto;
       
       try {
    	      	  
    	   ClasseFtp clsFtp = new ClasseFtp();

    	  // clsFtp.Testar();
    	   
           boolean bRetorno = clsFtp.Enviar(nomeArquivo, sPastaServidor, sHostFtp, sFtpUsuario, sFtpSenha);
                  
           if (bRetorno!=true) 
           {
               sTexto = "Erro";               
           } 
           else 
           {
               sTexto = "Arquivo Enviado com sucesso com " + iTotalPedidos + " pedido(s) gravado(s).";
               iTotalPedidos=0;
           }
           
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                        builder.setTitle("Pedidos OffLine");
                                        builder.setMessage(sTexto);
                                        builder.setPositiveButton("OK", null);
                                        builder.show();      

            AbreNavegador();
            
            
       } catch (Exception e)
       {    	   
    	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Erro Envio FTP ( " + e.getMessage() + ", o arquivo " + nomeArquivo + " foi gerado e pode ser enviado manualmente.");
           builder.setMessage(e.getMessage());
           builder.setPositiveButton("OK", null);
           builder.show();           
       }
       
    }
        
	private void AbreNavegador() {
		
        List<String[]> sParametros;        
        this.dh = new DataHelper(getApplicationContext());           
        sParametros = dh.BuscaUsuario(sNomeVendedor.substring(0, 3), sEmpresa);
        
        for (String[] name : sParametros) 
        {
             sCodEmp  = name[0].toString();             
             sUsuario = name[1].toString();
             sSenha   = name[2].toString();             
        }
        
        String sPath="";
        
        sPath = dh.BuscaIp();
        
        if (sPath=="") 
        {
        	dh.BuscaURL(1);	
        }
                
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(sPath + "/login.aspx?1=" + sCodEmp + "&2=" + sUsuario + "&3=" + sSenha + ""));
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(viewIntent);
        
    }
    
    private boolean ValidarPedidos() 
    {
        this.dh = new DataHelper(getApplicationContext());                                   
        return this.dh.VerificaExistePedido();        
    }
    
    private void AbreTelaConfig()
    {
        Intent in = new Intent(Home.this, Configuracoes.class);                            
        finish();
        startActivity(in); 
    }
           
    private void gravar_arquivo() 
    {        
        try
        {       
        	iTotalPedidos=1;
            String sPedido="";
            String sCliente="";
            String sPrimeiroCliente="";
            int iProximo=0;
            
            iProximo = this.dh.ProximoPedido();
            
            this.dh = new DataHelper(getApplicationContext());                           
            String sDados="";
            String sDados2="";
            String sSecund="";
            
            List<String[]> arrayPedidos =null ;
            //id_pedido, datapedido, codfrmpgt, codtipprz, CODCLI, PRODUTO, QTD, PRECO, DESC, OBS, univenda, CodTabPrc, CodTabPrz, CodTipMov, Status
            arrayPedidos = this.dh.ListagemPedidos("PEDIDOS.Status = 'Liberado'");
      
            for (String[] name : arrayPedidos) {

            	//Popula primeira vez
            	if ("".equals(sPedido)) 
                {
                    sPedido = name[0];                    
                }
            	
            	//Popula primeira vez
                if ("".equals(sCliente)) 
                {
                	sCliente = name[4];                	
                }
                
                //Buscar cod. secundario
                //Verificar se tem asterisco, se houver, gerar outra linha de pedido com nova numeracao
                //
            	sSecund = this.dh.BuscaSecundario(name[5].toString());
            	            	
            	if ("***".equals(sSecund)) 
            	{
            		
            		if (!name[4].equals(sCliente)) 
                    {                    	
                    	iProximo++;
                    	sPedido = name[0];
                        sCliente = name[4];
                    }
            		
            		//Se for mesmo cliente, porem outro pedido. Acrescenta numeracao
            		if (name[4].equals(sCliente)) 
                    {                    	
            			if (!name[0].equals(sPedido)) 
                        {
            				iProximo++;
                    	}
                    }
            		
        				sDados2 = sDados2 + iProximo + "|" +                          
                        Itens.preencheCom(name[2],"0",3,1) + "|" + 
                        Itens.preencheCom(name[3],"0",3,1) + "|" + 
                        Itens.preencheCom(name[4],"0",8,1) + "|" + 
                        name[5] + "|" + 
                        name[6] + "|" + 
                        name[7].replace(",", ".")+ "|" +
                        name[8].replace(",", ".")+ "|" +
                        name[9].replace("null", "")+ "|" +                         
                        name[11] + "|" +
                        name[12] + "|" +
                        name[10] + "|" +
                        name[13] + "|" +
                        "***" + "\n";
            	} 
            	else 
            	{
            	
            			 sDados = sDados + name[0] + "|" +                          
                         Itens.preencheCom(name[2],"0",3,1) + "|" + 
                         Itens.preencheCom(name[3],"0",3,1) + "|" + 
                         Itens.preencheCom(name[4],"0",8,1) + "|" + 
                         name[5] + "|" + 
                         name[6] + "|" + 
                         name[7].replace(",", ".")+ "|" +
                         name[8].replace(",", ".")+ "|" +
                         name[9].replace("null", "")+ "|" +                         
                         name[11] + "|" +
                         name[12] + "|" +
                         name[10] + "|" +
                         name[13] + "\n";                
            	}
            	
                if (!name[0].equals(sPedido)) 
                {
                    sPedido = name[0];
                    sCliente = name[4];
                    iProximo++;
                    iTotalPedidos++;
                }
                
                if (!name[4].equals(sCliente)) 
                {
                	sCliente = name[4];                	
                }
                
                if ("".equals(sPrimeiroCliente)) {
                	sPrimeiroCliente = name[15].substring(0, 8).replace("*", "").replace("-", "").replace(" ", "").trim().toString();
                }
                
            }

            File arq;
            
            //Concatena pedidos normais dos secundários
            sDados = sDados + sDados2;
            
            //Gera nome arquivo
            String sVendedor = this.dh.BuscaIDVendedor();
            Date datapedido = new Date();
            SimpleDateFormat formatador = new SimpleDateFormat("ddMMyyyy_HHmmss");  
            
            arq = new File(Environment.getExternalStorageDirectory(), sVendedor.toString() + "_" + formatador.format(datapedido) + "_" + sPrimeiroCliente + ".txt");
            FileOutputStream fos = null;

            fos = new FileOutputStream(arq,true);

			//escreve os dados e fecha o arquivo			
			fos.write(sDados.getBytes());
			fos.flush();
			fos.close();
            
            this.dh.GravaNomeArquivoTxt(sVendedor.toString() + "_" + formatador.format(datapedido) + "_" + sPrimeiroCliente + ".txt");
        
            
        } catch (Exception e)
        {
            Log.e("Home", "Erro " + e.getMessage());
        }

    }  
        
    private String MoverArquivo()
    {
                
        this.dh = new DataHelper(getApplicationContext());
        String sArquivo = this.dh.GravaNomeArquivoTxt("");
                
        File sDestino = new File(Environment.getExternalStorageDirectory() + "/enviar/" + sArquivo);
        File sOrigem  = new File(Environment.getExternalStorageDirectory() + "/" + sArquivo);
        
        try 
        {
            //Move arquivo para pasta enviar
            copy(sOrigem, sDestino);            
            
            //Apaga arquivo da raiz
            sOrigem.delete();
            
            //Apaga dados do banco
            this.dh.ExecutarSql("DELETE FROM PEDIDOS WHERE Status = 'Liberado'");            
            this.dh.ExecutarSql("DELETE FROM ARQUIVO");
            this.dh.ExecutarSql("DELETE FROM NUMPED"); //Exclui numeração de pedidos a cada envio
           
            //Se não tiver configuração do ftp na tabela parametros, busca do arquivo conf_offline.txt
            if ("".equals(sHostFtp)) {
                
            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Arquivo enviar/" + sArquivo + " gerado com sucesso.")
                       .setCancelable(false)
                       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {                           
                                                              
                               iTotalPedidos=0;
                               AbreNavegador();
                           }
                       })
                       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                                //nothing
                           }
                       });
                AlertDialog alert = builder.create();
                alert.show();
                			    
                
            }
            
            //UploadFile(Environment.getExternalStorageDirectory() + "/enviar/" + sArquivo, sHostFtp + "/recebe/" + sArquivo);
            
            return sArquivo;
            
        } catch (IOException e) {
            Log.e("Home", "Erro " + e.getMessage());
            return e.toString();                
        }
    }
      
      
    public boolean isOnline()
    {
		boolean bRetorno=false;

		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();

			if (netInfo != null && netInfo.isConnectedOrConnecting()) 
			{
				bRetorno = true;
			} 
			else 
			{
				bRetorno = false;
			}        
		} 
		catch (Exception e)
		{                
			    AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setTitle("Envio Arquivo FTP");
			    builder.setMessage("Não há conexão de dados disponível no momento, verifique se há sinal 3G / Wi-Fi e tente novamente.");
			    builder.setPositiveButton("OK", null);
			    builder.show();  
			    bRetorno = false;
		}
		return bRetorno;
	}
    
    public static void copy(File origem, File destino) throws IOException 
    {        
        InputStream in = new FileInputStream(origem);
        OutputStream out = new FileOutputStream(destino);           
        
        // Transferindo bytes de entrada para saída
        byte[] buffer = new byte[1024];
        int lenght;
        while ((lenght= in.read(buffer)) > 0)
        {
            out.write(buffer, 0, lenght);
        }
        in.close();
        out.close();     
                
    }
    
    /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
}