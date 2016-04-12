package edu.umd.cschroe2.event_garden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Christine Schroeder on 4/8/2016.
 */
public class LogIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);



        Button login = (Button) findViewById(R.id.loginbutton);
        login.setOnClickListener(new View.OnClickListener() {
            EditText username = (EditText) findViewById(R.id.username);
            EditText password = (EditText) findViewById(R.id.password);
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("")||password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "You need to enter username and password", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(LogIn.this,MainActivity.class);
                    startActivity(intent);

                }
            }
        });

    }
}
