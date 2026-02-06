package scripting;

import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import client.MapleClient;
import constants.ScriptConstants;
import java.util.LinkedList;
import java.util.Queue;

import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<MapleClient, NPCConversationManager>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final void start(final MapleClient c, int npc) {
        start(c, npc, null);
    }

    public final void start(final MapleClient c, int npc, String customScript) {
        start(c, npc, customScript, -1);
    }

    public final void start(final MapleClient c, final int npc, String customScript, int oid) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (cms.containsKey(c)) {
                dispose(c);
            }
            if (!cms.containsKey(c)/* && c.canClickNPC()*/) {
                Invocable iv;
                if (customScript == null) {
                    iv = getInvocable("npc/" + npc + ".js", c);
                    if (ScriptConstants.isMeisterVillageNpc(npc)) {
                        iv = getInvocable("npc/meisterVillage/" + npc + ".js", c);
                    }
                    if (ScriptConstants.isPartyQuestNpc(npc)) {
                        iv = getInvocable("npc/partyQuest/" + npc + ".js", c);
                    }
                    if (ScriptConstants.isExpeditionNpc(npc)) {
                        iv = getInvocable("npc/bossExpedition/" + npc + ".js", c);
                    }
                } else {
                    iv = getInvocable("item/" + customScript + ".js", c);
                }
                if (iv == null && customScript != null) {
                    iv = getInvocable("npc/" + customScript + ".js", c);
                }
                if (iv == null) {
                    iv = getInvocable("field/" + customScript + ".js", c);
                    if (customScript == null) {
                        iv = getInvocable("npc/notcoded.js", c);
                    }
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte) -1, iv);
                if (oid > 0) {
                    cm.setObjectId(oid);
                }
                cms.put(c, cm);
//                scriptengine.eval("load('nashorn:mozilla_compat.js');");
                scriptengine.put("cm", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                //System.out.println("NPCID started: " + npc);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                c.getPlayer().dropMessage(1, "잠시 후 다시 시도해 주세요.");
            }
        } catch (final Exception e) {
            System.err.println("[NOT CORDING SCRIPTS] NPC : " + npc + " (" + customScript + ")\r\nERROR : " + e + "\r\b");
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + npc + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (c.getPlayer().isGM() == true) {
            c.getPlayer().dropMessage(5, "- MODE : " + mode + " | TYPE : " + type + " | SELECTION : " + selection);
        }
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {

                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final Exception e) {
                System.err.println("Error executing NPC script. NPC ID : " + cm.getNpc() + ":" + e);
                dispose(c);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + cm.getNpc() + "." + e);
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (ScriptConstants.isTutorialQuest(quest) == true) {
                    iv = getInvocable("quest/userTutorial/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isStoryQuest(quest) == true) {
                    iv = getInvocable("quest/userStory/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isJobQuest(quest) == true) {
                    iv = getInvocable("quest/userJob/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isSkillQuest(quest) == true) {
                    iv = getInvocable("quest/userSkill/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isCrossHunterQuest(quest) == true) {
                    iv = getInvocable("quest/crossHunter/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isMushCatleQuest(quest) == true) {
                    iv = getInvocable("quest/mushCatle/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isNeoCityQuest(quest) == true) {
                    iv = getInvocable("quest/neoCity/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isArcaneRiverQuest(quest) == true) {
                    iv = getInvocable("quest/arcaneRiver/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isMedalQuest(quest) == true) {
                    c.getPlayer().dropMessage(-1, "<" + MapleQuest.getInstance(quest).getName() + "> 칭호를 획득 하셨습니다.");
                    MapleQuest.getInstance(quest).forceComplete(c.getPlayer(), npc);
                    dispose(c);
                    return;
                }

                if (iv == null) {
                    String say = "#eQUEST : #b" + MapleQuest.getInstance(quest).getName() + " (" + quest + ")#k\r\nTYPE : #bSTART QUEST SCRIPT#k#n\r\n\r\n해당 퀘스트는 현재 구현 중에 있습니다.";
                    c.sendPacket(CField.NPCPacket.getNPCTalk(2007, (byte) 0, say, "00 01", (byte) 0));
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 0, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);
                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0);
            }
        } catch (final Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        if (c.getPlayer().isGM() == true) {
            c.getPlayer().dropMessage(5, "- MODE : " + mode + " | TYPE : " + type + " | SELECTION : " + selection);
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null) && quest != 23205) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c) && c.canClickNPC()) {
                Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (ScriptConstants.isTutorialQuest(quest) == true) {
                    iv = getInvocable("quest/userTutorial/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isStoryQuest(quest) == true) {
                    iv = getInvocable("quest/userStory/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isJobQuest(quest) == true) {
                    iv = getInvocable("quest/userJob/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isSkillQuest(quest) == true) {
                    iv = getInvocable("quest/userSkill/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isCrossHunterQuest(quest) == true) {
                    iv = getInvocable("quest/crossHunter/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isMushCatleQuest(quest) == true) {
                    iv = getInvocable("quest/mushCatle/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isNeoCityQuest(quest) == true) {
                    iv = getInvocable("quest/neoCity/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isArcaneRiverQuest(quest) == true) {
                    iv = getInvocable("quest/arcaneRiver/" + quest + ".js", c, true);
                }
                if (ScriptConstants.isMedalQuest(quest) == true) {
                    c.getPlayer().dropMessage(-1, "<" + MapleQuest.getInstance(quest).getName() + "> 칭호를 획득 하셨습니다.");
                    MapleQuest.getInstance(quest).forceComplete(c.getPlayer(), npc);
                    dispose(c);
                    return;
                }

                if (iv == null) {
                    String say = "#eQUEST : #b" + MapleQuest.getInstance(quest).getName() + " (" + quest + ")#k\r\nTYPE : #bEND QUEST SCRIPT#k#n\r\n\r\n해당 퀘스트는 현재 구현 중에 있습니다.";
                    c.sendPacket(CField.NPCPacket.getNPCTalk(2007, (byte) 0, say, "00 01", (byte) 0));
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 1, iv);

                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        if (c.getPlayer().isGM() == true) {
            c.getPlayer().dropMessage(5, "- MODE : " + mode + " | TYPE : " + type + " | SELECTION : " + selection);
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        dispose(c, null);
    }

    public final void dispose(final MapleClient c, String custom) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            if (npccm.getType() == -1) {
                c.removeScriptEngine("scripts/npc/" + npccm.getNpc() + ".js");
                c.removeScriptEngine("scripts/npc/notcoded.js");
                if (custom != null) {
                    c.removeClickedNPC();
                    c.removeScriptEngine("scripts/npc/" + custom + ".js");
                    c.removeScriptEngine("scripts/field/" + custom + ".js");
                }
            } else {
                c.removeScriptEngine("scripts/quest/" + npccm.getQuest() + ".js");
                if (custom != null) {
                    c.removeScriptEngine("scripts/field/" + custom + ".js");
                }
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
        c.getSession().write(CWvsContext.enableActions());
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }

    public void scriptClear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
