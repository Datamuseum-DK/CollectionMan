package dk.datamuseum.mobilereg.service;

import static org.assertj.core.api.Assertions.assertThat;

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
}
