package tools.wzextractor;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 몽키프
 */
public class ChairShopExtractor {

    private static final MapleDataProvider item_data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/String.wz"));

    public static void main(String[] args) throws FileNotFoundException, IOException {
        List<String> 겹치지마 = new ArrayList<>();
        StringBuilder mh = new StringBuilder();
        int count = 1;
        String item_string = "";
        System.out.println("[의자추출한당 @author 몽키프]\r\n");
        System.out.println("[의자 추출한당]");
        MapleData pData = item_data.getData("Ins.img");
        boolean first = true;
        //(itemId >= 1212000 && itemId < 1500000) || (itemId >= 1520000 && itemId < 1540000);
        for (MapleData ddd : pData.getChildren()) {
            for (MapleData dd : ddd) {
                //System.out.println(ddd.getName());
                //for (MapleData d : dd) {
                //for (MapleData c : d) {
                //(c.getName()) {

                if (dd.getName().equals("name")) {
                    int string_itemcode_to_intvalue = Integer.valueOf(ddd.getName());
                    if (!겹치지마.contains(MapleDataTool.getString("name", ddd)) && (string_itemcode_to_intvalue >= 3010000 && string_itemcode_to_intvalue <= 3010590)) {
                        if (!first) {
                            mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                        }
                        mh.append("(308, ").append(ddd.getName()).append(", ").append(1).append(", ").append(count).append(", 0, 0, 3)");
                        item_string = MapleDataTool.getString("name", ddd);
                        System.out.println(MapleDataTool.getString("name", ddd));
                        겹치지마.add(MapleDataTool.getString("name", ddd));
                        ++count;
                        if (first)
                            first = false;
                    }
                }
                //}
                //}
                // }
                //if (pData.getChildByPath(dd.toString()) != null) {
                //    if (MapleDataTool.getString("name", dd) != null) {
                // System.out.println(MapleDataTool.getString("name", dd));
                //    }
                //}
                //prob = prob.substring(4);
                //int da = (int) Math.round(Double.parseDouble(prob) * 1000000D);
            }
        }
        /*
        for (int i = 1212000; i < 1500000; i++) {
            if (pData.getChildByPath("Eqp/Weapon/" + i) != null) {
                if (MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)).contains("프로스티")) {
                    if (!first) {
                        mh.append(",").append("\r\n");
                    }
                    mh.append("(306, ").append(i).append(", ").append(0).append(", ").append(count).append(", 4310066, 80, 1)");
                    System.out.println(MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)));
                    ++count;
                    first = false;
                }
            }
        }
        for (int i = 1520000; i < 1540000; i++) {
            if (pData.getChildByPath("Eqp/Weapon/" + i) != null) {
                if (MapleDataTool.getString("name", pData.getChildByPath("Eqp/Weapon/" + i)).contains("프로스티")) {
                    if (!first) {
                        mh.append(",").append("\r\n");
                    }
                    mh.append("(306, ").append(i).append(", ").append(0).append(", ").append(count).append(", 4310066, 80, 1)");
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
        cash_string = new FileOutputStream("frostyWeaponExtractor.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();
        */
        if (!mh.toString().isEmpty()) {
            mh.append(";").append("/*").append(item_string).append("*/");
        }
        FileOutputStream cash_string = null;
        cash_string = new FileOutputStream("ChairShop.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();
        System.out.println("추출이 완료 되었습니다!");
    }
}
