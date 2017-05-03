package com.euswag.eu.weight.sexselector;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.euswag.eu.R;
import com.euswag.eu.activity.login.FillInformationActivity;

import java.util.Arrays;

/**
 * Created by ASUS on 2017/4/11.
 */

public class SexSelect {
    private String Sex="男";
    private static final String[] PLANETS = new String[]{"男", "女", "保密"};
    private AlertDialog dialog;
    private Context context;

    public SexSelect(Context context){
        this.context = context;
    }

    public void showSex(){
        View outerView = LayoutInflater.from(context).inflate(R.layout.sex_wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(1);
        wv.setItems(Arrays.asList(PLANETS));
        wv.setSeletion(1);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Sex=item;
            }
        });
        dialog= new AlertDialog.Builder(context)
                .setView(outerView)
                .show();
        TextView txtSure= (TextView) outerView.findViewById(R.id.txt_sure);
        TextView txtCancle= (TextView) outerView.findViewById(R.id.txt_cancel);
        txtSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,Sex, Toast.LENGTH_LONG).show();
                ((FillInformationActivity)context).getBinding().fillInformSexselect.setText(Sex);
                dialog.cancel();
            }
        });
        txtCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
}
