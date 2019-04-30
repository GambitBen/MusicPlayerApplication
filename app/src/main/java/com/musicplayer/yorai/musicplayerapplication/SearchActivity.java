package com.musicplayer.yorai.musicplayerapplication;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;

import com.musicplayer.yorai.musicplayerapplication.Adapters.SongListAdapter;
import com.musicplayer.yorai.musicplayerapplication.Logic.Metaphone;
import com.musicplayer.yorai.musicplayerapplication.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity  extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static String GMS_SEARCH_ACTION = "com.google.android.gms.actions.SEARCH_ACTION";

    private RecyclerView mRecyclerView;
    private SongListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View rootView;

    private SearchView mSearchView;
    private String mQuery;

    private ArrayList<Song> baseSearchPlaylist;
    private ArrayList<Song> searchResultsPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_search);

        baseSearchPlaylist = (ArrayList<Song>) MainActivity.playlistToSearch.clone();
        searchResultsPlaylist = (ArrayList<Song>) baseSearchPlaylist.clone();
        Log.d("SEARCH ACTIVITY``````````````````````````", "baseSearchPlaylist = " + baseSearchPlaylist.toString());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout manager
        //int numColumns = getResources().getInteger(R.integer.search_results_columns);
        int numColumns = 1;
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new SongListAdapter(this, searchResultsPlaylist);

        mAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Song obj, int position) {
                // tell the music service what the current playlist is
//                MainActivity.currentPlaylist = (ArrayList<Song>) searchResultsPlaylist.clone(); // change the whole currentplaylist thing, clone because this activity is temporary
                MainActivity.musicSrv.setCurrentPlaylist(searchResultsPlaylist);
                // select the song that was clicked from the current playlist
                MainActivity.selectSong(position);
            }
        });


        mRecyclerView.setAdapter(mAdapter);
        mQuery = getIntent().getStringExtra("Query");
        doSearch(mQuery);
    }

    private void doSearch(String query) {
        query = query.toLowerCase();
        searchResultsPlaylist.clear();
        //create a map to index a song according to the similarityEstimation to insert in the correct order
        //ArrayList<Double> similarityEstimationIndexlist = new ArrayList<Double>();
        mAdapter.clearResults();
        Log.d("~~~~~~~~~1~~~~~~~~~~", "doSearch: clearResults");

        Metaphone metaphone = new Metaphone();
        String inquiry;
        double similarityEstimation = 0;
        int i = 0;
        int index = 0;
        while (i < baseSearchPlaylist.size()) {
            Song song = baseSearchPlaylist.get(i);
            similarityEstimation = 0;
            i++;
            //search by title for now
            inquiry = song.getTitle().toLowerCase();
            Log.d("~~~~~~~~2~~~~~~~~~~~", "doSearch: query = " + query + " ||| inquiry = " + inquiry);
            if (inquiry.contains(query)) {
                similarityEstimation = 100;
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!", "inquiry.contains(query) " + inquiry.contains(query));
            }
            // todo: split string and compare with metaphone, also (maybe) create a similarityEstimation for metaphone
            else if (metaphone.isMetaphoneEqual(query, inquiry)) {
                similarityEstimation = 90;
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!", "metaphone.isMetaphoneEqual(query, inquiry) " + metaphone.isMetaphoneEqual(query, inquiry));

            }
            else {
                for (String subInquiry : inquiry.split("\\s+")) {
                    Log.d("         .", "subInquiry: " + subInquiry);
                    Log.d("         .", "calculateDistance: " + calculateDistance(query, subInquiry));
                    similarityEstimation = Math.max(similarityEstimation, calculateDistance(query, subInquiry));
                }
                Log.d("~~~~~~~~~~~~~~~~~~~~", "calculateDistance: " + similarityEstimation);
            }

            // will allow up to 15% inaccuracy
            if (similarityEstimation > 85) {
//            if (similarityEstimation >= 50) {
                // todo: order/insert order searchResultsPlaylist by similarityEstimation
                searchResultsPlaylist.add(song);
                //probably call method to update recyclerview here
                Log.d("$$$$$$$$$$$$$$", "ADD SONG " + song.getTitle());
            }
        }

//        MainActivity.currentPlaylist = searchResultsPlaylist;

        //call method to update recyclerview here
        mAdapter.notifyDataSetChanged();
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
        double similarityEstimation = Math.max(sourceLength, targetLength);
        similarityEstimation = similarityEstimation - dist[sourceLength][targetLength];
        similarityEstimation = similarityEstimation / Math.min(sourceLength, targetLength);
        similarityEstimation = similarityEstimation * 100;
        return similarityEstimation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        mSearchView = (SearchView) searchItem.getActionView();
//        setupSearchView(searchItem);
//
//        if (mQuery != null) {
//            mSearchView.setQuery(mQuery, false);
//        }

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
