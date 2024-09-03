package com.example.androidmobilestock.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.Interface.OnQuantityChangeListener;
import com.example.androidmobilestock.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SettingHistoryItemAdapter extends RecyclerView.Adapter<SettingHistoryItemAdapter.MyViewHolder>{

    Context context;
    List<AC_Class.Item> items;
    AC_Class.InvoiceDetails invoiceDetails;
    ACDatabase db;
    String qty;
    ArrayList<AC_Class.InvoiceDetails> purchasedItems;
    OnQuantityChangeListener listener;


    public interface OnQuantityChangeListener {
        void onQuantityChanged(AC_Class.Item item, int quantity);
    }



    public SettingHistoryItemAdapter(Context context, List<AC_Class.Item> items, OnQuantityChangeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_settings_itemproduct_list,viewGroup, false);
        return new MyViewHolder(itemView, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        db = new ACDatabase(context);
        invoiceDetails = new AC_Class.InvoiceDetails();

        AC_Class.Item item = items.get(i);

        //set item name
        myViewHolder.itemName_text.setText(item.ItemCode);

        //set item description
        myViewHolder.description_textView.setText(item.Description);

        //set image
        Bitmap itemBitmap = db.getItemImage(item.ItemCode);
        if (itemBitmap != null) {
            // If Bitmap is not null, set it directly to the ImageView
            myViewHolder.item_image.setImageBitmap(itemBitmap);
        } else {
            // If Bitmap is null, set a default image
            myViewHolder.item_image.setImageResource(R.drawable.photo_empty);
        }

        //set item UOM
        myViewHolder.uom_textView.setText(item.UOM);

        // Assuming item.Price is a float
        double price = item.Price;
        String formattedPrice = String.format(Locale.US, "%.2f", price);

        //set item price
        myViewHolder.price_textView.setText(formattedPrice);

        //set quantity to zero
        myViewHolder.qty_text .setText("0");
        myViewHolder.qty_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //set the changed quantity
                qty = myViewHolder.qty_text.getText().toString();

                if (qty.length() > 0 && !qty.equals("-")) {
                    invoiceDetails.setQuantity(Double.valueOf(qty));
                }

                //get the change qty
                String qtyStr = s.toString();
                int qty = qtyStr.isEmpty() ? 0 : Integer.parseInt(qtyStr);

                // Notify listener
                listener.onQuantityChanged(item, qty);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle decrease button
        myViewHolder.decrease_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(myViewHolder.qty_text.getText().toString());

                if (qty > 0){
                    qty--;
                    myViewHolder.qty_text.setText(String.valueOf(qty));
                }
            }
        });

        //handle increase button
        myViewHolder.increase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(myViewHolder.qty_text.getText().toString());
                qty++;
                myViewHolder.qty_text.setText(String.valueOf(qty));
            }
        });

    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView itemName_text, description_textView, uom_textView, price_textView ;
        EditText qty_text;
        ImageButton decrease_btn, increase_btn;

        Context viewHolderContext;

        public MyViewHolder(View itemView, Context context) {
            super(itemView);
            viewHolderContext = context;

            item_image = itemView.findViewById(R.id.item_image);
            itemName_text = itemView.findViewById(R.id.itemName_text);
            description_textView = itemView.findViewById(R.id.description_textView);
            uom_textView = itemView.findViewById(R.id.uom_textView);
            price_textView = itemView.findViewById(R.id.price_textView);
            qty_text = itemView.findViewById(R.id.qty_text);
            decrease_btn = itemView.findViewById(R.id.decrease_btn);
            increase_btn = itemView.findViewById(R.id.increase_btn);

        }
    }
}
