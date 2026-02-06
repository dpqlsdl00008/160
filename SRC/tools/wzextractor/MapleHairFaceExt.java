package tools.wzextractor;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

/**
 *
 * @author 몽키프
 */
public class MapleHairFaceExt {
        public static void main(String[] args) {
            StringBuilder mh = new StringBuilder();    
            StringBuilder fh = new StringBuilder();   
            StringBuilder mf = new StringBuilder();            
            StringBuilder ff = new StringBuilder();              
            Console con = System.console();            
            boolean hair, face, comment;
            int starthairM = 0, starthairW = 0; 
            int startfaceM = 0, startfaceW = 0;            
            int lasthairM = 0, lasthairW = 0; 
            int lastfaceM = 0, lastfaceW = 0;
            System.out.println("[헤어, 성형 추출 프로그램에 오신 것을 환영합니다.]\r\n");
            hair = Boolean.parseBoolean(con.readLine("헤어를 추출합니까?[true/ false] : "));
            face = Boolean.parseBoolean(con.readLine("성형을 추출합니까?[true/ false] : "));
            System.out.println("\r\n");  
            System.out.println("헤어 : " + hair + "성형 : " + face);
            if (hair) {
                starthairM = Integer.parseInt(con.readLine("남자헤어 시작수를 입력해주세요 [권장:30000] : "));
                lasthairM = Integer.parseInt(con.readLine("남자헤어 끝자리수를 입력해주세요 : "));
                starthairW = Integer.parseInt(con.readLine("여자헤어 시작수를 입력해주세요 [권장:31000] : "));
                lasthairW = Integer.parseInt(con.readLine("여자헤어 끝자리수를 입력해주세요 : "));                
            }
            if (face) {
                startfaceM = Integer.parseInt(con.readLine("남자얼굴 시작수를 입력해주세요 [권장:20000] : "));
                lastfaceM = Integer.parseInt(con.readLine("남자얼굴 끝자리수를 입력해주세요 : "));
                startfaceW = Integer.parseInt(con.readLine("여자얼굴 시작수를 입력해주세요 [권장:21000] : "));
                lastfaceW = Integer.parseInt(con.readLine("여자얼굴 끝자리수를 입력해주세요 : "));                
            }
            MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("extractor.wzpath") + "/String.wz"));
            MapleData pData = data.getData("Eqp.img");                   
            if (pData == null) {
                System.out.println("WZ데이터가 없습니다!");
            }
            comment = Boolean.parseBoolean(con.readLine("아이템 이름을 주석처리 합니까?[true/ false] : "));
            if (hair) {
                mh.append("mhair = Array(\r\n");
                for (int i=starthairM; i<lasthairM; i++) {
                    if (pData.getChildByPath("Eqp/Hair/"+i) != null && i % 10 == 0) {//검은머리만
                        mh.append(i).append(",").append(comment ? ("//" + MapleDataTool.getString("name", pData.getChildByPath("Eqp/Hair/"+i), "")+"\r\n") : "");
                    } else if (pData.getChildByPath("Eqp/Hair/"+i) == null) {
                        System.err.println(i+"번 헤어코드가 없습니다.");
                    }
                }
                mh.append(");");
                fh.append("fhair = Array(\r\n");
                for (int i=starthairW; i<lasthairW; i++) {
                    if (pData.getChildByPath("Eqp/Hair/"+i) != null && i % 10 == 0) {//검은머리만
                        fh.append(i).append(",").append(comment ? ("//" + MapleDataTool.getString("name", pData.getChildByPath("Eqp/Hair/"+i), "")+"\r\n") : "");
                    } else if (pData.getChildByPath("Eqp/Hair/"+i) == null) {
                        System.err.println(i+"번 헤어코드가 없습니다.");
                    }
                }
                fh.append(");");                
            }
            if (face) {
                mf.append("mface = Array(\r\n");
                for (int i=startfaceM; i<lastfaceM; i++) {
                    if (pData.getChildByPath("Eqp/Face/"+i) != null) {
                        mf.append(i).append(",").append(comment ? ("//" + MapleDataTool.getString("name", pData.getChildByPath("Eqp/Face/"+i), "")+"\r\n") : "");
                    } else if (pData.getChildByPath("Eqp/Face/"+i) == null) {
                        System.err.println(i+"번 성형코드가 없습니다.");
                    }
                }
                mf.append(");");
                ff.append("fface = Array(\r\n");
                for (int i=startfaceW; i<lastfaceW; i++) {
                    if (pData.getChildByPath("Eqp/Face/"+i) != null) {
                        ff.append(i).append(",").append(comment ? ("//" + MapleDataTool.getString("name", pData.getChildByPath("Eqp/Face/"+i), "")+"\r\n") : "");
                    } else if (pData.getChildByPath("Eqp/Face/"+i) == null) {
                        System.err.println(i+"번 성형코드가 없습니다.");
                    }
                }
                ff.append(");"); 
            }
            FileOutputStream mhair = null, fhair = null, mface = null, fface = null;
            try {
                mhair = new FileOutputStream("남자헤어.txt", false);
                fhair = new FileOutputStream("여자헤어.txt", false);
                mface = new FileOutputStream("남자성형.txt", false);
                fface = new FileOutputStream("여자성형.txt", false);
                mhair.write(mh.toString().getBytes());
                fhair.write(fh.toString().getBytes());
                mface.write(mf.toString().getBytes());
                fface.write(ff.toString().getBytes());
            } catch (Exception ex) {
            } finally {
                try {
                    if (mhair != null) {
                        mhair.close();
                    }
                    if (fhair != null) {
                        fhair.close();
                    }
                    if (mface != null) {
                        mface.close();
                    }
                    if (fface != null) {
                        fface.close();
                    }                    
                } catch (IOException ex) {
                }
            }            
        }
}
