package com.example.vale.migpslocation;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity  {

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener myLocationListener;
    private final static int COD_PETICION_PERMISOS = 103;




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d(getClass().getCanonicalName(), "VUELTA DE PEDIR PERMISOS");

        if (requestCode == COD_PETICION_PERMISOS) {
            // Si el usaurio cancela, el array no tiene ninguna posición--Chequear
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d(getClass().getCanonicalName(), "PERMISO DE LOCALIZACIÓN FINA CONCECIDO en ejecución");


                //Para quitar el error
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); //si incluyo esta línea, aunque no haga nada con el valor, A studio deja de dar la vara con la SecurityException


                if (locationManager.isProviderEnabled(provider)) {
                    Log.d(getClass().getCanonicalName(), "EL Acceso fino por GPS está habilitado");
                    Location location_inicial = locationManager.getLastKnownLocation(provider);//"gps"
                    mostrarLocalizacion(location_inicial);

                }
                else
                {
                    Log.d(getClass().getCanonicalName(), "Pidiendo que habilite el acceso");
                    solicitarActivarLocalizacion();


                }


            } else {

                Log.d(getClass().getCanonicalName(), "PERMISO DE LOCALIZACIÓN DENEGADO en ejecución");
                Toast.makeText(this, "CHAMPION, sin PERMISO localización no se puede seguir. BYE", Toast.LENGTH_LONG);
                finish();

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!locationManager.isProviderEnabled(provider))
        {
            Toast.makeText(this, "CHAMPION, sin GPS ACTIVADO no se puede seguir. BYE", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void mostrarLocalizacion(Location location) {
        double lat = 0;
        double lng = 0;
        double alt = 0;


        TextView tv = (TextView)findViewById(R.id.localizacion);
        TextView tvh = (TextView) findViewById(R.id.hora);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateformatter = new SimpleDateFormat("E dd/MM/yyyy 'a las' hh:mm:ss");

        tvh.setText(dateformatter.format(calendar.getTime()));

        if (null != location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            alt = location.getAltitude();
            tv.setText(lat + " " + lng +" "+alt );
            Log.d(MainActivity.class.getCanonicalName(), "LATITUD = " + lat);
            Log.d(MainActivity.class.getCanonicalName(), "LONGITUD = " + lng);
            Log.d(MainActivity.class.getCanonicalName(), "ALTITUD = " + alt);

            Log.d(MainActivity.class.getCanonicalName(), "Proveedor = " + location.getProvider());

        } else {

            tv.setText("LOCALIZACIÓN DESCONOCIDA");
            Log.d(MainActivity.class.getCanonicalName(), "LOCALIZACIÓN null ");
        }


    }


    private void solicitarActivarLocalizacion ()

    {

        FragmentManager fm = this.getFragmentManager();
        DialogoGPS dialogo = new DialogoGPS();
        dialogo.show(fm, "Aviso");

       /* Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);//la petición va destinada al grupo
        //startActivity(viewIntent); //
        startActivityForResult(viewIntent, 500);*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(getClass().getCanonicalName(), "Entramos por oncreate ...");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocationListener = new MyLocationListener(this);
        provider = LocationManager.GPS_PROVIDER;

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, COD_PETICION_PERMISOS);

     }



    @Override
    protected void onResume() {
        super.onResume();

        Log.d(getClass().getCanonicalName(), "La aplicación entra (o vuelve a entrar) a primer plano, ACTIVO");


        try {
            locationManager.requestLocationUpdates(provider, 5000, 0, myLocationListener);
        } catch (SecurityException se) {
            Log.e(getClass().getCanonicalName(), "ERROR" , se);
        }
        catch (Throwable se) {
            Log.e(getClass().getCanonicalName(), "ERROR" , se);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(getClass().getCanonicalName(), "La aplicación entra en pausa (deja de estar visible), paro para optimizar");

        try {
            locationManager.removeUpdates(myLocationListener);

        } catch (SecurityException se) {
            Log.e(getClass().getCanonicalName(), "Sin permisos");
        }
    }


}