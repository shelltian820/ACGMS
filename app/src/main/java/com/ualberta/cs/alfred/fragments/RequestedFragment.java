package com.ualberta.cs.alfred.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.RatingCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.ualberta.cs.alfred.GeoCoder;
import com.ualberta.cs.alfred.ConnectivityChecker;
import com.ualberta.cs.alfred.LocalDataManager;
import com.ualberta.cs.alfred.R;
import com.ualberta.cs.alfred.Request;
import com.ualberta.cs.alfred.RequestDetailsActivity;
import com.ualberta.cs.alfred.RequestESGetController;
import com.ualberta.cs.alfred.RequestList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by carlcastello on 09/11/16.
 */

public class RequestedFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ArrayAdapter<Request> requestAdapter;
    private ListView requestedListView;
    private SharedPreferences preferences;
    private RequestFragmentsListController rFLC;
    private List<Pair<String, String>> listNeeded;
    private String userID;



    private Button button1;
    private Button button4;
    private TableLayout tableLayout;
    private RelativeLayout.LayoutParams params;


    // Custom Check View
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;

    // Custom Filter Input View
    private EditText filterInput1;
    private EditText filterInput2;
    private EditText filterInput3;

    private int searchType = R.id.radioButtonKeyword;

    public RequestedFragment() {
        this.rFLC = new RequestFragmentsListController();
        this.listNeeded = null;
        this.requestAdapter = null;
        this.requestedListView = null;
        this.preferences = null;
        this.userID = null;
    }

    public static RequestedFragment newInstance() {
        Bundle args = new Bundle();
        RequestedFragment requestedFragment = new RequestedFragment();
        requestedFragment.setArguments(args);
        return requestedFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();


        userID = preferences.getString("USERID", null);

        ArrayList<Request> requestedList;
        if (preferences.getString("MODE", null).contentEquals("Driver Mode")) {
            this.listNeeded = Arrays.asList(new Pair<String, String>("requestStatus", "Pending"),
                    new Pair<String, String>("requestStatus", "Requested"));
            requestedList = rFLC.getRequestList(Arrays.asList(listNeeded.get(0))).removeDriver(userID);
            requestedList.addAll(rFLC.getRequestList(Arrays.asList(listNeeded.get(1))).returnArrayList());
        } else {
            this.listNeeded = Arrays.asList(new Pair<String, String>("riderID", userID));
            requestedList = (ArrayList<Request>) rFLC.getRequestList(listNeeded).getSpecificRequestList("Requested");
        }

        //determine if there is connectivity. If there is, save the data for future use
        //if not, load from a previoiusly saved image
        if (ConnectivityChecker.isConnected(getContext())){

            LocalDataManager.saveRRequestList(requestedList,preferences.getString("MODE", null),getContext());

            requestAdapter = new ArrayAdapter<>(view.getContext(), R.layout.custom_row, requestedList);
            requestedListView.setAdapter(requestAdapter);
        }
        else{
            requestedList = LocalDataManager.loadRRequestList(preferences.getString("MODE", null), getContext());
            requestAdapter = new ArrayAdapter<>(view.getContext(), R.layout.custom_row, requestedList);
            requestedListView.setAdapter(requestAdapter);
        }


        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Requested", Integer.toString(requestedList.size()));
        editor.commit();
        ListFragment.update(getContext());

        requestedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request r = (Request) requestedListView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), RequestDetailsActivity.class);
                intent.putExtra("passedRequest",r);
                intent.putExtra("FROM", "Requested");
                startActivityForResult(intent,0);
                //updateRequestList();
            }
        });
    }

    // Catch a onFinish Statement of both clickable activities to update the list.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateRequestList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requested,container,false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        requestedListView = (ListView) view.findViewById(R.id.requestedListView);

        tableLayout = (TableLayout) view.findViewById(R.id.filter_table);
        tableLayout.setVisibility(View.GONE);

        button1 = (Button) view.findViewById(R.id.show_filter);
        button4 = (Button) view.findViewById(R.id.revert_filter);
        button4.setVisibility(View.GONE);
        button4.setOnClickListener(this);

        if (preferences.getString("MODE", null).contentEquals("Driver Mode")) {
            button1.setOnClickListener(this);
        } else {
            button1.setVisibility(View.GONE);
        }

        Button button2 = (Button) view.findViewById(R.id.request_cancel_button);
        button2.setOnClickListener(this);

        Button button3 = (Button) view.findViewById(R.id.request_done_button);
        button3.setOnClickListener(this);


        filterInput1 = (EditText) view.findViewById(R.id.filter_input1);
        filterInput2 = (EditText) view.findViewById(R.id.filter_input2);
        filterInput3 = (EditText) view.findViewById(R.id.filter_input3);

        // Defualt View
        filterInput2.setVisibility(View.GONE);
        filterInput3.setVisibility(View.GONE);



        rb1 = (RadioButton) view.findViewById(R.id.radioButtonKeyword);
        rb2 = (RadioButton) view.findViewById(R.id.radioButtonAddress);
        rb3 = (RadioButton) view.findViewById(R.id.radioButtonCoordinates);
        rb4 = (RadioButton) view.findViewById(R.id.radioButtonPrice);

        rb1.setOnCheckedChangeListener(this);
        rb2.setOnCheckedChangeListener(this);
        rb3.setOnCheckedChangeListener(this);
        rb4.setOnCheckedChangeListener(this);
        return view;
    }

    // Handles Click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_filter:
                button1.setVisibility(View.GONE);
                tableLayout.setVisibility(View.VISIBLE);
                params = (RelativeLayout.LayoutParams) requestedListView.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.filter_table);
                break;

            case R.id.revert_filter:
                button1.setVisibility(View.VISIBLE);
                button4.setVisibility(View.GONE);
                params = (RelativeLayout.LayoutParams) requestedListView.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.show_filter);
                updateRequestList();
                break;

            case R.id.request_cancel_button:
                button1.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.GONE);

                params = (RelativeLayout.LayoutParams) requestedListView.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.show_filter);
                break;

            case R.id.request_done_button:
                button4.setVisibility(View.VISIBLE);
                tableLayout.setVisibility(View.GONE);
                params = (RelativeLayout.LayoutParams) requestedListView.getLayoutParams();
                params.addRule(RelativeLayout.ABOVE, R.id.revert_filter);

                ArrayList<Request> requestsRequested = new ArrayList<>();
                parseInput();

                //determine if there is connectivity. If there is, save the data for future use
                //if not, load from a previoiusly saved image
                if (ConnectivityChecker.isConnected(getContext())){

                    LocalDataManager.saveRRequestList(requestsRequested,preferences.getString("MODE",null),getContext());
                    modifyAdapter(requestsRequested);
                }
                else{
                    requestsRequested = LocalDataManager.loadRRequestList(preferences.getString("MODE", null), getContext());
                    modifyAdapter(requestsRequested);

                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Requested", Integer.toString(requestsRequested.size()));
                editor.commit();
                requestAdapter.notifyDataSetChanged();

                break;
        }
    }

    // Custom Radio button
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //EditText editText = (EditText) getView().findViewById(R.id.filter_input);
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radioButtonKeyword:
                    filterInput1.setHint(R.string.keyword_text);
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                    rb4.setChecked(false);

                    filterInput2.setVisibility(View.GONE);
                    filterInput3.setVisibility(View.GONE);

                    searchType = R.id.radioButtonKeyword;

                    break;
                case R.id.radioButtonAddress:
                    filterInput1.setHint(R.string.address_text);
                    filterInput2.setHint(R.string.city_text);
                    rb1.setChecked(false);
                    rb3.setChecked(false);
                    rb4.setChecked(false);

                    filterInput2.setVisibility(View.VISIBLE);
                    filterInput3.setVisibility(View.GONE);

                    searchType = R.id.radioButtonAddress;

                    break;
                case R.id.radioButtonCoordinates:
                    filterInput1.setHint(R.string.latitude_text);
                    filterInput2.setHint(R.string.longitude_text);
                    filterInput3.setHint(R.string.distance_text);
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb4.setChecked(false);

                    filterInput2.setVisibility(View.VISIBLE);
                    filterInput3.setVisibility(View.VISIBLE);

                    searchType = R.id.radioButtonCoordinates;
                    break;
                case R.id.radioButtonPrice:
                    filterInput1.setHint(R.string.price_text);
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb3.setChecked(false);

                    filterInput2.setVisibility(View.GONE);
                    filterInput3.setVisibility(View.GONE);

                    searchType = R.id.radioButtonPrice;
                    break;
            }
        }
    }

    // Parse the user given filters
    // Sends the input for execution
    private void parseInput() {
        //Todo Search elastic Search by the given filter
        switch (searchType) {
            case R.id.radioButtonKeyword:
                // Todo do some querry with Keywords
                String filter = filterInput1.getText().toString();
                if (filter.matches("")) {
                    Toast.makeText(getContext(), "Input is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    executeQuery("","",filter,0);
                }
                break;

            case R.id.radioButtonAddress:
                // Todo do some querry with Keyword - Address
                String address = filterInput1.getText().toString();
                String city = filterInput2.getText().toString();

                if (address.matches("") || city.matches("")) {
                    Toast.makeText(getContext(), "Input is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    GeoCoder geoCoder = GeoCoder.getInstance();
                    geoCoder.calculateCoordinatesString();
                    String coordinates = String.format("[%s, %s]",
                            geoCoder.getLongitudeString(), geoCoder.getLatitudeString());
                    executeQuery("2km", coordinates,"",1);
                }
                break;

            case R.id.radioButtonCoordinates:
                String latitude = filterInput1.getText().toString();
                String longitude = filterInput2.getText().toString();
                if (latitude.matches("") || longitude.matches("")) {
                    Toast.makeText(getContext(), "Input is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double x = Double.parseDouble(latitude);
                        double y = Double.parseDouble(longitude);
                        String coordinates = String.format("[%s, %s]",y,x);
                        String distance = "";
                        if (filterInput3.getText().toString().matches("")){
                            distance = "10km";
                        } else {
                            distance = filterInput3.getText().toString() + "km";
                            executeQuery(distance,coordinates,"",1);
                        }
                    } catch (NumberFormatException e) {
                        String errorMessage = "Invalid Filter Input";
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }

                }
            case R.id.radioButtonPrice:
                // Todo do some querry wiih Price
                break;
        }

    }

    // Execute Query on the parsed input
    private void executeQuery (String distance, String coordinates,String filter,int type) {
        RequestESGetController.GetRequestByMultiplePreferencesTask retrievedRequestedKeyword =
                new RequestESGetController.GetRequestByMultiplePreferencesTask();
        RequestESGetController.GetRequestByMultiplePreferencesTask retrievedPendingKeyword =
                new RequestESGetController.GetRequestByMultiplePreferencesTask();
        RequestESGetController.GetRequestByLocationTask retrievedRequestedCoordinates =
                new RequestESGetController.GetRequestByLocationTask();
        RequestESGetController.GetRequestByLocationTask retrievedPendingCoordinates =
                new RequestESGetController.GetRequestByLocationTask();

        ArrayList<Request> requestList = new ArrayList<>();
        try {
            if (type == 0) {
                retrievedRequestedKeyword.execute(
                        "requestStatus", "string", "Requested",
                        "_all", "string", filter
                );
                retrievedPendingKeyword.execute(
                        "requestStatus", "string", "Pending",
                        "_all", "string", filter
                );
                requestList = retrievedRequestedKeyword.get();
                requestList.addAll(new RequestList(retrievedPendingKeyword.get()).removeDriver(userID));
            } else {
                retrievedRequestedCoordinates.execute(
                        "requestStatus", "string", "Requested",
                        distance, coordinates
                );
                retrievedPendingCoordinates.execute(
                        "requestStatus", "string", "Pending",
                        distance, coordinates
                );
                ArrayList<Request> requestsRequest = retrievedRequestedCoordinates.get();
                requestsRequest.addAll(new RequestList(retrievedPendingCoordinates.get()).removeDriver(userID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        modifyAdapter(requestList);

    }

    private void modifyAdapter(ArrayList<Request> list) {
        requestAdapter.clear();
        requestAdapter.addAll(list);
        requestAdapter.notifyDataSetChanged();

    }

    public void updateRequestList() {
        requestAdapter.clear();
        List returned;
        if (preferences.getString("MODE", null).contentEquals("Driver Mode")) {
            returned = rFLC.getRequestList(Arrays.asList(listNeeded.get(0))).removeDriver(userID);
            returned.addAll(rFLC.getRequestList(Arrays.asList(listNeeded.get(1))).returnArrayList());
            requestAdapter.addAll(returned);
        } else {
            returned = rFLC.getRequestList(listNeeded).getSpecificRequestList("Requested");
            requestAdapter.addAll(returned);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Requested", Integer.toString(returned.size()));
        editor.commit();

        requestAdapter.notifyDataSetChanged();
    }
}
