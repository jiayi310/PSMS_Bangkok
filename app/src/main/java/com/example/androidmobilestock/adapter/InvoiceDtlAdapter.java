package com.example.androidmobilestock.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.androidmobilestock.ACDatabase;
import com.example.androidmobilestock.AC_Class;
import com.example.androidmobilestock.R;
import com.example.androidmobilestock.databinding.RowInvdtlListBinding;

import java.text.DecimalFormat;
import java.util.List;

public class InvoiceDtlAdapter extends RecyclerView.Adapter<InvoiceDtlAdapter.RecyclerViewHolder> {

    Context context;
    List<AC_Class.InvoiceDetails> invdtl_classlist;
    ACDatabase db;
    RowInvdtlListBinding binding;
    private InvoiceDtlAdapter adapter;
    private static RecyclerViewClickListener listener;
    android.widget.ImageView imageView;

    public InvoiceDtlAdapter(Context context, List<AC_Class.InvoiceDetails> invdtl_classlist) {
        this.context = context;
        this.invdtl_classlist = invdtl_classlist;
    }

    @NonNull
    @Override
    public InvoiceDtlAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_invdtl_list, parent,false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceDtlAdapter.RecyclerViewHolder holder, int position) {
        binding.setInvDetail(invdtl_classlist.get(position));
        binding.bill.setText(Integer.toString(position+1));

        db = new ACDatabase(context);



            if(db.getItemImage(invdtl_classlist.get(position).getItemCode())!=null) {
                Glide.with(context)
                        .load(db.getItemImage(invdtl_classlist.get(position).getItemCode()))
                        .centerInside()
                        .fitCenter()
                        //.apply(GlideRGB565DecodeUtil.getARGBRequestOption())
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into(binding.ImageItemCart);

            }else{
                imageView.setImageResource(R.drawable.photo_empty);
            }

        holder.setIsRecyclable(false);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.invdtl_classlist.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        RowInvdtlListBinding binding;
//        if (convertView == null)
//        {
//            convertView = LayoutInflater.from(context).inflate(R.layout.row_invdtl_list, null);
//            binding = DataBindingUtil.bind(convertView);
//            convertView.setTag(binding);
//            imageView = (android.widget.ImageView) convertView.findViewById(R.id.ImageItemCart);
//            //add
//            db= new ACDatabase(context);
//            if(db.getItemImage(invdtl_classlist.get(position).getItemCode())!=null) {
//                binding.ImageItemCart.setImageBitmap(db.getItemImage(invdtl_classlist.get(position).getItemCode()));
//            }else{
//                imageView.setImageResource(R.drawable.photo_empty);
//            }
//        }
//        else
//        {
//            binding = (RowInvdtlListBinding) convertView.getTag();
//        }
//
//
//        binding.setInvDetail(invdtl_classlist.get(position));
//        return binding.getRoot();
//    }

    @BindingAdapter({"amount"})
    public static void setCurrencyAndAmount(TextView textView, double amount) {
        textView.setText(new DecimalFormat("#.###").format(amount));
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(itemView,getAdapterPosition());
        }
    }

    public  interface RecyclerViewClickListener{
        void onClick(View v,int position);

    }




}
