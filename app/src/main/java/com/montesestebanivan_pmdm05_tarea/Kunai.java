package com.montesestebanivan_pmdm05_tarea;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Kunai {

    private final float x;
    private float y;
    private final Bitmap imagen;
    private final float velocidad;


    public Kunai(float x, float y, Bitmap imagen, float velocidad) {
        this.x = x;
        this.y = y;
        this.imagen = imagen;
        this.velocidad = velocidad;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void meMuevo(){
        y -= velocidad;
    }

    public void meDibujo(Canvas c, Paint p) {
        c.drawBitmap(imagen, x, y, p);
    }

    public int getAnchoImagen(){
        return imagen.getWidth();
    }

    public int getAltoImagen(){
        return imagen.getHeight();
    }

    public boolean estoyFueraDelMar() {
        return y < 0;
    }
}
