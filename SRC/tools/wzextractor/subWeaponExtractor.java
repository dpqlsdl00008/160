package tools.wzextractor;

import provider.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author 몽키프
 */
public class subWeaponExtractor {
    
    private static final MapleDataProvider item_data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/Character.wz"));

    public static void main(String[] args) throws FileNotFoundException, IOException {
        StringBuilder mh = new StringBuilder();
        int item_string;
        System.out.println("[SubWeapon추출기 @author 몽키프]\r\n");
        System.out.println("[보조무기 추출한당]");
        for (MapleDataDirectoryEntry mdde : item_data.getRoot().getSubdirectories()) {
            if (!"Weapon".equals(mdde.getName())) {
                System.out.println("제외데이터 " + mdde.getName());
                continue;
            }
            for (MapleDataEntry mde : mdde.getFiles()) {
                MapleData d1 = item_data.getData(mdde.getName() + "/" + mde.getName());
                int reqLevel = MapleDataTool.getIntConvert("info/reqLevel", d1, 0);
                item_string = Integer.parseInt(mde.getName().substring(0, 8));
                if (item_string == 1352004 || item_string == 1352005 || item_string == 1352006 || item_string == 1352007 ||
                        item_string == 1352104 || item_string == 1352105 || item_string == 1352106 || item_string == 1352107 ||
                        item_string == 1352203 || item_string == 1352213 || item_string == 1352223 || item_string == 1352233 ||
                        item_string == 1352243 || item_string == 1352253 || item_string == 1352263 || item_string == 1352273 ||
                        item_string == 1352283 || item_string == 1352293 || item_string == 1352404 || item_string == 1352504 ||
                        item_string == 1352600 || item_string == 1352903 || item_string == 1352913 || item_string == 1352923 ||
                        item_string == 1352933 || item_string == 1352943 || item_string == 1352953 || item_string == 1352963 ||
                        item_string == 1352973 || item_string == 1353000) continue;
                if (item_string >= 1352000 && item_string <= 1353004) {
                    mh.append("(305, ").append(item_string).append(", ").append(reqLevel == 30 ? 80000 : reqLevel == 60 ? 330000 : reqLevel == 100 ? 580000 : 0).append(", 0, 0, 0, 0),").append("\r\n");
                }
            }
        }
        FileOutputStream cash_string = null;
        cash_string = new FileOutputStream("SubweaponExtractor.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();
        System.out.println("추출이 완료 되었습니다!");
    }
}
