package icoderslab.com.nfc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;


public class MainActivity extends AppCompatActivity  {

    private NfcAdapter nfcAdapter;
    Button bt_send, bt_select_image;
    private static final int GALLERY_INTENT_CALLED = 1;
    private int GALLERY_KITKAT_INTENT_CALLED = 2;
    private Uri fileUri;
    ImageView img_event_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_event_add = (ImageView)findViewById(R.id.img_event_add);
        bt_send = (Button)findViewById(R.id.bt_send);
        bt_select_image = (Button) findViewById(R.id.bt_select_image);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 sendPicture(view);
                }
        });
        bt_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < 19) {
                    Intent intent = new Intent();
                    intent.setType("image/jpeg");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT_CALLED);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_KITKAT_INTENT_CALLED);
                }
            }
        });


    }


    // code to sahe he masla  kia araha he

    public void sendPicture(View view) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        PackageManager pm = MainActivity.this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            Toast.makeText(MainActivity.this, "This device does not contain NFC hardware.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!nfcAdapter.isEnabled()){
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        else if(!nfcAdapter.isNdefPushEnabled()) {

            Toast.makeText(this, "Please enable NFC Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {

            Toast.makeText(this, " NFC  Beam is available ",
                    Toast.LENGTH_SHORT).show();
            if(fileUri == null)
                return;


            Uri picName = fileUri;

            File fileDirectory = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);


            File fileToShare = new File(fileDirectory, String.valueOf(picName));
            fileToShare.setReadable(true, false);


            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToShare)}, this);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        img_event_add.setImageURI(fileUri);
        if(resultCode  ==RESULT_OK) {
            try {

                fileUri = data.getData();
            } catch (Exception e) {

            }
        }
    }

}
