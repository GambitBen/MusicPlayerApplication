package com.musicplayer.yorai.musicplayerapplication;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;

import com.musicplayer.yorai.musicplayerapplication.Adapters.SongListAdapter;
import com.musicplayer.yorai.musicplayerapplication.Logic.DatabaseHelper;
import com.musicplayer.yorai.musicplayerapplication.Logic.Metaphone;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity  extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static String GMS_SEARCH_ACTION = "com.google.android.gms.actions.SEARCH_ACTION";

    private RecyclerView mRecyclerView;
    private SongListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SearchView mSearchView;
    private String mQuery;

    private ArrayList<Song> baseSearchPlaylist;
    private ArrayList<Song> searchResultsPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_search);

        baseSearchPlaylist = (ArrayList<Song>) MainActivity.currentPlaylist.clone();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout manager
        //int numColumns = getResources().getInteger(R.integer.search_results_columns);
        int numColumns = 1;
        mLayoutManager = new GridLayoutManager(this, numColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new SongListAdapter(this, MainActivity.currentPlaylist);
        mRecyclerView.setAdapter(mAdapter);

        onNewIntent(getIntent());
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SEARCH) ||
                action.equals(GMS_SEARCH_ACTION)) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            doSearch(mQuery);
        }
    }

    private void doSearch(String query) {
        query = query.toLowerCase();
        searchResultsPlaylist = new ArrayList<Song>();
        //create a map to index a song according to the similarityEstimation to insert in the correct order
        //ArrayList<Double> similarityEstimationIndexlist = new ArrayList<Double>();
        mAdapter.clearResults();

        Metaphone metaphone = new Metaphone();
        String inquiry;
        double similarityEstimation;
        int i = 0;
        int index = 0;
        while (i < baseSearchPlaylist.size()) {
            Song song = baseSearchPlaylist.get(i);
            i++;
            //search by title for now
            inquiry = song.getTitle().toLowerCase();
            if (metaphone.isMetaphoneEqual(query, inquiry))
                similarityEstimation = 100;
            else
                similarityEstimation = calculateDistance(query, inquiry);

            if (similarityEstimation > 80) {
                searchResultsPlaylist.add(song);
            }
        }

        MainActivity.currentPlaylist = searchResultsPlaylist;
    }

    /**
     * Calculates the string distance between source and target strings using
     * the Damerau-Levenshtein algorithm. The distance is case-sensitive.
     *
     * @param source The source String.
     * @param target The target String.
     * @return The distance between source and target strings.
     * @throws IllegalArgumentException If either source or target is null.
     */
    public static double calculateDistance(CharSequence source, CharSequence target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
        int sourceLength = source.length();
        int targetLength = target.length();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
                if (i > 1 &&
                        j > 1 &&
                        source.charAt(i - 1) == target.charAt(j - 2) &&
                        source.charAt(i - 2) == target.charAt(j - 1)) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        //return dist[sourceLength][targetLength];
        double similarityEstimation = sourceLength;
        similarityEstimation = similarityEstimation - dist[sourceLength][targetLength];
        similarityEstimation = similarityEstimation / Math.min(sourceLength, targetLength);
        similarityEstimation = similarityEstimation*100;
        return similarityEstimation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);

        if (mQuery != null) {
            mSearchView.setQuery(mQuery, false);
        }

        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        mSearchView.setIconifiedByDefault(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setFocusable(false);
        mSearchView.setFocusableInTouchMode(false);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        doSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
