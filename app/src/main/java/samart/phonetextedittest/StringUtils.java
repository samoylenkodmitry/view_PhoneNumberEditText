package samart.phonetextedittest;

/**
 * Copyright (C) 2014 Dmitry Samoylenko
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact email dmitrysamoylenko@gmail.com
 */
public class StringUtils {

    public static String getDigits(String str) {
        return str.replaceAll("([^\\d])+", "");
    }

    public static String formatPhoneNumber(String digitString) {
        int len = digitString.length();
        if (len > 10) return digitString;
        if (len == 0) return "";
        int last = 0;
        StringBuilder stringBuilder = new StringBuilder(15);
        stringBuilder.append('(');
        // "905"... -> "(905) "
        if (len > 3) {
            stringBuilder.append(digitString.substring(0, 3)).append(") ");
            last = 3;
        }
        // "905360".. -> "(905) 360-"
        if (len > 6) {
            stringBuilder.append(digitString.substring(3, 6)).append('-');
            last = 6;
        }
        // "90536094".. -> "(905) 360-94-"
        if (len > 8) {
            stringBuilder.append(digitString.substring(6, 8)).append('-');
            last = 8;
        }
        // "9053609402".. -> "(905) 360-94-02"
        stringBuilder.append(digitString.substring(last, len));
        return stringBuilder.toString();
    }
}
