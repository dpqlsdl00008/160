package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;

/**
 *
 * @author cudy_
 */


public class MapleQuestFactory {

    private final static MapleQuestFactory instance = new MapleQuestFactory();
    private final List<Pair<String, Integer>> say = new ArrayList<Pair<String, Integer>>();
    private final MapleDataProvider dataRoot = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Quest.wz"));

    public MapleQuestFactory() {
    initialize();
    }

    public static MapleQuestFactory getInstance() {
        return instance;
    }

    private void initialize() {
        if (!say.isEmpty()) {
            return;
        }
        for (MapleData z : dataRoot.getData("Say.img")) {
            final MapleData sayData = z.getChildByPath("1/yes");
            if (sayData == null) {
                continue;
            }
            for(int count = 0; count < sayData.getChildren().size(); count++)
                say.add(new Pair<String, Integer>(MapleDataTool.getString("" + count, sayData, "null"), Integer.parseInt(z.getName())));
        }
    }
    
    public List<Pair<String, Integer>> getQuest(int quest) {
        final List<Pair<String, Integer>> say = new ArrayList<Pair<String, Integer>>();
        for (int i = 0; i < this.say.size(); i++) {
            if(this.say.get(i).right == quest)
                say.add(this.say.get(i));
        }
        return say;
    }
    
    public List<Pair<String, Integer>> getAllQuest() {
        return say;
    }
}