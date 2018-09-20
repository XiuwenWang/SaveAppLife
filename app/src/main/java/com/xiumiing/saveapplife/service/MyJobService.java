package com.xiumiing.saveapplife.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

/**
 * ----------BigGod be here!----------/
 * ***┏┓******┏┓*********
 * *┏━┛┻━━━━━━┛┻━━┓*******
 * *┃             ┃*******
 * *┃     ━━━     ┃*******
 * *┃             ┃*******
 * *┃  ━┳┛   ┗┳━  ┃*******
 * *┃             ┃*******
 * *┃     ━┻━     ┃*******
 * *┃             ┃*******
 * *┗━━━┓     ┏━━━┛*******
 * *****┃     ┃神兽保佑*****
 * *****┃     ┃代码无BUG！***
 * *****┃     ┗━━━━━━━━┓*****
 * *****┃              ┣┓****
 * *****┃              ┏┛****
 * *****┗━┓┓┏━━━━┳┓┏━━━┛*****
 * *******┃┫┫****┃┫┫********
 * *******┗┻┛****┗┻┛*********
 * ━━━━━━神兽出没━━━━━━
 * 版权所有：个人
 * 作者：Created by a.wen.
 * 创建时间：2018/8/26
 * Email：13872829574@163.com
 * 内容描述：
 * 修改人：a.wen
 * 修改时间：${DATA}
 * 修改备注：
 * 修订历史：1.0
 */
public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("onStartJob", params.toString());
        try {
            Log.d("JobSchedulerService", "start~~" + System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //判断保活的service是否被杀死
                if (!isMyServiceRunning(RemoteService.class)) {
                    //重启service
                    startService(new Intent(this, ForegroundService.class));
                }
                //创建一个新的JobScheduler任务
                scheduleRefresh();
                jobFinished(params, false);
                Log.d("JobSchedulerService", "7.0 handleMessage task end~~" + System.currentTimeMillis());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
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

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("onStopJob", params.toString());
        return false;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

//    public void startJobScheduler() {
//        JobScheduler mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        if (DEBUG) {
//            Log.i(TAG, "startJobScheduler");
//        }
//        int id = JOB_ID;
//        if (DEBUG) {
//            Log.i(TAG, "开启AstJobService id=" + id);
//        }
//        mJobScheduler.cancel(id);
//        JobInfo.Builder builder = new JobInfo.Builder(id, new ComponentName(getApplication(), JobService.class));
//        if (Build.VERSION.SDK_INT >= 24) {
//            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS); //执行的最小延迟时间
//            builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);  //执行的最长延时时间
//            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
//            builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
//        } else {
//            builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
//        }
//        builder.setPersisted(true);  // 设置设备重启时，执行该任务
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        builder.setRequiresCharging(true); // 当插入充电器，执行该任务
//        JobInfo info = builder.build();
//        mJobScheduler.schedule(info); //开始定时执行该系统任务
//    }

}
