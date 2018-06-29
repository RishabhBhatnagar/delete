package com.example.vinay.mylibapp.data;

import android.os.Bundle;

import com.example.vinay.mylibapp.GoGoGadget;

/**
 * Created by vinay on 27-06-2018.
 */

public class DataHolder {
    // Using a singleton design pattern, since we only need one instance for this class

    // To set data for flask server, set this boolean to true
    private boolean testing = false;

    // Single instance for DataHolder that will be shared throughout the application
    private static DataHolder dataHolder;

    // Main Page where login Form is present
    private static String urlMainPage = "http://115.248.171.105:82/webopac/";

    // Complete url to the form action attribute
    // where we send a POST
    private static String urlLoginFormAction = urlMainPage + "opac.asp?m_firsttime=Y&m_memchk_flg=T";

    // Url of docs page
    private static String urlOutDocsPage = "http://115.248.171.105:82/webopac/l_renew.asp";

    // Url where reissue form is sent
    // static String urlOutDocsFormAction = l_renew1.asp;

    private Bundle bundleURLs;

    public Bundle getBundleURLs() {
        return bundleURLs;
    }

    private DataHolder() {

        if (testing) {
            urlMainPage = "http://192.168.1.66:5000/";
            urlLoginFormAction = urlMainPage + "userpage";
            urlOutDocsPage = urlMainPage + "out_docs";
        }

        // Create a bundle to pass in the URLs to the GoGoGadget object
        bundleURLs = new Bundle();
        bundleURLs.putString(GoGoGadget.keyMainPage, urlMainPage);
        bundleURLs.putString(GoGoGadget.keyLoginForm, urlLoginFormAction);
        bundleURLs.putString(GoGoGadget.keyOutDocs, urlOutDocsPage);
    }

    public static DataHolder getDataHolder(){
        if(dataHolder == null){
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }
}
