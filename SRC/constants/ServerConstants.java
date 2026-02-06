package constants;

public class ServerConstants {

    public static boolean ConnectorSetting = false;

    public static final short MAPLE_VERSION = (short) 160;
    public static final boolean BLOCK_CS = false;
    public static boolean Use_Localhost = true;
    public static final boolean TRIPLE_TRIO = false;
    public static final int Q_MAXLEVEL = 2000001;
    public static String server_Name_Source = "메이플 스토리";
       
    public static enum PlayerGMRank {

        NORMAL('@', 0),
        DONATOR('#', 1),
        SUPERDONATOR('$', 2),
        INTERN('%', 3),
        GM('!', 4),
        SUPERGM('!', 5),
        ADMIN('!', 6);
        private char commandPrefix;
        private int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }
        
        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }
    
     public static enum CommandType {

        NORMAL(0),
        TRADE(1);
        private int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }
}