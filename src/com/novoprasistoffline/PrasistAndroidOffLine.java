package com.novoprasistoffline;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class PrasistAndroidOffLine extends TabActivity {
    
	Context mContext=PrasistAndroidOffLine.this;
    SharedPreferences appPreferences;
    boolean isAppInstalled = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /**
         * check if application is running first time, only then create shorcut
         */
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isAppInstalled = appPreferences.getBoolean("isAppInstalled",false);
        if(isAppInstalled==false){
        	/**
        	 * create short code
        	 */
        	Intent shortcutIntent = new Intent(getApplicationContext(),PrasistAndroidOffLine.class);
        	shortcutIntent.setAction(Intent.ACTION_MAIN);
        	Intent intent = new Intent();
        	intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        	intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "PRASIST OffLine");
        	intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.logo));
        	intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        	getApplicationContext().sendBroadcast(intent);
        	/**
        	 * Make preference true
        	 */
        	SharedPreferences.Editor editor = appPreferences.edit();
        	editor.putBoolean("isAppInstalled", true);
        	editor.commit();
        }
       
        try {
        	
	        TabHost tabHost = getTabHost();
	        
	        TabSpec home = tabHost.newTabSpec("HOME");        
	        //home.setIndicator("",getResources().getDrawable(R.drawable.home)); //com icone
	        home.setIndicator("Home"); //sem icone.
	        Intent homeIntent = new Intent(this, Home.class);
	        home.setContent(homeIntent);
	        
	        TabSpec dados = tabHost.newTabSpec("PEDIDO");        
	        //dados.setIndicator("",getResources().getDrawable(R.drawable.dados));	        
	        dados.setIndicator("Dados"); //sem icone.
	        Intent dadosIntent = new Intent(this, PrePedido.class);
	        dados.setContent(dadosIntent);
	        
	        TabSpec itens = tabHost.newTabSpec("ITENS");        
            //itens.setIndicator("",getResources().getDrawable(R.drawable.itens));
	        itens.setIndicator("Itens"); //sem icone.
	        Intent itensIntent = new Intent(this, Itens.class);
	        itens.setContent(itensIntent);
                       	
    	    TabSpec tabpreco = tabHost.newTabSpec("PRECOS");        
    	    //tabpreco.setIndicator("",getResources().getDrawable(R.drawable.precos));
    	    tabpreco.setIndicator("Consultar Preços"); //sem icone.
            Intent PrecoIntent = new Intent(this, TabelaPrecos.class);
            tabpreco.setContent(PrecoIntent);
                        
            TabSpec historicofin = tabHost.newTabSpec("HISTORICO");        
    	    //historicofin.setIndicator("",getResources().getDrawable(R.drawable.precos));
            historicofin.setIndicator("Hist. Financ."); //sem icone.
            Intent HistIntent = new Intent(this, HistoricoFinanc.class);
            historicofin.setContent(HistIntent);
            
            TabSpec webpedidos = tabHost.newTabSpec("WEBPEDIDOS");       
    	    //
            webpedidos.setIndicator("Web Pedidos"); //sem icone.
            Intent WebPedidos = new Intent(this, WebPedidos.class);
            webpedidos.setContent(WebPedidos);
                      
            tabHost.addTab(home); 
            tabHost.addTab(dados);
            tabHost.addTab(itens);
            tabHost.addTab(tabpreco);
            tabHost.addTab(historicofin);
            tabHost.addTab(webpedidos);
              
		} catch (Exception e) {
			// TODO: escrever excecao			
		}
        
    }
    
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
	}
	
}