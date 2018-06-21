package com.pehchevskip.iqearth.ItemHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pehchevskip.iqearth.R;

public class DeviceListHolder extends RecyclerView.ViewHolder {
    TextView deviceName;
    public DeviceListHolder(View itemView) {
        super(itemView);
        deviceName=(TextView)itemView.findViewById(R.id.dv_name);
    }
    public void bind(String name){
        deviceName.setText(name);
    }


}
