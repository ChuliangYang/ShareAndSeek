package com.me.cl.capstoneproject;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.me.cl.capstoneproject.base.Constant.NetWork.BASE_URL;

/**
 * Created by CL on 11/1/17.
 */

@Module
@Singleton
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Resources provideResources(Context context)
    {
        return context.getResources();
    }

    @Provides
    @Singleton
    FirebaseDatabase provideFirebaseDatabaseReference(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(false);
        return firebaseDatabase;
    }

    @Provides
    @Singleton
    DatabaseReference provideDatabaseReference(FirebaseDatabase firebaseDatabase){return firebaseDatabase.getReference();}

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        HttpLoggingInterceptor httpLoggingInterceptor= new HttpLoggingInterceptor(message -> Log.i("RetrofitLog","retrofitBack = "+message));
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor){
        return new OkHttpClient.Builder().addNetworkInterceptor(httpLoggingInterceptor).build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }

    @Provides
    @Singleton
    FusedLocationProviderClient provideFusedLocationProviderClient(Context context){return LocationServices.getFusedLocationProviderClient(context);}
}
