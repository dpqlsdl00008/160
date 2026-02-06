/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.channel.handler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Start;

/**
 *
 * @author user
 */
public class EventList {

    public static List<Integer> Item = new ArrayList<>();
    public static List<Integer> 기간 = new ArrayList<>();
    public static byte 갯수;
    public static String 위에메시지;
    public static List<Byte> 템개수 = new ArrayList<>();
    public static List<String> 따로메시지 = new ArrayList<>();

    protected static String toUni(String kor) throws UnsupportedEncodingException {
        return new String(kor.getBytes("KSC5601"), "8859_1");
    }

    public static void run() {
        try {
            FileInputStream msg = new FileInputStream("addEventList.ini");
            Properties msgprobs = new Properties();
            msgprobs.load(msg);
            msg.close();
            갯수 = Byte.parseByte(msgprobs.getProperty(toUni("이벤트갯수")));
            위에메시지 = new String(msgprobs.getProperty(toUni("위에메시지")).getBytes("ISO-8859-1"), "euc-kr");
            for (byte i = 0; i <= 갯수; i++) {
                템개수.add(Byte.parseByte(msgprobs.getProperty(toUni("템개수" + String.valueOf(i)))));
                String abb = msgprobs.getProperty(toUni("기간" + String.valueOf(i)));
                기간.add(Integer.parseInt(abb.split(",")[0]));
                기간.add(Integer.parseInt(abb.split(",")[1]));
                // 기간.add(Integer.parseInt(msgprobs.getProperty(toUni("기간" + String.valueOf(i)).split(",")[1])));
                따로메시지.add(new String(msgprobs.getProperty(toUni("따로메시지" + String.valueOf(i))).getBytes("ISO-8859-1"), "euc-kr"));
            }
            for (byte bb = 1; bb <= 템개수.size(); bb++) {
                for (byte bbb = 0; bbb <= 템개수.get(bb - 1); bbb++) {
                    Item.add(Integer.parseInt(msgprobs.getProperty(toUni(String.valueOf(bb - 1) + "번째템코드" + String.valueOf(bbb)))));
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}