package com.xiumiing.saveapplife;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xiumiing.saveapplife.service.ForegroundService;
import com.xiumiing.saveapplife.service.MyJobService;
import com.xiumiing.saveapplife.service.RemoteService;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_open_ForegroundService)
    Button mBtnOpenForegroundService;
    @BindView(R.id.btn_open_remoteService)
    Button mBtnOpenRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



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

    @OnClick({R.id.btn_open_ForegroundService, R.id.btn_stop_ForegroundService,
            R.id.btn_open_remoteService, R.id.btn_stop_remoteService})
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
        }
    }
}
