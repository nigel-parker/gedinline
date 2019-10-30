package gedinline.lexical

import spock.lang.*

class AnselInputStreamReaderSpec extends Specification {

    static final int REPLACEMENT_CHARACTER = 0xFFFD;

    void 'test Ansel reader values'() {

        expect:

            ByteArrayInputStream inputStream = new ByteArrayInputStream([inputByte] as byte[])
            AnselInputStreamReader inputStreamReader = new AnselInputStreamReader(inputStream)
            def outputAsInt = output instanceof String ? Character.codePointAt(output as String, 0) : output

            inputStreamReader.read() == outputAsInt
            inputStreamReader.read() == -1

            inputStreamReader.close()
            inputStream.close()

        where:

            inputByte || output

            0x00      || 0x0000
            0x01      || 0x0001
            //...
            0x7F      || 0x007F
            0x80      || REPLACEMENT_CHARACTER
            0x80      || REPLACEMENT_CHARACTER
            0x81      || REPLACEMENT_CHARACTER
            0x82      || REPLACEMENT_CHARACTER
            0x83      || REPLACEMENT_CHARACTER
            0x84      || REPLACEMENT_CHARACTER
            0x85      || REPLACEMENT_CHARACTER
            0x86      || REPLACEMENT_CHARACTER
            0x87      || REPLACEMENT_CHARACTER
            0x88      || REPLACEMENT_CHARACTER
            0x89      || REPLACEMENT_CHARACTER
            0x8A      || REPLACEMENT_CHARACTER
            0x8B      || REPLACEMENT_CHARACTER
            0x8C      || REPLACEMENT_CHARACTER
            0x8D      || 0x200D // ZERO WIDTH JOINER
            0x8E      || 0x200C // ZERO WIDTH NON-JOINER
            0x8F      || REPLACEMENT_CHARACTER
            0x90      || REPLACEMENT_CHARACTER
            0x91      || REPLACEMENT_CHARACTER
            0x92      || REPLACEMENT_CHARACTER
            0x93      || REPLACEMENT_CHARACTER
            0x94      || REPLACEMENT_CHARACTER
            0x95      || REPLACEMENT_CHARACTER
            0x96      || REPLACEMENT_CHARACTER
            0x97      || REPLACEMENT_CHARACTER
            0x98      || REPLACEMENT_CHARACTER
            0x99      || REPLACEMENT_CHARACTER
            0x9A      || REPLACEMENT_CHARACTER
            0x9B      || REPLACEMENT_CHARACTER
            0x9C      || REPLACEMENT_CHARACTER
            0x9D      || REPLACEMENT_CHARACTER
            0x9E      || REPLACEMENT_CHARACTER
            0x9F      || REPLACEMENT_CHARACTER
            0xA0      || REPLACEMENT_CHARACTER
            0xA1      || 0x0141 // 'LATIN CAPITAL LETTER L WITH STROKE
            0xA2      || 'Ø'
            0xA3      || 'Ð'
            0xA4      || 'Þ'
            0xA5      || 'Æ'
            0xA6      || 0x0152 // LATIN CAPITAL LIGATURE OE
            0xA7      || 0x02B9 // MODIFIER LETTER PRIME
            0xA8      || '·'
            0xA9      || 0x266D // MUSIC FLAT SIGN
            0xAA      || '®'
            0xAB      || '±'
            0xAC      || 0x01A0 // LATIN CAPITAL LETTER O WITH HORN
            0xAD      || 0x01AF // LATIN CAPITAL LETTER U WITH HORN
            0xAE      || 0x02BC // MODIFIER LETTER RIGHT HALF RING
            0xAF      || REPLACEMENT_CHARACTER
            0xB0      || 0x02BB // MODIFIER LETTER LEFT HALF RING
            0xB1      || 0x0142 // LATIN SMALL LETTER L WITH STROKE
            0xB2      || 'ø'
            0xB3      || 0x0111 // LATIN SMALL LETTER D WITH STROKE'
            0xB4      || 'þ'
            0xB5      || 'æ'
            0xB6      || 0x0153 // LATIN SMALL LIGATURE OE
            0xB7      || 0x02BA // MODIFIER LETTER DOUBLE PRIME
            0xB8      || 0x0131 // LATIN SMALL LETTER DOTLESS I
            0xB9      || '£'
            0xBA      || 'ð'
            0xBB      || REPLACEMENT_CHARACTER
            0xBC      || 0x01A1 // LATIN SMALL LETTER O WITH HORN
            0xBD      || 0x01B0 // LATIN SMALL LETTER U WITH HORN
            0xBE      || REPLACEMENT_CHARACTER
            0xBF      || REPLACEMENT_CHARACTER
            0xC0      || '°'
            0xC1      || 0x2113 // SCRIPT SMALL L
            0xC2      || 0x2117 // SOUND RECORDING COPYRIGHT
            0xC3      || '©'
            0xC4      || 0x266F // MUSIC SHARP SIGN
            0xC5      || '¿'
            0xC6      || '¡'
            0xC7      || REPLACEMENT_CHARACTER
            0xC8      || REPLACEMENT_CHARACTER
            0xC9      || REPLACEMENT_CHARACTER
            0xCA      || REPLACEMENT_CHARACTER
            0xCB      || REPLACEMENT_CHARACTER
            0xCC      || REPLACEMENT_CHARACTER
            0xCD      || REPLACEMENT_CHARACTER
            0xCE      || REPLACEMENT_CHARACTER
            0xCF      || 'ß'
            0xD0      || REPLACEMENT_CHARACTER
            0xD1      || REPLACEMENT_CHARACTER
            0xD2      || REPLACEMENT_CHARACTER
            0xD3      || REPLACEMENT_CHARACTER
            0xD4      || REPLACEMENT_CHARACTER
            0xD5      || REPLACEMENT_CHARACTER
            0xD6      || REPLACEMENT_CHARACTER
            0xD7      || REPLACEMENT_CHARACTER
            0xD8      || REPLACEMENT_CHARACTER
            0xD9      || REPLACEMENT_CHARACTER
            0xDA      || REPLACEMENT_CHARACTER
            0xDB      || REPLACEMENT_CHARACTER
            0xDC      || REPLACEMENT_CHARACTER
            0xDD      || REPLACEMENT_CHARACTER
            0xDE      || REPLACEMENT_CHARACTER
            0xDF      || REPLACEMENT_CHARACTER
            0xE0      || 0x0309 // COMBINING HOOK ABOVE
            0xE1      || 0x0300 // COMBINING GRAVE ACCENT
            0xE2      || 0x0301 // COMBINING ACUTE ACCENT
            0xE3      || 0x0302 // COMBINING CIRCUMFLEX ACCENT
            0xE4      || 0x0303 // COMBINING TILDE
            0xE5      || 0x0304 // COMBINING MACRON
            0xE6      || 0x0306 // COMBINING BREVE
            0xE7      || 0x0307 // COMBINING DOT ABOVE
            0xE8      || 0x0308 // COMBINING DIAERESIS ABOVE
            0xE9      || 0x030C // COMBINING CARON
            0xEA      || 0x030A // COMBINING RING ABOVE
            0xEB      || 0xFE20 // COMBINING LIGATURE LEFT HALF
            0xEC      || 0xFE21 // COMBINING LIGATURE RIGHT HALF
            0xED      || 0x0315 // COMBINING COMMA ABOVE RIGHT
            0xEE      || 0x030B // COMBINING DOUBLE ACUTE ACCENT
            0xEF      || 0x0310 // COMBINING CANDRABINDU
            0xF0      || 0x0327 // COMBINING CEDILLA
            0xF1      || 0x0328 // COMBINING OGONEK
            0xF2      || 0x0323 // COMBINING DOT BELOW
            0xF3      || 0x0324 // COMBINING DIAERESIS BELOW
            0xF4      || 0x0325 // COMBINING RING BELOW
            0xF5      || 0x0333 // COMBINING DOUBLE LOW LINE
            0xF6      || 0x0332 // COMBINING LOW LINE
            0xF7      || 0x0326 // COMBINING COMMA BELOW
            0xF8      || 0x031C // COMBINING LEFT HALF RING BELOW
            0xF9      || 0x032E // COMBINING BREVE BELOW
            0xFA      || 0xFE22 // COMBINING DOUBLE TILDE LEFT HALF
            0xFB      || 0xFE23 // COMBINING DOUBLE TILDE RIGHT HALF
            0xFC      || REPLACEMENT_CHARACTER
            0xFD      || REPLACEMENT_CHARACTER
            0xFE      || 0x0313 // COMBINING COMMA ABOVE
            0xFF      || REPLACEMENT_CHARACTER
    }
}
