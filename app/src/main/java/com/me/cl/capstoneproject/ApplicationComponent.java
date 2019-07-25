package com.me.cl.capstoneproject;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by CL on 11/1/17.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MyApplication myApplication);
    DatabaseReference databaseReference();
    FirebaseDatabase firebaseDatabase();
    Retrofit retrofit();
    FusedLocationProviderClient fusedLocationProviderClient();
}
