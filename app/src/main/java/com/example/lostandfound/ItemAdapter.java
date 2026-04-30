package com.example.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText(item.getCategory());
        holder.tvLocation.setText("📍 " + item.getLocation());
        holder.tvDateTime.setText("🕐 " + item.getDateTime());


        holder.tvType.setText(item.getType());
        if ("Lost".equals(item.getType())) {
            holder.tvType.setBackgroundResource(R.drawable.badge_lost);
        } else {
            holder.tvType.setBackgroundResource(R.drawable.badge_found);
        }


        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            try {
                holder.ivItemImage.setImageURI(Uri.parse(item.getImageUri()));
            } catch (Exception e) {
                holder.ivItemImage.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            holder.ivItemImage.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Click → detail
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivItemImage;
        TextView tvTitle, tvCategory, tvType, tvLocation, tvDateTime;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView    = itemView.findViewById(R.id.cardView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvTitle     = itemView.findViewById(R.id.tvTitle);
            tvCategory  = itemView.findViewById(R.id.tvCategory);
            tvType      = itemView.findViewById(R.id.tvType);
            tvLocation  = itemView.findViewById(R.id.tvLocation);
            tvDateTime  = itemView.findViewById(R.id.tvDateTime);
        }
    }
}
