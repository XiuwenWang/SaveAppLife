package com.xiumiing.saveapplife.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class RemoteService extends Service {
    private static final String TAG = RemoteService.class.getSimpleName();
    public RemoteService() {
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //需要一个int类型作为jobInfo的id，这标记以便可以取消这个任务。
        int jobId = 1;
        //任务回调道MyJobService，继承JobService
        ComponentName componentName = new ComponentName(this,MyJobService.class);
        JobInfo.Builder jobBuilder = new JobInfo.Builder(jobId,componentName);
        // 设置重复周期为5秒一次
        jobBuilder.setPeriodic(TimeUnit.SECONDS.toMillis(5L));
        //设置为重复的任务
        jobBuilder.setPersisted(true);
        //设置为在idle模式下有效
        jobBuilder.setRequiresDeviceIdle(true);
        //可以给回调的任务传递参数
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("tag","hello");
        jobBuilder.setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobBuilder.build());
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }
}
