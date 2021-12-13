package com.picassos.mint.console.android.libraries.passwordstrength;

/**
 * created by Picassos, team.
 * https://picassos.xyz
 */

public enum PasswordStrength {

    WEAK(0),
    MEDIUM(33),
    STRONG(66),
    VERY_STRONG(100);

    public int callback;

    PasswordStrength(int callback) {
        this.callback = callback;
    }

    public static PasswordStrength validate(String password) {
        int score = 0;
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean specialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!specialChar && !Character.isLetterOrDigit(c)) {
                score++;
                specialChar = true;
            } else {
                if (!digit && Character.isDigit(c)) {
                    score++;
                    digit = true;
                } else {
                    if (!upper || !lower) {
                        if (Character.isUpperCase(c)) {
                            upper = true;
                        } else {
                            lower = true;
                        }

                        if (upper && lower) {
                            score++;
                        }
                    }
                }
            }
        }
        int length = password.length();
        int MIN_LENGTH = 8;
        int MAX_LENGTH = 16;
        if (length > MAX_LENGTH) {
            score++;
        } else if (length < MIN_LENGTH) {
            score = 0;
        }

        switch (score) {
            case 0 : return WEAK;
            case 1 : return MEDIUM;
            case 2 : return STRONG;
            case 3 : return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }
}
