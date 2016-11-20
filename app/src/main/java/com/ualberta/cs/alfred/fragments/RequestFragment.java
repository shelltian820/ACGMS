package com.ualberta.cs.alfred.fragments;

import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ualberta.cs.alfred.Address;
import com.ualberta.cs.alfred.MenuActivity;
import com.ualberta.cs.alfred.R;
import com.ualberta.cs.alfred.Request;
import com.ualberta.cs.alfred.RequestESAddController;
import com.ualberta.cs.alfred.RequestList;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by carlcastello on 09/11/16.
 * This is the fragment for making a new request
 */

public class RequestFragment extends Fragment implements View.OnClickListener {
    // Variable for edit text view
    private String Status;
    private EditText startPoint;
    private Address startAddress;
    private Address endAddress;
    private EditText endPoint;
    private String userName;
    private Double rideCost;
    private Double rideDistance;


    public RequestFragment() {
    }

    public static RequestFragment newInstance() {
        Bundle args = new Bundle();
        RequestFragment requestFragment = new RequestFragment();
        requestFragment.setArguments(args);
        return requestFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request,container,false);

        startPoint = (EditText) view.findViewById(R.id.start_input);
        endPoint = (EditText) view.findViewById(R.id.end_input);

        Button doneButton = (Button) view.findViewById(R.id.request_done_button);
        doneButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_done_button:
                // Initialize request status
                Status = "Requested";

                // Get user id from the user who requested a ride
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                userName = preferences.getString("USERNAME", null);


                String start = startPoint.getText().toString();
                String end = endPoint.getText().toString();

                // Check if input is null
                if (start.matches("") || end.matches("")) {
                    // do nothing
                } else {
                    Geocoder geocoder = new Geocoder(getContext(),Locale.getDefault());
                    // List of points returned by the address
                    List<android.location.Address> startCoordinates;
                    List<android.location.Address> endCoordinates;
                    try {
                        startCoordinates = geocoder.getFromLocationName(start,1);
                        endCoordinates = geocoder.getFromLocationName(end,1);

                        int startCoordinatesSize = startCoordinates.size();
                        int endCoordinatesSize = endCoordinates.size();

                        // Check if both list are empty
                        if (startCoordinatesSize > 0 && endCoordinatesSize > 0) {
                            // get the coordinates of the first results for both address
                            double x1 = startCoordinates.get(0).getLatitude();
                            double y1 = startCoordinates.get(0).getLongitude();
                            double x2 = endCoordinates.get(0).getLatitude();
                            double y2 = endCoordinates.get(0).getLongitude();

                            // Address is defined as
                            // Address(String location, double longitude, double latitude)
                            startAddress = new Address(start, y1, x1);
                            endAddress = new Address(end, y2, x2);

                            // Create an instance of a request and store into elastic search
                            Request request = new Request(Status,startAddress,endAddress,rideDistance,rideCost,userName);

                            // Notify save
                            Toast.makeText(getActivity(),"Ride Requested",Toast.LENGTH_SHORT).show();

                            // go to list
                            MenuActivity.bottomBar.selectTabAtPosition(1,true);

                        } else {
                            // Error messages
                            String errorMessage = "Unable to find start and destination Address";
                            if (startCoordinatesSize == 0) {
                                errorMessage = "Unable to find the start address";
                            }
                            if (endCoordinatesSize == 0) {
                                errorMessage = "Unable to find the destination address";
                            }
                            Toast.makeText(getActivity(),errorMessage,Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                
                /*
                // Hardcode distance for mock request
                //rideDistance = 122.00;
                // Hardcode cost for mock request
                rideCost = 21.55;

                // Get the values on the edit text view and use them to create and address instance
                String start = startPoint.getText().toString();
                String end = endPoint.getText().toString();
                try {
                    String[] startCoor = start.split(",");
                    String[] endCoor = end.split(",");
                    // Code for Geolocation
                    Double x1 = Double.parseDouble(startCoor[0]);
                    Double y1 = Double.parseDouble(startCoor[1]);
                    Double x2 = Double.parseDouble(endCoor[0]);
                    Double y2 = Double.parseDouble(endCoor[1]);
                    startAddress = new Address("", x1, y1);
                    endAddress = new Address("", x2, y2);
                    //approximate distance with geopoint coordinates
                    double lat1 = x1 / 1e6;
                    double lng1 = y1 / 1e6;
                    double lat2 = x2 / 1e6;
                    double lng2 = y2 / 1e6;
                    float [] dist = new float[1];
                    Location.distanceBetween(lat1, lng1, lat2, lng2, dist);
                    double d =(double) dist[0];
                    rideDistance = new Double(d*1e3);

                } catch (NumberFormatException exception) {
                    exception.printStackTrace();
                }


//                public Request(String requestStatus, Address sourceAddress, Address destinationAddress,
//                double distance, double cost, String riderID)

                // Create an instance of a request and store into elastic search
                Request request = new Request(Status,startAddress,endAddress,rideDistance,rideCost,userName);

                // Notify save
                Toast.makeText(getActivity(),"Ride Requested",Toast.LENGTH_SHORT).show();

                // go to list
                MenuActivity.bottomBar.selectTabAtPosition(1,true);


                break;
                  */
            //UserElasticSearchController.GetRider getRider = new UserElasticSearchController.GetRider();
            //getRider.execute(ge);
        }
    }
}
