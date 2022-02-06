package com.picassos.mint.console.android.libraries.smartcolorpicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.Toasto;

public class ColorPickerDialog {

    public interface OnColorPickerListener {

        void onCancel(ColorPickerDialog dialog);

        void onOk(ColorPickerDialog dialog, int color);
    }

    final Dialog dialog;
    final ColorPickerDialog.OnColorPickerListener listener;
    final View viewHue;
    final View viewHex;
    final ColorPickerSquare viewSatVal;
    final ImageView viewCursor;
    final ImageView viewTarget;
    final ViewGroup viewContainerSat;
    final ViewGroup viewContainerHue;
    final float[] currentColorHsv = new float[3];
    final String dialogTitle;
    final TextView title;
    int alpha;

    @SuppressLint("ClickableViewAccessibility")
    public ColorPickerDialog(final Context context, int color, String dialogTitle, ColorPickerDialog.OnColorPickerListener listener) {
        this.listener = listener;
        this.dialogTitle = dialogTitle;

        Color.colorToHSV(color, currentColorHsv);
        alpha = Color.alpha(color);

        @SuppressLint("InflateParams") final View view = LayoutInflater.from(context).inflate(R.layout.color_picker_dialog, null);
        viewHue = view.findViewById(R.id.viewHue);
        viewHex = view.findViewById(R.id.viewHex);
        viewSatVal = view.findViewById(R.id.viewSatVal);
        viewCursor = view.findViewById(R.id.viewCursor);
        viewTarget = view.findViewById(R.id.viewTarget);
        viewContainerSat = view.findViewById(R.id.viewContainerSat);
        viewContainerHue = view.findViewById(R.id.viewContainerHue);
        title = view.findViewById(R.id.dialogTitle);

        viewSatVal.setHue(getHue());
        viewCursor.setY(viewContainerHue.getY()+10);
        title.setText(dialogTitle);

        viewHue.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float y = event.getY();
                if (y < 0.f) y = 0.f;
                if (y > viewHue.getMeasuredHeight()) {
                    y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                }
                float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                if (hue == 360.f) hue = 0.f;
                setHue(hue);

                // update view
                viewSatVal.setHue(getHue());
                moveCursor();
                return true;
            }
            return false;
        });

        viewSatVal.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float x = event.getX(); // touch event are in dp units.
                float y = event.getY();

                if (x < 0.f) x = 0.f;
                if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                if (y < 0.f) y = 0.f;
                if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                // update view
                moveTarget();

                return true;
            }
            return false;
        });

        dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        view.findViewById(R.id.btnOK).setOnClickListener(v -> {
            if (ColorPickerDialog.this.listener != null) {
                ColorPickerDialog.this.listener.onOk(ColorPickerDialog.this, getColor());
            }
            dialog.dismiss();
        });
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            if (ColorPickerDialog.this.listener != null) {
                ColorPickerDialog.this.listener.onCancel(ColorPickerDialog.this);
            }
            dialog.dismiss();
        });

        // move cursor & target on first draw
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        EditText hexCode = view.findViewById(R.id.hexCode);
        hexCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // validate color
                try {
                    Helper.setGradientBackground(viewHex, "#" + hexCode.getText().toString(), "#" + hexCode.getText().toString());
                    // enable apply button
                    view.findViewById(R.id.btnOK).setEnabled(true);
                    view.findViewById(R.id.btnOK).setOnClickListener(v -> {
                        if (ColorPickerDialog.this.listener != null) {
                            ColorPickerDialog.this.listener.onOk(ColorPickerDialog.this, Color.parseColor("#" + hexCode.getText().toString()));
                        }
                        dialog.dismiss();
                    });
                } catch (IllegalArgumentException e) {
                    view.findViewById(R.id.btnOK).setOnClickListener(v -> Toasto.show_toast(context, context.getString(R.string.invalid_hex_code), 1, 2));
                }

                if (charSequence.length() == 0) {
                    view.findViewById(R.id.btnOK).setEnabled(true);
                    view.findViewById(R.id.btnOK).setOnClickListener(v -> {
                        if (ColorPickerDialog.this.listener != null) {
                            ColorPickerDialog.this.listener.onOk(ColorPickerDialog.this, getColor());
                        }
                        dialog.dismiss();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void moveCursor() {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() >> 1) - viewContainerHue.getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() >> 1) - viewContainerHue.getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() >> 1) - viewContainerSat.getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() >> 1) - viewContainerSat.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }

    private int getColor() {
        final int argb = Color.HSVToColor(currentColorHsv);
        return alpha << 24 | (argb & 0x00ffffff);
    }

    private float getHue() {
        return currentColorHsv[0];
    }

    private float getSat() {
        return currentColorHsv[1];
    }

    private float getVal() {
        return currentColorHsv[2];
    }

    private void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    private void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    private void setVal(float val) {
        currentColorHsv[2] = val;
    }

    public void show() {
        dialog.show();
    }
}