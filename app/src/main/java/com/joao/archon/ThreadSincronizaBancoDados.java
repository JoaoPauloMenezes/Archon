package com.joao.archon;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

/**
 * Created by Joao on 09/05/2017.
 */
public class ThreadSincronizaBancoDados extends AsyncTask<String, Void, Integer>{

    DbHelper dbHelper;
    Context context;
    String macAdress;
    private ProgressDialog load;
    View rootView;

    public ThreadSincronizaBancoDados(Context context, String macAdress, View view){
        this.rootView = view;
        this.context = context;
        this.macAdress = macAdress;
        dbHelper = new DbHelper(context);
    }

    @Override
    protected void onPreExecute(){
        Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread: " + Thread.currentThread().getName());
        load = ProgressDialog.show(rootView.getContext(), "Por favor Aguarde ...",
                "Atualizando sua Lista de Pedidos");
    }

    @Override
    protected Integer doInBackground(String... context) {
        SoapRequests ex = new SoapRequests();

        int codigoPedido = ex.solicitaPedidos(macAdress);
        Log.i("CodigoNovoPedido ", codigoPedido + "" );

        try {
        int cont = 0;
        while((codigoPedido != -1)&&(codigoPedido != -2)){
            dbHelper.inserirPedido(codigoPedido);
            Thread.sleep(1000);
            codigoPedido = ex.solicitaPedidos(macAdress);
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("AsyncTask", e.getMessage());
        }
        return codigoPedido;
    }

    @Override
    protected void onPostExecute(Integer codigo) {
        if((codigo == -1)||(codigo == -2))
        dbHelper.close();
        load.dismiss();
    }
}
