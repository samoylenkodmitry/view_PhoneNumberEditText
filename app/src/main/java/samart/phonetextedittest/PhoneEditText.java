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

    private static final boolean IS_DEBUG = false;
    private static final int MAX_DIGITS_COUNT_DEF_VALUE = Integer.MAX_VALUE;
    private String fixedPart;
    private PhoneFormatter mPhoneFormatter;
    private int maxDigitsCount;

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


    private void init(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PhoneEditText, 0, 0);
        try {
            fixedPart = typedArray.getString(R.styleable.PhoneEditText_fixedPhonePart);
            maxDigitsCount = typedArray.getInteger(R.styleable.PhoneEditText_maxDigitsCount,
                    MAX_DIGITS_COUNT_DEF_VALUE);
        } finally {
            typedArray.recycle();
        }
        setInputType(EditorInfo.TYPE_CLASS_PHONE);
        setFixedPart(fixedPart);
        mPhoneFormatter = new PhoneFormatter(new WeakReference<PhoneEditText>(this));
        addTextChangedListener(mPhoneFormatter);
    }

    /**
     * @return formatted representation of phone number, e.g. +7 (906) 728-96-86
     */
    public String getFormattedPhoneNumber() {
        return mPhoneFormatter.getFormattedPhoneNumber();
    }

    /**
     * @return digit-char representation of phone number, e.g. 89077285839
     */
    public String getDigitString() {
        return mPhoneFormatter.getDigitPhoneNumber();
    }

    /**
     * @param fixedPart fixed part of phone number string representation,
     *                  that not eligible for edit,
     *                  can`t be null
     */
    public void setFixedPart(String fixedPart) {
        this.fixedPart = fixedPart;
        setText(fixedPart);
        setSelection(fixedPart.length());
    }

    /**
     * @param phoneNumber digits without fixed part, e.g. 9067985989
     *                    can`t be null
     */
    public void setDigits(String phoneNumber) {
        final String number = fixedPart + StringUtils.getDigits(phoneNumber);
        setText(number);
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (IS_DEBUG) LogUtils.msg("onSelectionChanged");
        if (null != fixedPart && selStart < fixedPart.length()) {
            String text = getText().toString();
            if (text.length() >= fixedPart.length())
                setSelection(fixedPart.length(), selEnd);
        }
    }

    private static class PhoneFormatter implements TextWatcher {

        private final WeakReference<PhoneEditText> phoneEditTextRef;
        private String formattedString;
        private String lastText;
        private boolean isDelete;
        private boolean isEdit;
        private String digits;

        public PhoneFormatter(WeakReference<PhoneEditText> phoneEditTextRef) {
            this.phoneEditTextRef = phoneEditTextRef;
        }

        public String getFormattedPhoneNumber() {
            return formattedString;
        }

        public String getDigitPhoneNumber() {

            final PhoneEditText phoneEditText = phoneEditTextRef.get();
            if (null == phoneEditText) return "";
            return phoneEditText.fixedPart + digits;
        }

        @Override
        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
        }

        @Override
        public void beforeTextChanged(CharSequence p1, int start, int count, int after) {
            if (isEdit) return;
            final String newText = p1.toString();
            isDelete = count > 0;
            if (IS_DEBUG)
                LogUtils.msg(
                        String.format(
                                "isDelete: %b,countInserted: %d," +
                                        " countDeleted: %d, after: %d, lastText: %s, p1: %s",
                                isDelete, start, count, after, lastText, p1.toString()));
            this.lastText = newText;
        }

        @Override
        public void afterTextChanged(Editable p1) {
            //prevent stackoverflow when changing text in EditText
            if (isEdit) return;
            isEdit = true;

            final PhoneEditText phoneEditText = phoneEditTextRef.get();
            if (null == phoneEditText) return;

            final String text = phoneEditText.getText().toString();
            final int fixedPartLength = phoneEditText.fixedPart.length();

            //prevent fixed part deletion
            if (isDelete && text.length() <= fixedPartLength) {
                if (IS_DEBUG)
                    LogUtils.msg("isDelete, lastText.length = " + lastText.length());

                phoneEditText.setText(phoneEditText.fixedPart);
                phoneEditText.setSelection(fixedPartLength);

                isEdit = false;
                return;
            }

            digits = StringUtils.getDigits(text.substring(fixedPartLength));

            //prevent digits count limit overflowing
            if (digits.length() > phoneEditText.maxDigitsCount) {
                phoneEditText.setText(lastText);
                phoneEditText.setSelection(lastText.length());
                isEdit = false;
                return;
            }

            //formatting
            this.formattedString = phoneEditText.fixedPart + ' '
                    + StringUtils.formatPhoneNumber(digits);
            phoneEditText.setText(formattedString);
            phoneEditText.setSelection(formattedString.length());

            isEdit = false;
        }

    }
}
