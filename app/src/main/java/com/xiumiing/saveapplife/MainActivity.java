package com.xiumiing.saveapplife;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.evernote.android.job.JobRequest;
import com.xiumiing.saveapplife.service.ForegroundService;
import com.xiumiing.saveapplife.service.MyJobService;
import com.xiumiing.saveapplife.service.RemoteService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xiumiing.saveapplife.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.btn_open_ForegroundService)
    Button mBtnOpenForegroundService;
    @BindView(R.id.btn_open_remoteService)
    Button mBtnOpenRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        scheduleRefresh();

//
//        //需要一个int类型作为jobInfo的id，这标记以便可以取消这个任务。
//        int jobId = 1;
//        //任务回调道MyJobService，继承JobService
//        ComponentName componentName = new ComponentName(this, MyJobService.class);
//        JobInfo.Builder jobBuilder = new JobInfo.Builder(jobId, componentName);
//        // 设置重复周期为5秒一次
//        jobBuilder.setPeriodic(TimeUnit.SECONDS.toMillis(5L));
//        //设置为重复的任务
//        jobBuilder.setPersisted(true);
//        //设置为在idle模式下有效
//        jobBuilder.setRequiresDeviceIdle(true);
//        //可以给回调的任务传递参数
//        PersistableBundle bundle = new PersistableBundle();
//        bundle.putString("tag", "hello");
//        jobBuilder.setExtras(bundle);
//
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(jobBuilder.build());
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        long time = 2000;
//        PendingIntent pending = PendingIntent.getBroadcast(MainActivity.this,1
//                ,new Intent("com.arjinmc.test"),PendingIntent.FLAG_UPDATE_CURRENT);
//        if (Build.VERSION.SDK_INT >= 23) {
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time,
//                    pending);
//        } else if (Build.VERSION.SDK_INT >= 19) {
//            am.setExact(AlarmManager.RTC_WAKEUP, time, pending);
//        } else {
//            am.set(AlarmManager.RTC_WAKEUP, time, pending);
//        }
//        am.setRepeating(AlarmManager.RTC_WAKEUP,1000l,1000l,pending);

    }

    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        //jobId可根据实际情况设定
        JobInfo.Builder mJobBuilder = new JobInfo.Builder(0, new ComponentName(getPackageName(), MyJobService.class.getName()));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder.setMinimumLatency(5 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            PersistableBundle persiBundle = new PersistableBundle();
            mJobBuilder.setExtras(persiBundle);
        }

        if (mJobScheduler != null && mJobScheduler.schedule(mJobBuilder.build())
                <= JobScheduler.RESULT_FAILURE) {
            //Scheduled Failed/LOG or run fail safe measures
            Log.d("JobSchedulerService", "7.0 Unable to schedule the service FAILURE!");
        } else {
            Log.d("JobSchedulerService", "7.0 schedule the service SUCCESS!");
        }
    }
    public void startJobScheduler() {
        JobScheduler mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (DEBUG) {
            Log.i(TAG, "startJobScheduler");
        }
        int id = 0;
        if (DEBUG) {
            Log.i(TAG, "开启AstJobService id=" + id);
        }
        mJobScheduler.cancel(id);
        JobInfo.Builder builder = new JobInfo.Builder(id, new ComponentName(this, MyJobService.class));
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS); //执行的最小延迟时间
            builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);  //执行的最长延时时间
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
        } else {
            builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
        }
        builder.setPersisted(true);  // 设置设备重启时，执行该任务
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(true); // 当插入充电器，执行该任务
        JobInfo info = builder.build();
        mJobScheduler.schedule(info); //开始定时执行该系统任务

    }
    @OnClick({R.id.btn_open_ForegroundService, R.id.btn_stop_ForegroundService,
            R.id.btn_open_remoteService, R.id.btn_stop_remoteService,R.id.job})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open_ForegroundService:
                startService(new Intent(this, ForegroundService.class));
                break;
            case R.id.btn_stop_ForegroundService:
                stopService(new Intent(this, ForegroundService.class));
                break;
            case R.id.btn_open_remoteService:
                startService(new Intent(this, RemoteService.class));
                break;
            case R.id.btn_stop_remoteService:
                stopService(new Intent(this, RemoteService.class));
                break;
            case R.id.job:
                new JobRequest.Builder(DemoSyncJob.TAG)
                        .setExecutionWindow(30_000L, 40_000L)
                        .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .setRequirementsEnforced(true)
                        .setPeriodic(900L)
                        .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                        .build()
                        .schedule();
                break;
        }
    }
}
