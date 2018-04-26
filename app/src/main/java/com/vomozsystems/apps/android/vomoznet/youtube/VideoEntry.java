package com.vomozsystems.apps.android.vomoznet.youtube;

/**
 * Created by leksrej on 4/3/18.
 */

public class VideoEntry {
    private final String text;
    private final String videoId;

    public VideoEntry(String text, String videoId) {
        this.text = text;
        this.videoId = videoId;
    }

    public String getText() {
        return text;
    }

    public String getVideoId() {
        return videoId;
    }
}