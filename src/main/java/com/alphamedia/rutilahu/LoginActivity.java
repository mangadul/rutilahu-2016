package com.alphamedia.rutilahu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class LoginActivity extends Activity {

    Intent i;
    private boolean islogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createDir();

        // check if file data.json exist ?
        Boolean file = new File(Config.DATA_DIR).isFile();
        if (!file) {
            if(Download.isConnected(getApplicationContext()))
            {
                startActivity(new Intent(this, Download.class));
            } else
                Toast.makeText(getApplicationContext(), "Koneksi internet tidak ditemukan. Download GAGAL!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void btnLogin(View view)
    {
        EditText user = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);
        if((user.getText().toString().equals("surveyor")) && (pass.getText().toString().equals("bismillah")))
        {
            i = new Intent(LoginActivity.this, DataPenerimaRealmIO.class);
            i.putExtra("islogin", true);
            i.putExtra("devid", true);
            Toast.makeText(getApplicationContext(),"Login Berhasil", Toast.LENGTH_SHORT).show();
            startActivity(i);
            this.finish();
        } else {
            Toast.makeText(getApplicationContext(),"User / Pasword yang anda masukan salah", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnCancel(View view)
    {
        quit(false, getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this).setMessage(
                R.string.exit_message).setTitle(
                R.string.exit_title).setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                setResult(RESULT_CANCELED);
                                Intent i = new Intent();
                                quit(false,i);
                            }
                        }).setNeutralButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                }).show();
    }

    private void quit(boolean success, Intent i) {
        setResult((success) ? -1:0, i);
        this.finish();
    }

    private void createDir()
    {
        File fr = new File(Environment.getExternalStorageDirectory() + "/fieldreport/");
        if(!fr.exists()) {
            fr.mkdirs();
            File fotodir = new File(Environment.getExternalStorageDirectory() + "/fieldreport/foto/");
            if (!fotodir.exists())
                fotodir.mkdirs();
            File filedir = new File(Environment.getExternalStorageDirectory() + "/fieldreport/file/");
            if (!filedir.exists())
                filedir.mkdirs();
        } else
            Log.d("Error: ", "dir. already exists");
    }

}
