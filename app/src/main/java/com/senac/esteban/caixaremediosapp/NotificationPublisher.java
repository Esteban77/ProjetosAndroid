package com.senac.esteban.caixaremediosapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;

/**
 * Created by vita on 25-11-2016.
 */
public class NotificationPublisher extends BroadcastReceiver{
    Dao<Remedio,Integer> remedioDao;
    Remedio remedio;
    Boolean resultado;
    Intent notificationIntent;
    public static String NOTIFICATION_ID = "notification-id";
//    public static int NOTIFICATION_ID_VALUE = 199;
    public static String NOTIFICATION = "notification";
    AlarmManager alarmManager;
    Context c;

    @Override
    public void onReceive(Context context, Intent intent) {
        int acao = intent.getExtras().getInt("action");
//        notificationIntent = intent;
        c = context;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Mensagem rápida
        Toast.makeText(context, "Programa está aberto!\n"+acao, Toast.LENGTH_SHORT).show();

        switch (acao){
            case Constantes.BRODCAST_NOTIFICAR:
                //Gerar notificação
                Notification notification = intent.getParcelableExtra(NOTIFICATION);
                int id = intent.getIntExtra(NOTIFICATION_ID, 0);
                int idF = intent.getIntExtra("idmo", 0);
                int dela = intent.getIntExtra("delay",0);

                notificationManager.notify(idF, notification);

                try {
                    remedioDao = MyORMLiteHelper.getIntance(c).getRemedioDao();
                    remedio = remedioDao.queryForId(id);
                    remedio.setContaNot(remedio.getContaNot()-1);
                    remedioDao.createOrUpdate(remedio);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Integer qtDos = remedio.getQtDoses();
                Integer idmo = Integer.parseInt(idF+""+qtDos);
                if(remedio.getContaNot()>0){
                    notificationIntent = new Intent(context, NotificationPublisher.class);
                    scheduleNotification(getNotification(remedio.getNome() +" "+ c.getString( R.string.txt_Dose) + remedio.getDose() + " " + remedio.getTipoDose(), remedio.getId(),idmo),dela,id,idmo);
                }

                //id da notificacao

//                scheduleNotification(notification,dela,id,idF,qtDos);

                break;

            case Constantes.BRODCAST_EXECUTAR_ACAO:

                Toast.makeText(context, "Ação realizada", Toast.LENGTH_SHORT).show();
                int id1 = intent.getIntExtra(NOTIFICATION_ID, 0);
                int id2 = intent.getIntExtra("idmo", 0);
                resultado = modificarDose(id1,context);
                notificationManager.cancel(id2);
 //               if(resultado==true) {
                    //Testar
                    AlarmManager alarmManager4 = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
                    Intent I = new Intent(context, NotificationPublisher.class);
//                    String acti = intent.getAction();
//                    I.setAction(String.valueOf(id2));
                    PendingIntent P = PendingIntent.getBroadcast(context, id2, I, PendingIntent.FLAG_ONE_SHOT);
      //              PendingIntent P = PendingIntent.getBroadcast(context, Constantes.BRODCAST_NOTIFICAR, I, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager4.cancel(P);

                    P.cancel();
//                }
                break;

            case Constantes.BRODCAST_INICIAR_APP:
                //Se coloco o id remedio como request code na pendind intent no inicia a app
                Intent it = new Intent(context, PrincipalActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
                break;
        }
    }
//Se a quantidade de doses tomadas é igual a quantidade de doses a tomar... entao cancela a notification (pela id)
     private boolean modificarDose(Integer idRemedio, Context context){
        boolean res = false;
        try {
            remedioDao = MyORMLiteHelper.getIntance(context).getRemedioDao();
            remedio = remedioDao.queryForId(idRemedio);
            remedio.setDosesContar(remedio.getDosesContar()-1);
            remedioDao.createOrUpdate(remedio);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(remedio.getDosesContar()==0){
            res=true;
        }
        return res;
    }


    private void scheduleNotification(Notification notification, int delay,Integer idRemedio,int idmo) {
        notificationIntent.putExtra("idmo", idmo);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, idRemedio);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra("action", Constantes.BRODCAST_NOTIFICAR);
        String acti = notificationIntent.getAction();
        notificationIntent.putExtra("delay",delay);
        notificationIntent.setAction(String.valueOf(idmo));

        //id do Pending Intent (request code) colocar Id do Remedio
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, idmo, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        //      PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constantes.BRODCAST_NOTIFICAR, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime()+delay;
        alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,futureInMillis, pendingIntent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private Notification getNotification(String content,Integer idRemedio,Integer idmo) {
        Notification.Builder builder = new Notification.Builder(c);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.ic_dialog_dialer);

        notificationIntent.setAction(String.valueOf(idmo));
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, idRemedio);
        notificationIntent.putExtra("idmo", idmo);
        notificationIntent.putExtra("action", Constantes.BRODCAST_INICIAR_APP);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(c, Constantes.BRODCAST_INICIAR_APP, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationIntent.putExtra("action", Constantes.BRODCAST_EXECUTAR_ACAO);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(c, Constantes.BRODCAST_EXECUTAR_ACAO, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_menu_add, c.getString(R.string.bt_AbrirApp), pendingIntent2);
        builder.addAction(android.R.drawable.ic_menu_add, c.getString(R.string.bt_Tomei), pendingIntent3);
        builder.setAutoCancel(true);

        return builder.build();
    }
}
