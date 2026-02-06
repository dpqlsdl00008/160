package tools.wzextractor;

import provider.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 몽키프
 */
public class legendWeaponExtractor {

    private static final MapleDataProvider item_data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/String.wz"));

    public static void main(String[] args) throws FileNotFoundException, IOException {
        StringBuilder mh = new StringBuilder();
        int count = 1;
        System.out.println("[레전드무기 추출기 @author 몽키프]\r\n");
        System.out.println("[레전드무기 추출한당]");
        MapleData pData = item_data.getData("Eqp.img");
        boolean first = true;
        //(itemId >= 1212000 && itemId < 1500000) || (itemId >= 1520000 && itemId < 1540000);
        for (int i = 1212000; i < 1500000; i++) {
            if (pData.getChildByPath("Eqp/Weapon/" + i) != null) {
                if (MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)).contains("레전드")) {
                    if (!first) {
                        mh.append(",").append("\r\n");
                    }
                    mh.append("(9000084, ").append(i).append(", ").append(0).append(", ").append(count).append(", 4310027, 50, 1)");
                    System.out.println(MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)));
                    ++count;
                    first = false;
                }
            }
        }
        for (int i = 1520000; i < 1540000; i++) {
            if (pData.getChildByPath("Eqp/Weapon/" + i) != null) {
                if (MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)).contains("레전드")) {
                    if (!first) {
                        mh.append(",").append("\r\n");
                    }
                    mh.append("(9000084, ").append(i).append(", ").append(0).append(", ").append(count).append(", 4310027, 50, 1)");
                    System.out.println(MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)));
                    ++count;
                    first = false;
                }
            }
        }
        if (!mh.toString().isEmpty()) {
            mh.append(";");
        }
        FileOutputStream cash_string = null;
        cash_string = new FileOutputStream("legendWeaponExtractor.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();
        System.out.println("추출이 완료 되었습니다!");
    }
}
