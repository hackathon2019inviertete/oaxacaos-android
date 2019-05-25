package com.example.oaxacaos;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.oaxacaos.Models.UXMethods;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    boolean isFABOpen = false;
    View fabBGLayout;
    FloatingActionButton optionsFab, reportFab, corruptionFab;
    LinearLayout reportLl, corruptionLl;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Comprobar que los permisos sean concedidos y que sean de ACCES_FINE_LOCATION
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                UXMethods.showToast(getContext(), getString(R.string.permissions_req), Toast.LENGTH_LONG);
            }
        } else {
            UXMethods.showToast(getContext(), getString(R.string.permissions_req), Toast.LENGTH_LONG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inicializar vista raiz y elementos para el fragmento del mapa desde fragment_events_map.xml
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        fabBGLayout = rootView.findViewById(R.id.fabBGLayout);
        optionsFab = rootView.findViewById(R.id.options_fab);
        reportLl = rootView.findViewById(R.id.report_ll);
        corruptionLl = rootView.findViewById(R.id.corruption_ll);
        reportFab = rootView.findViewById(R.id.report_fab);
        corruptionFab = rootView.findViewById(R.id.corruption_fab);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // Solicitar permisos
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

                // Obtener mejor localizacion y usarla para hacer zoom
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                CameraPosition cameraPosition;
                if (location != null) {
                    cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
                } else {
                    cameraPosition = new CameraPosition.Builder().target(new LatLng(17.0436248,-96.7119411)).zoom(13).build();
                }
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // float buttons animations
                optionsFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isFABOpen) {
                            showFABMenu();
                        } else {
                            closeFABMenu();
                        }
                    }
                });
                fabBGLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFABOpen) {
                            closeFABMenu();
                        }
                    }
                });

                reportFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                });

                corruptionFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

        return rootView;
    }

    public void showFABMenu(){
        isFABOpen=true;
        reportLl.setVisibility(View.VISIBLE);
        corruptionLl.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        optionsFab.animate().rotationBy(180);
        reportLl.animate().translationY(-188);
        corruptionLl.animate().translationY(-386);
    }

    public void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        optionsFab.animate().rotationBy(-180);
        reportLl.animate().translationY(0);
        corruptionLl.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    reportLl.setVisibility(View.GONE);
                    corruptionLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

}
