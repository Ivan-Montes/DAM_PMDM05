package com.montesestebanivan_pmdm05_tarea;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BotonAccion {

    private float x;
    private float y;
    private Bitmap imagen;
    private String nombre;
    private boolean pulsado;
    private final Context context;

    public void setPulsado(boolean pulsado) {
        this.pulsado = pulsado;
    }

    public String getNombre() {
        return nombre;
    }


    public BotonAccion(Context c){
        this.context = c;
    }

    public BotonAccion(Context c, float x, float y){
        this.x = x;
        this.y = y;
        this.context = c;
    }

    public void cargarImagen(int recurso){
        imagen = BitmapFactory.decodeResource(context.getResources(), recurso);
    }

    public void meDibujo(Canvas canvas, Paint paint) {
        canvas.drawBitmap(this.imagen, x, y, paint);
    }

    public void comprueboSiHeSidoPulsado(float x, float y) {
        if (x > this.x && x < this.x + getAnchoImagen()
                        && y > this.y
                        && y < this.y + getAltoImagen()) {

            pulsado = true;
        }
    }

    public Bitmap getImagen(){
        return imagen;
    }

    public void setImagen(Bitmap imagen){ this.imagen = imagen;}

    public int getAnchoImagen(){
        return imagen.getWidth();
    }

    public int getAltoImagen(){
        return imagen.getHeight();
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public boolean getPulsado(){
        return this.pulsado;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}//Fin
