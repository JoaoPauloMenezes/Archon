package com.joao.archon;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

/**
 * Created by Joao on 09/05/2017.
 */
public class ThreadAtualizaEstadoPedido extends AsyncTask<String, Void, Integer>{

    DbHelper dbHelper;
    Context context;
    String macAdress;
    int codigoPedido;
    private ProgressDialog load;
    View rootView;

    public ThreadAtualizaEstadoPedido(Context context, String macAdress, int codigoPedido, View view){
        this.rootView = view;
        this.context = context;
        this.codigoPedido = codigoPedido;
        this.macAdress = macAdress;
        dbHelper = new DbHelper(context);
    }

    @Override
    protected void onPreExecute(){
        Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread: " + Thread.currentThread().getName());
        load = ProgressDialog.show(rootView.getContext(), "Por favor Aguarde ...",
                "Atualizando o Estado do Pedido");
    }

    @Override
    protected Integer doInBackground(String... context) {
        SoapRequests ex = new SoapRequests();

        ex.atualizaEstadoPedido(macAdress,codigoPedido);
        Log.i("AsyncTask", "Atualizando Estado do Pedido " + codigoPedido );

        return codigoPedido;
    }

    @Override
    protected void onPostExecute(Integer codigo) {
        if((codigo == -1)||(codigo == -2))
        dbHelper.close();
        load.dismiss();
    }
}
