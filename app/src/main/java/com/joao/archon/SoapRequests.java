package com.joao.archon;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;

public class SoapRequests implements Serializable {

    private static final boolean DEBUG_SOAP_REQUEST_RESPONSE = true;
    private static final String NAMESPACE = "https://http://util.archon.joao";
    private static final String MAIN_REQUEST_URL =
                              "http://192.168.137.1:8080/archon/services/ArchonWebServiceImpl?wsdl";
    private static final String SOAP_ACTION = "http://util.archon.joao/hello";


    private static String SESSION_ID = "1";


    public SoapRequests(){

    }

    public void enviaLocalizacao(String codigoEntregador, String codigoPedido, String latitude, String longitude) {
        String methodname = "atualizaCoordenadas";

        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("codigoEntregador",codigoEntregador);
        request.addProperty("codigoPedido",codigoPedido);
        request.addProperty("latitude",latitude);
        request.addProperty("longitude",longitude);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        HttpTransportSE ht = getHttpTransportSE();
        try {
            ht.call(SOAP_ACTION, envelope);
            testHttpResponse(ht);
            SoapPrimitive resultsString = (SoapPrimitive)envelope.getResponse();

        } catch (SocketTimeoutException t) {
            t.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception q) {
            q.printStackTrace();
        }
    }

    public void atualizaEstadoPedido(String codigoMACEntregador, Integer codigoPedido) {
        String methodname = "atualizaEstadoPedido";

        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("codigoEntregador",codigoMACEntregador);
        request.addProperty("codigoPedido",codigoPedido);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        HttpTransportSE ht = getHttpTransportSE();
        try {
            ht.call(SOAP_ACTION, envelope);
            testHttpResponse(ht);
            SoapPrimitive resultsString = (SoapPrimitive)envelope.getResponse();

        } catch (SocketTimeoutException t) {
            t.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception q) {
            q.printStackTrace();
        }
    }

    public int solicitaPedidos(String codigoEntregador) {
        String methodname = "solicitaPedidos";
        int codigoPedido = -2;

        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("codigoEntregador",codigoEntregador);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        HttpTransportSE ht = getHttpTransportSE();
        try {
            ht.call(SOAP_ACTION, envelope);
            testHttpResponse(ht);

            SoapPrimitive resultsString = (SoapPrimitive)envelope.getResponse();

            codigoPedido = Integer.parseInt(resultsString.toString());

        } catch (SocketTimeoutException t) {
            t.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception q) {
            q.printStackTrace();
        }

        return codigoPedido;
    }



    public String getMessage(String mensagem) {
        String data = "Inicializada";
        String methodname = "hello";

        SoapObject request = new SoapObject(NAMESPACE, methodname);
        request.addProperty("name", mensagem);

        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);

        HttpTransportSE ht = getHttpTransportSE();
        try {
            ht.call(SOAP_ACTION, envelope);
            testHttpResponse(ht);
            SoapPrimitive respostaWebService = (SoapPrimitive) envelope.getResponse();
            return respostaWebService.toString();

        } catch (SocketTimeoutException t) {
            t.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception q) {
            q.printStackTrace();
        }
        return data;
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(MAIN_REQUEST_URL);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    private final void testHttpResponse(HttpTransportSE ht) {
        ht.debug = DEBUG_SOAP_REQUEST_RESPONSE;
        if (DEBUG_SOAP_REQUEST_RESPONSE) {
            Log.v("SOAP RETURN", "Request XML:\n" + ht.requestDump);
            Log.v("SOAP RETURN", "\n\n\nResponse XML:\n" + ht.responseDump);
        }
}
}

