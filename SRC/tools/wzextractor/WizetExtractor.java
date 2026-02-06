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
public class WizetExtractor {

    private static final MapleDataProvider item_data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/String.wz"));

    public static void main(String[] args) throws FileNotFoundException, IOException {
        List<String> 겹치지마 = new ArrayList<>();
        StringBuilder mh = new StringBuilder();
        int z = 1;
        int x = 1;
        int c = 1;
        int v = 1;
        int b = 1;
        int n = 1;
        int m = 1;
        int a = 1;
        int s = 1;
        int d = 1;
        int f = 1;
        int g = 1;
        int h = 1;
        int j = 1;
        int k = 1;
        int l = 1;
        int q = 1;
        int shopsnumber = 307;
        String item_string = "";
        System.out.println("[영자옷추출한당 @author 몽키프]\r\n");
        System.out.println("[영자옷추출한당]");
        MapleData pData = item_data.getData("Eqp.img");
        boolean first = true;
        //(itemId >= 1212000 && itemId < 1500000) || (itemId >= 1520000 && itemId < 1540000);
        mh.append("INSERT INTO `shopitems` (`shopid`,`itemid`,`price`, `position`, `reqitem`, `reqitemq`, `rank`) VALUES").append("\r\n");
        for (MapleData ddd : pData.getChildren()) {
            for (MapleData dd : ddd) {
                //System.out.println(ddd.getName());
                //for (MapleData d : dd) {
                //for (MapleData c : d) {
                //(c.getName()) {
                if (dd.getName().equals("Face") || dd.getName().equals("Hair")) {
                    continue;
                }
                System.out.println(dd.getName());
                shopsnumber++;
                for (MapleData na : dd) {
                        if (dd.getName().equals("Cap")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(z).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            ++z;
                            if (z > 1000) {
                                z = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Cape")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(x).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++x;
                            if (x > 1000) {
                                x = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Coat")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(c).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++c;
                            if (c > 1000) {
                                c = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Dragon")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(v).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++v;
                            if (v > 1000) {
                                v = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Mechanic")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(b).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++b;
                            if (b > 1000) {
                                b = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Glove")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(n).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++n;
                            if (n > 1000) {
                                n = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Longcoat")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(m).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++m;
                            if (m > 1000) {
                                m = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Pants")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(a).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++a;
                            if (a > 1000) {
                                a = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("PetEquip")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(s).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++s;
                            if (s > 1000) {
                                s = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Shield")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(d).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++d;
                            if (d > 1000) {
                                d = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Shoes")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(f).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++f;
                            if (f > 1000) {
                                f = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Taming")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(g).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++g;
                            if (g > 1000) {
                                g = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Weapon")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(h).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++h;
                            if (h > 1000) {
                                h = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Ring")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(j).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++j;
                            if (j > 1000) {
                                j = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Android")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(k).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++k;
                            if (k > 1000) {
                                k = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Accessory")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(l).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++l;
                            if (l > 1000) {
                                l = 1;
                                shopsnumber++;
                            }
                            if (first)
                                first = false;
                        } else if (dd.getName().equals("Bits")) {
                            if (!first) {
                                mh.append(",").append("/*").append(item_string).append("*/").append("\r\n");
                            }
                            mh.append("(").append(shopsnumber).append(", ").append(na.getName()).append(", ").append(1).append(", ").append(q).append(", 0, 0, 0)");
                            item_string = MapleDataTool.getString("name", na);
                            System.out.println(MapleDataTool.getString("name", na));
                            겹치지마.add(MapleDataTool.getString("name", na));
                            ++q;
                            if (q > 1000) {
                                q = 1;
                                shopsnumber++;
                            }
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
        if (!mh.toString().isEmpty()) {
            mh.append(";");
        }
        FileOutputStream cash_string = null;
        cash_string = new FileOutputStream("Wi.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();
*/
        if (!mh.toString().isEmpty()) {
            mh.append(";").append("/*").append(item_string).append("*/").append("\r\n");
        }

        mh.append("INSERT INTO `shops` (`shopid`,`npcid`) VALUES").append("\r\n");
        for (int i=308; i<shopsnumber+1; i++) {
            mh.append("(").append(i).append(",9001000").append(i == shopsnumber+1 ? ");" : "),").append("\r\n");
        }
        FileOutputStream cash_string = null;
        cash_string = new FileOutputStream("WizetEqp.txt");
        cash_string.write(mh.toString().getBytes());
        cash_string.close();

        System.out.println("추출이 완료 되었습니다!");
    }
}
