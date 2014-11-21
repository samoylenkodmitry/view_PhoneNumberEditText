PhoneEditText
=============

Handy implementation of format phone number in EditText. 

Usage
-----
1. Define in xml

```xml
  <samart.phonetextedittest.PhoneEditText
       xmlns:customNS="http://schemas.android.com/apk/res-auto"
        android:id="@+id/phoneEditText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:drawableLeft="@android:drawable/ic_menu_call"
        android:freezesText="true"
        android:hint="+7 (xxx) xxx-xx-xx"
        customNS:fixedPhonePart="+7"
        customNS:maxDigitsCount="10"
        />
```
Use `fixedPhonePart` field to set uneditable part of phone number.
Use `maxDigitsCount` field to set limit of phone number length.

2.  Use public methods to obtain number
```java

        //sets fixed part of phone number, e.g. +7
        phoneEditText.setFixedPart("8");

        //sets remaining part of phone number
        phoneEditText.setDigits("9062738275");

        //obtain digit string of phone number, e.g.
        // 89067285686
        String digitsString = phoneEditText.getDigitString();

        //obtain formatted representation of phone number,
        // e.g. +7 (906) 728-56-86
        String formattedPhoneNumber = phoneEditText.getFormattedPhoneNumber();

```
[Download test apk](app/app-release.apk)