package com.r3pi.task.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.api.services.books.model.Volume;

import java.util.List;

/**
 * Created by margarita on 11/24/18.
 */
public class BookListAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<Volume> mVolumes;

    public BookListAdapter(List<Volume> volumes) {
        this.mVolumes = volumes;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.setVolume(mVolumes.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mVolumes.get(position).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        return mVolumes.size();
    }
}
