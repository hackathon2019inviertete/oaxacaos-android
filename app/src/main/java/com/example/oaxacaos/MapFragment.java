package com.example.oaxacaos;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oaxacaos.Api.GetLikesMethod;
import com.example.oaxacaos.Api.GetMethod;
import com.example.oaxacaos.Api.PostMethod;
import com.example.oaxacaos.Models.Reports;
import com.example.oaxacaos.Models.UXMethods;
import com.example.oaxacaos.Models.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    boolean isFABOpen = false;
    View fabBGLayout;
    FloatingActionButton optionsFab, reportFab, corruptionFab;
    LinearLayout reportLl, corruptionLl;
    ViewGroup root;
    ArrayList<Reports> al_reports;
    LatLng currentLatLng;
    boolean isMapReady = false;
    Reports selectedReport;

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    MediaPlayer mediaPlayer;
    boolean isRecording = false;

    private static final int REQUEST_RECORDING = 10;
    private static final int REQUEST_MAP = 20;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Comprobar que los permisos sean concedidos y que sean de ACCES_FINE_LOCATION
        if (requestCode == REQUEST_MAP) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    UXMethods.showToast(getContext(), getString(R.string.permissions_req), Toast.LENGTH_LONG);
                }
            } else {
                UXMethods.showToast(getContext(), getString(R.string.permissions_req), Toast.LENGTH_LONG);
            }
        } else if (requestCode == REQUEST_RECORDING) {
            if (grantResults.length> 0) {
                boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    startRecording();
                } else {
                    UXMethods.showToast(getContext(), getString(R.string.permissions_req), Toast.LENGTH_LONG);
                }
            }
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

        /**
         * Extras para ambos popup
         */
        // dim background para popup detalles
        root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();

        // Tamaño de pantalla para calcular tamaño de popup nuevo reporte
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int pxScreenWidth = size.x;
        int pxPopupMarginsWidth = UXMethods.convertDensityPointsToPixels(120, getContext());

        // Layout inflater service
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /**
         * Inflater para obtener elementos xml del popup nuevo reporte y poder modificarlos
        */
        final View detailsView = layoutInflater.inflate(R.layout.popup_new_report, new LinearLayout(rootView.getContext()), false);
        final TextView popupAddress = detailsView.findViewById(R.id.popup_address_tv);
        final Button popupSendReport = detailsView.findViewById(R.id.popup_send_report);
        final NumberPicker popupTypeNp = detailsView.findViewById(R.id.popup_report_type_np);
        String [] tipos = {"Semáforo", "Accidente", "Bloqueo", "Obstrucción"};
        popupTypeNp.setMinValue(0);
        popupTypeNp.setMaxValue(tipos.length-1);
        popupTypeNp.setDisplayedValues(tipos);

        // Establecer tamaño de popup nuevo reporte
        final PopupWindow popupWindow = new PopupWindow(detailsView, pxScreenWidth - pxPopupMarginsWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        /**
         * Inflater para obtener elementos xml del popup detalles y poder modificarlos
         */
        final View detailsRepView = layoutInflater.inflate(R.layout.popup_report_details, new LinearLayout(rootView.getContext()), false);
        final TextView popupDetAddress = detailsRepView.findViewById(R.id.popup_det_address_tv);
        final ImageButton popupLikeBtn = detailsRepView.findViewById(R.id.popup_like_btn);

        // Establecer tamaño de popup detalles
        final PopupWindow popupDetWindow = new PopupWindow(detailsRepView, pxScreenWidth - pxPopupMarginsWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Mapa y funciones
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
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_MAP);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

                // Obtener mejor localizacion y usarla para hacer zoom
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                CameraPosition cameraPosition;
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    cameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(15).build();
                } else {
                    currentLatLng = new LatLng(17.0436248,-96.7119411);
                    cameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(13).build();
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
                        closeFABMenu();
                        Toast.makeText(getContext(), "Por favor haz click en la ubicación", Toast.LENGTH_SHORT).show();
                        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                            @Override
                            public void onMapLongClick(final LatLng latLng) {
                                try {
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                                    // Mostrar direccion
                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> markerInfo = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                    String newAddress = markerInfo.get(0).getAddressLine(0);

                                    popupAddress.setText(newAddress);
                                    popupWindow.setAnimationStyle(R.style.popup_window_animation);
                                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    popupWindow.setFocusable(true);
                                    popupWindow.setOutsideTouchable(true);
                                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            UXMethods.clearDim(root);
                                            googleMap.setOnMapLongClickListener(null);
                                            getReports();
                                        }
                                    });
                                    popupSendReport.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sendReport(latLng, popupTypeNp.getValue());
                                            popupWindow.dismiss();
                                            googleMap.setOnMapLongClickListener(null);
                                            getReports();
                                        }
                                    });
                                    popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                                    UXMethods.applyDim(root, 0.5f);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                corruptionFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: FINAL
                        if(checkPermission()) {
                            if (!isRecording) {
                                startRecording();
                                isRecording = true;
                                Log.i("TestApp", "voa graba");
                            } else {
                                mediaRecorder.stop();
                                isRecording = false;
                                Log.i("TestApp", "ia we");
                                Toast.makeText(getContext(), "detenido...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            requestPermission();
                        }
                    }
                });

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String tag = (String) marker.getTag();
                        for (Reports reports : al_reports) {
                            if (reports.getReport_id().equals(tag)) {
                                selectedReport = reports;
                                popupDetAddress.setText(reports.getReportType() + ": " + reports.getAddress() + "\nLikes: " + reports.getLikes().length());
                            }
                        }

                        popupDetWindow.setAnimationStyle(R.style.popup_window_animation);
                        popupDetWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupDetWindow.setFocusable(true);
                        popupDetWindow.setOutsideTouchable(true);
                        popupDetWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                UXMethods.clearDim(root);
                            }
                        });
                        popupLikeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedReport != null){
                                    uploadLike(selectedReport.getReport_id());
                                    getReports();
                                }
                                popupDetWindow.dismiss();
                            }
                        });
                        popupDetWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                        UXMethods.applyDim(root, 0.5f);
                        return false;
                    }
                });

                getReports();
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

    public void sendReport(final LatLng latLng, final int typeReport) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonReport = new JSONObject();
                    jsonReport.put("report_type", typeReport);
                    jsonReport.put("latitude", latLng.latitude);
                    jsonReport.put("longitude", latLng.longitude);

                    PostMethod postMethod = new PostMethod();
                    JSONObject jsonResponse = postMethod.makeRequest(getString(R.string.create_report_url), jsonReport, getContext(), true);

                    final String mensaje;
                    if (jsonResponse != null) {
                        mensaje = "Reporte enviado!";
                    } else {
                        mensaje = "Error, por favor intenta mas tarde";
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getReports() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                al_reports = new ArrayList<>();
                try {
                    JSONObject jsonLocation = new JSONObject();
                    jsonLocation.put("latitude", currentLatLng.latitude);
                    jsonLocation.put("longitude", currentLatLng.longitude);
                    GetMethod getMethod = new GetMethod();
                    JSONArray jsonResponse = getMethod.makeRequest(getString(R.string.get_reports_url), jsonLocation, getContext(), true);
                    if (jsonResponse != null) {
                        for (int index = 0; index < jsonResponse.length(); index++) {
                            final JSONObject report = jsonResponse.getJSONObject(index);
                            Reports reports = new Reports();
                            reports.setLatitude((double) report.getJSONObject("location").getJSONArray("coordinates").get(1));
                            reports.setLongitude((double) report.getJSONObject("location").getJSONArray("coordinates").get(0));
                            reports.setLikes(report.getJSONArray("likes"));
                            reports.setStatus(report.getInt("status"));
                            reports.setReportType(report.getInt("report_type"));
                            reports.setAddress(report.getString("address"));
                            reports.setUserId(report.getString("user_id"));
                            reports.setCreatedAt(report.getString("created_at"));
                            reports.setUpdatedAt(report.getString("updated_at"));
                            reports.setReport_id(report.getString("_id"));

                            al_reports.add(reports);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                googleMap.clear();
                                reloadMap();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Error al recuperar marcadores", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void reloadMap() {
        for (Reports report : al_reports) { // TODO porque itera menos ciclos
            Log.i("TestApp", report.getLatitude() + " " + report.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(report.getLatitude(), report.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            marker.setTag(report.getReport_id());
        }
    }

    public void uploadLike(final String report_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetLikesMethod getMethod = new GetLikesMethod();
                JSONArray jsonLikes = getMethod.makeRequest(getString(R.string.create_report_url) + report_id + getString(R.string.update_likes_url), getContext(), true);
                Log.i("TestApp", jsonLikes.toString());
            }
        }).start();
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_RECORDING);
    }

    public void startRecording() {
        Calendar cal = Calendar.getInstance();
        String timeName = cal.getTime().toString();

        AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + timeName + "AudioRecording.3gp";
        UserData.getInstance().files.add(AudioSavePathInDevice);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(getContext(), "grabando...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
