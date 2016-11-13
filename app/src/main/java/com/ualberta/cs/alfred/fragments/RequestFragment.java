package com.ualberta.cs.alfred.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ualberta.cs.alfred.Address;
import com.ualberta.cs.alfred.R;
import com.ualberta.cs.alfred.Request;
import com.ualberta.cs.alfred.RequestElasticSearchController;
import com.ualberta.cs.alfred.RequestList;

/**
 * Created by carlcastello on 09/11/16.
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

    private RequestList requestList;

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
                Status = "Pending";
                // Get user id from the user who requested a ride
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                userName = preferences.getString("USERNAME", null);
                // Hardcode distance for mock request
                rideDistance = 122.00;
                // Hardcode cost for mock request
                rideCost = 21.03;

                // Get the values on the edit text view and use them to create and address instance
                String start = startPoint.getText().toString();
                String end = endPoint.getText().toString();
                String[] startCoor = start.split(",");
                String[] endCoor = end.split(",");
                // Code for Geolocation
                Double x1 = Double.parseDouble(startCoor[0]);
                Double y1 = Double.parseDouble(startCoor[1]);
                Double x2 = Double.parseDouble(endCoor[0]);
                Double y2 = Double.parseDouble(endCoor[1]);
                startAddress = new Address("",x1,y1);
                endAddress = new Address("",x2,y2);

//                public Request(String requestStatus, Address sourceAddress, Address destinationAddress,
//                double distance, double cost, String riderID)

                // Create an instance of a request and store into elastic search
                Request request = new Request(Status,startAddress,endAddress,rideDistance,rideCost,userName);
                // Connect this to elastic search.
                RequestElasticSearchController.AddRequestTask requestTask = new RequestElasticSearchController.AddRequestTask();
                requestTask.execute(request);

                break;
            //UserElasticSearchController.GetRider getRider = new UserElasticSearchController.GetRider();
            //getRider.execute(ge);
        }
    }
}