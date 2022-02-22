/*
 * Mint APP, Mint Console, Design (except icons and some open-source libraries) and Donut API is copyright of Rixhion, inc. - © Rixhion, inc 2019. All rights reserved.
 *
 * Any redistribution or reproduction of part or all of the contents in any form is prohibited other than the following:
 *  - YOU MAY USE, EDIT DATA INSIDE MINT ONLY.
 *
 * You may not, except with our express written permission, distribute or commercially exploit the content. Nor may you transmit it or store it in any other website & app or other form of electronic retrieval system.
 *
 */

package app.mynta.console.android.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import app.mynta.console.android.R;

public class RequestDialog extends Dialog {

    public RequestDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_request);

        // set cancelable
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            Window window = getWindow();
            WindowManager.LayoutParams WLP = window.getAttributes();
            WLP.gravity = Gravity.CENTER;
            window.setAttributes(WLP);
        }
    }
}
