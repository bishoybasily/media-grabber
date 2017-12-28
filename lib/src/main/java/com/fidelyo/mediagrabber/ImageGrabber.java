package com.fidelyo.mediagrabber;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * Created by bishoy on 12/26/17.
 */

public class ImageGrabber {

    public final static Integer CODE = 987654321;
    public final static String EXTRA = "extra";

    private final String TAG = getClass().getSimpleName();

    public Observable<String> grab(Activity activity) {
        return Observable.create(e -> {
            getFragment(activity)
                    .setEmitter(e)
                    .startActivityForResult(new Intent(activity, ActivityImageGrabber.class), CODE);
        });
    }

    private ImageGrabberFragment getFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        ImageGrabberFragment fragment = (ImageGrabberFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new ImageGrabberFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    public final static class ImageGrabberFragment extends Fragment {

        private ObservableEmitter<String> emitter;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public ImageGrabberFragment setEmitter(ObservableEmitter<String> emitter) {
            this.emitter = emitter;
            return this;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == ImageGrabber.CODE) {
                    if (data != null) {
                        if (emitter != null) {
                            emitter.onNext(data.getStringExtra(EXTRA));
                            emitter.onComplete();
                        }
                    }
                }
            }
        }

    }
}
