package com.xcheko51x.appinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class SplashScreen extends AppCompatActivity {

    // DURACION EN MILISEGUNDOS QUE SE MOSTRARA LA PANTALLA
    private final int DURACION_PANTALLA = 3000; // 3 Segundos

    ImageView gifLogo;
    TextView tvEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // OCULTA EL ACTION BAR
        getSupportActionBar().hide();

        // PONER LA ACTIVITY A FULL
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gifLogo = findViewById(R.id.gifLogo);
        tvEmpresa = findViewById(R.id.tvEmpresa);

        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/komtit.ttf");
        tvEmpresa.setTypeface(face);

        // PARA MOSTRAR EL GIF
        Glide.with(this).load(R.drawable.dispositivos).into(gifLogo);

        // LLAMADA A METODO PARA CAMBIAR LA ACTIVITY
        cambiarPantalla();
    }

    // FUNCION QUE CAMBIA DE ACTIVITY DESPUES DE X SEGUNDOS
    private void cambiarPantalla() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Navegacion.class);
                startActivity(intent);
                finish();
            }
        }, DURACION_PANTALLA);
    }

}
