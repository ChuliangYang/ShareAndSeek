package com.me.cl.capstoneproject.ui.upload.commercial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.bean.MyLocation;
import com.me.cl.capstoneproject.bean.Photo;
import com.me.cl.capstoneproject.di.CommercialUploadQL;
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadInteractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_AVERAGE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_CATEGORY;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_CITY;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_LATITUDE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_LOCATION_LISTENER;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_LONGTITUDE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_PHONE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_RATE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_STATE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_STREET;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_SUMMARY;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_TITLE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_WECHAT;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_ZIP;

/**
 * Created by CL on 11/15/17.
 */

public class CommercialUploadInteractorImpl implements CommercialUploadInteractor {

    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private LocationRequest locationRequest;
    private HashMap<String,Object> caches=new HashMap<>();

    @Inject
    public CommercialUploadInteractorImpl(@CommercialUploadQL Context context, FusedLocationProviderClient fusedLocationProviderClient
            , FirebaseStorage firebaseStorage, FirebaseDatabase firebaseDatabase, LocationRequest locationRequest) {
        this.context = context;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.firebaseStorage = firebaseStorage;
        this.firebaseDatabase = firebaseDatabase;
        this.locationRequest = locationRequest;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Single<Location> getCurrentLocation(GoogleApiClient mGoogleApiClient) {
        return Single.create(e -> fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
        if (location != null) {
            e.onSuccess(location);
        }
    }));
    }

    @Override
    public @Nullable MyLocation parseLocation(Location location) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addressList=new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Handle case where no address was found.
            if (addressList == null || addressList.size() == 0) {
            return null;
            } else {
                Address address = addressList.get(0);
                if (address != null) {
                    String street = address.getAddressLine(0).trim();
                    String city = "";
                    if (street != null && street.split(",").length > 1) {
                        city = street.split(",")[1].trim();
                        street = street.split(",")[0].trim();
                    }
                    if (address.getAddressLine(1) != null) {
                        city = address.getAddressLine(1).split(",")[0].trim();// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    }
                    if ("New York".equals(city)) {
                        city = "Manhattan";
                    }
                    String state = address.getAdminArea().trim();
//                String country = address.getCountryName();
                    String zip = address.getPostalCode();
                    MyLocation myLocation = new MyLocation();
                    myLocation.setAddress(street);
                    myLocation.setCity(city);
                    myLocation.setState(state);
                    myLocation.setZipCode(zip);
                    myLocation.setLatitude(location.getLatitude());
                    myLocation.setLongitude(location.getLongitude());
                    return myLocation;
                } else {
                    return new MyLocation();
                }
            }
    }

    @Override
    public @Nullable Photo convertIntoPhotoData(Object... data) {
        if (data[0] instanceof Uri) {
            Uri uri= (Uri) data[0];
            Photo photo=new Photo();
            photo.setDownloadUri(uri.toString());
            return photo;
        }
        return null;
    }

    @Override
    public Observable storePhotosInCloud(ArrayList<Photo> photos) {
        return Observable.fromIterable(photos).flatMap(photo -> Single.create(e -> {
            StorageReference storageRef=firebaseStorage.getReference();
            Uri file = Uri.fromFile(new File(photo.path));
            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment()+System.currentTimeMillis());
            UploadTask uploadTask = riversRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(exception -> {
                e.onError(exception);
                // Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                photo.setDownloadUri(taskSnapshot.getDownloadUrl().toString());
                photo.setSize(taskSnapshot.getMetadata().getSizeBytes());
                photo.setUploaded(true);
                e.onSuccess(photo);
            });
        }).toObservable());
    }
    @Override
    public Boolean storeFormInCloud(ArrayList<Photo> photos, HashMap<String, Object> form){
            DatabaseReference databaseReference=firebaseDatabase.getReference(Constant.Database.BASE_LIST_LOCATION);
            String listId=databaseReference.push().getKey();
            CommercialItem commercialItem=new CommercialItem();
            CommercialItem.LocationBean locationBean=new CommercialItem.LocationBean();
            locationBean.setStreet((String) form.get(MAP_KEY_STREET));
            locationBean.setCity((String) form.get(MAP_KEY_CITY));
            locationBean.setState((String) form.get(MAP_KEY_STATE));
            locationBean.setZip((String) form.get(MAP_KEY_ZIP));
            locationBean.setLatitude(String.valueOf(form.get(MAP_KEY_LATITUDE)));
            locationBean.setLongitude(String.valueOf(form.get(MAP_KEY_LONGTITUDE)));
            commercialItem.setLocation(locationBean);
            if (!TextUtils.isEmpty((String) form.get(MAP_KEY_AVERAGE))) {
            commercialItem.setAverage_costume(Float.valueOf((String) form.get(MAP_KEY_AVERAGE)));
            } else {
                commercialItem.setAverage_costume(0f);
            }
            commercialItem.setCategory((String) form.get(MAP_KEY_CATEGORY));
            CommercialItem.ContactBean contactBean=new CommercialItem.ContactBean();
            contactBean.setPhone((String) form.get(MAP_KEY_PHONE));
            contactBean.setWechat((String) form.get(MAP_KEY_WECHAT));
            commercialItem.setContact(contactBean);
            if (TextUtils.isEmpty(String.valueOf(form.get(MAP_KEY_RATE)))) {
                commercialItem.setRating(0);
                commercialItem.setReview_count(0);
            }
            commercialItem.setSummary((String) form.get(MAP_KEY_SUMMARY));
            commercialItem.setTitle((String) form.get(MAP_KEY_TITLE));
            List<CommercialItem.PhotoBean> photoBeans=new ArrayList<>();
            for (Photo photo: photos) {
                CommercialItem.PhotoBean photoBean=new CommercialItem.PhotoBean();
                photoBean.setUri(photo.getDownloadUri().toString());
                photoBean.setSize(String.valueOf(photo.getSize()));
                photoBean.setPhoto_last_name(photo.name);
                photoBeans.add(photoBean);

            }
            commercialItem.setPhotoBeanList(photoBeans);
            commercialItem.setCreate_time(String.valueOf(System.currentTimeMillis()));
            databaseReference.child(listId).setValue(commercialItem);
            return true;
    }

    @Override
    public LocationRequest getLocationRequest(){
            return locationRequest;
    }

    @Override
    public LocationListener getLocationListener(){
        if (!caches.containsKey(MAP_KEY_LOCATION_LISTENER)) {
            LocationListener locationListener= location -> {

            };
            caches.put(MAP_KEY_LOCATION_LISTENER,locationListener);
        }
        return (LocationListener) caches.get(MAP_KEY_LOCATION_LISTENER);
    }

    @Override
    public void saveLocationListener(LocationListener locationListener){
        caches.put(MAP_KEY_LOCATION_LISTENER,locationListener);
    }

    @Override
    public GoogleApiClient getGoogleLocationApiClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener){
        if (connectionCallbacks!=null||onConnectionFailedListener!=null) {
            GoogleApiClient googleApiClient= new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(onConnectionFailedListener)
                    .addApi(LocationServices.API).build();
            caches.put(Constant.UploadPage.MAP_KEY_GOOGLE_LOCATION_CLIENT,googleApiClient);
        }
        return (GoogleApiClient) caches.get(Constant.UploadPage.MAP_KEY_GOOGLE_LOCATION_CLIENT);
    }


    @SuppressLint("NewApi")
    @Override
    public MyLocation parseAddress(String street, String city, String state){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(String.format("%s,%s,%s",street,city,state), 1);
            } catch (IOException error) {
                error.printStackTrace();
            }

            if (addressList == null || addressList.size() == 0) {
               return null;
            } else {
                Address address = addressList.get(0);
                MyLocation myLocation=new MyLocation();
                myLocation.setAddress(street);
                myLocation.setCity(city);
                myLocation.setState(state);
                myLocation.setLatitude(address.getLatitude());
                myLocation.setLongitude(address.getLongitude());
                return myLocation;
            }

    }

    @Override
    public void saveToCache(String key,Object value) {
        caches.put(key,value);
    }
    @Override
    public Object getFromCache(String key) {
        return caches.get(key);
    }

    @Override
    public void storeAddressLocation(String wholeAddress,MyLocation location){
        caches.put(wholeAddress,location);
    }

    @Override
    @SuppressLint("NewApi")
    public MyLocation getLocationFromAddress(String wholeAddress){
        if (caches.get(wholeAddress)!=null) {
            return (MyLocation) caches.get(wholeAddress);
        }
        return null;
    }

    @Override
    public String getWholeAddressFromLocationBean(MyLocation myLocation){
        StringBuffer wholeAddress=new StringBuffer();
        if (!TextUtils.isEmpty(myLocation.getAddress())) {
            wholeAddress.append(myLocation.getAddress()+",");
        }

        if (!TextUtils.isEmpty(myLocation.getCity())) {
            wholeAddress.append(myLocation.getCity()+",");
        }

        if (!TextUtils.isEmpty(myLocation.getState())) {
            wholeAddress.append(myLocation.getState()+",");
        }
        if (wholeAddress.length() > 0) {
            wholeAddress.deleteCharAt(wholeAddress.length()-1);
        }
        Timber.d("-------------"+wholeAddress.toString());
        return wholeAddress.toString();
    }



}
