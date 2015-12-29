package at.wien.ma14.pvzd.verifysigapitest;

import java.util.List;
import javax.smartcardio.*;
import org.junit.Test;
import org.apache.commons.codec.binary.Hex;


public class JavaSCIOtest {

    private void cardConnect(CardTerminal terminal) throws Exception {
        try {
            System.out.println("Card present, connecting ..");
            Card card = null;
            card = terminal.connect("*");
            System.out.println("Connected to card: " + card);
            CardChannel channel = card.getBasicChannel();
            byte[] apdu = {(byte) 0x80, (byte) 0xC0, 0x02, (byte) 0xA1};  // 80 C0 02 A1: GEMPLUS MPCOS-EMV / Get Info / Chip SN
            ResponseAPDU r = channel.transmit(new CommandAPDU(apdu));
            System.out.println("Response on get Chip SN request: " + Hex.encodeHexString(r.getBytes()));
            card.disconnect(false);
        } catch (javax.smartcardio.CardException e) {
            System.out.println("Could not conenct to card: " + e);
        }

    }


    @Test
    public void test1() throws Exception {
        System.out.println("JavaSmartcardIO test build 12");
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        if (terminals.size() == 0) {
            System.out.println("No PCSC terminal found.");
        }
        else {
            System.out.println("Terminals: " + terminals);
            CardTerminal terminal = terminals.get(0);
            System.out.println("Testing for card in the first terminal");
            if (terminal.isCardPresent()) {
                cardConnect(terminal);
            } else {
                System.out.println("Card not present, waiting 10s for card to be inserted");
                if (terminal.waitForCardPresent(10000)) {
                    cardConnect(terminal);
                }
                else {
                    System.out.println("Timeout, card not inserted");
                }
            }
        }
    }
}