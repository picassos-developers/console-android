package app.mynta.console.android.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.Helper;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateAuthLoginActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        ConsolePreferences consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_generate_auth_login);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // qr placeholder
        ImageView qr = findViewById(R.id.qr_placeholder);

        // set dimension
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimenstion = Math.min(width, height);
        smallerDimenstion = smallerDimenstion * 3 / 4;

        // create qr code
        QRGEncoder qrgEncoder = new QRGEncoder("_TOK?" + consolePreferences.loadToken(), null, QRGContents.Type.TEXT, smallerDimenstion);
        try {
            Bitmap bitmap = qrgEncoder.getBitmap();
            qr.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.qr_locker).setOnClickListener(v -> findViewById(R.id.qr_locker).setVisibility(View.GONE));
    }
}