package com.example.fbu_instagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_instagram.EndlessRecyclerViewScrollListener;
import com.example.fbu_instagram.PostsAdapter;
import com.example.fbu_instagram.R;
import com.example.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public final String APP_TAG = "PostsFragment";
    private RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> mPosts;

    // needed for swipe to refresh
    private SwipeRefreshLayout swipeContainer;

    // needed for infinite pagination
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    // max_id that is updated and tracked for infinite pagenation
    private long max_id = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // showing the instagram logo on the toolbar
        ActionBar bar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        bar.setLogo(R.drawable.nav_logo_whiteout);
        bar.setDisplayUseLogoEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.setHomeButtonEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        rvPosts = (RecyclerView) view.findViewById(R.id.rvPosts);

        // create the data source
        mPosts = new ArrayList<>();
        // create the adapter
        adapter = new PostsAdapter(getContext(), mPosts);
        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        // setting up swipe container for pull to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // enabling endless pagination below
        // setting up the scrollListener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                fetchTimelineAsync(true);
            }
        };

        // set up scroller
        // rvPosts.addOnScrollListener(scrollListener);

        queryPosts();
    }

    // query posts method
    protected void queryPosts(){
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20); // required feature to limit to top 20 posts
        postQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){ // there was an error
                    Log.e(APP_TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                mPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                for(int i = 0; i < posts.size(); i++){
                    Log.d(APP_TAG, "Post " + posts.get(i).getDescription()+
                            " username: "+posts.get(i).getUser().getUsername());
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mPosts.clear();
        queryPosts();
        // signal refresh has finished
        swipeContainer.setRefreshing(false);
    }

    // method to fetch new data
    // when isNotRefresh is true, we fetch data for the first time and max_id is -1
    // so that the default page is shown
    // when false, it indicates that infinite pagenation is enabled and max_id is
    // set to the top of the next Post id
    public void fetchTimelineAsync(final boolean isNotRefresh) {

        if(isNotRefresh){
            max_id = mPosts.get(mPosts.size()-1).hashCode();
        }
        else{
            max_id = -1;
            adapter.clear();
        }
        populateTimeline();
    }

    private void loadTopPosts(){
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        Log.d("HomeActivity", "Post["+i+"] = "
                                + objects.get(i).getDescription()
                                +"\nusername = " + objects.get(i).getUser().getUsername()
                        );
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateTimeline(){
        loadTopPosts();
        adapter.notifyItemChanged(mPosts.size()-1);
    }

}
