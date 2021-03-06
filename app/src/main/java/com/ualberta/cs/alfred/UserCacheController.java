package com.ualberta.cs.alfred;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

/**
 * This user cache is used to store the user cache generated from the last
 * use of the application.
 *
 * @author mmcote
 * @version 1.0
 */

public class UserCacheController {
    private static final String USERFILE = "user.sav";
    private Context startUpContext;

    private static UserCache userCache;

    /**
     * constructor for creating new userCacheController
     *
     *@param context the desired context
     */
    public UserCacheController(Context context) {
        this.startUpContext = context;
        this.userCache = userLoggedIn();
    }

    /**
     * Keeps user logged in when app is on standby
     *
     *@param user user
     */
    public void keepLoggedIn(User user) {
        try {
            FileOutputStream fos = startUpContext.openFileOutput(USERFILE, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(user, out);
            out.flush();

            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * keeps user logged in
     *
     *
     */
    public UserCache userLoggedIn() {
        try {
            FileInputStream fis = this.startUpContext.openFileInput(USERFILE);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();
            Type userCache = new TypeToken<UserCache>(){}.getType();

            return gson.fromJson(in, userCache);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}