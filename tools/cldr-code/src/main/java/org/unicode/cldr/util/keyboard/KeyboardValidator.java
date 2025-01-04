package org.unicode.cldr.util.keyboard;

/** container class for validating keyboard files */
public class KeyboardValidator {
    /** result code for validity test */
    public enum KeyboardValidity {
        /** String is invalid */
        error,
        /** Keyboard is valid, but there's a suggestion */
        warning;

        /** true if this KeyboardValidity is an error */
        boolean isError() {
            return this == error;
        }
    }

    public static final class KeyboardStatus extends IllegalArgumentException {
        private KeyboardValidity validity;
        private String message;

        public KeyboardStatus(final KeyboardValidity validity, final String message) {
            this.validity = validity;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public KeyboardValidity getValidity() {
            return validity;
        }

        /** if this is an error, throw it otherwise do nothing */
        public void throwIfError() {
            if (validity.isError()) {
                throw this;
            }
        }
    }
}
