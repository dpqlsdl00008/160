package server.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogCommiter {

    private FileOutputStream fos = null;
    private final File file;

    protected LogCommiter(long interval) {
        file = new File("updatesql/log.sql");
        try {
            fos = new FileOutputStream(file);
            fos.write(("-- WhiteStar Log SQL -- " + System.getProperty("line.separator") + System.getProperty("line.separator")).getBytes(Charset.forName("MS949")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogCommiter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LogCommiter.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("[LogCommiter] Log Commiter Initialized.");
    }

    public void shutdown() {
        try {
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(LogCommiter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addQuery(String query) {
        try {
            fos.write((query + ";" + System.getProperty("line.separator")).getBytes(Charset.forName("MS949")));
        } catch (IOException ex) {
        }

    }

    protected void commitToDB() {
    }
}
