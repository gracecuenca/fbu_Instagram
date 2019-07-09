package com.example.fbu_instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText etDescription;
    private Button btnCreate;
    private Button btnRefresh;
    private Button btnLogOut;
    private Button btnCapture;
    private ImageView ivPostImage;

    // needed for photo capturing intent
    public final String APP_TAG = "HomeActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etDescription = (EditText) findViewById(R.id.etDescription);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnCapture = (Button) findViewById(R.id.btnCapture);
        ivPostImage = (ImageView) findViewById(R.id.ivPostImage);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = etDescription.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                // checking to see if the user uploaded a file
                if(photoFile == null || ivPostImage.getDrawable() == null){
                    Log.e("HomeActivity", "No photo to submit");
                    Toast.makeText(HomeActivity.this, "There is no photo!", Toast.LENGTH_LONG).show();
                    return;
                }
                savePost(description, user, photoFile);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTopPosts(); // for refresh
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // capturing an image with the phone's camera
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        queryPosts();

    }

    // query posts method
    private void queryPosts(){
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){ // there was an error
                    Log.e("HomeActivity", "Error with query");
                    e.printStackTrace();
                    return;
                }
                for(int i = 0; i < posts.size(); i++){
                    Log.d("HomeActivity", "Post " + posts.get(i).getDescription()+
                            " username: "+posts.get(i).getUser().getUsername());
                }
            }
        });
    }

    private void launchCamera(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(HomeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void savePost(String description, ParseUser user, File photoFile){
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("HomeActivity", "Successfully save post!");
                    etDescription.setText(""); // clear out description text box
                    ivPostImage.setImageResource(0); // clear out the image view
                    Toast.makeText(HomeActivity.this, "Successfully posted!", Toast.LENGTH_LONG).show();
                }else{
                    Log.e("HomeActivity", "Error while saving post");
                    e.printStackTrace();
                    return;
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
                } else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void logout(){
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
