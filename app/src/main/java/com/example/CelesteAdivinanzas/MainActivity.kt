package com.example.CelesteAdivinanzas
//-------------------------------------------------------//
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import com.airbnb.lottie.LottieAnimationView
import com.example.CelesteAdivinanzas.R
import com.github.javafaker.Faker
import com.google.android.flexbox.FlexboxLayout
import kotlin.random.Random
//-------------------------------------------------------//
class MainActivity : AppCompatActivity() {
    private lateinit var txtPregunta:TextView
    private var respuesta:String = ""
    private lateinit var flexAlfabeto:FlexboxLayout
    private lateinit var flexResponse:FlexboxLayout
    private var indicesOcupados:ArrayList<Int> = arrayListOf()
    private var intentosPermitidos:Int = 0
    private var intentosHechos:Int = 0
    private lateinit var txtCantIntentos:TextView
    private lateinit var txtMsjIntentos:TextView
    private var finalizado:Boolean = false
    private lateinit var lottieResult:LottieAnimationView
    private lateinit var lotieAnimThinking:LottieAnimationView
    private lateinit var textMsjResultado:TextView
    private lateinit var txtMsjRespuestaCorrecta:TextView
    private lateinit var mediaPlayerVictoria: MediaPlayer
    private lateinit var  mediaPlayererror: MediaPlayer
    private lateinit var  mediaplayerclick: MediaPlayer
    private lateinit var btnRestart: Button
    //-------------------------------------------------------//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContentView(R.layout.activity_main)
        btnRestart = findViewById(R.id.btnRestart)
        txtPregunta = findViewById(R.id.txtPregunta)
        lotieAnimThinking = findViewById(R.id.animation_view_thik)
        flexResponse = findViewById(R.id.edt)
        flexAlfabeto = findViewById(R.id.flexboxLayout)
        txtCantIntentos = findViewById(R.id.txtCantIntentos)
        txtMsjIntentos = findViewById(R.id.textMsjIntentos)
        lottieResult = findViewById(R.id.animation_view_resultado)
        textMsjResultado = findViewById(R.id.txtMsjResultado)
        txtMsjRespuestaCorrecta = findViewById(R.id.txtMsjRespuestaCorrecta)
        mediaPlayerVictoria = MediaPlayer.create(this, R.raw.correcto)
        mediaPlayererror = MediaPlayer.create(this, R.raw.incorrecta)
        mediaplayerclick = MediaPlayer.create(this, R.raw.click)
        btnRestart = findViewById(R.id.btnRestart)

        
//-------------------------------------------------------//

        //-------------------------------------------------------//
        respuesta = obtenerPalabraAleatoria().uppercase()
        intentosPermitidos = respuesta.length + 2
        txtCantIntentos.text = "$intentosHechos/$intentosPermitidos"
        val alfabeto = generarAlfabeto(respuesta)
        val alfabetoDesorden = desordenar(alfabeto)
        mostrarEspacioRespuesta(respuesta.length, flexResponse)
        mostrarAlfabeto(alfabetoDesorden.uppercase(), flexAlfabeto)
        //-------------------------------------------------------//

