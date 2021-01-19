package com.navisens.pojostick.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.navisens.pojostick.navibeacon.NaviBeacon;
import com.navisens.pojostick.navibeacon.NaviBeaconCallback;
import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;
import com.navisens.pojostick.navisenscore.NavisensCore;
import com.navisens.pojostick.navisenscore.NavisensPlugin;
import com.navisens.pojostick.navisensmaps.NavisensMaps;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NaviBeaconCallback, OnMapReadyCallback {
    public GoogleMap mMap;
    double d1=0,d2=0,d3=0;
    NavisensCore core;
    public LatLng locat=null;
    private static final String MOTIONDNA_KEY = "0yyenHvfHNXOcBT0MnzXCIQjTbodKJ1yJx1gfmJ83Kngo1W13gm49cPQSVuxilnl";
    private boolean resetRequired = true;
    private static final double THRESHOLD = 0.25;
    private static final String TAG = "MainActivity";
    private static final double MARGIN = 0.25;
    private static final int REQUEST_MDNA_PERMISSIONS = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    public static final float DEFAULT_ZOOM = 15f;
    public Marker pos_marker;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //if(locat!=null){
          //  pos_marker = mMap.addMarker(new MarkerOptions().position(locat).title("Marker in Sydney"));
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locat, DEFAULT_ZOOM));

        //}
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locat,DEFAULT_ZOOM));
       // pos_marker = mMap.addMarker(new MarkerOptions().position(locat).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locat, DEFAULT_ZOOM));
        if (mLocationPermissionsGranted) {
            //getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //mMap.setMyLocationEnabled(true);
        }
      //  ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions(), REQUEST_MDNA_PERMISSIONS);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         core=new NavisensCore(MOTIONDNA_KEY,this);

        NavisensMaps maps = core.init(NavisensMaps.class)
                //     .addMap(NavisensMaps.Maps.OSM_Mapnik)
                //.addMap("https://www.google.com/maps/@?api=1&map_action=map","")
                .useLocalOnly()
                .showPath()
                .hideMarkers();
        // core.getMotionDna().setLocationGPSOnly();
       NaviBeacon beacon = core.init(NaviBeacon.class)
                //.setBeaconCallback(onBeaconResponded(beacon.,5.,5.,null,1))


             .addBeacon(Identifier.fromUuid(UUID.fromString("e2c56db5-dffb-48d2-b060-d0f5a71096e0")), 2., -1.,null,1)
                .addBeacon(Identifier.fromUuid(UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825")),3.,4.,null,1)
               .addBeacon(Identifier.fromUuid(UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d")),-3.,2.,null,1)
        //.setBeaconCallback(MainActivity.this)
           .setScanningPeriod(50,50)
        .setBeaconCallback(this);

        // test te=new test(MOTIONDNA_KEY,this);



         getFragmentManager().beginTransaction().add(android.R.id.content, maps).commit();
        //test t =  new test(MOTIONDNA_KEY,this);
        ;

    }
    public synchronized double[]  trilat(int A[],int B[],int C[],double d1,double d2,double d3){
        int i,j;
        double tab1[]= {1,1,-2*A[0],-2*A[1],A[0]*A[0]+A[1]*A[1]-d1*d1};
        double tab2[]= {1,1,-2*B[0],-2*B[1],B[0]*B[0]+B[1]*B[1]-d2*d2};
        double tab3[]= {1,1,-2*C[0],-2*C[1],C[0]*C[0]+C[1]*C[1]-d3*d3};
        double eq1[]=new double[3];
        double eq2[]=new double[3];
        double fin[]=new double[2];

        for(i=0,j=2;i<3;i++,j++) {
            eq1[i]=tab1[j]-tab2[j];
            eq2[i]=tab1[j]-tab3[j];
        }

        //for(i=0;i<2;i++) {
        fin[0]=eq2[0]*eq1[1]-eq1[0]*eq2[1];
        fin[1]=eq2[0]*eq1[2]-eq1[0]*eq2[2];
        //}

        double y=(double)-fin[1]/fin[0];
        double x=(-eq1[2]-(eq1[1]*y))/eq1[0];
        double tabl[]=new double[2];
        tabl[0]=x;
        tabl[1]=y;
        return tabl;
    }


    private void getDeviceLocation() {
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mfusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            //                      moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                        } else {
                            Toast.makeText(MainActivity.this, "impossible", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }

    }
    //private void moveCamera(LatLng latLng,float zoom){

    //      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    //}

    /*private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }*/

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
      //          initMap();
            } else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
                //ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions(), REQUEST_MDNA_PERMISSIONS);
            }
        } else {
            ////ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions(), REQUEST_MDNA_PERMISSIONS);
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onBeaconResponded(Beacon beacon, Double latitude, Double longitude, Double heading, Integer floor) {
        //latitude=5.;
        //longitude=5.;
        //heading =null;
        //int p1[]={2,-1};
        //int p2[]={3,4};
        //int p3[]={-3,2};

       Identifier a=Identifier.fromUuid(UUID.fromString("e2c56db5-dffb-48d2-b060-d0f5a71096e0"));//beacon.getIdentifier(0);
        Identifier b=Identifier.fromUuid(UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825"));
        Identifier c=Identifier.fromUuid(UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"));
        //String str=a.toUuid().toString();
        //Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
            if(beacon.getIdentifier(0).equals(a)){
                d1=beacon.getDistance();
                //Toast.makeText(this,"d1 "+String.valueOf(d1),Toast.LENGTH_SHORT).show();
            }
             if (beacon.getIdentifier(0).equals(b)){
                d2=beacon.getDistance();
                //Toast.makeText(this,"d2 "+String.valueOf(d2),Toast.LENGTH_SHORT).show();
        }
         if(beacon.getIdentifier(0).equals(c)){
                d3=beacon.getDistance();
                Toast.makeText(this,"d3 "+String.valueOf(d3),Toast.LENGTH_SHORT).show();
            }
            if(d1!=0&&d2!=0&&d3!=0){
              //  Toast.makeText(this,"Positionning",Toast.LENGTH_SHORT).show();
                double res[]=new double[2];
                res=trilat(p1,p2,p3,d1,d2,d3);
                Toast.makeText(this,"x "+String.valueOf(res[0])+"y "+String.valueOf(res[1]),Toast.LENGTH_SHORT).show();
                core.getMotionDna().setCartesianPositionXY(res[0], res[1]);
                d1=0;
                d2=0;
                d3=0;

            }




    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
        if(MotionDnaApplication.checkMotionDnaPermissions(this)){
            //getApplicationContext().startService(motionDnaServiceIntent);
            new test(MOTIONDNA_KEY,this);
        }
        //   new test(MOTIONDNA_KEY,this);

        //if (MotionDnaApplication.checkMotionDnaPermissions(this)) {
        //    test nav=new test(MOTIONDNA_KEY,this);
        //}

    }*/

    public class test implements MotionDnaInterface  {
        Activity act;
        MotionDnaApplication motionDnaApplication;
        public test(String devkey,Activity activ) {
            startMotionDna(devkey);
            act=activ;
        }
        public void startMotionDna(String dev){
            String devkey= dev;//"0yyenHvfHNXOcBT0MnzXCIQjTbodK+J1yJx1gfmJ83Kngo1W13gm49cPQSVuxilnl";
             motionDnaApplication=new MotionDnaApplication(this);
            motionDnaApplication.runMotionDna(devkey);

            motionDnaApplication.setLocationNavisens();
            motionDnaApplication.setBackpropagationEnabled(true);
            motionDnaApplication.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY);
            motionDnaApplication.setBinaryFileLoggingEnabled(true);
            motionDnaApplication.startUDP();
            motionDnaApplication.setBackpropagationBufferSize(2000);
            motionDnaApplication.setCallbackUpdateRateInMs(500);
            //motionDnaApplication.setCallbackUpdateRateInMs(500);
            motionDnaApplication.setARModeEnabled(true);
            motionDnaApplication.resume();

        }


        @Override
        public void receiveMotionDna(MotionDna motionDna) {
            double x=(double)motionDna.getLocation().globalLocation.latitude;
            double y=(double)motionDna.getLocation().globalLocation.longitude;
            if(locat==null){
                locat=new LatLng(x,y);
                 pos_marker = mMap.addMarker(new MarkerOptions().position(locat).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locat, DEFAULT_ZOOM));
            }

        }

        @Override
        public void receiveNetworkData(MotionDna motionDna) {

        }

        @Override
        public void receiveNetworkData(MotionDna.NetworkCode networkCode, Map<String, ?> map) {

        }

        @Override
        public void reportError(MotionDna.ErrorCode errorCode, String s) {

        }

        @Override
        public Context getAppContext() {
            return getApplicationContext();
        }

        @Override
        public PackageManager getPkgManager() {
            return this.act.getPackageManager();
        }
    }
}
