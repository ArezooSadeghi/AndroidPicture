package com.example.androidpicture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpicture.databinding.PictureAdapterItemBinding;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {
    private Context mContext;
    private List<String> filePaths;

    public PictureAdapter(Context context, List<String> filePaths) {
        mContext = context;
        this.filePaths = filePaths;
    }

    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PictureHolder(DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.picture_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PictureHolder holder, int position) {
        String filePath = filePaths.get(position);
        holder.bindFilePath(filePath);
        holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) mContext;
                Intent intent = LargeImageViewActivity.newIntent(activity, filePath);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filePaths.size();
    }

    public class PictureHolder extends RecyclerView.ViewHolder {
        private PictureAdapterItemBinding mBinding;

        public PictureHolder(PictureAdapterItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindFilePath(String filePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            mBinding.picture.setImageBitmap(bitmap);
        }
    }
}
