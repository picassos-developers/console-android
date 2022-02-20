package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paypal.checkout.paymentbutton.PayPalButton;

import app.mynta.console.android.R;
import app.mynta.console.android.utils.Toasto;

public class PaymentMethodBottomSheetModal extends BottomSheetDialogFragment {

    View view;

    public interface OnSelectListener {
        void onSelectListener(String method);
    }

    OnSelectListener onSelectListener;

    public PaymentMethodBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onSelectListener = (OnSelectListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context + " must implement onSelectListener");
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.payment_method_bottom_sheet_modal, container, false);

        // check if credits are allowed
        if (!requireArguments().getBoolean("isCredits", true)) {
            view.findViewById(R.id.console_credits).setVisibility(View.GONE);
        }

        // console credits
        TextView totalCredits = view.findViewById(R.id.total_credits);
        totalCredits.setText(getString(R.string.your_available_balance) + " $" + requireArguments().getInt("credits"));
        view.findViewById(R.id.console_credits).setOnClickListener(v -> {
            if (requireArguments().getInt("price") <= requireArguments().getInt("credits")) {
                view.findViewById(R.id.checkout).setEnabled(true);
                view.findViewById(R.id.console_credits).setBackground(requireContext().getDrawable(R.drawable.item_background_darker_selected));
                view.findViewById(R.id.paypal).setBackground(requireContext().getDrawable(R.drawable.item_background_darker));
                view.findViewById(R.id.checkout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.paypal_checkout_container).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.checkout).setEnabled(false);
                Toasto.show_toast(requireContext(), getString(R.string.insufficient_credits_to_checkout), 1, 2);
            }
        });

        // checkout via paypal
        view.findViewById(R.id.paypal).setOnClickListener(v -> {
            view.findViewById(R.id.console_credits).setBackground(requireContext().getDrawable(R.drawable.item_background_darker));
            view.findViewById(R.id.paypal).setBackground(requireContext().getDrawable(R.drawable.item_background_darker_selected));
            view.findViewById(R.id.checkout).setVisibility(View.GONE);
            view.findViewById(R.id.paypal_checkout_container).setVisibility(View.VISIBLE);
        });

        // checkout button
        view.findViewById(R.id.checkout).setOnClickListener(v -> {
            if (requireArguments().getInt("price") <= requireArguments().getInt("credits")) {
                onSelectListener.onSelectListener("credits");
                dismiss();
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.insufficient_credits_to_checkout), 1, 2);
            }
        });

        // paypal checkout button
        PayPalButton paypalCheckout = view.findViewById(R.id.paypal_checkout);
       /** paypalCheckout.setup(
                createOrderActions -> {
                    List<PurchaseUnit> purchaseUnits = new List<>();
                    purchaseUnits.add(
                            new PurchaseUnit.Builder()
                                    .amount(
                                            new Amount.Builder()
                                                    .currencyCode(CurrencyCode.USD)
                                                    .value("10.00")
                                                    .build()
                                    )
                                    .build()
                    );
                    Order order = new Order(
                            OrderIntent.CAPTURE,
                            new AppContext.Builder()
                                    .userAction(UserAction.PAY_NOW)
                                    .build(),
                            purchaseUnits
                    );
                    createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                },
                approval -> approval.getOrderActions().capture(result -> {
                    Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                })
        );**/

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
