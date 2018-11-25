package com.example.kaariina.proyectoandroid

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {
    //nombre de la base de datos
    var dbName = "MisNotas"

    //table name
    var dbTable = "Notes"

    //columnas
    var colID = "ID"
    var colTitle = "Titulo"
    var colDes= "Descripcion"

    //Version de la db
    var dbVersion =  1

    //Crear la table si no existe MisNotas
    val sqlCreateTable = "CREATE TABLE IF NOTE EXISTS"+dbTable+"("+colID+"INTEGER PRIMARY KEY,"+colTitle+"TEXT,"+colDes+"TEXT);"
    var sqlDB:SQLiteDatabase?=null

    constructor(context: Context){
        var db = DataBaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }
    inner class DataBaseHelperNotes: SQLiteOpenHelper{
        var context: Context?=null
        constructor(context:Context):super(context,dbName,null,dbVersion){
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context,"Base de datos creada", Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table si existe" + dbTable)
        }

    }

    fun insert(values:ContentValues):Long{
        val ID = sqlDB!!.insert(dbTable,"",values)
        return ID
    }

    fun Query(projection:Array<String>, selection:String, selectionArgs:Array<String>,sorOrder:String): Cursor{
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTable
        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sorOrder )
        return cursor
    }

    fun delete(selection:String, selectionArgs:Array<String>):Int{
        val count = sqlDB!!.delete(dbTable,selection, selectionArgs)
        return count
    }

    fun update(values:ContentValues,selection:String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTable,values,selection,selectionArgs)
        return count
    }

}