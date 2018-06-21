package com.pehchevskip.iqearth.Adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pehchevskip.iqearth.ItemHolders.DeviceListHolder;
import com.pehchevskip.iqearth.R;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListHolder> {
    private ArrayList<BluetoothDevice> mDevices;
    private BluetoothAdapter adapter;

    public DeviceListAdapter(ArrayList<BluetoothDevice> number){
        mDevices=number;
        adapter=BluetoothAdapter.getDefaultAdapter();
    }
    @Override
    public DeviceListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        int layoutidFordevice= R.layout.device_list_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(layoutidFordevice,parent,false);
        DeviceListHolder holder=new DeviceListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeviceListHolder holder, final int position) {
        holder.bind(mDevices.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.cancelDiscovery();
                Log.d("ONCLICK",mDevices.get(position).getName());
                mDevices.get(position).createBond();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }
}
