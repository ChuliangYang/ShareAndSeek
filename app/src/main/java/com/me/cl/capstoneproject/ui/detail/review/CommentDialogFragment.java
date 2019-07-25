package com.me.cl.capstoneproject.ui.detail.review;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.me.cl.capstoneproject.MyApplication;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.adapter.recyclerview.PictureUploadAdapter;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.bean.Photo;
import com.me.cl.capstoneproject.bean.ReviewBean;
import com.me.cl.capstoneproject.event.UploadCompleteEvent;
import com.me.cl.capstoneproject.ui.detail.review.di.CommentDialogModule;
import com.me.cl.capstoneproject.ui.detail.review.di.DaggerCommentDialogComponent;
import com.me.cl.capstoneproject.util.BaseUtils;
import com.me.cl.capstoneproject.widget.RecyclerViewSpacesItemDecoration;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.me.cl.capstoneproject.base.Constant.DetailPage.BUNDLE_KEY_COMMERCIAL_ID;

/**
 * Created by CL on 11/29/17.
 */

public class CommentDialogFragment extends DialogFragment {

//    @BindView(R.id.iv_head)
//    ImageView ivHead;
    @BindView(R.id.rb_stars)
    RatingBar rbStars;
    @BindView(R.id.rv_photo)
    RecyclerView rvPhoto;
    @BindView(R.id.ev_comment)
    EditText evComment;

    @Inject
    PictureUploadAdapter pictureUploadAdapter;
    @Inject
    FirebaseStorage firebaseStorage;
    @Inject
    FirebaseDatabase firebaseDatabase;

    LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(this);

    private String commercialId;

    private String rating;

    private String comment;

    private MaterialDialog indeterminateProgress;

    Unbinder unbinder;

    final String KEY_PHOTO_LIST="KEY_PHOTO_LIST";

    public CommentDialogFragment() {
    }

    public static CommentDialogFragment newInstance(String commercialId) {

        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_COMMERCIAL_ID,commercialId);
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerCommentDialogComponent.builder().applicationComponent(((MyApplication)(getActivity().getApplication())).getApplicationComponent())
                .commentDialogModule(new CommentDialogModule(this)).build().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 设置主题的构造方法
        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        commercialId =getArguments().getString(BUNDLE_KEY_COMMERCIAL_ID);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_comment, null);
        unbinder = ButterKnife.bind(this, view);
        rvPhoto.setLayoutManager(new GridLayoutManager(getContext(),3));
        rvPhoto.setHasFixedSize(true);
        if (savedInstanceState!=null&&savedInstanceState.containsKey(KEY_PHOTO_LIST)) {
            pictureUploadAdapter.replaceAll(savedInstanceState.getParcelableArrayList(KEY_PHOTO_LIST));
        }
        rvPhoto.setAdapter(pictureUploadAdapter);
        pictureUploadAdapter.MAX_PHOTO_NUM=6;
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,20);
        RecyclerViewSpacesItemDecoration recyclerViewSpacesItemDecoration=new RecyclerViewSpacesItemDecoration(stringIntegerHashMap);
//        rvPhoto.addItemDecoration(recyclerViewSpacesItemDecoration);

        boolean wrapInScrollView = true;

        return new MaterialDialog.Builder(getActivity())
                .customView(view, wrapInScrollView)
                .positiveText("commite").onPositive((dialog, which) -> {
                    commit();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .negativeText("cancel")
                .autoDismiss(false)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void commit(){
        if (rbStars.getRating()==0f) {
            BaseUtils.Dialog.showAlertDialog(getString(R.string.sorry),getString(R.string.please_rate),getString(R.string.positive),null,getContext());
            return;
        }
        rating= String.valueOf(rbStars.getRating());
        comment=evComment.getText().toString();
        ArrayList<Photo> photos=pictureUploadAdapter.getPhotoList();
        Observable.fromIterable(photos)
                .observeOn(Schedulers.io()).
                flatMap(photo -> Single.create(e -> {
                    StorageReference storageRef=firebaseStorage.getReference();
                    Uri file = Uri.fromFile(new File(photo.path));
                    StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment()+System.currentTimeMillis());
                    UploadTask uploadTask = riversRef.putFile(file);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(exception -> {
//                        e.onError(exception);
                        // Handle unsuccessful uploads
                    }).addOnSuccessListener(taskSnapshot -> {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        photo.setDownloadUri(taskSnapshot.getDownloadUrl().toString());
                        photo.setSize(taskSnapshot.getMetadata().getSizeBytes());
                        photo.setUploaded(true);
                        e.onSuccess(true);
                    });
                }).toObservable()).concatWith(Single.create(e -> e.onSuccess(true)).toObservable()).lastOrError()
                .doOnSubscribe(disposable -> indeterminateProgress= BaseUtils.Dialog.controlIndeterminateProgress(true,getString(R.string.please_wait),getString(R.string.uploading),null,getActivity()))
                .observeOn(Schedulers.io())
                .map(o -> {
            DatabaseReference databaseReference=firebaseDatabase.getReference(Constant.Database.REVIEW_LIST_LOCATION);
            String listId=databaseReference.push().getKey();
            ReviewBean reviewBean=new ReviewBean();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Name, email address, and profile photo Url
                        reviewBean.setName(user.getDisplayName());
                        reviewBean.setAvatar(user.getPhotoUrl().toString());
                    } else {
                        reviewBean.setName(getString(R.string.empty_user));
                        reviewBean.setAvatar(getString(R.string.empty_avator_url));
                    }

            reviewBean.setPhotos(photos);
            reviewBean.setContent(comment);
            reviewBean.setRate(rating);
            reviewBean.setDate(String.format(("%d/%d/%d"),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DATE),Calendar.getInstance().get(Calendar.YEAR)));
            databaseReference.child(commercialId).child(listId).setValue(reviewBean);
            return true;
        })
                .doFinally(() -> BaseUtils.Dialog.controlIndeterminateProgress(false,null,null,indeterminateProgress,null))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.bindToLifecycle())
                .subscribe(aBoolean ->{
                BaseUtils.Dialog.showAlertDialog(getString(R.string.success), getString(R.string.upload_success), getString(R.string.positive), (dialog, which) -> {dialog.dismiss();dismiss();},getActivity());
                EventBus.getDefault().postSticky(new UploadCompleteEvent.CommentPublish(true,Float.valueOf(rating)));
                }, throwable -> {
                    Timber.e(throwable.toString());
                });
    }

    public void notifyPhotosAdd(ArrayList<Photo> photos){
        pictureUploadAdapter.addPhotos(photos);
    }

    public void notifyPhotosReplace(ArrayList<Photo> photos){
        pictureUploadAdapter.replaceAll(photos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PHOTO_LIST,pictureUploadAdapter.getPhotoList());
    }
}
