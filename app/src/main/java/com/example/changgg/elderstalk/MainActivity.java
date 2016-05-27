package com.example.changgg.elderstalk;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.changgg.elderstalk.RecyclerView.Contact;
import com.example.changgg.elderstalk.RecyclerView.ContactsAdapter;
import com.example.changgg.elderstalk.Service.RadioService;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
// https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    private static final String         TAG                 = "MainActivity";
    private FloatingActionButton        fab;
    private StateCheck                  stateCheck;
    private AudioRecord                 audioRecord;

    private Handler                     mHandler            = new Handler();
    private boolean                     isRunning           = true;

    private RecyclerView                mRecyclerView;
    private ContactsAdapter             mContactAdapter;

    private Boolean                     isBtnLongPressed    = false;
    private Boolean                     isRadioRunning      = false;

    static final String FTP_HOST = "219.85.226.203";
    static final String FTP_USER = "CC";
    static final String FTP_PASS = "";
    private File Directory;
    private File[] localFiles;
    private FTPFile[] serverFiles;

    int i =0;

    @Override
    protected void onPause() {
        super.onPause();
        if (audioRecord.mMediaRecorder != null) {
            audioRecord.mMediaRecorder.release();
            audioRecord.mMediaRecorder = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        initElements();
        initUIElements();

        // if NO NETWORK but RadioService still RUNNING, stop it.
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    try {
                        Thread.sleep(50000);    //50Second check one time.
                        Thread.interrupted();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!stateCheck.isNetworkConnected() && stateCheck.isRadioServiceRunning(RadioService.class)) {
                                    stopService(new Intent(MainActivity.this, RadioService.class));
                                    Toast.makeText(MainActivity.this, "Due to NO NETWORK, Radio OFF!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e){
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        FTPClient ftpClient = new FTPClient();
                        try {
                            //連接FTP
                            ftpClient.connect(InetAddress.getByName(FTP_HOST));
                            //登入FTP
                            if (ftpClient.login(FTP_USER, FTP_PASS)) {
                                ftpClient.enterLocalPassiveMode();
                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                                serverFiles = ftpClient.listFiles("/");

                                Directory = new File(audioRecord.getStrPathName());
                                localFiles = Directory.listFiles();
                                for (FTPFile serverFile : serverFiles) {
                                    Log.v(TAG, "FTP ServerFile: " + serverFile);
                                }
                                for (File localFile : localFiles) {
                                    Log.v(TAG, "FTP LocalFiles: " + localFile + " _ Size: " + localFile.length());
                                }
                                Log.v(TAG, "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ");

                                boolean result;

                                // Uploading a file to a FTP server from android phone?:
                                // http://stackoverflow.com/questions/11600988/uploading-a-file-to-a-ftp-server-from-android-phone
                                // ANDROID利用APACHE COMMONS NET達到FTP上傳檔案效果:
                                // http://blog.jeremyhuang.com/2014/04/androidapache-commons-netftp.html
                                    // Download File.
                                if(serverFiles.length > localFiles.length){
                                    Log.v(TAG, "FTP ServerFile ADDed: " + serverFiles[serverFiles.length-1]);
//                                    for(i=localFiles.length ; i< serverFiles.length; i++) {
                                    for(int i=0; i<serverFiles.length; i++){
                                        String serverFileName = Directory + "/" + serverFiles[i].getName();
                                        File file = new File(serverFileName);
//                                        os = new BufferedOutputStream(new FileOutputStream(file));
                                        OutputStream fileOS  = new FileOutputStream(new File(serverFileName));
                                        result = ftpClient.retrieveFile(serverFiles[i].getName(), fileOS);
                                        fileOS.close();
                                        if (result) {
                                            Log.v("FTP FileDownload", "Successed: " + serverFileName);
                                        }
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "There's " + (serverFiles.length-localFiles.length) + " new serverFile(s).", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    //Upload File.
                                }else if(serverFiles.length < localFiles.length){
                                    Log.v(TAG, "FTP LocalFile ADDed: " + localFiles[localFiles.length-1]);
                                    for(i=serverFiles.length ; i< localFiles.length ; i++) {
                                        FileInputStream fileIS = new FileInputStream(localFiles[i]);
                                        String storeFileName = localFiles[i].getName();
                                        result = ftpClient.storeFile(storeFileName, fileIS);
                                        fileIS.close();
                                        if (result) {
                                            Log.v("FTP FileUpload", "Successed: " + storeFileName);
                                        }
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "There's " + (localFiles.length-serverFiles.length) + " new localFile(s).", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                Log.v(TAG, " ");

                                ftpClient.logout();
                                ftpClient.disconnect();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "FTP Failed" + "_ IOException");
                        }
                        Thread.sleep(15000);
                        Thread.interrupted();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "FTP Failed" + "_ InterruptedException");
                    }
                }
            }
        }).start();

    }

    private void initElements() {
        stateCheck = new StateCheck(this);
        audioRecord = new AudioRecord();
//        // Galaxy A7 - E4:58:E7:64:9C:34
//        // UMChang - BC:CF:CC:66:28:D8
    }

    private void initUIElements() {
        // How to change FloatingActionButton between Tabs?:
        // http://stackoverflow.com/questions/31415742/how-to-change-floatingactionbutton-between-tabs
        fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(this);
        fab.setOnLongClickListener(this);

        // Material Design Patterns 教學 (4) - RecyclerView:
        // http://blog.30sparks.com/material-design-4-recyclerview/
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mContactAdapter = new ContactsAdapter(Contact.generateSampleList( audioRecord.getStrPathName() ));
        mRecyclerView.setAdapter(mContactAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        isRadioRunning = stateCheck.isRadioServiceRunning(RadioService.class);
        if (!isRadioRunning) {
            fab.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.stop));
            Snackbar.make(v, "Radio ON!", Snackbar.LENGTH_LONG).setAction("Undo", null).show();

            startService(new Intent(MainActivity.this, RadioService.class));
        }// 這部分容易還沒完全啟動就按終止、導致關閉無效。
        else {
            fab.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.radio));
            Snackbar.make(v, "Radio OFF!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            stopService(new Intent(MainActivity.this, RadioService.class));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // Android Design Support Library（3）- Snackbar的使用:
        // http://blog.csdn.net/leejizhou/article/details/50513833
        isRadioRunning = stateCheck.isRadioServiceRunning(RadioService.class);
        if(isRadioRunning)
            stopService(new Intent(MainActivity.this, RadioService.class));

        fab.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.record));
        isBtnLongPressed = true;
        audioRecord.onRecord(true);
        final Snackbar skBar = Snackbar.make(v, "Recording...", Snackbar.LENGTH_INDEFINITE);

        skBar.setActionTextColor(Color.RED).setAction("Save", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.radio));
                audioRecord.onRecord(false);
                skBar.dismiss();
            }
        }).show();
        return true;
        // Android onTouchEvent, onClick及onLongClick的调用机制:
        // http://blog.csdn.net/ddna/article/details/5451722
    }

    public void releaseMediaPlayer(MediaPlayer mp) {
        mp.stop();
        mp.reset();
        mp.release();
    }

    public boolean playRecorded(final String audioName){
        String path = audioRecord.getStrPathName() + audioName;
        boolean isSuccess = false;
        File file = new File(path);

        if (file.exists()) {
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(path);
                mp.prepare();
                mp.start();
                Thread.sleep(mp.getDuration() + 1000);
                Thread.interrupted();
                Log.i(TAG, "Playing..." + path);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                    Log.i(TAG, "Played OVER..." + audioName);
                }
            });
        }
        return isSuccess;


    }
}