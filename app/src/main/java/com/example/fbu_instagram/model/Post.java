package com.example.fbu_instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKED = "liked";
    public static final String KEY_LIKED_BY = "likedBy";

    // each post has its own likedBy array
    private ArrayList<ParseUser> users = new ArrayList<ParseUser>();

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public boolean getLiked(){
        return getBoolean(KEY_LIKED);
    }

    public void setLiked(boolean liked){
        put(KEY_LIKED, liked);
    }

    public void setLikedBy(){
        put(KEY_LIKED_BY, users);
    }

    public List<ParseUser> getLikedBy(){
        return getList(KEY_LIKED_BY);
    }

    public void addLikedUser(ParseUser user){
        put(KEY_LIKED_BY, users.add(user));
    }

    public void removeLikedUser(ParseUser user){
        put(KEY_LIKED_BY, users.remove(user));
    }

    public boolean hasUser(ParseUser user){
        for(int i = 0; i < users.size(); i ++){
            if(user.getUsername().equals(users.get(i).getUsername())) return true;
        }
        return false;
    }

    public static class Query extends ParseQuery<Post> {

        public Query(){
            super(Post.class);
        }

        public Query getTop(){
            setLimit(20); // we only want the first 20 posts
            return this; // the builder pattern
        }

        public Query withUser(){
            include("user");
            return this;
        }

    }
}
