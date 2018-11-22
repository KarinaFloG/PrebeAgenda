package com.example.kaariina.proyectoandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.ContentLoadingProgressBar
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrarActivity : AppCompatActivity() {
    //declaracion de variables
    private lateinit var txtName:EditText
    private lateinit var txtLastName:EditText
    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference:DatabaseReference
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)
        txtName=findViewById(R.id.txtName)
        txtLastName=findViewById(R.id.txtLastName)
        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)

        //Para mandar a llamar al progress bar
        progressBar= findViewById(R.id.progressBar)

        //Instancia a la base de base y al usuario
        database= FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //Se crea user en firebase y dentro de este se van creando todos los usuarios
        dbReference=database.reference.child("User")

    }

    //Registra un usuario
    fun registrar(view:View){
        createNewAccount()
    }

    //Crea un usuario
    private fun createNewAccount(){
        val name:String=txtName.text.toString()
        val lastName:String=txtLastName.text.toString()
        val email:String=txtEmail.text.toString()
        val password:String=txtPassword.text.toString()

        //Para verificar que los campos no se encuentren vacios
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            //Para que se muestre el progress bar
            progressBar.visibility=View.VISIBLE
            //Para dar de alta el usuario y contraseña
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){
                task ->
                if(task.isComplete){
                    val user: FirebaseUser?=auth.currentUser
                    verifyEmail(user)
                    val userBD= dbReference.child(user?.uid!!)
                    //Agregamos los datos en la bd
                    userBD.child("Name").setValue(name)
                    userBD.child("LastName").setValue(lastName)
                    action()
                }
            }
        }
    }

    private fun action(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
        private fun verifyEmail(user:FirebaseUser?){
            user?.sendEmailVerification()
                ?.addOnCompleteListener(this){
                    //Obtenemos la tarea
                    task ->
                    if(task.isComplete){
                        Toast.makeText(this, "Email enviado", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Error al enviar", Toast.LENGTH_LONG).show()
                    }
                }
        }
}


