package samart.phonetextedittest;

import android.content.Context;
import android.content.res.TypedArray;
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


    private static class PhoneFormatter implements TextWatcher {

        private String formattedString;
        private String digitString;

        public String getFormattedPhoneNumber() {
            return formattedString;
        }

        public String getDigitPhoneNumber() {
            return digitString;
        }

        private final WeakReference<PhoneEditText> phoneEditTextRef;

        public PhoneFormatter(WeakReference<PhoneEditText> phoneEditTextRef) {
            this.phoneEditTextRef = phoneEditTextRef;
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        private String lastText;
        private boolean isDelete;
        private boolean isEdit;

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
            if (null == phoneEditText) {
                return;
            }
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

            this.digitString = phoneEditText.fixedPart + digits;
            this.formattedString = phoneEditText.fixedPart + ' '
                    + StringUtils.formatPhoneNumber(digits);
            phoneEditText.setText(formattedString);
            phoneEditText.setSelection(formattedString.length());

            isEdit = false;
        }

    }

    private String fixedPart;

    /**
     * @param fixedPart fixed part of phone number string representation, that not eligible for edit
     */
    public void setFixedPart(String fixedPart) {
        this.fixedPart = fixedPart;
        setText(fixedPart);
        setSelection(fixedPart.length());
    }

    public PhoneEditText(android.content.Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PhoneEditText(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PhoneEditText(android.content.Context context) {
        super(context);
        init(context, null);
    }

    private PhoneFormatter mPhoneFormatter;

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
}
