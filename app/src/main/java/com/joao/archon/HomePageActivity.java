package com.joao.archon;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HomePageActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private static final int TL = Toast.LENGTH_SHORT;
    DbHelper dbHelper;
    static FloatingActionButton iniciarEntrega;
    static FloatingActionButton finalizarEntrega;

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationManager locationManager;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        createLocationRequest();
    }

    protected void startLocationUpdates() {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private final void getMessage(final String toConvert) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SoapRequests ex = new SoapRequests();
                ex.getMessage(toConvert);
                Log.i("Inicio Pedido >", ex.getMessage(toConvert) );
            }
        }).start();
    }

    private final void getPedidos(final String macAdress) {
        new Thread (new Runnable() {

            @Override
            public void run() {

            }
        }).start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation.setLatitude(location.getLatitude());
        mLastLocation.setLongitude(location.getLongitude());

        //String msg = "Latitude: " + location.getLatitude()
        //        + "Longitude: " + location.getLongitude();
        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        //pega a logalização via intent
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

        Toast.makeText(getBaseContext(), "GPS Desligado!! ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "GPS Ligado!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        dbHelper = new DbHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Inicializando o Google Play Services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Inicializando o LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Parametros :
        //   provider    :  Registro do provedor GPS
        //   minTime     :  intervalo mínimo para notificação(em milissegundos)
        //   minDistance :  distância mínima para notificações, em métros
        //   listener     :  metodo chamado para acontecer a atualização da localização

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        }catch (SecurityException s){
            Log.d("IFMG", "Erro ao inicializar a busca pelo gps");
        }

        // Inicializando as configurações para acessar o WebService


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


       iniciarEntrega = (FloatingActionButton) findViewById(R.id.iniciar_entrega);
       finalizarEntrega = (FloatingActionButton) findViewById(R.id.finalizar_entrega);

        /**
         * Funcionalidades do Botão de Iniciar Entrega
         */

        iniciarEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(view.getContext(), "Item numero " + itemListaPedidos , TL).show();
                iniciarEntrega.setVisibility(View.INVISIBLE);
                finalizarEntrega.setVisibility(View.VISIBLE);

                final Spinner listaPedidosView = (Spinner) findViewById(R.id.listaPedidos);
                final int itemListaPedidos = Integer.parseInt(listaPedidosView.getSelectedItem().toString());

                    atualizaValoresPedidos(listaPedidosView.getSelectedItemPosition());
                    dbHelper.setEstadoPedido(itemListaPedidos, 1);

                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                    Log.i("Inicio Pedido >", "Iniciando Rastreio da Entrega Do Pedido" + itemListaPedidos);
                    final ThreadPedido novaEntrega = new ThreadPedido(itemListaPedidos,info.getMacAddress());
                    novaEntrega.setEntregaEmAndamento(true);


                    ThreadAtualizaEstadoPedido tAp =
                            new ThreadAtualizaEstadoPedido(getBaseContext(),
                                                            info.getMacAddress(),
                                                            itemListaPedidos , view );
                    tAp.execute();

                    final Thread threadPedido = new Thread(novaEntrega);
                    threadPedido.start();


                    /**
                     * Funcionalidades do Botão de FINALIZAR Entrega
                     */
                    finalizarEntrega.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finalizarEntrega.setVisibility(View.INVISIBLE);
                                Spinner listaPedidosView = (Spinner) findViewById(R.id.listaPedidos);
                                FloatingActionButton iniciarEntrega = (FloatingActionButton) findViewById(R.id.iniciar_entrega);

                                dbHelper.setEstadoPedido(itemListaPedidos, 2);
                                atualizaValoresPedidos(listaPedidosView.getSelectedItemPosition());

                            WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            WifiInfo info = manager.getConnectionInfo();
                            ThreadAtualizaEstadoPedido tAp =
                                    new ThreadAtualizaEstadoPedido(getBaseContext(),
                                            info.getMacAddress(),
                                            itemListaPedidos , view );
                            tAp.execute();

                                novaEntrega.shutdown();
                                threadPedido.interrupt();


                        }
                    });
            }
        }

            );


            /**
             * Funcionalidades do Botão de CRIAR NOVA Entrega
             */
            FloatingActionButton novaEntrega = (FloatingActionButton) findViewById(R.id.adiciona_entrega);

            novaEntrega.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View view){

                Spinner listaPedidosView = (Spinner) findViewById(R.id.listaPedidos);
                Adapter adaptadorDaListaAntiga = listaPedidosView.getAdapter();

                //Inserção do Pedido no Banco de Dados através do DBHelper

                    WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = manager.getConnectionInfo();
                    ThreadSincronizaBancoDados tAp = new ThreadSincronizaBancoDados(getBaseContext(),
                                                                                    info.getMacAddress(),
                                                                                    view );
                    tAp.execute();


                final ArrayList listaDePedidos = dbHelper.getTodosPedidos();
                if (listaDePedidos == null) {
                    listaDePedidos.add(" ");
                }

                ArrayAdapter<String> adaptadorDeLista = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, listaDePedidos);
                listaPedidosView.setAdapter(adaptadorDeLista);
                listaPedidosView.setSelection(listaDePedidos.size() - 1, true);

            }
            }

            );

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.

        }

    public void atualizaValoresPedidos(int position) {
        Spinner listaPedidosView = (Spinner) findViewById(R.id.listaPedidos);
        EditText codigoPedido = (EditText) findViewById(R.id.codigoPedido);
        EditText estadoPedido = (EditText) findViewById(R.id.estadoPedido);
        DbHelper dbHelper = new DbHelper(this);


        estadoPedido.setText(dbHelper.getNomeEstadoPedido(Integer.parseInt(listaPedidosView.getSelectedItem().toString())));
        codigoPedido.setText(listaPedidosView.getSelectedItem().toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, ConfigActivity.class);
            //**20**
            startActivity(intent);
            return true;
        }

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        if(!mGoogleApiClient.isConnected())
        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HomePage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.joao.archon/http/host/path")
        );

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HomePage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.joao.archon/http/host/path")
        );
      mGoogleApiClient.disconnect();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        String dataFormatada;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

            //Carregando e Gerando Lista de Pedidos
            final DbHelper dbHelper = new DbHelper(rootView.getContext());

            final ArrayList listaDePedidos = dbHelper.getTodosPedidos();
            if (listaDePedidos == null) {
                listaDePedidos.add(" ");
            }

            ArrayAdapter<String> adaptadorDeLista = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listaDePedidos);

            RelativeLayout infoPedidos = (RelativeLayout) rootView.findViewById(R.id.infoPedido);
            EditText dataPedido = (EditText) rootView.findViewById(R.id.dataPedido);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd");
            dataFormatada = df.format(c.getTime());

            infoPedidos.setVisibility(View.INVISIBLE);
            dataPedido.setText(dataFormatada);

            final Spinner listaPedidosView = (Spinner) rootView.findViewById(R.id.listaPedidos);
            Log.i("", "" + listaPedidosView.getChildCount());

            listaPedidosView.setAdapter(adaptadorDeLista);
            listaPedidosView.setSelection(listaDePedidos.size() - 1);
            listaPedidosView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    RelativeLayout infoPedidos = (RelativeLayout) getActivity().findViewById(R.id.infoPedido);
                     iniciarEntrega = (FloatingActionButton) getActivity().findViewById(R.id.iniciar_entrega);
                     finalizarEntrega = (FloatingActionButton) getActivity().findViewById(R.id.finalizar_entrega);
                    infoPedidos.setVisibility(View.VISIBLE);
                    atualizaValoresPedidos(position);

                    int estadoPedido = dbHelper.getEstadoPedido(Integer.parseInt(listaPedidosView.getSelectedItem().toString()));

                    if(estadoPedido == 0){
                        finalizarEntrega.setVisibility(View.INVISIBLE);
                        iniciarEntrega.setVisibility(View.VISIBLE);
                    }else{

                        if(estadoPedido == 1){
                            finalizarEntrega.setVisibility(View.VISIBLE);
                            iniciarEntrega.setVisibility(View.INVISIBLE);
                        } else {
                            finalizarEntrega.setVisibility(View.INVISIBLE);
                            iniciarEntrega.setVisibility(View.INVISIBLE);
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    RelativeLayout infoPedidos = (RelativeLayout) getActivity().findViewById(R.id.infoPedido);
                    infoPedidos.setVisibility(View.INVISIBLE);
                }
            });

            return rootView;
        }

        public void atualizaValoresPedidos(int position) {
            Spinner listaPedidosView = (Spinner) getActivity().findViewById(R.id.listaPedidos);
            EditText codigoPedido = (EditText) getActivity().findViewById(R.id.codigoPedido);
            EditText estadoPedido = (EditText) getActivity().findViewById(R.id.estadoPedido);
            DbHelper dbHelper = new DbHelper(getActivity());

            estadoPedido.setText(dbHelper.getNomeEstadoPedido(listaPedidosView.getSelectedItemPosition() + 1));
            codigoPedido.setText(listaPedidosView.getAdapter().getItem(position).toString());

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return MapsActivity.newInstance();
                // default: return MyFrag.newInstance();
                    /* You could use default here instead*/
                //STILL NEED TO CHANGE THE getCount!!
            }
            return null; //if you use default, you would not need to return null
        }


        //added the new getCount0000
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Entregas";//change these to change tab names
                case 1:
                    return "Mapa";
            }
            return null;
        }

    }
}

