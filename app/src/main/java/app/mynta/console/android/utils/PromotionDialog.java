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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import app.mynta.console.android.R;
import app.mynta.console.android.constants.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PromotionDialog extends Dialog {

    public PromotionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_promotion);

        // set cancelable
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        setOnCancelListener(dialogInterface -> dismiss());
        setOnDismissListener(dialogInterface -> dismiss());

        requestPromotion();

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            Window window = getWindow();
            WindowManager.LayoutParams WLP = window.getAttributes();
            WLP.gravity = Gravity.CENTER;
            window.setAttributes(WLP);
        }
    }

    /**
     * request promotion data
     */
    private void requestPromotion() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PROMOTION,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("app");
                        JSONObject promotion = root.getJSONObject("promotion");

                        // open link
                        findViewById(R.id.promotion_container).setOnClickListener(v -> {
                            try {
                                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(promotion.getString("uri"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dismiss();
                        });

                        SimpleDraweeView promotionImage = findViewById(R.id.promotion_image);
                        promotionImage.setController(
                                Fresco.newDraweeControllerBuilder()
                                        .setTapToRetryEnabled(true)
                                        .setUri(promotion.getString("image"))
                                        .build());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                }, error -> {
            findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
            dismiss();
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", "promotion");
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
