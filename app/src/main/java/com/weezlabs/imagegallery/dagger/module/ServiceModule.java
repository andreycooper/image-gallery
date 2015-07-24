package com.weezlabs.imagegallery.dagger.module;

import android.content.Context;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import com.weezlabs.imagegallery.dagger.scope.PerActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @PerActivity
    @Provides
    public JobManager provideJobManager(Context context) {
        Configuration configuration = new Configuration.Builder(context)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)    //always keep at least one consumer alive
                .maxConsumerCount(3)    //up to 3 consumers at a time
                .loadFactor(3)          //3 jobs per consumer
                .consumerKeepAlive(60) //wait 2 minute
                .build();
        return new JobManager(context, configuration);
    }
}
