package com.example.fbu_instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String imagePath = "/document/primary:Download/puppy.jpg";
    private EditText etDescription;
    private Button btnCreate;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etDescription = (EditText) findViewById(R.id.etDescription);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = etDescription.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                // hardcoded image file for now
                final File file = new File(imagePath);
                final ParseFile parseFile = new ParseFile(file);

                /*
                // getExternalFilesDir() + "/Pictures" should match the declaration in fileprovider.xml paths
File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

// wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
bmpUri = FileProvider.getUriForFile(MyActivity.this, "com.codepath.fileprovider", file);

WILL NEED TO DO THIS FILE -> FILE PROVIDER stuff later
                 */

                // create the post
                createPost(description, parseFile, user);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTopPosts();
            }
        });

    }

    private void createPost(String description, ParseFile image, ParseUser user){
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(image);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("HomeActivity", "Post created successfully!");
                }else{
                    e.printStackTrace();
                }
            }
        });
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
}
