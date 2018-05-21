package com.example.kiribaty.chatroomapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DeleteActivity extends AppCompatActivity {

    //List/listadapter
    ArrayList<String> myRoomList = new ArrayList<>();
    ArrayAdapter<String> myArrayAdapter;
    //variable
    String myRoomName;
    //intentbundle
    Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        ListView myDeleteRoomView = findViewById(R.id.DeleteRoomView);
        bundle = getIntent().getExtras();
        myRoomList = bundle.getStringArrayList("RoomList");
        //Adapter för att hålla alla chatrum i en listview med simpel layout
        myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myRoomList);
        //sätter adaptern till listviewn
        myDeleteRoomView.setAdapter(myArrayAdapter);

        //Metod för att radera chattrum samt och all chattlog med en alertdialog
        myDeleteRoomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Alertdialog
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DeleteActivity.this);
                mBuilder.setTitle("Are you sure you want to delete this Room? All chat logs will be lost!");

                myRoomName = myRoomList.get(position);

                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Radera chattrum samt uppdatera listan
                        myRoomList.remove(myRoomName);
                        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        mDatabaseReference.child(myRoomName).removeValue();
                        myArrayAdapter.notifyDataSetChanged();
                    }

                });
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                mBuilder.show();

            }


        });
    }

    //Overflowmeny
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deletemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Stäng ner appen
        switch (item.getItemId()) {
            case R.id.CloseApp:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