        btnRestart.setOnClickListener {
            resetearJuego()
        }
//-------------------------------------------------------//

    }

    //-------------------------------------------------------//

    private  fun generarAlfabeto(semilla: String):String {
        val randomValues = List(5) { Random.nextInt(65, 90).toChar() }
        return "$semilla${randomValues.joinToString(separator = "")}"
    }
    private  fun desordenar(theWord: String):String {
        val theTempWord=theWord.toMutableList()
        for (item in 0..Random.nextInt(1,theTempWord.count()-1))
        {
            val indexA=Random.nextInt(theTempWord.count()-1)
            val indexB=Random.nextInt(theTempWord.count()-1)
            val temp=theTempWord[indexA]
            theTempWord[indexA]=theTempWord[indexB]
            theTempWord[indexB]=temp
        }
        return theTempWord.joinToString(separator = "")
    }
    private fun obtenerPalabraAleatoria(): String {
        val faker = Faker()
        val palabra = faker.artist().name()
        return palabra.split(' ').get(0)
    }
    private fun mostrarEspacioRespuesta(cantidad:Int, vista:FlexboxLayout){
        for (letter in 1..cantidad) {
            val btnLetra = EditText(this)
            btnLetra.isEnabled = false
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(5, 5, 5, 5)
            btnLetra.layoutParams = layoutParams
            vista.addView(btnLetra)
        }
    }

    private fun verificarResultado(){
        if (intentosHechos == intentosPermitidos || indicesOcupados.size == respuesta.length){
            finalizado = true

            if (indicesOcupados.size == respuesta.length){
                lottieResult.setAnimation(R.raw.animation_ln9fqefj)
                textMsjResultado.text = "Felicidades!"
                mediaPlayerVictoria.start()
            }
            else{
                lottieResult.setAnimation(R.raw.animation_ln9fs9bm)
                textMsjResultado.text = "Perdiste :("
                mediaPlayererror.start()
            }
            txtMsjRespuestaCorrecta.setText("La respuesta correcta es: $respuesta")

            textMsjResultado.visibility = View.VISIBLE
            lottieResult.visibility = View.VISIBLE
            txtMsjRespuestaCorrecta.visibility = View.VISIBLE
            btnRestart.visibility = View.VISIBLE

            flexResponse.visibility = View.GONE
            txtCantIntentos.visibility = View.GONE
            flexAlfabeto.visibility = View.GONE
            txtMsjIntentos.visibility = View.GONE
            txtPregunta.visibility = View.GONE
            lotieAnimThinking.visibility = View.GONE
        }
    }

    private fun clickLetra(btnClicked:Button) {
        if(!finalizado){
            var starIndex = 0
            var resIndex = respuesta.indexOf(btnClicked.text.toString())
            while(indicesOcupados.contains(resIndex)){
                starIndex = resIndex + 1
                resIndex = respuesta.indexOf(btnClicked.text.toString(), starIndex)
            }

            if(resIndex != -1){
                val flexRow = flexResponse.get(resIndex) as EditText
                flexRow.setText( respuesta.get(resIndex).toString())
                indicesOcupados.add(resIndex)
                btnClicked.setBackgroundColor(Color.GREEN)
                btnClicked.isEnabled = false
                btnClicked.setTextColor(Color.BLACK)
                mediaplayerclick.start()
            }
            else{
                Toast.makeText(applicationContext, "No es una letra valida",
                    Toast.LENGTH_SHORT).show()
                mediaPlayererror.start()
                btnClicked.setBackgroundColor(Color.RED)
                btnClicked.isEnabled = false
                btnClicked.setTextColor(Color.BLACK)

            }
            intentosHechos++
            txtCantIntentos.text = "$intentosHechos/$intentosPermitidos"
            verificarResultado()
        }
    }
    private fun mostrarAlfabeto(alfabeto:String, vista:FlexboxLayout){
        for (letter in alfabeto) {
            val btnLetra = Button(this)
            btnLetra.text = letter.toString()
            btnLetra.textSize = 12f
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(5, 5, 5, 5)
            btnLetra.layoutParams = layoutParams
            vista.addView(btnLetra)
            btnLetra.setOnClickListener{
                clickLetra(it as Button)
            }
        }
    }
    private fun resetearJuego() {

        Log.d("MainActivity", "Resetear juego llamado")
        respuesta = obtenerPalabraAleatoria().uppercase()
        intentosPermitidos = respuesta.length + 2
        intentosHechos = 0
        indicesOcupados.clear()
        finalizado = false

        flexResponse.removeAllViews()
        flexAlfabeto.removeAllViews()

        textMsjResultado.visibility = View.GONE
        lottieResult.visibility = View.GONE
        txtMsjRespuestaCorrecta.visibility = View.GONE
        flexResponse.visibility = View.VISIBLE
        txtCantIntentos.visibility = View.VISIBLE
        flexAlfabeto.visibility = View.VISIBLE
        txtMsjIntentos.visibility = View.VISIBLE
        txtPregunta.visibility = View.VISIBLE
        lotieAnimThinking.visibility = View.VISIBLE

        mostrarEspacioRespuesta(respuesta.length, flexResponse)
        val alfabeto = generarAlfabeto(respuesta)
        val alfabetoDesorden = desordenar(alfabeto)
        mostrarAlfabeto(alfabetoDesorden.uppercase(), flexAlfabeto)

        btnRestart.visibility = View.VISIBLE
    }
}

