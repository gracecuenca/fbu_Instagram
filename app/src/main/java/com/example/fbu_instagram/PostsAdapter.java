package com.example.fbu_instagram;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    // creates one individual row in the recycler view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent,false);
        return new ViewHolder(view);
    }

    // bind the data view at the position specified
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    // returns how many items are in the data set
    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvHandle;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvSmallHandle;
        private TextView tvTimeStamp;
        private ImageButton ibLike;

        public ViewHolder(View itemView){

            super(itemView);

            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvSmallHandle = (TextView) itemView.findViewById(R.id.tvSmallHandle);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ibLike = (ImageButton) itemView.findViewById(R.id.ibLike);

            // set up on click listener for each post in the timeline, redirects to detailed view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Post post = posts.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, PostDetailsActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });

            // onclick listener for like button
            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // gets item position
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Post post = posts.get(position);
                        if(!post.hasUser(ParseUser.getCurrentUser())){
                            ibLike.setImageResource(R.drawable.ufi_heart_active);
                            post.addLikedUser(ParseUser.getCurrentUser());
                        } else{
                            ibLike.setImageResource(R.drawable.ufi_heart);
                            post.removeLikedUser(ParseUser.getCurrentUser());
                        }
                        post.saveInBackground();
                    }
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(Post post){
            tvHandle.setText(post.getUser().getUsername());
            tvSmallHandle.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            tvDescription.setText(post.getDescription());
            tvTimeStamp.setText(getRelativeTimeAgo(post));
            // post.setLikedBy();
            if(post.hasUser(ParseUser.getCurrentUser())){
                ibLike.setImageResource(R.drawable.ufi_heart_active);
            } else{
                ibLike.setImageResource(R.drawable.ufi_heart);
            }
        }

    }

    // calculating the relative time stamp
    public String getRelativeTimeAgo(Post post) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        long dateMillis = post.getCreatedAt().getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }

    // Clean all elements of the recycler for swipe to refresh
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items for swipe to refresh
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
