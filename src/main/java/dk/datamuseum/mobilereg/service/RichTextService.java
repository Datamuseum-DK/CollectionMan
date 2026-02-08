package dk.datamuseum.mobilereg.service;

import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

/**
 * Converter for rich text.
 */
public class RichTextService {

    /** Pattern for URLs. */
    static Pattern urlPattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:;/~+#-]*[\\w@?^=%&/~+#;])");
    static String  urlReplacement = "<a href=\"$0\">$0</a>";

    /** Pattern for Item numbers. */
    //static Pattern itemPattern = Pattern.compile("(^|\\G|[\\s\\p{Punct}&&[^/]])(1[01]0[01]\\d{4})([\\s\\p{Punct}]|$)", Pattern.MULTILINE);
    static Pattern itemPattern = Pattern.compile("\\[\\[genstand: *(1[01]0[01]\\d{4})\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  itemReplacement = "<a href=\"$1\">Genstand:$1</a>";

    static Pattern itemPattern1 = Pattern.compile("\\[\\[genstand: *(1[01]0[01]\\d{4}) *\\|([^\\]]+)\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  itemReplacement1 = "<a href=\"$1\">$2</a>";

    static Pattern bitsPattern = Pattern.compile("\\[\\[bits: *(300[01]\\d{4})\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  bitsReplacement = "<a href=\"https://ta.ddhf.dk/wiki/Bits:$1\">Bits:$1</a>";

    static Pattern qrPattern = Pattern.compile("\\[\\[qr: *(500[01]\\d{4})\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  qrReplacement = "<a href=\"https://gier.dk/$1\">QR:$1</a>";

    /**
     * Produce HTML with detected links. A simple markdown syntax.
     * item numbers are detected, URLs are detected.
     *
     * @param plainText - the text field from the database.
     * @return HTML escaped text with some HTML tags.
     */
    public static String richText(String plainText) {
        String richDesc = HtmlUtils.htmlEscape(plainText, "UTF-8");
        richDesc = urlPattern.matcher(richDesc).replaceAll(urlReplacement);
        richDesc = itemPattern1.matcher(richDesc).replaceAll(itemReplacement1);
        richDesc = itemPattern.matcher(richDesc).replaceAll(itemReplacement);
        richDesc = bitsPattern.matcher(richDesc).replaceAll(bitsReplacement);
        richDesc = qrPattern.matcher(richDesc).replaceAll(qrReplacement);

        return richDesc;
    }
}
