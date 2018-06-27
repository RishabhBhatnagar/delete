package com.example.vinay.mylibapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import static com.example.vinay.mylibapp.GoGoGadget.ERROR_INCORRECT_PID_OR_PASSWORD;

public class LoginActivity extends AppCompatActivity implements MyCallback{

    EditText et_pid;
    EditText et_pwd;
    Button btn_login_submit;
    TextView tv_result_login;
    String pid;
    String pwd;

    // Main Page where login Form is present
    static String urlMainPage = "http://115.248.171.105:82/webopac/";

    // Complete url to the form action attribute
    // where we send a POST
    static String urlLoginFormAction = urlMainPage + "opac.asp?m_firsttime=Y&m_memchk_flg=T";

    // Url of docs page
    static String urlOutDocsPage = "http://115.248.171.105:82/webopac/l_renew.asp";

    // Url where reissue form is sent
    // static String urlOutDocsFormAction = l_renew1.asp;

    Bundle bundleURLs;

    Handler handler = new Handler();


    AlertDialog.Builder alertDialogBuilder;
    Dialog loadingDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    final String titleSharedPrefs = "my_prefs";
    final String KEY_PID = "pid";
    final String KEY_PWD = "pwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create SharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences(titleSharedPrefs, MODE_PRIVATE);
        pid = sharedPreferences.getString(KEY_PID, null);
        pwd = sharedPreferences.getString(KEY_PWD, null);

        if(pid != null && pwd != null){
            // There exists previous user

            GoGoGadget goGoGadget = new GoGoGadget((MyCallback) LoginActivity.this,
                    bundleURLs,
                    GoGoGadget.LOGIN_AND_GET_COOKIES,
                    handler);
            new Thread(goGoGadget).start();
        }

        // Create a loadingDialog instance for the activity to show during network operations
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(R.layout.progress);
        loadingDialog = alertDialogBuilder.create();
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);


        et_pid = findViewById(R.id.et_pid);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login_submit = findViewById(R.id.btn_login_submit);
        tv_result_login = findViewById(R.id.tv_login_result);

        // Create a bundle to pass in the URLs to the GoGoGadget object
        bundleURLs = new Bundle();
        bundleURLs.putString(GoGoGadget.keyMainPage, urlMainPage);
        bundleURLs.putString(GoGoGadget.keyLoginForm, urlLoginFormAction);
        bundleURLs.putString(GoGoGadget.keyOutDocs, urlOutDocsPage);


        //region btn_login_submit OnClickListener
        btn_login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setLoadingDialog(true);

                pid = et_pid.getText().toString();
                pwd = et_pwd.getText().toString();


                final GoGoGadget goGoGadget = new GoGoGadget((MyCallback) LoginActivity.this,
                        bundleURLs,
                        GoGoGadget.LOGIN_AND_GET_COOKIES,
                        handler);
                new Thread(goGoGadget).start();
            }
        });
        //endregion
    }

    @Override
    public void sendBooksToCaller(List<Book> books) {

    }

    @Override
    public void sendStudentNameToCaller(String name) {
        // This method will be called when login is correct


        setLoadingDialog(false);
        tv_result_login.setText("SUCCESS LOGIN");

        // Go to MainActivity
        final AlertDialog loginSuccessDialog = new AlertDialog.Builder(
                this).create();
        loginSuccessDialog.setTitle("Login Successful!");
        loginSuccessDialog.setMessage("Welcome " + name);
        loginSuccessDialog.setButton(Dialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        loginSuccessDialog.show();
    }

    @Override
    public void passErrorsToCaller(int errorCode) {
        setLoadingDialog(false);
        tv_result_login.setText("Some error happened"+ errorCode);

        switch (errorCode){
            case ERROR_INCORRECT_PID_OR_PASSWORD:

        }
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public String getPwd() {
        return pwd;
    }

    @Override
    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void userHasBorrowedNoBooks() {

    }

    private void setLoadingDialog(boolean show){
        // Sauce for dialog creation, and this setter method:
        // https://stackoverflow.com/a/14853439/9485900

        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }
}
