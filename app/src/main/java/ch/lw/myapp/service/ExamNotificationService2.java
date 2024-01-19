package ch.lw.myapp.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ch.lw.myapp.db.DbHelper;

public class ExamNotificationService2 extends JobService {
    private NotificationManagerCompat notificationManager;
    private Handler handler;
    private HandlerThread handlerThread;
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = NotificationManagerCompat.from(this);
        dbHelper = new DbHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("defaultChannel", "Test", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        handlerThread = new HandlerThread("ExamNotificationHandlerThread");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                try {
                    dbHelper = new DbHelper(ExamNotificationService2.this);
                    notificationManager = NotificationManagerCompat.from(ExamNotificationService2.this);
                    sendNotificationForUpcomingExams();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                handler.sendEmptyMessageDelayed(0, 5 * 60 * 60 * 1000);
            }
        };
        scheduleJob();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.sendEmptyMessage(0);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        handler.sendEmptyMessage(0);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeCallbacksAndMessages(null);
        return true;
    }

    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, ExamNotificationService2.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(TimeUnit.MINUTES.toMillis(300))
                .setPersisted(true) // bleibt auch beu neustart vorhanden
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo);
        }
    }

    private void sendNotificationForUpcomingExams() throws ParseException {
        List<String> upcomingExams = findUpcomingExams();

        if (!upcomingExams.isEmpty()) {
            StringBuilder message = new StringBuilder("Erinnerung: Prüfungen in den nächsten 3 Tagen\n Prüfung: ");
            for (String exam : upcomingExams) {
                message.append("- ").append(exam).append("\n");
            }
            sendNotification(message.toString());
        }
    }

    private List<String> findUpcomingExams() throws ParseException {
        List<String> upcomingExams = new ArrayList<>();
        Cursor cursor = dbHelper.readAllExam();
        if (cursor != null && cursor.moveToFirst()) {
            Date currentDate = new Date();
            Date threeDaysLaterDate = new Date(currentDate.getTime() + 3 * 24 * 60 * 60 * 1000);
            do {
                String examDateString = cursor.getString(cursor.getColumnIndexOrThrow("exam_date"));
                String examSubject = cursor.getString(cursor.getColumnIndexOrThrow("exam_subject"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                try {
                    Date examDate = dateFormat.parse(examDateString);
                    if (examDate.after(currentDate) && examDate.before(threeDaysLaterDate)) {
                        upcomingExams.add(examSubject);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return upcomingExams;
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "defaultChannel")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Prüfungserinnerung")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());//TODO check permission
    }
}

