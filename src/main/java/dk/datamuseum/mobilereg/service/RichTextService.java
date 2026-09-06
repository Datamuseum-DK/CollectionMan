package dk.datamuseum.mobilereg.service;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.util.HtmlUtils;

/**
 * Converter for rich text.
 * TODO: Merger item patterns to one.
 */
@Slf4j
public class RichTextService {

    /** Pattern for URLs. */
    static Pattern urlPattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:;/~+#-]*[\\w@?^=%&/~+#;])");
    static String  urlReplacement = "<a href=\"$0\">$0</a>";

    /** Pattern for Item numbers. */
    static Pattern itemPattern = Pattern.compile("\\[\\[genstand: *(1[01]0\\d{5})\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  itemReplacement = "<a href=\"$1\">Genstand:$1</a>";

    static Pattern itemPattern1 = Pattern.compile("\\[\\[genstand: *(1[01]0\\d{5}) *\\|([^\\]]+)\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  itemReplacement1 = "<a href=\"$1\">$2</a>";

    static Pattern bitsPattern = Pattern.compile("\\[\\[bits: *(300\\d{5})\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  bitsReplacement = "<a href=\"https://ta.ddhf.dk/wiki/Bits:$1\">Bits:$1</a>";

    static Pattern qrPattern = Pattern.compile("\\[\\[qr: *(500\\d{5})\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  qrReplacement = "<a href=\"https://gier.dk/$1\">QR:$1</a>";

    /* Pattern for Reverse links */
    static Pattern revPattern = Pattern.compile("\\[\\[genstand: *(1[01]0\\d{5}) *\\|?([^\\]]*)\\]\\]",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);

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

    /*
     * Ufærdig.
     */
    private static String replaceText(String text, Pattern pattern, String replacement) {
        Matcher itemMatches = pattern.matcher(text);
        while (itemMatches.find()) {
            String found = itemMatches.group(1);
            log.info("Pattern: {}", found);
            String linkText = itemMatches.group(2).trim();
            if (linkText.equals("")) {
                linkText=found;
            }

        }
        return text;
    }
    /**
     * Find references to other items in the text area.
     * These are always integers and will be stored in a database table.
     *
     * @param plainText - the text field from the database.
     * @param refs - the set of references already found.
     * @return the updated set of references.
     */
    public static void extractRefs(String plainText, Set<Integer> refs) {
        Matcher itemMatches = revPattern.matcher(plainText);
        while (itemMatches.find()) {
            String found = itemMatches.group(1);
            log.debug("Pattern 1: {}", found);
            refs.add(Integer.valueOf(found));
        }
    }
}
