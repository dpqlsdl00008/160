package connector;

import constants.ServerConstants;
import scripting.NPCConversationManager;

import java.util.Collection;

public class connectorThread extends Thread {

    @Override
    public void run() {
        if (ServerConstants.ConnectorSetting) {
            Collection<connectorClient> clients = connectorServer.getInstance().getClients();
            for (connectorClient client : clients) {
                if (!client.getId().equalsIgnoreCase("")) {
                    if (System.currentTimeMillis() - client.getLastPing() > 25000) {
                        System.out.println(client.getLastPing());
                        client.closeSession();
                        connectorWalker.setAlive(client.getId(), false);
                        String data = "아이디 : " + client.getId() + " 아이피 : " + client.getIP() + " 서스펜드 시도\r\n";
                        NPCConversationManager.writeLog("Log/Server/Heartbeat.log", data, true);
                    }
                }
            }
        }
    }
}
