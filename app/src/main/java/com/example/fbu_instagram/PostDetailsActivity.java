package com.example.fbu_instagram;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_instagram.model.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {

    // Post to display
    Post post;

    // post attributes
    private TextView tvHandle;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvSmallHandle;
    private TextView tvTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // making sure instagram logo shows up
        ActionBar bar = getSupportActionBar();
        bar.setLogo(R.drawable.nav_logo_whiteout);
        bar.setDisplayUseLogoEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.setHomeButtonEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        // unwrapping the post sent in the intent
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvHandle = (TextView) findViewById(R.id.tvHandle);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvSmallHandle = (TextView) findViewById(R.id.tvSmallHandle);
        tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);

        tvHandle.setText(post.getUser().getUsername());
        tvSmallHandle.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(getApplicationContext()).load(image.getUrl()).into(ivImage);
        }
        tvDescription.setText(post.getDescription());
        tvTimeStamp.setText(getRelativeTimeAgo(post));
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
}
