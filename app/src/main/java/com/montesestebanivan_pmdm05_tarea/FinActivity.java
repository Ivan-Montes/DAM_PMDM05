package com.montesestebanivan_pmdm05_tarea;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin);
        int codResultado = 0;
        if ( getIntent().hasExtra("COD_RESULTADO") ){
            codResultado = getIntent().getIntExtra("COD_RESULTADO",0);
        }
        mensajeFinJuego(codResultado);
        configurarEventosBotones();
        reConfigurarActionBar();
        musicaEnding();
        animaciones();

    }

    private void reConfigurarActionBar(){

        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayUseLogoEnabled(true);
            ab.setLogo(R.mipmap.ic_launcher_pulpifondo_round);String tituloBarra = "";
            if (ab.getTitle() != null) {
                tituloBarra = ab.getTitle().toString();
            }
            //Cambiamos color vía HTML
            ab.setTitle(Html.fromHtml("<font color='#000000'>"
                    + tituloBarra + "</font>", 1));
        }
    }

    private void mensajeFinJuego(int codResultado){

        TextView tvmsg = findViewById(R.id.tvMensajeFinal);
        tvmsg.setTypeface(getResources().getFont(R.font.chinesetakeaway));
        switch (codResultado){
            case 1:
                tvmsg.setText(R.string.msg_final_v);
                break;
            case 2:
                tvmsg.setText(R.string.msg_final_d);
                break;
            default:
                tvmsg.setText(R.string.msg_final_e);
                break;
        }
    }

    private void configurarEventosBotones(){

        Button btReintentar = findViewById(R.id.btReintentar);
        btReintentar.setOnClickListener(v -> reintentarJuego());

        Button btVolverMenuInicio = findViewById(R.id.btVolverInicio);
        btVolverMenuInicio.setOnClickListener(v -> volverMenuInicio());

    }

    public void musicaEnding(){

        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(this, R.raw.victory);
        mediaPlayer.setVolume(0.4f,0.4f);
        mediaPlayer.setOnCompletionListener(mp -> {});
        mediaPlayer.start();
    }

    private void reintentarJuego(){
        startActivity(new Intent(getApplicationContext(),LanzadorJuegoActivity.class));
    }

    private void volverMenuInicio(){
        startActivity(new Intent(getApplicationContext(),InicioActivity.class));
    }

    private void animaciones(){
        TranslateAnimation animacionRebote = new TranslateAnimation(0, 0,0, 25);
        animacionRebote.setDuration(1500);
        animacionRebote.setRepeatCount(Animation.INFINITE);
        animacionRebote.setRepeatMode(Animation.REVERSE);
        ImageView ivFamilia = findViewById(R.id.ivFamilia);
        ivFamilia.startAnimation(animacionRebote);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),InicioActivity.class));
    }

}
