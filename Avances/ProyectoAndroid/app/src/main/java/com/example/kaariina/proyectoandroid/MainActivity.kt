package com.example.kaariina.proyectoandroid

import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.EventLogTags
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notepad.*
import kotlinx.android.synthetic.main.row.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Load from DB
        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Titulo", "Descripcion")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Titulo")
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Titulo"))
                val Description = cursor.getString(cursor.getColumnIndex("Descripcion"))

                listNotes.add(Note(ID, Title, Description))
            } while (cursor.moveToNext())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //set adapter
        notesLv.adapter = myNotesAdapter

        //obtiene el numero total de tareas de ListView
        val total = notesLv.count
        //actionbar
        val mActionBar = supportActionBar
        if(mActionBar != null){
            //ingresar al actionbar como subtitulo del actonbar
            mActionBar.subtitle = "Tienes $total tareas pendientes en la lista"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        //searchView
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%" + newText + "%")
                return false
            }
        });
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null){
            when(item.itemId){
                R.id.addNote->{
                    startActivity(Intent(this, AddNoteActivity::class.java))
                }
                R.id.action_settings->{
                    Toast.makeText(this, "Configuracion",Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    inner class MyNotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context: Context?=null

        constructor(context: Context,listNotesAdapter: ArrayList<Note>) : super(){
            this.listNotesAdapter = listNotesAdapter
            this.context = context

        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row,null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDes

            //delete click button
            myView.deleteBtn.setOnClickListener{
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQuery("%")
            }
            //edit //update button click
            myView.editBtn.setOnClickListener{
                GoToUpdateFun(myNote)
            }
            //clck al boton de copiar
            myView.copyBtn.setOnClickListener{
                //obtener titulo
                val title = myView.titleTv.text.toString()
                //obtener la descripcion
                val desc = myView.descTv.text.toString()
                //concatenamos ambos
                val s = title + "\n" + desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
                cb.text = s //Se agrega al clipboard
                Toast.makeText(this@MainActivity,"Copiado...", Toast.LENGTH_SHORT).show()
            }
            //click al boton de compartir
            myView.shareBtn.setOnClickListener{
                //obtener titulo
                val title = myView.titleTv.text.toString()
                //obtener la descripcion
                val desc = myView.descTv.text.toString()
                //concatenamos ambos
                val s = title + "\n" + desc
                //share Intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent,s))
            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    private fun GoToUpdateFun(myNote: Note) {
        var intent  = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID",myNote.nodeID)//Inserta el ID
        intent.putExtra("name",myNote.nodeName) //Inserta el nombre
        intent.putExtra("des",myNote.nodeDes)//Inserta la descripcion
        startActivity(intent) //Empieza la actividad
    }


}
