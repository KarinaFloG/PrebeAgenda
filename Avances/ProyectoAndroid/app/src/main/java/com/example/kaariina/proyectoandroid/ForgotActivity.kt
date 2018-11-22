package com.example.kaariina.proyectoandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotActivity : AppCompatActivity() {
    private lateinit var txtEmail: EditText
    private lateinit var auth:FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        txtEmail=findViewById(R.id.txtEmail)
        progressBar=findViewById(R.id.progressBar)
        auth=FirebaseAuth.getInstance()
    }

    fun send(view: View){
        val email=txtEmail.text.toString()

        //Verifica que el emai no este vacio
        if(!TextUtils.isEmpty(email)){
            //Envia el email para la contraseña
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this){
                    task ->

                    //Para comparar sitodo salio bien en el envio del mensaje
                    if(task.isSuccessful){
                        progressBar.visibility=View.VISIBLE
                        //Sitodo ok regresa al usuario a la pagina principal
                        startActivity(Intent(this,LoginActivity::class.java))
                    }else{
                        Toast.makeText(this,"Error al recuperar la contraseña", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
