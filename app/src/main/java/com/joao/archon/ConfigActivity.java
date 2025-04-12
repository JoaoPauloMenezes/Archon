package com.joao.archon;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConfigActivity extends AppCompatActivity {

    public ConfigActivity() {
    }

    public static ConfigActivity newInstance(){
        ConfigActivity fragment = new ConfigActivity();
        return fragment;
    }

    public static String macAddress;
    TextView enderecoMAC;
    TextView codigoEntregador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_config);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Coletando o Endereço MAC do dispositivo
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        macAddress = info.getMacAddress();


        //Setando o Endereço MAC obtido na variável da tela
        enderecoMAC = (TextView) findViewById(R.id.textView3);
        enderecoMAC.setText(macAddress);

        //Obtendo e Setando o Código do Entregador Através do MAC
        codigoEntregador = (TextView) findViewById(R.id.textView5);
        codigoEntregador.setText(macAddress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

