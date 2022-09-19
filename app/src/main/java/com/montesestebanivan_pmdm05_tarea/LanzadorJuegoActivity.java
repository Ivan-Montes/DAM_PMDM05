package com.montesestebanivan_pmdm05_tarea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class LanzadorJuegoActivity extends AppCompatActivity {

    private Juego juego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_lanzador_juego);

        juego=new Juego(this);
        hideSystemUI();
        setContentView(juego);
    }

    private void hideSystemUI() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            //A partir de kitkat
            juego.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            //cuando se presiona volumen, por ejemplo, se cambia la visibilidad, hay que volver
            //a ocultar
            juego.setOnSystemUiVisibilityChangeListener(visibility -> hideSystemUI());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Pre-Jelly Bean, we must manually hide the action bar
            // and use the old window flags API.
            getActionBar().hide();
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        juego.finBucleAndLiberarRecursos();
        super.onDestroy();
    }

}