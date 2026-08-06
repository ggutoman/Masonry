package com.gag.masonry.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.masonry.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.Entities.EUserObjects;
import java.util.List;

public class Adapter_Drawer extends RecyclerView.Adapter<Adapter_Drawer.VH_Drawer>{

    private final List<EUserObjects> laMenus;
    private final SelectMenu loCallback;

    public interface SelectMenu{
        void OnSelectItem(EUserObjects loObject);
    }

    public Adapter_Drawer(List<EUserObjects> faMenus, SelectMenu foCallback){
        this.laMenus = faMenus;
        this.loCallback = foCallback;
    }

    @NonNull
    @Override
    public VH_Drawer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Drawer(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_menu_parent, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_Drawer holder, int position) {

        holder.img_icon.setVisibility(View.GONE);
        holder.mtv_title.setText(laMenus.get(position).getSDescriptxx());
        holder.mtv_title.setTextSize(12);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loCallback.OnSelectItem(laMenus.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return laMenus.size();
    }

    public static class VH_Drawer extends RecyclerView.ViewHolder {

        private final View view;
        private final ShapeableImageView img_icon;
        private final MaterialTextView mtv_title;

        public VH_Drawer(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            img_icon = itemView.findViewById(R.id.img_icon);
            mtv_title = itemView.findViewById(R.id.mtv_title);
        }
    }

}
