package com.example.fbu_instagram.fragments;

import android.util.Log;

import com.example.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

// we extend from PostsFragment because most of the information that we'll display in Profile
// will be similar to the info given in PostsFragment!
public class ProfileFragment extends PostsFragment {

    @Override
    protected void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20); // required feature to limit to top 20 posts
        postQuery.addDescendingOrder(Post.KEY_CREATED_AT); // most recent posts shown at top
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
}
