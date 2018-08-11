package com.example.gabriela.evaluacionfinal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gabriela.evaluacionfinal.activities.ListActivity;
import com.example.gabriela.evaluacionfinal.cupboardclasses.OpenHelper;
import com.example.gabriela.evaluacionfinal.item.Info;
import com.example.gabriela.evaluacionfinal.utils.CustomInfoWindowAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    /*private static final String PARAM_TODO_ID = "PARAM_TODO_ID";
    private static final String PARAM_TYPE = "PARAM_TYPE";
    private static final String PARAM_TYPE_ADD = "PARAM_TYPE_ADD";*/
    private static final String PARAM_TYPE_EDIT = "PARAM_TYPE_EDIT";

    private GoogleMap mMap;
    String provider;
    private Marker marker;
    double lat = 0.0;
    double lng = 0.0;
    String s;
    String itemText;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private String TAG;

    String action;
    Long taskId = null;
    OpenHelper dbHelper;
    Info info;

    @BindView(R.id.txvLatitude) TextView txvLatitude;
    @BindView(R.id.txvLongitude) TextView txvLongitude;

    @NotEmpty(messageId = R.string.validation1, order = 1)
    @MinLength(value = 3, messageId = R.string.validation1, order = 2)
    @BindView(R.id.edtTitle) EditText edtTitle;

    @NotEmpty(messageId = R.string.validation1, order = 1)
    @MinLength(value = 3, messageId = R.string.validation1, order = 2)
    @BindView(R.id.edtDescription) EditText edtDescription;

    @BindView(R.id.btnList) Button btnList;
    @BindView(R.id.btnAdd) Button btnAdd;
    @BindView(R.id.spPlaceType) Spinner spPlaceType;
    @BindView(R.id.txvDate) TextView txvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mayAskPermission()) {
            requestLocationUpdates();
        }

        dbHelper = new OpenHelper(this);
        /*action = getIntent().getStringExtra(PARAM_TYPE);
        if (PARAM_TYPE_EDIT.equals(action)) {
            taskId = getIntent().getLongExtra(PARAM_TODO_ID, 0L);
            //CARGAR LA INFO DE LA BSE DE DATOS
            ListActivity.db = dbHelper.getReadableDatabase();
            info = cupboard().withDatabase(ListActivity.db).get(Info.class, Long.valueOf(taskId));
            ListActivity.db.close();

            this.edtTitle.setText(info.title);
            this.edtDescription.setText(info.description);
        }*/

        //mostrar fecha
        Date date = Calendar.getInstance().getTime();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s = formatter.format(date);
        txvDate.setText(s);
        //mostra seleccion del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.placeType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlaceType.setAdapter(adapter);
        spPlaceType.setOnItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MyUbication();
    }

    private void addMarker(double lat, double lng) {
        LatLng coordinates = new LatLng(lat, lng);
        CameraUpdate myUbication = CameraUpdateFactory.newLatLngZoom(coordinates, 16);
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(coordinates).title(coordinates.toString())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
        mMap.animateCamera(myUbication);
    }

    private void updateUbication(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            addMarker(lat, lng);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateUbication(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void MyUbication() {
        provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(provider);
        updateUbication(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);
    }

    //request permission
    public void requestLocationUpdates(){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);

        mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG, "Lat: "+locationResult.getLastLocation().getLatitude() + "Lng: "+locationResult.getLastLocation().getLongitude());
                lat = locationResult.getLastLocation().getLatitude();
                lng = locationResult.getLastLocation().getLongitude();
                String lati = Double.toString(lat);
                String longi = Double.toString(lng);
                txvLatitude.setText(lati);
                txvLongitude.setText(longi);
            }
        },getMainLooper());
    }

    private boolean mayAskPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                }
                break;
        }
    }

    //metodos de el spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemText = (String) spPlaceType.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @OnClick(R.id.btnList)
    public void btnList(View v) {
        Intent intent = new Intent(MapsActivity.this, ListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnAdd)
    public void btnAdd(View v) {
        if(FormValidator.validate(this, new SimpleErrorPopupCallback(this))){
            try{
                if (PARAM_TYPE_EDIT.equals(action)) {
                    //to access database
                    OpenHelper conn = new OpenHelper(this);
                    // Gets the data repository in write mode
                    ListActivity.db = conn.getWritableDatabase();
                    // Create a new map of values, where column names are the keys
                    info.title = edtTitle.getText().toString();
                    info.description = edtDescription.getText().toString();
                    info.latitude = lat;
                    info.longitude = lng;
                    //info.placeType = spPlaceType.getSelectedItem().toString();
                    info.date = txvDate.getText().toString();

                    //put the data into db
                    cupboard().withDatabase(ListActivity.db).put(info);
                    ListActivity.db.close();
                    // Insert the new row, returning the primary key value of the new row
                    Toast.makeText(this, edtTitle.getText().toString(), Toast.LENGTH_SHORT).show();
                    edtTitle.setText("");
                    edtDescription.setText("");
                    txvLatitude.setText(String.valueOf(lat));
                    txvLongitude.setText(String.valueOf(lng));
                    //spPlaceType.setSelection(Integer.parseInt(info.placeType));
                    //proceder si es ok
                    setResult(Activity.RESULT_OK);
                    finish();

                } else {
                    create();
                    //proceder si es ok
                    setResult(Activity.RESULT_OK);
                }
            }catch (SQLException e) {
                Log.e("errorGuargando", e.getMessage());
                ListActivity.db.close();
            }
        }
    }

    //metodo para crear una row en la base de datos
    private void create(){
        //to access database
        OpenHelper conn = new OpenHelper(this);
        // Gets the data repository in write mode
        ListActivity.db = conn.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        Info info = new Info();
        info.title = edtTitle.getText().toString();
        info.description = edtDescription.getText().toString();
        info.date = txvDate.getText().toString();
        info.latitude = lat;
        info.longitude = lng;
        info.placeType = spPlaceType.getSelectedItem().toString();
        //put the data into db
        cupboard().withDatabase(ListActivity.db).put(info);
        ListActivity.db.close();
        // Insert the new row, returning the primary key value of the new row
        Toast.makeText(this, edtTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        edtTitle.setText("");
        edtDescription.setText("");
        txvLatitude.setText(String.valueOf(lat));
        txvLongitude.setText(String.valueOf(lng));
        spPlaceType.setOnItemSelectedListener(this);
        txvDate.setText(s);
    }
}



