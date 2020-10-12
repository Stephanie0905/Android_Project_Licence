package com.example.kelys.PopUp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.kelys.R;

public class LoadingBar {

    AlertDialog dialog;
    Activity activity;

    LoadingBar(Activity myActivity){
        activity = myActivity;
    }

    void startLoadingBar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();

    }

    void dismissdialog(){
        dialog.dismiss();
    }

}
