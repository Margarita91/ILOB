package com.r3pi.task.activities;

import android.Manifest;
import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.books.model.Volume;
import com.r3pi.task.R;
import com.r3pi.task.adapters.BookListAdapter;
import com.r3pi.task.api.GetContactsTask;
import com.r3pi.task.api.SearchTask;
import com.r3pi.task.utils.Constants;
import com.r3pi.task.utils.SearchFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SearchTask.SearchListener, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private String mQuery;
    private SearchView mSearchBooksView;
    private RecyclerView mRecyclerViewBooks;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mLoadingProgressBar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private Account mAuthorizedAccount;
    private int mPaging = 1;

    private List<Volume> mVolumeList = new ArrayList<>();
    private List<String> mPermDeniedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authorizeContactsAccess();
        findViewsByIds();
        initSearchFragment();
        //setUp RecyclerView
        setUpRecyclerView();
        //parallax effect
        initCollapsingToolbar();
        //parallax image
        try {
            Picasso.with(this).load(R.drawable.ic_title_cover).error(R.drawable.ic_no_cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
     setQueryListener();
    }

    private void initSearchFragment() {
        SearchFragment searchFragment =
                (SearchFragment) getSupportFragmentManager().findFragmentByTag(Constants.SEARCH_FRAGMENT_TAG);
        if (searchFragment != null) {
            mVolumeList = searchFragment.getVolumeList();
            mSearchBooksView.setQuery(searchFragment.getLatestQuery(), false);
        } else {
            mVolumeList = new ArrayList<>();
            searchFragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(searchFragment, Constants.SEARCH_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void setQueryListener() {
        mSearchBooksView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPaging = 1;
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        BookListAdapter adapter = new BookListAdapter(mVolumeList);
        mRecyclerViewBooks.setHasFixedSize(true);
        mRecyclerViewBooks.setLayoutManager(mLayoutManager);
        mRecyclerViewBooks.setAdapter(adapter);
        mRecyclerViewBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Check for scroll down.
                if (dy > 0) {
                    mVisibleItemCount = mLayoutManager.getChildCount();
                    mTotalItemCount = mLayoutManager.getItemCount();
                    mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
                        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(
                                Constants.SEARCH_FRAGMENT_TAG);
                        if (searchFragment != null) {
                            mPaging++;
                            searchFragment.searchBooks(mQuery, mPaging);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERM_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent scanIntent = new Intent(this, BookScannerActivity.class);
                startActivityForResult(scanIntent, Constants.RC_BARCODE_CAPTURE);
            }
        }
    }

    ///Permissions block
    private void checkPermission(List<String> access, int requestCode) {
        String[] stringArray = access.toArray(new String[access.size()]);
        ActivityCompat.requestPermissions(this, stringArray, requestCode);
    }

    private boolean isPermGranted(String perm) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(MainActivity.this, perm) == PackageManager.PERMISSION_GRANTED;
    }

    private void initCollapsingToolbar() {
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    void findViewsByIds() {
        mSearchBooksView =  findViewById(R.id.search_view);
        mRecyclerViewBooks =  findViewById(R.id.rcview_books_list);
        mLoadingProgressBar =  findViewById(R.id.loading_view);
        FloatingActionButton mScanBarCodeFloatButton = findViewById(R.id.fl_button_scan_barcode);
        mScanBarCodeFloatButton.setOnClickListener(this);
    }

    void searchBooks(String query) {
        SearchFragment searchFragment =
                (SearchFragment) getSupportFragmentManager().findFragmentByTag(
                        Constants.SEARCH_FRAGMENT_TAG);
        if (searchFragment != null) {
            mQuery = query;
            searchFragment.searchBooks(query, mPaging);
        }
    }

    @Override
    public void onSearching() {
        if (mPaging == 1) {
            mVolumeList.clear();
        }
        mRecyclerViewBooks.getAdapter().notifyDataSetChanged();
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResult(List<Volume> volumes) {
        mVolumeList.addAll(volumes);
        mRecyclerViewBooks.getAdapter().notifyDataSetChanged();
        mLoadingProgressBar.setVisibility(View.GONE);
    }


    private void authorizeContactsAccess() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().
                        requestScopes(
                                new Scope(Constants.GOOGLE_API_URL))
                        .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(
                        this /* AppCompatActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.RC_AUTHORIZE_CONTACTS);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connection failed: ", connectionResult.getErrorCode() + "");
        Toast.makeText(
                this,
                "Connection failed. " + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_AUTHORIZE_CONTACTS) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                if (googleSignInAccount != null) {
                    mAuthorizedAccount = googleSignInAccount.getAccount();
                    getContacts();
                }
            }
        } else if (requestCode == Constants.RC_REAUTHORIZE) {
            if (resultCode == RESULT_OK) {
                getContacts();
            }
        } else if (requestCode == Constants.RC_BARCODE_CAPTURE && resultCode == Constants.RC_BARCODE_CAPTURE) {
            mSearchBooksView.setQuery(data.getStringExtra(Constants.BAR_CODE),true);
        }
    }
    private void getContacts() {
        GetContactsTask task = new GetContactsTask(mAuthorizedAccount, this);
        task.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_button_scan_barcode:
                if (isPermGranted(Manifest.permission.READ_EXTERNAL_STORAGE) && isPermGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) && isPermGranted(Manifest.permission.CAMERA)) {
                    Intent scanIntent = new Intent(this, BookScannerActivity.class);
                    startActivityForResult(scanIntent, Constants.RC_BARCODE_CAPTURE);
                } else {
                    mPermDeniedList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    checkPermission(mPermDeniedList, Constants.PERM_REQUEST_READ_EXTERNAL_STORAGE);
                }
                break;
        }
    }


}
