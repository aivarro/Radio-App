package com.akaver.tabbedradio;

/**
 * Created by akaver on 10/04/2017.
 *
 * Define and keep all your constants here
 */

public class C {

    public static final int STREAM_STATUS_STOPPED = 0;
    public static final int STREAM_STATUS_BUFFERING = 1;
    public static final int STREAM_STATUS_PLAYING = 2;

    public static final String INTENT_STREAM_SOURCE = "com.akaver.tabbedradio.intent.streamsource";
    public static final String INTENT_STREAM_STATUS_STOPPED = "com.akaver.tabbedradio.intent.streamstatus.stopped";
    public static final String INTENT_STREAM_STATUS_BUFFERING = "com.akaver.tabbedradio.intent.streamstatus.buffering";
    public static final String INTENT_STREAM_STATUS_PLAYING = "com.akaver.tabbedradio.intent.streamstatus.playing";

    public static final String INTENT_STREAM_VOLUME_CHANGED = "com.akaver.tabbedradio.intent.streamvolume.changed";

    public static final String INTENT_STREAM_INFO = "com.akaver.tabbedradio.intent.streaminfo";
    public static final String INTENT_STREAM_INFO_ARTIST = "com.akaver.tabbedradio.intent.streaminfo.artist";
    public static final String INTENT_STREAM_INFO_TITLE = "com.akaver.tabbedradio.intent.streaminfo.title";

}
