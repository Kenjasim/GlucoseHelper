package com.kenjasim.glucosehelper.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kenjasim.glucosehelper.R;
import com.kenjasim.glucosehelper.other.CircleTransform;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private TextView nameTV;
    private ImageButton profilePhoto;
    private Bitmap bitmap;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private static final int GALLERY_INTENT = 2;
    private static final String TAG = "Login";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    public Bitmap getBitmapFromURL (String src){
     try{
         URL url = new URL(src);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setDoInput(true);
         connection.connect();
         InputStream input = connection.getInputStream();
         Bitmap bitmap = BitmapFactory.decodeStream(input);
         return bitmap;
     }catch (Exception e){
         e.printStackTrace();
         return null;
     }

    }



    public void onActivityCreated(Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        super.onActivityCreated(savedInstanceState);

        auth =  FirebaseAuth.getInstance();

        nameTV = (TextView) getView().findViewById(R.id.nameTV);
        profilePhoto = (ImageButton) getView().findViewById(R.id.profilePhoto);

        mStorage = FirebaseStorage.getInstance().getReference();

        mProgressDialog = new ProgressDialog(getActivity());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {

                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
                String photobitmap = user.getPhotoUrl() != null ? user.getPhotoUrl().getPath() : null;

                Glide.with(this).load(photoUrl)
                        .crossFade()
                        .override(300,300)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profilePhoto);


                nameTV.setText(name);
                profilePhoto.setImageURI(photoUrl);

            };
        }

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/+");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });





    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri dataURI = data.getData();

            StorageReference filepath = mStorage.child("ProfilePhotos").child(System.currentTimeMillis()+"_profile.jpg");

            mProgressDialog.setMessage("Uploading");
            mProgressDialog.show();

            filepath.putFile(dataURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri profilephotoURI = taskSnapshot.getDownloadUrl();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Toast.makeText(getContext(), "Upload Done", Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();
            };






        });
    }
}
}



