package com.r3pi.task.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.books.model.Volume;
import com.r3pi.task.R;
import com.r3pi.task.activities.BookInfoActivity;
import com.r3pi.task.api.BookData;
import com.r3pi.task.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by margarita on 11/24/18.
 */
public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Volume mVolume;

    BookViewHolder(ViewGroup viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_rec_book, viewGroup, false));
        itemView.setOnClickListener(this);
    }

    void setVolume(Volume volume) {
        this.mVolume = volume;
        System.out.println(volume.getVolumeInfo().getInfoLink());
        Volume.VolumeInfo.ImageLinks imageLinks = volume.getVolumeInfo().getImageLinks();

        if (imageLinks != null) {
            String medium = imageLinks.getMedium();
            String large = imageLinks.getLarge();
            String small = imageLinks.getSmall();
            String thumbnail = imageLinks.getThumbnail();
            String smallThumbnail = imageLinks.getSmallThumbnail();
            String imageLink = "";

            if (large != null) {
                imageLink = large;
            } else if (medium != null) {
                imageLink = medium;
            } else if (small != null) {
                imageLink = small;
            } else if (thumbnail != null) {
                imageLink = thumbnail;
            } else if (smallThumbnail != null) {
                imageLink = smallThumbnail;
            }

            imageLink = imageLink.replace("edge=curl", "");
            System.out.println(imageLink);
            Picasso.with(itemView.getContext()).load(imageLink).error(R.drawable.ic_no_cover).into((ImageView) itemView.findViewById(R.id.cover));
            String unknown = itemView.getContext().getString(R.string.unknown);

            ((TextView)itemView.findViewById(R.id.txt_book_title)).setText(
                    (volume.getVolumeInfo() != null && volume.getVolumeInfo().getTitle() != null &&
                            volume.getVolumeInfo().getTitle().isEmpty()) ?
                            volume.getVolumeInfo().getTitle() : unknown);
            ((TextView)itemView.findViewById(R.id.txt_book_title)).setText(
                    volume.getVolumeInfo().getTitle());
        } else {
            System.err.println("No images ??");
        }
    }

    @Override
    public void onClick(View v) {
        Volume.VolumeInfo volumeInfo = mVolume.getVolumeInfo();
        BookData bookData = new BookData();
        Context context = itemView.getContext();

        if (volumeInfo != null) {
            Volume.VolumeInfo.ImageLinks imageLinks = volumeInfo.getImageLinks();
            if (volumeInfo.getTitle() != null) {
                bookData.setTitle( volumeInfo.getTitle());
            }
            if (volumeInfo.getSubtitle() != null) {
                bookData.setSubtitle(volumeInfo.getSubtitle());
            }
            if (volumeInfo.getInfoLink() != null) {
                bookData.setInfoUrl(volumeInfo.getInfoLink());
            }
            if (volumeInfo.getDescription() != null) {
                bookData.setDescription( volumeInfo.getDescription());
            }
            if (volumeInfo.getPublisher() != null) {
                bookData.setPublisher(volumeInfo.getPublisher());
            }
            if (volumeInfo.getAuthors() != null) {
                bookData.setAuthors(volumeInfo.getAuthors().toArray(
                        new String[volumeInfo.getAuthors().size()]));
            }
            if (volumeInfo.getPublishedDate() != null) {
                bookData.setPublishedDate( volumeInfo.getPublishedDate());
            }
            if (imageLinks != null) {
                String image = null;
                if (imageLinks.getExtraLarge() != null) {
                    image = imageLinks.getExtraLarge();
                } else if (imageLinks.getLarge() != null) {
                    image = imageLinks.getLarge();
                } else if (imageLinks.getMedium() != null) {
                    image = imageLinks.getMedium();
                } else if (imageLinks.getSmall() != null) {
                    image = imageLinks.getSmall();
                } else if (imageLinks.getThumbnail() != null) {
                    image = imageLinks.getThumbnail();
                } else if (imageLinks.getSmallThumbnail() != null) {
                    image = imageLinks.getSmallThumbnail();
                }
                if (image != null) {
                    bookData.setCover(image);
                }
            }
        }
        context.startActivity(new Intent(context,
                              BookInfoActivity.class).putExtra(Constants.METADATA, bookData));
    }
}
