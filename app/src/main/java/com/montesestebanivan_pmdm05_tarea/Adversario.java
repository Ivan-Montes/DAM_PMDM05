package com.montesestebanivan_pmdm05_tarea;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Adversario {

    private float x ;
    private float y;
    private final Bitmap imagen;
    private final Point dimensiones;//Dimensiones de la pantalla

    float direccionVertical = 1;  //inicialmente hacia abajo
    float direccionHorizontal; //inicialmente derecha
    float velocidadInicial = 1;
    float nivel = 1;

    public Adversario(float x, float y, Bitmap imagen, Point dimensiones) {
        this.x = x;
        this.y = y;
        this.imagen = imagen;
        this.dimensiones = dimensiones;
        this.direccionHorizontal = (Math.random() >= 0.5)?1:-1;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getAnchoImagen(){
        return imagen.getWidth();
    }

    public int getAltoImagen(){
        return imagen.getHeight();
    }

    public void meMuevo(){

        float velocidadReal = (velocidadInicial+nivel)*dimensiones.y/1920;

        x += direccionHorizontal*velocidadReal;
        y += direccionVertical*velocidadReal;

        //Cambios de direcciones al llegar a los bordes de la pantalla
        if( x<= 0 && direccionHorizontal==-1)
            direccionHorizontal=1;
        if(x > dimensiones.x-imagen.getWidth() && direccionHorizontal==1)
            direccionHorizontal=-1;
        if(y>= dimensiones.y && direccionVertical ==1)
            direccionVertical=-1;
        if(y<=0 && direccionVertical==-1)
            direccionVertical=1;
    }

    public void meDibujo(Canvas c, Paint p){
            c.drawBitmap(imagen,x,y,p);
    }


}
