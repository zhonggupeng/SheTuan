package com.example.asus.shetuan.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ShetuanInformation;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.databinding.ActivityShetuanInformationBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ShetuanInformationActivity extends AppCompatActivity {
    private String datajsonstring;
    private ActivityShetuanInformationBinding binding=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shetuan_information);
        Intent intent = getIntent();
        datajsonstring = intent.getStringExtra("datajson3");
        JSONObject jsonObject = null;
        ShetuanMsg shetuanMsg = null;
        try {
            jsonObject = new JSONObject(datajsonstring);
            shetuanMsg = new ShetuanMsg(jsonObject.getString("cmName"),jsonObject.getString("cmDetail"),jsonObject.getString("cmBackground"),jsonObject.getString("cmLogo"));
            shetuanMsg.setShetuanid(jsonObject.getInt("cmid"));
            shetuanMsg.setShetuantype(jsonObject.getInt("cmType"));
            shetuanMsg.setShetuanattr(jsonObject.getInt("cmAttr"));
            shetuanMsg.setShetuanrecruit(jsonObject.getInt("cmRecruit"));
            shetuanMsg.setShetuanheat(jsonObject.getInt("cmHeat"));
            shetuanMsg.setShtuanannouncement(jsonObject.getString("cmAnnouncement"));
            shetuanMsg.setShetuanschool(jsonObject.getString("cmSchool"));
            System.out.println(jsonObject.getLong("cmBoss"));
            shetuanMsg.setShetuanboss(jsonObject.getLong("cmBoss"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.setShetuanInformationData(shetuanMsg);
        binding.setShetuanInformation(new ShetuanInformation(this));
        binding.shetuanInformationShetuanheat.setText("有"+shetuanMsg.getShetuanheat()+"个小伙伴哦");
        if (shetuanMsg.getShetuantype()==0){
            binding.shetuanInformationShetuantype.setText("兴趣");
        }
        else if (shetuanMsg.getShetuantype()==1){
            binding.shetuanInformationShetuantype.setText("学术");
        }
        else if (shetuanMsg.getShetuantype()==2){
            binding.shetuanInformationShetuantype.setText("运动");
        }
        binding.shetuanInformationShetuanboss.setText(String.valueOf(shetuanMsg.getShetuanboss()));
        binding.shetuanInformationBackground.setImageURI(shetuanMsg.getBackgroundimage());
    }
}
