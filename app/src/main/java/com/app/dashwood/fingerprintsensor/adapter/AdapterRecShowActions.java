package com.app.dashwood.fingerprintsensor.adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.dashwood.fingerprintsensor.R;
import com.app.dashwood.fingerprintsensor.dataset.InformationActions;
import com.app.dashwood.fingerprintsensor.dataset.InformationChildItem;
import com.app.dashwood.fingerprintsensor.extra.A;
import com.app.dashwood.fingerprintsensor.log.T;
import com.app.dashwood.fingerprintsensor.preferences.PreferenceSettings;

import java.util.ArrayList;
import java.util.concurrent.RunnableFuture;

public class AdapterRecShowActions extends RecyclerView.Adapter<AdapterRecShowActions.ViewHolder> {

    private ArrayList<InformationActions> actions = new ArrayList<>();
    private Context context;
    private boolean mChildMusicVisibilty = true;
    private boolean mChildVulomeVisibilty = true;
    private AdapterChildItem adapterChildItem;
    private T toastManager;
    private int lastPosition = -1;

    public AdapterRecShowActions(Context context) {
        this.context = context;
        toastManager = new T(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_rec_show_action, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final InformationActions informationActions = actions.get(holder.getAdapterPosition());
        if (informationActions.getActionValueView() == 4) {
            holder.txtNameApp.setText(informationActions.getName() + " " + informationActions.getPhoneNumber());
        } else {
            holder.txtNameApp.setText(informationActions.getName());
        }
        holder.imgApp.setImageDrawable(informationActions.getIcon());
        holder.layoutRootApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (informationActions.getName().equals("میزان صدا")) {
                    if (!checkPermissionDistrub()) {
                        toastManager.sT("لطفا دسترسی را بدهید", context.getDrawable(R.drawable.ic_block));
                        return;
                    }
                }
                String[] actions = context.getResources().getStringArray(R.array.action_value_work);
                switch (informationActions.getActionValueView()) {
                    case 1:
                        adapterChildItem = new AdapterChildItem(context);
                        if (informationActions.getName().equals("قفل صفحه")) {
                            if (!A.getDevicePolicyManager().isAdminActive(A.getComponentName())) {
                                toastManager.lT(context.getString(R.string.error_toast_admin_not_active), context.getDrawable(R.drawable.ic_block));
                                context.startActivity(new Intent(context, PreferenceSettings.class));
                            }
                        }
                        if (!informationActions.getName().equals("میزان صدا")) {
                            A.setActionvalue(actions[holder.getAdapterPosition()]);
                        }
                        if (informationActions.getChild() == 5) {
                            if (mChildMusicVisibilty) {
                                holder.recChildItem.setVisibility(View.VISIBLE);
                                ArrayList<InformationChildItem> items = new ArrayList<>();
                                holder.recChildItem.setAdapter(adapterChildItem);
                                holder.recChildItem.setLayoutManager(new LinearLayoutManager(context));
                                String[] nameChild = context.getResources().getStringArray(R.array.music_work);
                                int[] imgChild = {R.drawable.ic_play, R.drawable.ic_stop, R.drawable.ic_next, R.drawable.ic_previous};
                                for (int i = 0; i < nameChild.length; i++) {
                                    InformationChildItem informationChildItem = new InformationChildItem();
                                    informationChildItem.setName(nameChild[i]);
                                    informationChildItem.setImg(context.getDrawable(imgChild[i]));
                                    informationChildItem.setPrent(0);
                                    items.add(informationChildItem);
                                }
                                adapterChildItem.sendChild(items);
                                mChildMusicVisibilty = false;
                            } else {
                                holder.recChildItem.setVisibility(View.GONE);
                                mChildMusicVisibilty = true;
                            }
                        }
                        if (informationActions.getChild() == 7) {
                            if (mChildVulomeVisibilty) {
                                holder.recChildItem.setVisibility(View.VISIBLE);
                                ArrayList<InformationChildItem> items = new ArrayList<>();
                                holder.recChildItem.setAdapter(adapterChildItem);
                                holder.recChildItem.setLayoutManager(new LinearLayoutManager(context));
                                String[] nameChild = context.getResources().getStringArray(R.array.volume_work);
                                int[] imgChild = {R.drawable.ic_volumedown, R.drawable.ic_volumeup};
                                for (int i = 0; i < nameChild.length; i++) {
                                    InformationChildItem informationChildItem = new InformationChildItem();
                                    informationChildItem.setName(nameChild[i]);
                                    informationChildItem.setImg(context.getDrawable(imgChild[i]));
                                    informationChildItem.setPrent(1);
                                    items.add(informationChildItem);
                                }
                                adapterChildItem.sendChild(items);
                                mChildVulomeVisibilty = false;
                            } else {
                                holder.recChildItem.setVisibility(View.GONE);
                                mChildVulomeVisibilty = true;
                            }

                        }
                        toastManager.sT(informationActions.getName(), informationActions.getIcon());
                        A.setDisplayName("");
                        break;
                    case 2:
                        A.setActionvalue(informationActions.getName());
                        A.setDisplayName("");
                        toastManager.sT(informationActions.getName(), informationActions.getIcon());
                        break;
                    case 3:
                    case 4:
                        A.setActionvalue(informationActions.getPhoneNumber());
                        A.setDisplayName(informationActions.getName());
                        toastManager.sT(informationActions.getName(), informationActions.getIcon());
                        break;
                }

            }
        });
        setAnimation(holder.layoutRootApp, position);
    }

    private boolean checkPermissionDistrub() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                return true;
            }
            return false;
        } else {
            return true;
        }


    }

    private void setAnimation(final View viewToAnimate, final int position) {
        if (position > lastPosition) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    animation.setDuration(1000);
                    viewToAnimate.startAnimation(animation);
                    lastPosition = position;
                }
            }).start();

        }
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void sendActions(ArrayList<InformationActions> list) {
        actions = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNameApp;
        private ImageView imgApp;
        private ViewGroup layoutRootApp;
        private RecyclerView recChildItem;

        ViewHolder(final View itemView) {
            super(itemView);
            txtNameApp = (TextView) itemView.findViewById(R.id.txtNameAction);
            imgApp = (ImageView) itemView.findViewById(R.id.imgAction);
            layoutRootApp = (ViewGroup) itemView.findViewById(R.id.layoutRootAction);
            recChildItem = (RecyclerView) itemView.findViewById(R.id.recChildItem);
            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {


                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    if (recChildItem.getVisibility() == View.VISIBLE) {
                        recChildItem.setVisibility(View.GONE);
                        mChildMusicVisibilty = true;
                        mChildVulomeVisibilty = true;
                    }


                }
            });
        }
    }

}
