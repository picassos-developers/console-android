package app.mynta.console.android.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterRange implements InputFilter {
    private int min, max;

    public InputFilterRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterRange(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        try {
            int input = Integer.parseInt(spanned.toString() + charSequence.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException ignored) { }
        return "";
    }
}
