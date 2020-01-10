package com.appsbygreatness.ideasappformobiledevelopers.database;


import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import androidx.annotation.NonNull;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors instance;
    private Executor diskIO;
    private Executor mainThread;
    private Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread){

        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;
    }

    public  static AppExecutors getInstance(){

        if(instance == null){
            synchronized (LOCK){

                instance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }

        }
        return instance;
    }

    public Executor getDiskIO(){
        return diskIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor{

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {

            mainThreadHandler.post(runnable);
        }
    }
}
