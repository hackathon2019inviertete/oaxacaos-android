package com.example.oaxacaos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inicializar vista raiz y elementos para el fragmento del mapa desde fragment_events_map.xml
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

}
