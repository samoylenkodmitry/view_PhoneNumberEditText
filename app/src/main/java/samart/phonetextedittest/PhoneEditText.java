package samart.phonetextedittest;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * EditText with auto formatted phone number mask.
 */
public class PhoneEditText extends EditText {

    public static final String BUNDLE_KEY_FIXED_PART = "fixedPart";
    public static final String BUNDLE_KEY_PHONE_NUMBER = "phoneNumber";
    public static final String BUNDLE_KEY_SUPER_STATE = "superState";
    private String fixedPart;
    private PhoneFormatter mPhoneFormatter;

    public PhoneEditText(android.content.Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();

        bundle.putParcelable(BUNDLE_KEY_SUPER_STATE, superState);
        bundle.putString(BUNDLE_KEY_PHONE_NUMBER, mPhoneFormatter.getDigits());
        bundle.putString(BUNDLE_KEY_FIXED_PART, fixedPart);

        LogUtils.msg("state saved");
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            state = bundle.getParcelable(BUNDLE_KEY_SUPER_STATE);
            this.fixedPart = bundle.getString(BUNDLE_KEY_FIXED_PART);
            this.setDigits(bundle.getString(BUNDLE_KEY_PHONE_NUMBER));

            LogUtils.msg("state restored");
        }
        super.onRestoreInstanceState(state);
    }

    public PhoneEditText(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PhoneEditText(android.content.Context context) {
        super(context);
        init(context, null);
    }

    /**
     * @return formatted representation of phone number, e.g. +7 (906) 728-56-86
     */
    public String getFormattedPhoneNumber() {
        return this.mPhoneFormatter.getFormattedPhoneNumber();
    }

    /**
     * @return digit-char representation of phone number, e.g. 89077285834
     */
    public String getDigitString() {
        return this.mPhoneFormatter.getDigitPhoneNumber();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (null != fixedPart && selStart < fixedPart.length()) {
            String text = getText().toString();
            if (text.length() >= fixedPart.length())
                setSelection(fixedPart.length(), selEnd);
        }
    }

    /**
     * @param fixedPart fixed part of phone number string representation, that not eligible for edit
     */
    public void setFixedPart(String fixedPart) {
        this.fixedPart = fixedPart;
        setText(fixedPart);
        setSelection(fixedPart.length());
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PhoneEditText, 0, 0);
        try {
            this.fixedPart = typedArray.getString(R.styleable.PhoneEditText_fixedPhonePart);
        } finally {
            typedArray.recycle();
        }
        this.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        setFixedPart(fixedPart);
        this.mPhoneFormatter = new PhoneFormatter(new WeakReference<PhoneEditText>(this));
        addTextChangedListener(mPhoneFormatter);
    }

    /**
     * @param phoneNumber digits without fixed part, e.g. 9067285686
     */
    public void setDigits(String phoneNumber) {
        final String number = fixedPart + phoneNumber;
        setText(number);
        setSelection(number.length());
    }

    private static class PhoneFormatter implements TextWatcher {

        private final WeakReference<PhoneEditText> phoneEditTextRef;
        private String formattedString;
        private String digitString;
        private String lastText;
        private boolean isDelete;
        private boolean isEdit;

        public PhoneFormatter(WeakReference<PhoneEditText> phoneEditTextRef) {
            this.phoneEditTextRef = phoneEditTextRef;
        }

        public String getFormattedPhoneNumber() {
            return formattedString;
        }

        public String getDigitPhoneNumber() {

            PhoneEditText phoneEditText = phoneEditTextRef.get();
            if (null == phoneEditText) return "";
            return phoneEditText.fixedPart + digits;
        }

        public String getDigits() {
            return digits;
        }

        private String digits;

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void beforeTextChanged(CharSequence p1, int countInserted, int countDeleted, int after) {
            if (isEdit) return;
            String newText = p1.toString();
            this.isDelete = countDeleted > 0;
            LogUtils.msg(
                    String.format(
                            "isDelete: %b,countInserted: %d, countDeleted: %d, after: %d, lastText: %s, p1: %s",
                            isDelete, countInserted, countDeleted, after, lastText, p1.toString()));
            this.lastText = newText;
        }

        @Override
        public void afterTextChanged(Editable p1) {
            if (isEdit) return;
            PhoneEditText phoneEditText = phoneEditTextRef.get();
            if (null == phoneEditText) return;
            isEdit = true;
            int fixedPartLength = phoneEditText.fixedPart.length();
            String text = phoneEditText.getText().toString();
            if (isDelete) {
                LogUtils.msg("isDelete, lastText.length = " + lastText.length());
                if (text.length() <= fixedPartLength) {
                    phoneEditText.setText(phoneEditText.fixedPart);
                    phoneEditText.setSelection(fixedPartLength);

                    isEdit = false;
                    return;
                }
            }

            String digits = StringUtils.getDigits(text.substring(fixedPartLength));

            if (digits.length() > 10) {
                phoneEditText.setText(lastText);
                phoneEditText.setSelection(lastText.length());
                isEdit = false;
                return;
            }

            this.digits = digits;
            this.formattedString = phoneEditText.fixedPart + ' '
                    + StringUtils.formatPhoneNumber(digits);
            phoneEditText.setText(formattedString);
            phoneEditText.setSelection(formattedString.length());

            isEdit = false;
        }

    }
}
