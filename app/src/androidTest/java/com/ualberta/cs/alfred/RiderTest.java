package com.ualberta.cs.alfred;

import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.Gson;
import com.ualberta.cs.alfred.MainActivity;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Create all the test cases for Request
 * @author mmcote
 * @version 1
 * @see Rider
 */
public class RiderTest extends ActivityInstrumentationTestCase2 {
    public RiderTest() {
        super(MainActivity.class);
    }

    public void test() {
        Rider rider = new Rider("Mag", "Clown", "mag1", new Date(), "12345678",
                "jimbo@ualberta.ca", "34903845854");
        assertTrue(true);
    }

    public void testGetRider() {
        UserElasticSearchController.GetRider retrievedRider = new UserElasticSearchController.GetRider();
        retrievedRider.execute("jimbo");

        try {
            Rider rider = retrievedRider.get();
            System.out.println("++++++++++++++++++++++++");
            System.out.println("FIRSTNAME: " + rider.getFirstName());
            System.out.println("++++++++++++++++++++++++");
            assert(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void testAddRiderInfo() {
        RiderInfo riderInfo = new RiderInfo("23423432424");
        User user = new User("Jimmy", "Fallon", "jimFal", new Date(), "32432234", "jimmyFallon@ualberta.ca", riderInfo);

        UserElasticSearchController.AddUser<User> addUser = new UserElasticSearchController.AddUser<User>();
        addUser.execute(user);

        UserElasticSearchController.GetUserInfo getUserInfo = new UserElasticSearchController.GetUserInfo();
        getUserInfo.execute("jimFal");
        User userReturn = null;
        try {
            userReturn = getUserInfo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertTrue(userReturn.getUserName().contentEquals("jimFal"));
    }

    public void testUserInfoWithoutPrivate() {
        UserElasticSearchController.GetUserInfoWithoutPrivateInfo getUserInfoWithoutPrivateInfo = new UserElasticSearchController.GetUserInfoWithoutPrivateInfo();
        getUserInfoWithoutPrivateInfo.execute("jimFal", "riderInfo.creditCardNumber");

        User userReturn = null;
        try {
            userReturn = getUserInfoWithoutPrivateInfo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertTrue(userReturn.getUserName().contentEquals("jimFal"));
    }

    public void testUpdateUser() {
        UserElasticSearchController.GetUserInfo getUserInfo = new UserElasticSearchController.GetUserInfo();
        getUserInfo.execute("jimFal");
        User user = null;

        try {
            user = getUserInfo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        UserElasticSearchController.UpdateUser<RiderInfo> updateUser = new UserElasticSearchController.UpdateUser<RiderInfo>(user.getUserID(), "riderInfo");
        RiderInfo riderInfo = new RiderInfo("testCreditCardNumber2");
        Gson gson = new Gson();
        String json = gson.toJson(riderInfo);

        updateUser.execute(riderInfo);

        UserElasticSearchController.GetUserInfo getUserInfoAfter = new UserElasticSearchController.GetUserInfo();
        getUserInfoAfter.execute("jimFal");

        try {
            user = getUserInfo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
            String item = user.getEmail();
    }
}

