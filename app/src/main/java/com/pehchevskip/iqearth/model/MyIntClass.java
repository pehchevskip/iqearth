package com.pehchevskip.iqearth.model;

import android.content.Context;
import android.widget.Toast;

public class MyIntClass {
    private int value;
    private int tries;
    private boolean isMsgShowed = false;
    private Context context;
    public MyIntClass(int value, Context context) {
        this.value = value;
        this.tries = 0;
        this.context = context;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public void increase(int i) {
        value += i;
    }
    public void increaseTries() {
        tries++;
        if(tries >= 2) showMsg();
    }
    private void showMsg() {
        if(!isMsgShowed && value < 2) {
            Toast.makeText(context, "Error with downloading the needed data, please restart the app!", Toast.LENGTH_LONG).show();
            isMsgShowed = true;
        }
    }
}
