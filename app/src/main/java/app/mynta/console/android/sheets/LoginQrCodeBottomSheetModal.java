package app.mynta.console.android.sheets;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import app.mynta.console.android.R;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;

public class LoginQrCodeBottomSheetModal extends BottomSheetDialogFragment {
    View view;

    public LoginQrCodeBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_qr_code_bottom_sheet_modal, container, false);

        ConsolePreferences consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // qr placeholder
        ImageView qr = view.findViewById(R.id.qr_placeholder);

        // set dimension
        WindowManager manager = (WindowManager) requireContext().getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = Math.min(width, height);
        smallerDimension = smallerDimension * 3 / 4;

        // create qr code
        QRGEncoder qrgEncoder = new QRGEncoder("_TOK?" + consolePreferences.loadToken(), null, QRGContents.Type.TEXT, smallerDimension);
        try {
            Bitmap bitmap = qrgEncoder.getBitmap();
            qr.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
