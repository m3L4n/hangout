package com.example.ft_hangout;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomSmsAdapter extends RecyclerView.Adapter {

    private Context context;
    Activity activity;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE =2;
    private ArrayList msg_id, msg_content , msg_isSender, msg_time;
    public CustomSmsAdapter(Activity activity, Context context, ArrayList msg_id, ArrayList msg_content, ArrayList msg_isSender, ArrayList msg_time){
        this.activity = activity;
        this.context = context;
        this.msg_id = msg_id;
        this.msg_content = msg_content;
        this.msg_isSender = msg_isSender;
        this.msg_time = msg_time;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_RECEIVE)
        {
        View view = LayoutInflater.from(context).inflate(R.layout.layoutsenderchat, parent, false);
            return  new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.layoutreceiverchat, parent, false);
            return  new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.textViewmsg.setText(String.valueOf(msg_content.get(position)));
            viewHolder.timestampmsg.setText(String.valueOf(msg_time.get(position)));
        }
        else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.textViewmsg.setText(String.valueOf(msg_content.get(position)));
            viewHolder.timestampmsg.setText(String.valueOf(msg_time.get(position)));
        }
    }
    @Override
    public int getItemViewType(int position) {
        String msg =String.valueOf(msg_isSender.get(position));
        if (msg.equals("false")){
            return  ITEM_RECEIVE;
        }
        else {
            return  ITEM_SEND;
        }
    }

    @Override
    public int getItemCount() {
        return msg_id.size();
    }
    class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView textViewmsg;
        TextView timestampmsg;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmsg = (TextView) itemView.findViewById(R.id.textMsg);
            timestampmsg = (TextView) itemView.findViewById(R.id.timemsg);
        }
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView textViewmsg;
        TextView timestampmsg;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmsg = (TextView) itemView.findViewById(R.id.textMsg);
            timestampmsg = (TextView) itemView.findViewById(R.id.timemsg);
        }
    }
}
