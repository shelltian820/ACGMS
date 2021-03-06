package com.ualberta.cs.alfred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Creates the type RequestList.
 *
 * @author ookmm
 * @version 1.3
 */
public class RequestList {

    /**
     * The Request list.
     */
    protected List<Request> requestList;

    /**
     * Return array list array list.
     *
     * @return the array list
     */
    public ArrayList<Request> returnArrayList() {
        return (ArrayList<Request>) requestList;
    };

    /**
     * Instantiates a new Request list.
     */
    public RequestList() {
        this.requestList = new ArrayList<Request>();
    }

    /**
     * Instantiates a new Request list (Assignment Operator)
     *
     * @param requestList the request list
     */
    public RequestList(ArrayList<Request> requestList) {
        this.requestList = requestList;
    }

    /**
     * Adds list into a list
     *
     * @param list the list
     * @return request list
     */
    public List mergeRequestList(ArrayList<Request> list) {
        this.requestList.addAll(list);
        return requestList;
    }

    /**
     * Gets request list.
     *
     * @return the request list
     */
    public List<Request> getRequestList() {
        return requestList;
    }

    /**
     * Gets request.
     *
     * @param requestID the request id
     * @return the request
     */
    public Request getRequest(String requestID) {

        Request aRequest = null;
        for (Request r : requestList) {
            if (requestID.equals(r.getRequestID())) {
                aRequest = r;
                break;
            }
        }
        return aRequest;
    }

    /**
     * Gets request list ordered by date.
     *
     * @return the request ordered list
     */
    public List<Request> getRequestOrderedList() {

        Collections.sort(requestList, new Comparator<Request>() {
            public int compare(Request r1, Request r2) {
                return r1.getRequestDate().compareTo(r2.getRequestDate());
            }
        });

        return this.requestList;
    }

    /**
     * Gets a request list sorted by price
     *
     * @return the array list
     */
    public ArrayList<Request> sortByPricePerKM() {

        ArrayList<Request> list = new ArrayList<>();
        list.addAll(requestList);

        Collections.sort(list, new Comparator<Request>() {
            @Override
            public int compare(Request r1, Request r2) {
                return Double.compare((r1.getCost()/r1.getDistance()), (r2.getCost()/r2.getDistance()));
            }
        });
        return list;
    }

    public ArrayList<Request> sortByPrice() {

        ArrayList<Request> list = new ArrayList<>();
        list.addAll(requestList);

        Collections.sort(list, new Comparator<Request>() {
            @Override
            public int compare(Request r1, Request r2) {
                return Double.compare(r1.getCost(),r2.getCost());
            }
        });
        return list;
    }

    /**
     * Gets specific request list.
     *
     * @param requestStatus the request status
     * @return the specific request list
     */
    public ArrayList<Request> getSpecificRequestList(String requestStatus) {

        ArrayList<Request> specificRequestList = new ArrayList<Request>();

        if (requestList != null) {
            for (Request r : requestList) {
                if (r.getRequestStatus().equals(requestStatus)) {
                    specificRequestList.add(r);
                }
            }
        }

        return specificRequestList;
    }

    /**
     * removes a driver
     *
     * @param userID the username of the driver
     * @return the array list
     */
    public ArrayList<Request> removeDriver(String userID) {
        ArrayList<Request> tempRequestList = new ArrayList<>();
        for (Request request : requestList) {
            if (request.getDriverIDList() != null) {
                if (!request.getDriverIDList().contains(userID)) {
                    tempRequestList.add(request);
                }
            }
        }
        return tempRequestList;
    }


    /**
     * get the requested driver
     *
     * @param userName the username of the driver
     * @return the with driver
     */
    public ArrayList<Request> getWithDriver(String userName) {
        ArrayList<Request> tempRequestList = new ArrayList<>();
        for (Request request : requestList) {
            if (request.getDriverIDList() != null) {
                if (request.getDriverIDList().contains(userName)) {
                    tempRequestList.add(request);
                }
            }
        }
        return tempRequestList;
    }


    /**
     * Has request boolean.
     *
     * @param request the request
     * @return the boolean
     */
    public Boolean hasRequest(Request request) {
        return requestList.contains(request);
    }

    /**
     * Add request.
     *
     * @param request the request
     */
    public void addRequest(Request request) {
        requestList.add(request);
    }

    /**
     * Add multiple request.
     *
     * @param requests the requests
     */
    public void addMultipleRequest(List<Request> requests) {
        requestList.addAll(requests);
    }


    /**
     * Delete request.
     *
     * @param request the request
     */
    public void deleteRequest(Request request) {
        requestList.remove(request);
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
        return requestList.size();
    }

    /**
     * Gets with driver as selected.
     *
     * @param userID the user id
     * @return the with driver as selected
     */
    public ArrayList<Request> getWithDriverAsSelected(String userID) {
        ArrayList<Request> tempRequestList = new ArrayList<>();
        for (Request request : requestList) {
            if (request.getDriverID() != null) {
                if (request.getDriverID().contentEquals(userID)) {
                    tempRequestList.add(request);
                }
            }
        }
        return tempRequestList;
    }
}
