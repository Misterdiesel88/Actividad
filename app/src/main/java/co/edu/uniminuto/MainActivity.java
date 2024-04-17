package co.edu.uniminuto;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 25;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int CONTACTS_PERMISSION_REQUEST_CODE = 102;
    private static final int BIOMETRIC_PERMISSION_REQUEST_CODE = 103;
    //1. declaracion de los objetos de la interface que se usaran en la párte logica
    private Button btnCheckPermissions;
    private Button btnRequestPermissions;
    private TextView tvCamera;
    private TextView tvBiometric;
    private TextView tvExternalWS;
    private TextView tvReadExternalS;
    private TextView tvInternet;
    private TextView tvResponse;
    //1.1 Objetos para recursos
    private TextView versionAndroid;
    private int versionSDK;
    private ProgressBar pbLevelBatt;
    private TextView tvLevelBatt;
    private TextView tvConexion;
    IntentFilter batFilter;
    CameraManager cameraManager;
    String cameraId;
    private Button btnOn;
    private Button btnOff;
    ConnectivityManager conexion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //3. Llamado del metodo de enlace de objetos
        initObject();
        //4. Enlace de botones a los metodos
        btnCheckPermissions.setOnClickListener(this::voidCheckPermissions);
        btnRequestPermissions.setOnClickListener(this::voidRequestPermissions);
        //Botones de la linterna
        btnOn.setOnClickListener(this::onLigth);
        btnOff .setOnClickListener(this::offLigth);
        //Bateria
        batFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,batFilter);
    }
   //10. Bateria
   BroadcastReceiver receiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
           pbLevelBatt.setProgress(levelBaterry);
           tvLevelBatt.setText("Level Baterry:"+levelBaterry+" %");
       }
   };


//8. Implementacion del OnResume para la version del Android

    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Version SO:" + versionSO + " / SDK:" + versionSDK);
    }
    //9. Encendido y apagado de linterna
    private void onLigth(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId,true);
        }catch (Exception e){
            Toast.makeText(this, "NO SE PUEDE ENCENDER LA LINTERNA", Toast.LENGTH_SHORT).show();
            Log.i("FLASH",e.getMessage());
        }
    }
    private void offLigth(View view) {
        try {

            cameraManager.setTorchMode(cameraId,false);
        }catch (Exception e){
            Toast.makeText(this, "NO SE PUEDE ENCENDER LA LINTERNA", Toast.LENGTH_SHORT).show();
            Log.i("FLASH",e.getMessage());
        }
    }

    // 5. Verificacion de permisos
    private void voidCheckPermissions(View view) {
        //si hay permiso este metodo me da un 0 si no un 1
        int statusCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int statusWES = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int statusRES = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int statusInternet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        int statusBiometric = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC);

        tvCamera.setText("Status Camera:" + statusCamera);
        tvExternalWS.setText("Status WES:" + statusWES);
        tvReadExternalS.setText("Status RES:" + statusRES);
        tvInternet.setText("Status Internet:" + statusInternet);
        tvBiometric.setText("Status Biometric:" + statusBiometric);
        btnRequestPermissions.setEnabled(true);
    }

    //6. Solicitud de permiso de Camara
    private void voidRequestPermissions(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }
    }

    // 6. Solicitud de permiso para el biometrico
    private void voidRequestBiometricPermissions(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso para usar el biometrico
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.USE_BIOMETRIC}, BIOMETRIC_PERMISSION_REQUEST_CODE);
        }
    }

    public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    CONTACTS_PERMISSION_REQUEST_CODE);
        }
    }
    //7. Gestion de respuesta del usuario respecto a la solicitud del permiso



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tvResponse.setText(" " + grantResults[0]);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this).setTitle("Box Permissions").setMessage("You denied the permissions Camera")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
            } else {
                Toast.makeText(this, "Usted no ha otorgado los permisos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Usted no ha otorgado los permisos", Toast.LENGTH_SHORT).show();
        }
    }




    //2. Enlace de objetos
    private void initObject() {
        btnCheckPermissions = findViewById(R.id.btnCheckPermission);
        btnRequestPermissions = findViewById(R.id.btnRequestPermission);
        btnRequestPermissions.setEnabled(false);
        tvCamera = findViewById((R.id.tvCamera));
        tvBiometric = findViewById(R.id.tvDactilar);
        tvExternalWS = findViewById(R.id.tvEws);
        tvReadExternalS = findViewById(R.id.tvRS);
        tvInternet = findViewById(R.id.tvReadContacts);
        tvResponse = findViewById(R.id.tvResponse);
        versionAndroid = findViewById(R.id.tvVersionAndroid);
        pbLevelBatt = findViewById(R.id.pbLevelBattery);
        tvLevelBatt = findViewById(R.id.tvLevelBattery);
        tvConexion = findViewById(R.id.tvConexion);
        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);


    }
}