package dk.datamuseum.mobilereg.service;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

import dk.datamuseum.mobilereg.MobileRegProperties;

/**
 * Utilities.
 */
@Component
public class Utilities {

    private final MobileRegProperties properties;

    /**
     * Constructor.
     */
    public Utilities(
            MobileRegProperties properties) {
        this.properties = properties;

    }

// String regex = "\\d+";
//
// // positive test cases, should all be "true"
// System.out.println("1".matches(regex));
// System.out.println("12345".matches(regex));
// System.out.println("123456789".matches(regex));
//
// // negative test cases, should all be "false"
// System.out.println("".matches(regex));
// System.out.println("foo".matches(regex));
// System.out.println("aa123bb".matches(regex));
    /*
     * Check if string is a number.
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /*
     * Get rid of URL part.
     * Also works if the string is just a number.
     *
     * @param qrInput - The QR value - scanned or entered.
     */
    public Integer evaluateQRString(String qrInput) {
        String prefixProperty = properties.getQrUrlPrefixes();
        String[] prefixes = prefixProperty.split("\\s*,\\s*");

        for (String prefix : prefixes) {
            if (qrInput.startsWith(prefix)) {
                qrInput = qrInput.substring(prefix.length());
                break;
            }
        }
        if (Utilities.isNumeric(qrInput)) {
            return Integer.parseInt(qrInput);
        } else
            throw new IllegalArgumentException("QR code is not numeric");
    }

}
