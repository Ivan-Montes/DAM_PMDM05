package com.montesestebanivan_pmdm05_tarea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Juego extends SurfaceView implements SurfaceHolder.Callback, SurfaceView.OnTouchListener {

    private final SurfaceHolder holder;
    private BucleJuego bucle;

    private int anchoPantalla = 0;
    private int altoPantalla = 0;
    private int movimientoLateral = 13;
    private int movimientoKunai = 13;

    private final List<BotonAccion> listaBotonAccion = new ArrayList<>();
    private List<Bitmap> listaImagenesPulposEscaladas = new ArrayList<>();
    private List<Adversario> listaPulpoAdversarios =new ArrayList<>();
    private List<Kunai> listaKunaisLanzados = new ArrayList<>();
    private List<Impacto> listaImpactos = new ArrayList<>();

    private BotonAccion cocinero = null;
    private Bitmap fondoEscalado = null;
    private Bitmap knifeEscalado = null;
    private MediaPlayer mediaPlayer = null;

    public final int tropasPulperas = 30;
    private final int adversariosMinuto = 75;
    private int framesHastaNuevoEnemigo = 0;
    private int numPulposAbatidos = 0;
    private int adversariosPulposCreados = 0;

    private int framesHastaNuevoDisparo = 0;
    private final int MAX_FRAMES_ENTRE_DISPARO = 20;
    private boolean nuevoDisparo=false;

    private boolean victoria = false;
    private boolean derrota = false;

    private final Function<Integer,Integer> dameNum = (limite)-> new Random().nextInt(limite);

    public Juego(Activity context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);

        calcularPantalla();
        prepararCocina();
        avisarCocinero();
        cargaControles();
        cargarImagenesPulperas();
        ponmeMusica();
        setOnTouchListener(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Aquí se crea la superficie
        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);
        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);
        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);
        //comenzar el bucle
        bucle.start();
    }

    private void calcularPantalla(){

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);//Incluye la barra, si no deja un hueco.
        anchoPantalla = size.x;
        altoPantalla = size.y;

    }


    private void prepararCocina(){
        Bitmap fondoResource = BitmapFactory.decodeResource(getResources(),R.drawable.mar_profundo);
        fondoEscalado = Bitmap.createScaledBitmap(fondoResource,anchoPantalla,altoPantalla,false);
        movimientoLateral = anchoPantalla*40/1080;
        movimientoKunai = anchoPantalla*30/1080;
        Bitmap knifeResource = BitmapFactory.decodeResource(getResources(), R.drawable.knife_slim);
        knifeEscalado = Bitmap.createScaledBitmap(knifeResource,anchoPantalla/25,altoPantalla/10,false);
    }

    /**
     * Instancia al cocinero usando la clase BotonAccion. Se asigna una ubicación por defecto
     */
    private void avisarCocinero(){
        cocinero = new BotonAccion(getContext());
        Bitmap imgCocinero = BitmapFactory.decodeResource(getResources(),R.drawable.sushichef2);
        Bitmap imgCocineroRedux = Bitmap.createScaledBitmap(imgCocinero,
                                                    imgCocinero.getWidth()/2,
                                                    imgCocinero.getHeight()/2,
                                                        false);
        cocinero.setImagen(imgCocineroRedux);
        cocinero.setX(  anchoPantalla/2-cocinero.getAnchoImagen()/2 );
       // cocinero.setY( altoPantalla/5 *4); //Imagen demasiado abajo en resolución de pruebas  1080 x 1920
        cocinero.setY( altoPantalla/1.35f);
    }

    private void cocinarCocinero(Canvas canvas){
        canvas.drawBitmap(cocinero.getImagen(),cocinero.getX(),cocinero.getY(),null);
    }

    /**
     * Utilizamos la clase BotonAccion para crear los botones con las acciones para mover al protagonista
     */
    private void cargaControles(){

        //BotonAccion izq = new BotonAccion( getContext(),0,altoPantalla/5*4+cocinero.getAltoImagen() );
        BotonAccion izq = new BotonAccion( getContext() );
        izq.cargarImagen( R.drawable.flecha_izda);
        izq.setNombre("IZQUIERDA");
        izq.setX(0f);
        izq.setY( altoPantalla - izq.getAltoImagen() );

        BotonAccion der  = new BotonAccion( getContext(),izq.getAnchoImagen()+izq.getX()+5,izq.getY() );
        der.cargarImagen(R.drawable.flecha_dcha);
        der.setNombre("DERECHA");

        //BotonAccion katon = new BotonAccion(getContext(),5.0f/7.0f*anchoPantalla,izq.getY());
        BotonAccion katon = new BotonAccion(getContext());
        katon.cargarImagen(R.drawable.katon);
        katon.setNombre("KATON");
        katon.setX( anchoPantalla - katon.getAnchoImagen() );
        katon.setY(izq.getY());

        listaBotonAccion.add(izq);
        listaBotonAccion.add(der);
        listaBotonAccion.add(katon);
    }

    private void cargarImagenesPulperas(){
        framesHastaNuevoEnemigo = BucleJuego.MAX_FPS*60/adversariosMinuto;
        //Lista de imagenes de enemigos escaladas
        List<Bitmap> listaImagenesPulpos = new ArrayList<>();
        listaImagenesPulpos.add(BitmapFactory.decodeResource(getResources(), R.drawable.calamar_retrato));
        listaImagenesPulpos.add(BitmapFactory.decodeResource(getResources(), R.drawable.octopus_adorable_18));
        listaImagenesPulpos.add(BitmapFactory.decodeResource(getResources(), R.drawable.octopus_cute_baby_octopus_3));

        listaImagenesPulposEscaladas = listaImagenesPulpos
                .stream()
                .map( b -> Bitmap.createScaledBitmap(b,anchoPantalla/7,anchoPantalla/7,false))
                .collect(Collectors.toList());
    }

    private void cocinarPulpo(){
        if( tropasPulperas - adversariosPulposCreados > 0) {
            listaPulpoAdversarios.add(new Adversario(dameNum.apply(anchoPantalla-anchoPantalla/5),
                                                                0,
                                                                listaImagenesPulposEscaladas.get(dameNum.apply(3)),
                                                                new Point(anchoPantalla,altoPantalla)));
            adversariosPulposCreados++;
        }
    }

    private void ponmeMusica(){

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.tengen_uzui_theme);
        mediaPlayer.setVolume(0.3f,0.3f);
        mediaPlayer.setOnCompletionListener(MediaPlayer::start);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x, y;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                x = event.getX();
                y = event.getY();
                synchronized (this) {

                    for (BotonAccion b : listaBotonAccion) {
                        b.comprueboSiHeSidoPulsado(x, y);
                    }
                }
                break;
        }
        return true;
    }

    private void cargaKunai(){
        listaKunaisLanzados.add(new Kunai(cocinero.getX(),
                                            cocinero.getY(),
                                            knifeEscalado,
                                            movimientoKunai));
    }

    //Compara las dimensiones y localización de dos elementos para comprobar si se cruzan
    private boolean hayUnInpacto(Kunai k, Adversario a){

        int altoMayor = Math.max(k.getAltoImagen(), a.getAltoImagen());
        int anchoMayor = Math.max(k.getAnchoImagen(), a.getAnchoImagen());
        float diferenciaX = Math.abs( k.getX() - a.getX() );
        float diferenciaY = Math.abs( k.getY() - a.getY() );
        return diferenciaX < anchoMayor - anchoMayor/3 && diferenciaY < altoMayor - altoMayor / 2;//Resto una cantidad para que el impacto requiera más acercamiento

    }

    public boolean impactoCocinero(Adversario a){

        int altoMayor = Math.max(cocinero.getAltoImagen(), a.getAltoImagen());
        int anchoMayor = Math.max(cocinero.getAnchoImagen(), a.getAnchoImagen());
        float diferenciaX = Math.abs( cocinero.getX() - a.getX() );
        float diferenciaY = Math.abs( cocinero.getY() - a.getY() );
        return diferenciaX < anchoMayor && diferenciaY < altoMayor - altoMayor/2;//Resto una cantidad para que el impacto requiera más acercamiento


    }

    private void comprobarVictoriaDerrota(){

        if( numPulposAbatidos == tropasPulperas){
            victoria = true;
        }

        for(Adversario a:listaPulpoAdversarios){
            if(impactoCocinero(a)){
                listaImpactos.add(new Impacto(a.getX(), a.getY()));
                derrota = true;
            }
        }
    }

    private void pintarMarcador(Canvas canvas, Paint pincel){

        pincel.setTextSize(anchoPantalla / 20);
        pincel.setStyle(Paint.Style.FILL);
        pincel.setColor(Color.WHITE);
        pincel.setAlpha(255);
        Typeface tipoFuente = getResources().getFont(R.font.chinesetakeaway);
        pincel.setTypeface(tipoFuente);
        canvas.drawText("Pulpos neutralizados : " + numPulposAbatidos,75, 75, pincel);

    }

    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */
    public void actualizar() {

        Optional<BotonAccion> optBa= listaBotonAccion
                .stream()
                .filter(BotonAccion::getPulsado)
                .findFirst();

        listaBotonAccion
                .stream()
                .filter(BotonAccion::getPulsado)
                .forEach( bt -> bt.setPulsado(false));

        if (optBa.isPresent()){

            switch (optBa.get().getNombre() ){

                case "IZQUIERDA":
                    if ( cocinero.getX() > 0 ){
                        cocinero.setX(cocinero.getX() - movimientoLateral);
                    }
                    break;
                case "DERECHA":
                    if (cocinero.getX() < anchoPantalla - cocinero.getAnchoImagen()){
                        cocinero.setX(cocinero.getX() + movimientoLateral);
                    }
                    break;
                case "KATON":
                    nuevoDisparo = true;
                    if (framesHastaNuevoDisparo <= 0) {
                        if (nuevoDisparo) {
                            cargaKunai();
                            nuevoDisparo = false;
                        }
                        //nuevo ciclo de disparos
                        framesHastaNuevoDisparo = MAX_FRAMES_ENTRE_DISPARO;
                    }
                    break;
            }
        }

        framesHastaNuevoDisparo--;
        listaKunaisLanzados
                .forEach(Kunai::meMuevo);

        listaKunaisLanzados.removeIf(Kunai::estoyFueraDelMar);

        if(framesHastaNuevoEnemigo==0){
            cocinarPulpo();
            //nuevo ciclo de enemigos
            framesHastaNuevoEnemigo = BucleJuego.MAX_FPS*60/adversariosMinuto;
        }
        framesHastaNuevoEnemigo--;

        listaPulpoAdversarios
                .forEach(Adversario::meMuevo);

        //Comprobamos si hay impacto contra pulpos
        List<Kunai> listaKunaisLanzadosParaEliminar = new ArrayList<>();
        List<Adversario> listaPulpoAdversariosParaEliminar = new ArrayList<>();

        for(Kunai kunai: listaKunaisLanzados){
            for(Adversario adver: listaPulpoAdversarios){
                if(hayUnInpacto(kunai,adver)){
                    //java.util.ConcurrentModificationException: Por ejemplo eliminar al usar listAny.remove(objeto) desde la propia iteración de listAny
                    listaKunaisLanzadosParaEliminar.add(kunai);
                    listaPulpoAdversariosParaEliminar.add(adver);
                    listaImpactos.add(new Impacto(adver.getX(),adver.getY()));
                    numPulposAbatidos++;
                }
            }
        }
        //Eliminamos de las listas de elementos activos
        listaKunaisLanzados.removeAll(listaKunaisLanzadosParaEliminar);
        listaPulpoAdversarios.removeAll(listaPulpoAdversariosParaEliminar);
        listaImpactos.removeIf( i -> i.animacionFinalizada() );
        List<Impacto> listaImpactosEncontrados = listaImpactos
                                                .stream()
                                                .peek( i -> i.avanzaAnimacion())
                                                .filter(Impacto::animacionFinalizada)
                                                .collect(Collectors.toList());
        listaImpactos.removeAll(listaImpactosEncontrados);

        if(!derrota && !victoria){comprobarVictoriaDerrota();}

    }//Fin actualizar()

    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas) {

        if (canvas != null) {

            Paint pincel = new Paint();
            pincel.setStyle(Paint.Style.STROKE);
            pincel.setColor(Color.WHITE);

            canvas.drawBitmap(fondoEscalado, 0, -1, null);
            cocinarCocinero(canvas);

            listaPulpoAdversarios
                    .forEach( a -> a.meDibujo(canvas,pincel));

            listaKunaisLanzados
                    .forEach( k -> k.meDibujo(canvas,pincel));

            listaImpactos
                    .forEach( i -> i.meDibujo(canvas,pincel));

            pincel.setAlpha(200);
            listaBotonAccion
                    .forEach( b -> b.meDibujo(canvas,pincel));


            pintarMarcador(canvas,pincel);

            if(victoria){finDePartida(1);}

            if(derrota){finDePartida(2);}
        }

    }//Fin rederizar()

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        while (retry) {
            try {
                bucle.finBucle();
                bucle.join();
                retry = false;
            } catch (InterruptedException ignored) {

            }
        }
    }

    public void finDePartida(int codResultado){

            finBucleAndLiberarRecursos();
            Intent i = new Intent();
            i.putExtra("COD_RESULTADO",codResultado);
            i.setClass(getContext(),FinActivity.class);
            getContext().startActivity(i);

    }


    public void finBucleAndLiberarRecursos(){
        bucle.finBucle();
        mediaPlayer.release();
    }

    //Clase para gestionar la animación de los impactos entres kunais y pulpos
    private class Impacto{

        private final int anchoSprite;
        private final int altoSprite;
        private final int numImagenesSprite = 12;
        private final float x, y;
        private int estado;
        private final Bitmap imagen;

        public Impacto(float x, float y){

            this.imagen = BitmapFactory.decodeResource(getResources(),R.drawable.explosion_large);
            this.x = x;
            this.y = y;
            this.anchoSprite = imagen.getWidth() / numImagenesSprite;
            this.altoSprite = imagen.getHeight();
            this.estado = -1; //recien creado
            MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.impacto_burbuja);
            mediaPlayer.setVolume(0.2f,0.2f);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
        }


        public void avanzaAnimacion(){
            //incrementamos el estado al siguiente momento de la explosión
            estado++;
        }

        public void meDibujo(Canvas c, Paint p){
            int posicionSprite = estado * anchoSprite;

            if(!animacionFinalizada()) {
                //Calculamos el cuadrado del sprite que vamos a dibujar
                Rect origen = new Rect(posicionSprite, 0, posicionSprite + anchoSprite, altoSprite);

                //calculamos donde vamos a dibujar la porcion del sprite
                Rect destino = new Rect((int)x,(int) y,(int) x + anchoSprite,
                        (int) y + imagen.getHeight());

                c.drawBitmap(imagen, origen, destino, p);
            }
        }

        public boolean animacionFinalizada(){
            return estado >= numImagenesSprite;
        }
    }//Fin Clase Impacto


}//Fin
