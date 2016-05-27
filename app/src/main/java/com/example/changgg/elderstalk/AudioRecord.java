package com.example.changgg.elderstalk;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Audio Capture:
// https://developer.android.com/guide/topics/media/audio-capture.html
public class AudioRecord {
    private static final String TAG = "AudioRecord";
    private SimpleDateFormat sdf = null;
    private Date dt = null;
    private static String strPathName = null;

    protected MediaRecorder mMediaRecorder  = null;

    public static String getStrPathName() {
        return strPathName;
    }

    public AudioRecord(){
        sdf = new SimpleDateFormat("yyMMdd_HHmmss");
        strPathName = Environment.getExternalStorageDirectory() + "/media/record/";
        File folder = new File(strPathName);
        if( !folder.exists() )
            Log.i(TAG, "isSuccess Create folder: " + folder.mkdirs());
        else
            Log.i(TAG, "the Folder has been created: " + strPathName);
    }

    protected void onRecord(boolean start) {
        if(start)
            startRecording();
        else {
            stopRecording();
        }
    }

    private void startRecording() {
        dt = new Date();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setOutputFile(strPathName + sdf.format(dt) + ".3gp");
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mMediaRecorder.start();
    }

    private void stopRecording() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }
}
