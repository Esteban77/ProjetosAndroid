package com.senac.esteban.caixaremediosapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

@SuppressWarnings("MissingPermission")
public class PrincipalActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private static final int REQUEST_PERMISSAO = 10;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        // verifica conexao internet

        //Inicia serviço de localização
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    public void chamarTela(View v){

        Intent it = null;

        switch (v.getId()){
            case R.id.bt_gerenciar:
                it = new Intent(this,CadastrarRemedios.class);
                break;
            case R.id.bt_verFarmacia:
                boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // Caso não esteja ativo abre um novo diálogo com as configurações para
                // realizar se ativamento
                if (!enabled) {
                    Toast.makeText(getApplicationContext(), "Ative o GPS", Toast.LENGTH_LONG);
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

                boolean result = verificaConexao();

                if(!result){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(PrincipalActivity.this);
                    alerta.setTitle("Sem conexão com a internet");
                    alerta.setMessage("Por favor conecte-se com uma rede de internet");
                    alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    alerta.show();
                }
                if(result && enabled){
                    localizacaoByProviderSigle(LocationManager.GPS_PROVIDER);
                }
                break;
            case R.id.bt_verRemedios:
                it = new Intent(this, VerRemedios.class);
                break;
            case R.id.bt_fechar:
                finish();
                break;
        }

        if(it!=null){
            startActivityForResult(it, RESULT_FIRST_USER);
        }
    }

    /* Função para verificar existência de conexão com a internet
	 */
    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public void localizacaoByProviderSigle(String provider) {

        verificarPermissao();

        //quando a requisicao da posicao é constante
        long tempo = 1000 * 5; //5 minutos
        float distancia = 30; // 30 metros
        locationManager.requestLocationUpdates(provider , tempo , distancia,  locationListener = new LocationListener() {

            //quando a requesicao da posicao é realizada uma unica vez
            //       locationManager.requestSingleUpdate(provider, new LocationListener() {

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                Toast.makeText(getApplicationContext(), "Status alterado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(String arg0) {
                Toast.makeText(getApplicationContext(), "Provider Habilitado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String arg0) {
                Toast.makeText(getApplicationContext(), "Provider Desabilitado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLocationChanged(Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                if (location != null) {

                   Toast.makeText(PrincipalActivity.this, "Lat.:" + latitude + "Lng.:" + longitude, Toast.LENGTH_SHORT).show();

                    try {
                       // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + latitude + "," + longitude + "?z=15"));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+latitude+","+longitude+"?z=15&q=farmacias"));
                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Florianópolis"));
                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+location.getLatitude()+","+location.getLongitude()+"(SENAI)"));
                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.streetview:cbll="+location.getLatitude()+","+location.getLongitude()));
                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=UFSC"));

                        //https://developers.google.com/maps/documentation/android-api/intents?hl=pt-br
                        //http://www.botecodigital.info/android/pegando-a-posicao-gps-no-android/
                        //Implementar o requestLocationUpdates

                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    }catch(ActivityNotFoundException anfe){
                        Toast.makeText(PrincipalActivity.this, "Baixe o google maps", Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("market://search?q=pname:" + "com.google.android.apps.maps");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }

            }
        }, null);
    }

   @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            locationManager.removeUpdates(locationListener);
        }catch(Exception e){

        }

    }

    public void verificarPermissao() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSAO);
            return;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_PERMISSAO && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(PrincipalActivity.this, "Permissão Concedida!\nRepita a ação!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PrincipalActivity.this, "Permissão Negada!", Toast.LENGTH_SHORT).show();
        }
    }
}
