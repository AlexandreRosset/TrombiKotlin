package com.ynov.aro.trombi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_student.*
import android.view.WindowManager
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class DetailStudentActivity : AppCompatActivity() {

    private var messageAEnvoyer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_student)
        val intent = intent
        val picture = intent.getStringExtra("picture").toString()
        val studentPicture = findViewById<ImageView>(R.id.picture)
        Picasso.get().load(picture).into(studentPicture)
        val nom = intent.getStringExtra("nom").toString()
        val nomStudent = findViewById<TextView>(R.id.nom)
        nomStudent.text = nom
    }
    fun onClickEvent(view: View) {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.another_view,null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )
        // Set an elevation for the popup window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }


        // If API level 23 or higher then execute the code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupWindow.exitTransition = slideOut

        }

        // Get the widgets reference from custom view
        val message = view.findViewById<TextView>(R.id.msg)
        val envoyerMessage = view.findViewById<Button>(R.id.button_popup)

        // Set a click listener for popup's button widget
        envoyerMessage.setOnClickListener{
            messageAEnvoyer = view.findViewById<EditText>(R.id.msg).text.toString()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE), 14540)
            popupWindow.dismiss()
        }

        // Set a dismiss listener for popup window
        popupWindow.setOnDismissListener {
            Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
            root_layout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
        popupWindow.isFocusable = true
        popupWindow.update()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults:IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val nom = findViewById<AppCompatTextView>(R.id.nom).text.toString()
        val message = messageAEnvoyer
        //val message = findViewById<EditText>(R.id.msg).text.toString()
        if (requestCode == 14540 && message.isNotEmpty()){
            if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                val manager = SmsManager.getDefault()
                manager.sendTextMessage("0651678660", null, "Bonjour $nom,\n\n$message", null, null)
            }
        }
    }
}
