package dk.datamuseum.mobilereg.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TreeSet;
import org.junit.Test;

import static dk.datamuseum.mobilereg.service.RichTextService.*;

public class RichTextServiceTest {

    @Test
    public void bitsTexts() {
        assertThat(richText("[[Bits:30003000]]"))
            .isEqualTo("<a href=\"https://ta.ddhf.dk/wiki/Bits:30003000\">Bits:30003000</a>");
    }
    
    @Test
    public void genstandMarkup() {
        //assertThat(richText("11001100")).isEqualTo("<a href=\"11001100\">11001100</a>");
        assertThat(richText("[[Genstand: 11001100]]"))
            .isEqualTo("<a href=\"11001100\">Genstand:11001100</a>");
        assertThat(richText("[[Genstand:11001100]],[[genstand:10000032]]\n\n"))
            .isEqualTo("<a href=\"11001100\">Genstand:11001100</a>,"
            + "<a href=\"10000032\">Genstand:10000032</a>\n\n");
    }

    @Test
    public void genstandMarkupText() {
        assertThat(richText("[[Genstand: 11001100|Siemens P6000]]"))
            .isEqualTo("<a href=\"11001100\">Siemens P6000</a>");
        assertThat(richText("[[Genstand: 11001100|]]"))
            .isEqualTo("[[Genstand: 11001100|]]");
        assertThat(richText("[[Genstand: 11001100 | Siemens P6000]]"))
            .isEqualTo("<a href=\"11001100\"> Siemens P6000</a>");
    }

    @Test
    public void urlTests() {
        assertThat(richText(" https://www.ddhf.dk "))
            .isEqualTo(" <a href=\"https://www.ddhf.dk\">https://www.ddhf.dk</a> ");
        assertThat(richText("https://www.old-computers.com/museum/computer.asp?c=488&st=1"))
            .isEqualTo("<a href=\"https://www.old-computers.com/museum/"
                + "computer.asp?c=488&amp;st=1\">https://www.old-computers.com/museum/"
                + "computer.asp?c=488&amp;st=1</a>");
    }

    @Test
    public void qrTests() {
        assertThat(richText("[[QR:50001694]]"))
            .isEqualTo("<a href=\"https://gier.dk/50001694\">QR:50001694</a>");
    }

    /**
     * Test that extractRefs finds all item references.
     */
    @Test
    public void multipleRefsTest() {
        TreeSet<Integer> refs = new TreeSet<Integer>();
        String plainText = """
        ABC800 mikrocomputer system med ABC815 monitor og [[genstand:11004607|ABC830 dobbelt 5.25" diskettedrev]].
        Endvidere [[Genstand:11004608|Luxor ABC80 EPSON MX-80F/T printer]].

        Monitor sandsynligvis i [[genstand:11002501]].
        """;
        extractRefs(plainText, refs);
        assertThat(refs.size()).isEqualTo(3);
        assertThat(refs.contains(11004608)).isTrue();
    }

}
