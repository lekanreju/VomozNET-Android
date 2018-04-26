package com.vomozsystems.apps.android.vomoznet.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.fragment.VideoFragment.OnVideoListFragmentInteractionListener;
import com.vomozsystems.apps.android.vomoznet.fragment.dummy.DummyContent.DummyItem;
import com.vomozsystems.apps.android.vomoznet.youtube.DeveloperKey;
import com.vomozsystems.apps.android.vomoznet.youtube.StandalonePlayerDemoActivity;
import com.vomozsystems.apps.android.vomoznet.youtube.VideoEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyVideoEntryRecyclerViewAdapter extends RecyclerView.Adapter<MyVideoEntryRecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    private final List<VideoEntry> mValues;
    private final OnVideoListFragmentInteractionListener mListener;
    private List<View> entryViews;
    private Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private LayoutInflater inflater;
    private ThumbnailListener thumbnailListener;

    public MyVideoEntryRecyclerViewAdapter(Activity activity, List<VideoEntry> items, OnVideoListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        thumbnailListener = new ThumbnailListener();
        thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_videoentry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.thumbnail.setPadding(10,10,10,10);
        holder.thumbnail.setTag(holder.mItem.getVideoId());
        YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(holder.thumbnail);
        if (loader == null) {
            // 2) The view is already created, and is currently being initialized. We store the
            //    current videoId in the tag.
            holder.thumbnail.setTag(holder.mItem.getVideoId());
        } else {
            // 3) The view is already created and already initialized. Simply set the right videoId
            //    on the loader.
            holder.thumbnail.setImageResource(R.drawable.loading_thumbnail);
            loader.setVideo(holder.mItem.getVideoId());
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onVideoListFragmentInteraction(holder.mItem);
                    Intent intent = new Intent(activity, StandalonePlayerDemoActivity.class);
                    intent.putExtra(StandalonePlayerDemoActivity.VIDEOID, holder.mItem.getVideoId());
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public YouTubeThumbnailView thumbnail;
        public VideoEntry mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.getText() + "'";
        }
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            view.setImageResource(R.drawable.no_thumbnail);
        }
    }
}
