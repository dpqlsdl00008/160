package tools.wzextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;

/**
 *
 * @author 몽키프
 */
public class ChairExtractor {
    
    private static final MapleDataProvider item_data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/Item.wz"));
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        StringBuilder mh = new StringBuilder(); 
        System.out.println("[ChairExtractor @author 몽키프]\r\n");
        System.out.println("[의자만뺀당]");
         
        MapleData pDatas = item_data.getData("Install/0301.img");
        if (item_data == null) {
            System.out.println("WZ데이터가 없습니다!");
        }
        for (MapleData dd : pDatas.getChildren()) {
            mh.append(dd.getName().substring(1, 8)).append("\r\n");
        }
        FileOutputStream chair_string = null;
        chair_string = new FileOutputStream("ChairExtractor.txt");
        chair_string.write(mh.toString().getBytes());
        chair_string.close();
        System.out.println("추출이 완료 되었습니다!");
    }
}
