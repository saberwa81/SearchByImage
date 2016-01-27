package rikka.searchbyimage.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import rikka.searchbyimage.R;
import rikka.searchbyimage.database.DatabaseHelper;
import rikka.searchbyimage.database.table.CustomEngineTable;
import rikka.searchbyimage.staticdata.CustomEngine;
import rikka.searchbyimage.ui.apdater.SearchEngineAdapter;

public class EditSitesActivity extends AppCompatActivity {
    Activity mActivity;

    DatabaseHelper mDbHelper;

    CoordinatorLayout mCoordinatorLayout;

    Toolbar mToolbar;
    FloatingActionButton mFAB;
    RecyclerView mRecyclerView;
    static SearchEngineAdapter mAdapter;

    List<CustomEngine> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sites);

        mActivity = this;

        mDbHelper = DatabaseHelper.instance(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, EditSiteInfoActivity.class));
            }
        });
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mFAB.getLayoutParams();
        p.setBehavior(new FloatingActionButton.Behavior() {
            @Override
            public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                               final FloatingActionButton child,
                                               final View directTargetChild, final View target, final int nestedScrollAxes) {
                // Ensure we react to vertical scrolling
                return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                        || super.onStartNestedScroll(coordinatorLayout, child,
                        directTargetChild, target, nestedScrollAxes);
            }

            @Override
            public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
                super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                    // User scrolled down and the FAB is currently visible -> hide the FAB
                    child.hide();
                } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                    // User scrolled up and the FAB is currently not visible -> show the FAB
                    child.show();
                }
            }
        });
        mFAB.setLayoutParams(p);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mData = CustomEngine.getList(this);
        mAdapter = getAdapter(this);
        mAdapter.notifyItemInserted(1);
        mRecyclerView.setAdapter(mAdapter);

        /*mRecyclerView.addItemDecoration(new BaseRecyclerViewItemDecoration(
                new ColorDrawable(ContextCompat.getColor(this, R.color.dividerSearchEngineList))) {
            int offsetLeft = Utils.dpToPx(48);

            @Override
            public boolean canDraw(RecyclerView parent, View child, int childCount, int position) {
                return (position != 0 && position != 6 && position != 7);
            }

            @Override
            public void draw(Canvas c, int left, int top, int right, int bottom) {
                super.draw(c, left + offsetLeft, top, right, bottom);
            }

            @Override
            public int getHeight() {
                return 1;
            }
        });*/
        mAdapter.setOnItemClickListener(new SearchEngineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, final int realPosition, CustomEngine item) {
                Intent intent = new Intent(mActivity, EditSiteInfoActivity.class);
                intent.putExtra(EditSiteInfoActivity.EXTRA_EDIT_LOCATION, realPosition);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int position, final int realPosition, final CustomEngine item) {
                if (item.id < 6) {
                    return;
                }

                new AlertDialog.Builder(mActivity)
                        .setItems(
                                new CharSequence[] {"Delete"},
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                delete(item.id);

                                                mData.remove(realPosition);
                                                mAdapter.notifyItemRemoved(position);

                                                //Snackbar.make(mCoordinatorLayout, "Deleted", Snackbar.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                })
                        .show();
            }
        });
    }

    public static synchronized SearchEngineAdapter getAdapter(Context context) {
        if (mAdapter == null) {
            mAdapter = new SearchEngineAdapter(CustomEngine.getList(context));
        }

        return mAdapter;
    }

    private void delete(int id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = CustomEngineTable.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id)};
        db.delete(CustomEngineTable.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}