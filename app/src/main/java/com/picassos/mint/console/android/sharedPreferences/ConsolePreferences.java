package com.picassos.mint.console.android.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class ConsolePreferences {

    private final SharedPreferences sharedPreferences;

    /*---------------- Shared Pref ---------------*/
    public ConsolePreferences(Context context) {

        sharedPreferences = context.getSharedPreferences("Console Preferences", Context.MODE_PRIVATE);

    }

    /*---------------- Set Token ---------------*/
    public void setToken(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Token", state);

        editor.apply();

    }

    /*---------------- Load Token ---------------*/
    public String loadToken() {

        return sharedPreferences.getString("Token", "exception:error?token");

    }

    /*---------------- Set Secret API Key ---------------*/
    public void setSecretAPIKey(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("SecretApiKey", state);

        editor.apply();

    }

    /*---------------- Load Secret API Key ---------------*/
    public String loadSecretAPIKey() {

        return sharedPreferences.getString("SecretApiKey", "exception:error?sak");

    }

    /*---------------- Set Firebase Access Token ---------------*/
    public void setFirebaseAccessToken(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("FirebaseAccessToken", state);

        editor.apply();

    }

    /*---------------- Load Firebase Access Token ---------------*/
    public String loadFirebaseAccessToken() {

        return sharedPreferences.getString("FirebaseAccessToken", "exception:error?fak");

    }

    /*---------------- Set Package Name ---------------*/
    public void setPackageName(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("PackageName", state);

        editor.apply();

    }


    /*---------------- Load Package Name ---------------*/
    public String loadPackageName() {

        return sharedPreferences.getString("PackageName", "exception:error?package_name");

    }

    /*---------------- Set Project Name ---------------*/
    public void setProjectName(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("ProjectName", state);

        editor.apply();

    }


    /*---------------- Load Project Name ---------------*/
    public String loadProjectName() {

        return sharedPreferences.getString("ProjectName", "exception:error?project_name");

    }

    /*---------------- Set Username ---------------*/
    public void setUsername(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Username", state);

        editor.apply();

    }


    /*---------------- Load Username ---------------*/
    public String loadUsername() {

        return sharedPreferences.getString("Username", "exception:error?username");

    }

    /*---------------- Set Email ---------------*/
    public void setEmail(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Email", state);

        editor.apply();

    }


    /*---------------- Load Email ---------------*/
    public String loadEmail() {

        return sharedPreferences.getString("Email", "exception:error?email");

    }

    /*---------------- Set Dark Mode ---------------*/
    public void setDarkMode(Integer state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("DarkMode", state);

        editor.apply();

    }


    /*---------------- Load Dark Mode ---------------*/
    public Integer loadDarkMode() {

        return sharedPreferences.getInt("DarkMode", 0);

    }

    /*---------------- Set Guide ---------------*/
    public void setGuide(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("Guide", state);

        editor.apply();

    }


    /*---------------- Load Guide ---------------*/
    public Boolean loadGuide() {

        return sharedPreferences.getBoolean("Guide", false);

    }

    /*---------------- Set Guide ---------------*/
    public void setStoreGuide(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("StoreGuide", state);

        editor.apply();

    }

    /*---------------- Load StoreGuide ---------------*/
    public Boolean loadStoreGuide() {

        return sharedPreferences.getBoolean("StoreGuide", false);

    }

    /*---------------- Set Promotion ID ---------------*/
    public void setPromotionID(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("PromotionID", state);

        editor.apply();

    }

    /*---------------- Load Promotion ID ---------------*/
    public String loadPromotionID() {

        return sharedPreferences.getString("PromotionID", "");

    }

}
