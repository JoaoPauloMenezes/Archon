package com.joao.archon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ThreadPedido implements Runnable{

    private int codigo;
    private boolean entregaEmAndamento;
    String macAddress;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String message = "" + bundle.getInt("codigoPedido");

            SoapRequests ex = new SoapRequests();
            enviaLocalizacao(macAddress,message,String.valueOf(HomePageActivity.mLastLocation.getLatitude()),String.valueOf(HomePageActivity.mLastLocation.getLongitude()));

        }
    };

    public ThreadPedido(int codigo,String macAddress){
        this.macAddress = macAddress;
        this.codigo=codigo;
        entregaEmAndamento = true;
    }

    @Override
    public void run() {
        loop();
    }

    public void shutdown(){
        this.entregaEmAndamento =false;
    }

    public void loop(){
        do {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("codigoPedido", codigo);
            message.setData(bundle);
            handler.sendMessage(message);
            Log.i("Inicio Pedido >", "Iniciando Rastreio da Entrega Do Pedido " + codigo);
        }while (entregaEmAndamento);
    }

    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return ""+this.codigo;
    }

    public void setEntregaEmAndamento(boolean estado) {
       entregaEmAndamento=estado;
    }

    public static final void enviaLocalizacao(final String codigoEntregador, final String codigoPedido, final String latitude, final String longitude) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SoapRequests ex = new SoapRequests();
                ex.enviaLocalizacao(codigoEntregador,codigoPedido,latitude,longitude);
            }
        }).start();
    }
}