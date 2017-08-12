package thesonid.com.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.net.URL;

import thesonid.com.bakingapp.utilities.NetworkUtils;
import thesonid.com.bakingapp.utilities.OpenVideoUtils;

import static android.R.attr.data;

/**
 * Created by user on 9/8/17.
 */

public class MediaFragment extends Fragment {

    TextView mDescriptionText;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private String description,videouri,image;
    private boolean tabletSize;
    private ImageView imageView;
    private String onRese;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public MediaFragment(){
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View rootView=inflater.inflate(R.layout.fragment_media,container,false);
        mDescriptionText=(TextView)rootView.findViewById(R.id.text_description);
        mPlayerView=(SimpleExoPlayerView) rootView.findViewById(R.id.playerview) ;

        tabletSize = getResources().getBoolean(R.bool.isTablet);
        imageView=(ImageView)rootView.findViewById(R.id.image_thumb);

        updateLayout(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE,tabletSize);

        description=getArguments().getString("description");
        videouri=getArguments().getString("video");
        image=getArguments().getString("image");
        onRese=getArguments().getString("reset");

        if (videouri!=null && videouri!="-"){
            mPlayerView.setVisibility(View.VISIBLE);
            loadVideo(videouri);
        }else{
            mPlayerView.setVisibility(View.GONE);
        }

        if (description!=null && description!="-"){
              mDescriptionText.setText(description);
        }
        if (image!=null && image!="-"){
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(image).into(imageView);
        }else {
            imageView.setVisibility(View.GONE);
        }

        if (onRese.equals("yes")){
            setPosition(getArguments().getInt("position_video"));
        }

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayout(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE,tabletSize);
    }

    private void updateLayout(boolean isLandscape,boolean tablet) {
        if (isLandscape) {
            mDescriptionText.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        } else {
            mDescriptionText.setVisibility(View.VISIBLE);
        }
        if (tablet) {
            mDescriptionText.setVisibility(View.VISIBLE);
        }
    }

    public void loadVideo(String data){
        mPlayerView.setVisibility(View.VISIBLE);
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
        mPlayerView.setPlayer(mExoPlayer);
        Uri mediaUri=Uri.parse(data);
        String userAgent = Util.getUserAgent(getActivity(), "BakingApp");
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    public ExoPlayer getPlayer(){
        return mExoPlayer;
    }

    public int getCurrentPosition() {
        return (int) mExoPlayer.getCurrentPosition();
    }

    public void setPosition(int i){
        loadVideo(videouri);
        mExoPlayer.seekTo(i);
    }

}
