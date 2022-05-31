package com.montesestebanivan_pmdm05_tarea;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reConfigurarActionBar();
        configurarEventosPantallaInicial();
        musicaOpening();
        animaciones();

    }

    private void reConfigurarActionBar(){

        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayUseLogoEnabled(true);
            ab.setLogo(R.mipmap.ic_launcher_pulpifondo_round);
            String tituloBarra = "";
            if (ab.getTitle() != null) {
                tituloBarra = ab.getTitle().toString();
            }
            //Cambiamos color vía HTML
            ab.setTitle(Html.fromHtml("<font color='#000000'>"
                    + tituloBarra + "</font>", 1));
        }
    }

    private void configurarEventosPantallaInicial(){

        Button btStart = findViewById(R.id.btStart);
        btStart.setOnClickListener(v -> iniciarJuego());

        Button btIntrucciones = findViewById(R.id.btInstrucciones);
        btIntrucciones.setOnClickListener(v -> mostrarInstrucciones());

    }

    private void iniciarJuego(){
        Intent inicioLanzadorDelJuego = new Intent(getApplicationContext(),LanzadorJuegoActivity.class);
        startActivity(inicioLanzadorDelJuego);
    }

    private void mostrarInstrucciones(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(R.string.instrucciones_txt)
                .setTitle(R.string.instrucciones_titulo)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                });
        AlertDialog d = b.create();
        d.show();

    }


    public void musicaOpening(){

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(this, R.raw.freeflowflava_final_round);
        mediaPlayer.setVolume(0.6f,0.6f);
        mediaPlayer.setOnCompletionListener(MediaPlayer::start);
        mediaPlayer.start();
    }

    private void animaciones(){
        TranslateAnimation animacionRebote =
                new TranslateAnimation(0, 0,0, 25);
        animacionRebote.setDuration(1500);
        animacionRebote.setRepeatCount(Animation.INFINITE);
        animacionRebote.setRepeatMode(Animation.REVERSE);
        ImageView vistaAnimada = findViewById(R.id.ivLetreroPulpo);
        vistaAnimada.startAnimation(animacionRebote);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        //int altoPantalla = metrics.heightPixels; //No incluye la barra
        ImageView pulpi1 = findViewById(R.id.ivPulpi);
        ImageView pulpi2 =  findViewById(R.id.ivPulpi2);
        pulpi1.setVisibility(ImageView.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(-300, metrics.widthPixels + 300,-80, 200);
        animation.setDuration(10000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        pulpi1.startAnimation(animation);
        pulpi2.startAnimation(animation);

    }


}//Fin