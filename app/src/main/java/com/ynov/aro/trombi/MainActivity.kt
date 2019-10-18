package com.ynov.aro.trombi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    class Student {
        var nom =""
        var picture =""
        var present = false
    }

    class StudentAdapter(private val context: Context,
                        private val dataSource: ArrayList<Student>) : BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val rowView = inflater.inflate(R.layout.list_item_student, parent, false)
            val nomStudent = rowView.findViewById(R.id.nom) as TextView
            val presentStudent = rowView.findViewById(R.id.present) as CheckBox
            val student = getItem(position) as Student
            nomStudent.text = student.nom
            nomStudent.setOnClickListener {

                val selectedStudent = dataSource[position]
                val intent = Intent(context, DetailStudentActivity::class.java)
                intent.putExtra("picture", selectedStudent.picture)
                intent.putExtra("nom", selectedStudent.nom)
                startActivity(context, intent, null)
            }
            presentStudent.isChecked = student.present
            presentStudent.setOnClickListener {
                dataSource[position].present = presentStudent.isChecked
            }
            return rowView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val context = this

        var compteur = 0
        val url = URL("http://student.ddadev.fr/dev_mobile/trombi.php")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    val resultat = JSONArray(line)
                    var student:Student
                    val listItems = ArrayList<Student>()
                    println(listItems.size)
                    while (compteur < resultat.length()){
                        student = Student()
                        student.nom = resultat.getJSONObject(compteur).getString("nom")
                        student.picture = resultat.getJSONObject(compteur).getString("picture")
                        listItems.add(student)
                        compteur++
                    }
                    val adapter = StudentAdapter(context, listItems)
                    listStudent.adapter = adapter

                    listStudent.setOnItemClickListener { _, _, position, _ ->

                        val selectedStudent = listItems[position]
                        val intent = Intent(context, DetailStudentActivity::class.java)
                        intent.putExtra("picture", selectedStudent.picture)
                        intent.putExtra("nom", selectedStudent.nom)
                        startActivity(intent)
                    }
                }
            }
        }

    }

    fun savePresenceStudent(view: View){
        val adapter = listStudent.adapter
        var compteur = 0
        val path = this@MainActivity.getExternalFilesDir(null)
        val f = File(path, LocalDate.parse(LocalDate.now().toString()).toString() + ".txt")
        println(f.name)
        f.delete()
        f.createNewFile()
        while (compteur < adapter.count){
            var element:Student = adapter.getItem(compteur) as Student
            f.appendText(element.nom + ":" + element.present + System.getProperty("line.separator"))
            compteur++
        }
        f.forEachLine {
            println(it)
        }
        Toast.makeText(this, "Fichier sauvegardé", Toast.LENGTH_SHORT).show()
    }

}
