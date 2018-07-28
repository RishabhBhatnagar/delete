package org.sfitengg.library.mylibapp.nav_drawer_fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sfitengg.library.mylibapp.R;

import java.io.IOException;
import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create only a single webview object
        // We will set this to content in setContentToWebViewAndLoadData
        webView = new WebView(this);

        // Originally display a loading screen,
        // This will be changed to webView when data is received
        setContentView(R.layout.loading_screen);


        String url = getIntent().getExtras().getString("url");
        ArrayList<String> urlStrings = getIntent().getExtras().getStringArrayList("urlList");

        if(url.equals("") || url == null || urlStrings == null){
            Toast.makeText(this, "Url could not be loaded.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(!isConnectedToInternet()){
                // If not connected to internet

                //region Create and show no internet alert dialog
                AlertDialog noInternetDialog = new AlertDialog.Builder(this).create();
                noInternetDialog.setTitle("You are not connected to the internet!");
                noInternetDialog.setMessage("Please try again when you have internet connectivity.");
                noInternetDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Dimiss the dialog to prevent window leak
                                dialogInterface.dismiss();

                                // Go back to MainActiviyt
                                finish();
                            }
                        });
                noInternetDialog.show();
                //endregion
            }
        else {

            loadUrl(url);
        }
    }

    private void loadUrl(final String url) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder builder = new StringBuilder();

                    try {
                        Document doc = Jsoup.connect(url).get();
                        Elements links = doc.select("div.inner_the");

                        for (Element link : links) {
                            builder.append(link.outerHtml());
                        }
                    } catch (IOException e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }

                    String s = new String(builder);
                    s = s.replaceAll("src=\"", "src=\"http://www.sfitengg.org/");
                    final String finalS = s;

                    // Finally send the data back to UI thread to draw webview
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setContentToWebViewAndLoadData(finalS);
                        }
                    });


                }// run end
            }//new runnable end
            ).start();//new thread end

    }

    private void setContentToWebViewAndLoadData(String data){
        // Now that data is arrived, setContentView to webview
        setContentView(webView);


        WebSettings webSettings = webView.getSettings();

        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);


        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
