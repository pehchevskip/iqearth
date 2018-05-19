package com.pehchevskip.iqearth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterNickname extends AppCompatActivity {

    //Views
    EditText mEditTextnickname;
    Button mButtonSumbit;

    //Nickame of player
    String nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_nickname);
        mEditTextnickname=(EditText) findViewById(R.id.nickname);
        mButtonSumbit=(Button)findViewById(R.id.submit);
        mButtonSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("" != mEditTextnickname.getText().toString()){
                    nickname=mEditTextnickname.getText().toString();
                    Intent start_activity;
                    start_activity = new Intent(EnterNickname.this,StartActivity.class);
                    start_activity.putExtra("nickname",nickname);
                    startActivityForResult(start_activity,1);


                }
            }
        });
    }
}
