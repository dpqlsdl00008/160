package tools.wzextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

/**
 *
 * @author 몽키프
 */
public class CashExtractor {
    
    private static final MapleDataProvider item_data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/Character.wz"));
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        StringBuilder mh = new StringBuilder();     
        int item_string;
        System.out.println("[CashExtractor @author 몽키프]\r\n");
        System.out.println("[Accessory, Cap, Cape, Coat, Glove, LongCoat, Pants, Ring, Shield, Shoes, Weapon]");
        MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/String.wz"));
        MapleData pData = data.getData("Eqp.img");                   
        if (pData == null || item_data == null) {
            System.out.println("WZ데이터가 없습니다!");
        }
        for (MapleDataDirectoryEntry mdde : item_data.getRoot().getSubdirectories()) {
            if ("Afterimage".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("Android".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("Bits".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("Dragon".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("Face".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("Hair".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("Mechanic".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("PetEquip".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            } else if ("TamingMob".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            }
            for (MapleDataEntry mde : mdde.getFiles()) {
                MapleData d1 = item_data.getData(mdde.getName() + "/" + mde.getName());
                int cash = MapleDataTool.getIntConvert("info/cash", d1, 0);
                if (cash == 1) {
                    item_string = Integer.parseInt(mde.getName().substring(0, 8));
                    if (pData.getChildByPath("Eqp/"+mdde.getName()+"/"+item_string) != null) {
                        mh.append(item_string).append("//").append(MapleDataTool.getString("name", pData.getChildByPath("Eqp/"+mdde.getName()+"/"+item_string)) != null ? MapleDataTool.getString("name", pData.getChildByPath("Eqp/"+mdde.getName()+"/"+item_string)) : "").append("\r\n");
                    } else if (pData.getChildByPath("Eqp/"+mdde.getName()+"/"+item_string) == null) {
                        System.out.println(item_string + "은 스트링데이터가 존재하지 않습니다!");
                        mh.append(item_string).append("//").append("\r\n");
                    }
                }
            }
        }
        FileOutputStream cash_string = null;
        cash_string = new FileOutputStream("CashExtractor.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();
        System.out.println("추출이 완료 되었습니다!");
    }
}
