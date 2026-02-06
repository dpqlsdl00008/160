package server.log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import tools.PacketTester;

public class DBLogger {

    private static final DBLogger instance = new DBLogger();
    private final LogCommiter com = new LogCommiter(10000);

    public static DBLogger getInstance() {
        return instance;
    }

    public void shutdown() {
        com.shutdown();
    }

    public void forceCommitLogToDB() {
        com.commitToDB();
    }

    private String escape(String input) {
        return input.replace("\\", "\\\\").replace("\'", "\\'").replace("\"", "\\\"");
    }
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");

    public static String CurrentReadable_Time() {
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String CurrentReadable_Time1() {
        return sdf_.format(Calendar.getInstance().getTime());
    }

    public void logChat(LogType.Chat type, int cid, String charname, String message, String etc) {
        //EtcServer.broadcast(EtcPacket.getChatResult("[" + type.name() + "] " + charname + " : ", message + "코드 : " + etc + ""));
        String jgys = "";
        if (type == LogType.Chat.General) {
            jgys = "모두에게";
        }
        if (type == LogType.Chat.Messenger) {
            jgys = "메신저";
        }
        if (type == LogType.Chat.Whisper) {
            jgys = etc + "에게";
        }
        if (type == LogType.Chat.Megaphone) {
            jgys = "확성기";
        }
        if (type == LogType.Chat.Note) {
            jgys = "쪽지";
        }
        if (type == LogType.Chat.Buddy) {
            jgys = "친구에게";
        }
        if (type == LogType.Chat.Guild) {
            jgys = "길드에게";
        }
        if (type == LogType.Chat.Trade) {
            jgys = "교환";
        }
        if (type == LogType.Chat.Party) {
            jgys = "파티에게";
        }
        if (type == LogType.Chat.Alliance) {
            jgys = "연합에게";
        }
        //FileoutputUtil.log("Log/Etc/[" + getDCurrentTime() + "] Chat.txt", "[" + CurrentReadable_Time() + "]\r\n캐릭터 : " + escape(charname) + "" + escape(etc) + "\r\n내용 : [" + jgys + "] " + escape(message) + "\r\n");
        PacketTester.logchat("" + escape(charname) + " : (" + jgys + ") " + escape(message) + "");
        //com.addQuery(String.format("["+ CurrentReadable_Time() +"] 캐릭터ID : "+cid+"  /  닉네임 : "+escape(charname)+"  /  메세지 : "+escape(message)+"  /  맵 : "+escape(etc)+"\r\n========================================================================================================================================"));
        //ConnecterServerHandler.logchat("캐릭터ID : " + cid + "  /  닉네임 : " + escape(charname) + "  /  메세지 : " + escape(message) + "  /  맵 : " + escape(etc) + "\r\n");
    }

    public void logItem(LogType.Item type, int cid, String name, int itemid, int quantity, String itemname, long meso, String etc) {
        //FileoutputUtil.log("Log/Etc/[" + getDCurrentTime() + "] Item_Acheive.txt", "[" + CurrentReadable_Time() + "]\r\n캐릭터 : " + escape(name) + "\r\n아이템 : " + escape(itemname) + "(" + itemid + ") " + quantity + "개\r\n메소 : " + getNum(meso) + " 메소\r\n" + escape(etc) + "\r\n");
    }

    public void logTrade(LogType.Trade type, int cid, String name, String partnername, String item, String etc) {
        if (name.equals(partnername)) {
            return;
        }
        String jgys = "";
        if (type == LogType.Trade.Trade) {
            jgys = "교환";
        }
        if (type == LogType.Trade.Duey) {
            jgys = "택배";
        }
        //FileoutputUtil.log("Log/Etc/[" + getDCurrentTime() + "] Trade.txt", "\r\n[" + CurrentReadable_Time() + "]\r\n캐릭터 : " + escape(partnername) + " → " + escape(name) + "\r\n구분 : " + jgys + "\r\n아이템 : " + escape(item) + "");
    }

    public String getNum(long dd) {
        String df = new DecimalFormat("###,###,###,###,###,###").format(dd);
        return df;
    }
}