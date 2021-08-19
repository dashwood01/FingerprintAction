package com.app.dashwood.fingerprintsensor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.dataset.InformationChildItem;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.log.T;

import java.util.ArrayList;

class AdapterChildItem extends RecyclerView.Adapter<AdapterChildItem.ViewHolder> {

    private ArrayList<InformationChildItem> actionses = new ArrayList<>();
    private Context context;
    private String[] actions = {""};
    private T toastManager;
    private int[] icon;
    AdapterChildItem(Context context){
        this.context = context;
        toastManager = new T(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_rec_child_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final InformationChildItem informationActions = actionses.get(holder.getAdapterPosition());
        holder.txtNameChildItem.setText(informationActions.getName());
        holder.imgChildItem.setImageDrawable(informationActions.getIcon());
        switch (informationActions.getPrent()){
            case 0:
                actions = context.getResources().getStringArray(R.array.music_value_action);
                icon = new int[]{R.drawable.ic_play, R.drawable.ic_stop, R.drawable.ic_next, R.drawable.ic_previous};
                break;
            case 1:
                actions = context.getResources().getStringArray(R.array.volume_work_value);
                icon = new int[]{R.drawable.ic_volumedown, R.drawable.ic_volumeup};
                break;
        }

        holder.layoutRootChildItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                A.setActionvalue(actions[holder.getAdapterPosition()]);
                toastManager.sT(informationActions.getName(),context.getDrawable(icon[holder.getAdapterPosition()]));
            }
        });

    }
    @Override
    public int getItemCount() {
        return actionses.size();
    }
    void sendChild(ArrayList<InformationChildItem> list){
        actionses = list;
        notifyItemRangeChanged(0,actionses.size());
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private ViewGroup layoutRootChildItem;
        private TextView txtNameChildItem;
        private ImageView imgChildItem;
        ViewHolder(View itemView) {
            super(itemView);
            layoutRootChildItem = (ViewGroup)itemView.findViewById(R.id.layoutRootChildItem);
            txtNameChildItem = (TextView)itemView.findViewById(R.id.txtNameChildItem);
            imgChildItem = (ImageView)itemView.findViewById(R.id.imgChildItem);
        }
    }
}
