package com.r3pi.task.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.r3pi.task.R;
import com.r3pi.task.api.BookData;
import com.r3pi.task.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by margarita on 11/24/18.
 */

public class BookInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //VIEWS
    private TextView mSubtitle;
    private TextView mBookTitle;
    private TextView mPublishedDate;
    private TextView mPublisher;
    private TextView mAuthor;
    private TextView mDescription;
    private ImageView mCover;
    ///Data
    private List<String> mPermDeniedList = new ArrayList<>();
    private BookData mBookData = new BookData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        mBookData = (BookData) getIntent().getSerializableExtra(Constants.METADATA);
        findViewsByIds();
        updateViews();
    }

    /////Initializing views with  book information
    private void updateViews() {

        if (mBookData.getCover() != null) {
            Picasso.with(this).load(Uri.parse(mBookData.getCover())).error(R.drawable.ic_no_cover).into(mCover);
        }

        String unknown = getString(R.string.unknown);

        boolean validTitle = mBookData != null &&
                mBookData.getTitle() != null &&
                !mBookData.getTitle().isEmpty();
        mBookTitle.setText(validTitle ? mBookData.getTitle() : unknown);

        boolean validPublishDate = mBookData != null &&
                mBookData.getPublishedDate() != null &&
                !mBookData.getPublishedDate().isEmpty();
        mPublishedDate.append(
                validPublishDate ? " " + mBookData.getPublishedDate() : " " + unknown);

        boolean validPublisher = mBookData != null &&
                mBookData.getPublisher() != null &&
                !mBookData.getPublisher().isEmpty();
        mPublisher.append(validPublisher ? " " + mBookData.getPublisher() : " " + unknown);

        boolean validAuthor = mBookData != null &&
                mBookData.getAuthors() != null &&
                mBookData.getAuthors().length > 0;
        if (validAuthor) {
            String author = "\n";
            String[] authors = mBookData.getAuthors();
            for (int i = 0; i < authors.length; i++) {
                String singleAuthor = authors[i];
                if (TextUtils.isEmpty(singleAuthor)) {
                    continue;
                }
                author += singleAuthor.concat(i == authors.length - 1 ? "" : "\n");
                mAuthor.append(author);
            }
        }
        boolean validSubtitle = mBookData != null &&
                mBookData.getSubtitle() != null &&
                !mBookData.getSubtitle().isEmpty();
        if (validSubtitle) {
            mSubtitle.setText(mBookData.getSubtitle());
        } else {
            mSubtitle.setVisibility(View.GONE);
        }
        boolean validDescription = mBookData != null &&
                mBookData.getDescription() != null &&
                !mBookData.getDescription().isEmpty();
        if (validDescription) {
            mDescription.setText(mBookData.getDescription());
        }
    }

    void findViewsByIds() {
        mAuthor = findViewById(R.id.author);
        mPublishedDate = findViewById(R.id.publishedDate);
        mBookTitle = findViewById(R.id.txt_book_title);
        mDescription = findViewById(R.id.description);
        mPublisher = findViewById(R.id.publisher);
        mSubtitle = findViewById(R.id.subtitle);
        mCover = findViewById(R.id.cover);
        TextView mButtonWeb = findViewById(R.id.txt_view_web);
        TextView mBackButton = findViewById(R.id.txt_page_name);
        mButtonWeb.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        ImageView mShareBook = findViewById(R.id.img_share);
        mShareBook.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERM_READ_URI) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareBookIntent();
            }
        }
    }

    ///Permissions block
    private void checkPermission(List<String> access, int requestCode) {
        String[] stringArray = access.toArray(new String[access.size()]);
        ActivityCompat.requestPermissions(this, stringArray, requestCode);
    }

    private boolean isPermGranted(String perm) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(BookInfoActivity.this, perm) == PackageManager.PERMISSION_GRANTED;
    }
    private void shareBookIntent() {

        ImageView ivImage = findViewById(R.id.cover);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBookData.getTitle());
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image)));

    }
    // Returns the URI path to the Bitmap displayed in cover imageview
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), getString(R.string.book_cover) + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(getApplication().getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_view_web:
                String infoUrl = mBookData.getInfoUrl();
                Intent intent = new Intent(this, BookGoogleInfoWebViewActivity.class);
                intent.putExtra(Constants.BOOK_URL, infoUrl);
                startActivity(intent);
                break;
            case R.id.img_share:
                if(isPermGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    shareBookIntent();
                }
                else{
                    mPermDeniedList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    checkPermission(mPermDeniedList, Constants.PERM_READ_URI);
                }
                break;
            case R.id.txt_page_name:
                onBackPressed();
                break;
        }
    }
}