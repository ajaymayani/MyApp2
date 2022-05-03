package com.vidshort.lyrical.video.status.myapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vidshort.lyrical.video.status.myapp.MainActivity;
import com.vidshort.lyrical.video.status.myapp.R;
import com.vidshort.lyrical.video.status.myapp.model.Apps;

import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    Context context;
    ArrayList<Apps> appsArrayList;

    public AppsAdapter(Context context, ArrayList<Apps> appsArrayList) {
        this.context = context;
        this.appsArrayList = appsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(appsArrayList.get(position).getAppImage()).into(holder.imageView);
        holder.textView.setText(appsArrayList.get(position).getAppName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(appsArrayList.get(holder.getAdapterPosition()).getAppLink()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
