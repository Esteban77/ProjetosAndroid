package com.senac.esteban.caixaremediosapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CadastrarRemedios extends AppCompatActivity {
    Intent notificationIntent;
    List<Remedio> listRemedio;
    ArrayAdapter<Remedio> adRemedios;
    Dao<Remedio,Integer> remedioDao;
    Remedio remedio;
    EditText edNome, edTipoDose, edDose, edPeriodo, edQtdDose, edHora, edMinutos;
    ListView listViewRemedios;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_remedios);
        listRemedio = new ArrayList<>();
        remedio = new Remedio();

        listViewRemedios = (ListView) findViewById(R.id.listRemedios);
        edNome = (EditText) findViewById(R.id.editNomeRemedio);
        edTipoDose = (EditText) findViewById(R.id.editTipoDose);
        edDose = (EditText) findViewById(R.id.editDose);
        edPeriodo = (EditText) findViewById(R.id.editPeriodo);
        edQtdDose = (EditText) findViewById(R.id.editQtdDose);
        edHora = (EditText) findViewById(R.id.editHora);
        edMinutos = (EditText) findViewById(R.id.editMinutos);

        try {
            remedioDao = MyORMLiteHelper.getIntance(this).getRemedioDao();
            listRemedio = remedioDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adRemedios = new ArrayAdapter<Remedio>(this, android.R.layout.simple_list_item_1, listRemedio);
        listViewRemedios.setAdapter(adRemedios);

        listViewRemedios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                remedio = adRemedios.getItem(position);
                edNome.setText(remedio.getNome());
                edTipoDose.setText(remedio.getTipoDose());
                edDose.setText(String.valueOf(remedio.getDose()));
                edPeriodo.setText(String.valueOf(remedio.getTempoLembrar()));
                edQtdDose.setText(String.valueOf(remedio.getQtDoses()));
                edHora.setText(String.valueOf(remedio.getHoraInicio()));
                edMinutos.setText(String.valueOf(remedio.getMinutosInicio()));
            }
        });

        listViewRemedios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                remedio = adRemedios.getItem(position);
                try {
                    remedioDao.delete(remedio);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adRemedios.remove(remedio);
                adRemedios.notifyDataSetChanged();
                return true;
            }
        });
    }

    public void salvarRemedio(View v){
        String horaP = edHora.getText().toString();
        String minutosP = edMinutos.getText().toString();
        String tempoLembrarP = edPeriodo.getText().toString();
        String nomeP = edNome.getText().toString();
        String tipoDoseP = edTipoDose.getText().toString();
        String doseP = edDose.getText().toString();
        String qtdDosesP = edQtdDose.getText().toString();

        if(TextUtils.isEmpty(horaP) || TextUtils.isEmpty(minutosP) || TextUtils.isEmpty(tempoLembrarP) ||
                TextUtils.isEmpty(nomeP) || TextUtils.isEmpty(tipoDoseP) || TextUtils.isEmpty(doseP)
                || TextUtils.isEmpty(qtdDosesP)){
            Toast.makeText(CadastrarRemedios.this, "Edite todos os Campos", Toast.LENGTH_LONG).show();
        }else {
            int hora = Integer.parseInt(edHora.getText().toString());
            int minutos = Integer.parseInt(edMinutos.getText().toString());
            Double tempoLembrar = Double.valueOf(edPeriodo.getText().toString());
            int tempoLembrarMilis = (int) (tempoLembrar * 60 * 1000 * 60);
            String nome = edNome.getText().toString();
            String tipoDose = edTipoDose.getText().toString();
            Double dose = Double.valueOf(edDose.getText().toString());
            Integer qtdDoses = Integer.valueOf(edQtdDose.getText().toString());

        /* Set the alarm to start at 10:30 AM */
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hora);
            calendar.set(Calendar.MINUTE, minutos);

            remedio.setNome(nome);
            remedio.setTipoDose(tipoDose);
            remedio.setDose(dose);
            remedio.setTempoLembrar(tempoLembrar);
            remedio.setQtDoses(qtdDoses);
            remedio.setHoraInicio(hora);
            remedio.setMinutosInicio(minutos);
            remedio.setDosesContar(qtdDoses);
            remedio.setContaNot(qtdDoses);

            Toast.makeText(CadastrarRemedios.this, remedio.getNome(), Toast.LENGTH_SHORT).show();

            try {
                remedioDao.createOrUpdate(remedio);
                adRemedios = new ArrayAdapter<Remedio>(this, android.R.layout.simple_list_item_1, remedioDao.queryForAll());
                listViewRemedios.setAdapter(adRemedios);

                //Chama a notificacao na hora de cadastrar um remedio

                notificationIntent = new Intent(this, NotificationPublisher.class);
                scheduleNotification(getNotification(nome +" "+ getString( R.string.txt_Dose) + dose + " " + tipoDose, remedio.getId(),qtdDoses), tempoLembrarMilis, calendar.getTimeInMillis(), remedio.getId(),qtdDoses);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            remedio = new Remedio();
        }
    }

 /*   private void scheduleNotification(Notification notification, int delay, Long starAlarm, Integer idRemedio,Integer dose) {

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, idRemedio);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra("action", Constantes.BRODCAST_NOTIFICAR);
        String acti = notificationIntent.getAction();
        notificationIntent.putExtra("idmo",idRemedio);
        notificationIntent.putExtra("dose",dose);
                                                                            //id do Pending Intent (request code) colocar Id do Remedio
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idRemedio, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constantes.BRODCAST_NOTIFICAR, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = delay;
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, starAlarm,futureInMillis, pendingIntent);
    }*/

   private void scheduleNotification(Notification notification, int delay, Long starAlarm, Integer idRemedio,Integer dose) {

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, idRemedio);
        notificationIntent.putExtra("idmo",idRemedio);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        notificationIntent.putExtra("action", Constantes.BRODCAST_NOTIFICAR);
        notificationIntent.putExtra("delay",delay);
        notificationIntent.putExtra("dose",dose);

//        String acti = notificationIntent.getAction();
        //id do Pending Intent (request code) colocar Id do Remedio
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, idRemedio, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        //      PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constantes.BRODCAST_NOTIFICAR, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime();
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,futureInMillis, pendingIntent);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private Notification getNotification(String content,Integer idRemedio,Integer qtdDose) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.ic_dialog_dialer);
        notificationIntent.putExtra("idmo",idRemedio);
        notificationIntent.setAction(String.valueOf(idRemedio));
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, idRemedio);

        notificationIntent.putExtra("action", Constantes.BRODCAST_INICIAR_APP);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, Constantes.BRODCAST_INICIAR_APP, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationIntent.putExtra("action", Constantes.BRODCAST_EXECUTAR_ACAO);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, Constantes.BRODCAST_EXECUTAR_ACAO, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(android.R.drawable.ic_menu_add, getString(R.string.bt_AbrirApp), pendingIntent2);
        builder.addAction(android.R.drawable.ic_menu_add, getString(R.string.bt_Tomei), pendingIntent3);
        builder.setAutoCancel(true);


        return builder.build();
    }
}
