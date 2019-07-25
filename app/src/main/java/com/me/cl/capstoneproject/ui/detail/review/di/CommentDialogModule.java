package com.me.cl.capstoneproject.ui.detail.review.di;


import android.support.v4.app.DialogFragment;

import com.google.firebase.storage.FirebaseStorage;
import com.me.cl.capstoneproject.adapter.recyclerview.PictureUploadAdapter;
import com.me.cl.capstoneproject.di.FragmentScope;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by CL on 11/30/17.
 */
@Module
@FragmentScope
public class CommentDialogModule {

    private DialogFragment dialogFragment;

    public CommentDialogModule(DialogFragment dialogFragment) {
        this.dialogFragment = dialogFragment;
    }

    @Provides
    @FragmentScope
    FirebaseStorage provideFirebaseStorage(){return FirebaseStorage.getInstance();}

    @Provides
    PictureUploadAdapter providePictureUploadAdapter(){
        return new PictureUploadAdapter(dialogFragment.getActivity(),new ArrayList<>());
    }

}
