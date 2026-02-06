package server;

import java.util.List;
import java.util.ArrayList;

import client.inventory.Item;

public class MerchItemPackage {

    private long lastsaved;
    private int mesos = 0, packageid, map, x, y;
    private List<Item> items = new ArrayList<Item>();

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setSavedTime(long lastsaved) {
        this.lastsaved = lastsaved;
    }

    public long getSavedTime() {
        return lastsaved;
    }

    public int getMesos() {
        return mesos;
    }

    public void setMesos(int set) {
        mesos = set;
    }

    public int getPackageid() {
        return packageid;
    }

    public void setPackageid(int packageid) {
        this.packageid = packageid;
    }
    
    public int getMap() {
        return map;
    }

    public void setMap(int v1) {
        map = v1;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int v1) {
        x = v1;
    }
    
    public int getY() {
        return y;
    }

    public void setY(int v1) {
        y = v1;
    }
}
