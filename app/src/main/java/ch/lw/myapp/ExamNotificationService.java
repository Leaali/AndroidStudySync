package ch.lw.myapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.lw.myapp.db.DbHelper;

public class ExamNotificationService extends Service {
    //ehemalig von https://android-er.blogspot.com/2011/04/start-service-to-send-notification.html?m=1 aber viel hinzugefügt und angepast
    private static final String CHANNEL_ID = "defaultChannel";
    private static final int NOTIFICATION_ID = 0;

    private NotificationManagerCompat notificationManager;
    private Handler handler;
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = NotificationManagerCompat.from(this);
        dbHelper = new DbHelper(this);

        createNotificationChannel();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    sendNotificationForUpcomingExams();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.sendEmptyMessage(0);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Test", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotificationForUpcomingExams() throws ParseException {
        List<String> upcomingExams = findUpcomingExams();

        if (!upcomingExams.isEmpty()) {
            StringBuilder message = new StringBuilder("Erinnerung: Prüfungen in den nächsten buup 3 Tagen\n Prüfung: ");
            for (String exam : upcomingExams) {
                message.append("- ").append(exam).append("\n");
            }
            sendNotification(message.toString());
        }
    }

    private List<String> findUpcomingExams() throws ParseException {
        List<String> upcomingExams = new ArrayList<>();
        Cursor cursor = dbHelper.readAllExam();
        if (cursor != null) {
            try {
                Date currentDate = new Date();
                Date threeDaysLaterDate = new Date(currentDate.getTime() + 3 * 24 * 60 * 60 * 1000);

                while (cursor.moveToNext()) {
                    String examDateString = cursor.getString(cursor.getColumnIndexOrThrow("exam_date"));
                    String examSubject = cursor.getString(cursor.getColumnIndexOrThrow("exam_subject"));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    Date examDate = dateFormat.parse(examDateString);

                    if (examDate.after(currentDate) && examDate.before(threeDaysLaterDate)) {
                        upcomingExams.add(examSubject);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return upcomingExams;
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Prüfungserinnerung")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());//TODO check permission
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
