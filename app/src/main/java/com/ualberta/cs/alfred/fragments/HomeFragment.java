package com.ualberta.cs.alfred.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ualberta.cs.alfred.BuildConfig;
import com.ualberta.cs.alfred.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by carlcastello and shelleytian on 08/11/16.
 */

public class HomeFragment extends Fragment {

    MapView map;
    GeoPoint startPoint;
    GeoPoint destinationPoint;
    Marker startMarker;
    Marker endMarker;
    IMapController mapController;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        //important! set your user agent to prevent getting banned from the osm servers
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = (MapView) view.findViewById(R.id.map_view);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        startPoint = new GeoPoint(48.13, -1.63);
        destinationPoint = new GeoPoint(48.4, -1.9);

        mapController = map.getController();
        mapController.setZoom(9);
        mapController.setCenter(startPoint);

        startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setSnippet("Start");
        map.getOverlays().add(startMarker);

        endMarker = new Marker(map);
        endMarker.setPosition(destinationPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setSnippet("End");
        map.getOverlays().add(endMarker);

        //save & display changes
        map.invalidate();


        return view;
    }
}