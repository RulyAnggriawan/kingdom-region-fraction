import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

class KingdomMap{
  char[][]map;
  int currentLine;
  int ySize;
  int xSize;
  List<Army> armies = new ArrayList<Army>();
  List<Region> regions = new ArrayList<Region>();

  public KingdomMap(int y, int x) {
    this.ySize = y;
    this.xSize = x;
    this.currentLine = 0;
    this.map = new char[y][x];
  }

  public void addMapLine(String chars){
    for(int i=0;i<xSize;i++){
      map[currentLine][i] = chars.charAt(i);
    }
    currentLine++;
  }

  public char[][] getMap(){
    return map;
  }

  private void locateArmies(){
    for(int i=0;i<ySize;i++){
      for(int j=0;j<xSize;j++){
        char charValue = map[i][j];
        if(charValue != '.' && charValue != '#'){
          Army army = new Army(charValue, i, j);
          armies.add(army);
          // System.out.println(army);
        }
      }
    }
  }

  public void calculateRegion(){
    locateArmies();

    for (Army army : armies) {
      checkArmyRegion(army);
    }
  }

  public List<Region> getRegions(){
    return this.regions;
  }

  public void checkArmyRegion(Army army){
    int y = army.getY();
    int x = army.getX();
    Region region = new Region();
    region.controller = army;
    boolean[][] visited = new boolean[ySize][xSize];
    simulateExplore(map, visited, y, x, region);
    this.regions.add(region);
  }

  private void simulateExplore(char[][] map, boolean[][] visited, int y, int x, Region region){
    if(y<0 || y >= ySize) return;
    
    if (x < 0 || x >= xSize) return;

    if(visited[y][x] == true) return;

    visited[y][x] = true;

    if(map[y][x] == '#') return;

    char charValue = map[y][x];
    if(charValue != '.'){
      if(region.getController() != null){
        if(region.getController().getFaction() != charValue){
          region.isContested = true;
          region.controller = null;
        }
      }
    }
    
    if(region.getX() > x){
      region.setX(x);
    }

    if(region.getY() > y){
      region.setY(y);
    }
    
    //right
    simulateExplore(map, visited, y, x+1, region);
    //left
    simulateExplore(map, visited, y, x-1, region);
    //top
    simulateExplore(map, visited, y-1, x, region);
    //down
    simulateExplore(map, visited, y+1, x, region);
    
  }

  @Override
  public String toString(){
    StringBuilder string = new StringBuilder();
    for(int i=0;i<ySize;i++){
      for(int j=0;j<xSize;j++){
        string.append(map[i][j]);
      }
      string.append(System.lineSeparator());
    }
    return string.toString();
  }
}

class Region{
  int x;
  int y;
  boolean isContested;
  Army controller;

  public Region() {
    this.isContested = false;
    this.x = Integer.MAX_VALUE;
    this.y = Integer.MAX_VALUE;
  }

  public void setController(Army controller) {
    this.controller = controller;
  }

  public Army getController(){
    return this.controller;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public boolean isContested() {
    return isContested;
  }

  public void setContested(boolean isContested) {
    this.isContested = isContested;
  }

  @Override
  public String toString() {
    return "Region [controller=" + controller + ", isContested=" + isContested + ", x=" + x + ", y=" + y + "]";
  }
  
}

class Army{
  int x;
  int y;
  char faction;

  public Army(char faction, int y, int x) {
    this.x = x;
    this.y = y;
    this.faction = faction;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public char getFaction() {
    return faction;
  }

  public void setFaction(char faction) {
    this.faction = faction;
  }

  @Override
  public String toString() {
    return "Army [faction=" + faction + ", y=" + y + ", x=" + x + "]";
  }

}

public class Main{
    
     public static void main(String []args){
        Scanner myReader = null;
        try{
          myReader = readFile("input.txt");
        
          int totalCase = Integer.parseInt(myReader.nextLine());
          for(int i=0;i<totalCase;i++){
            int ySize = Integer.parseInt(myReader.nextLine());
            int xSize = Integer.parseInt(myReader.nextLine());
            KingdomMap kingdomMap = new KingdomMap(ySize, xSize);
            for(int j=0;j<ySize;j++){
              String chars = myReader.nextLine();

              kingdomMap.addMapLine(chars);
            }
            System.out.println("Case "+(i+1)+":");
            // System.out.println(kingdomMap);

            kingdomMap.calculateRegion();

            Map<String, Integer> report = new TreeMap<String, Integer>(new Comparator<String>(){

              @Override
              public int compare(String o1, String o2) {
                if(o1.length() == o2.length())
                  return o1.compareTo(o2);
                return o1.length() - o2.length();
              }
              
            });
            boolean[][] checked = new boolean[ySize][xSize];
            for (Region region : kingdomMap.getRegions() ){
              
              if(checked[ region.getY() ][ region.getX() ])
                continue;
              
              checked[region.getY()][region.getX()] = true;

              if(region.isContested()){
                if(report.containsKey("contested")){
                  report.put("contested", report.get("contested")+1);
                }
                else
                  report.put("contested", 1);
              }
              else{
                String faction = Character.toString(region.getController().getFaction());
                if(report.containsKey(faction)){
                  report.put(faction, report.get(faction)+1);
                }
                else
                  report.put(faction, 1);
              }
            }
            
            if(!report.containsKey("contested")){
              report.put("contested", 0);
            }

            //print report
            for(Entry<String, Integer> entry : report.entrySet()){
              System.out.println(entry.getKey() + " "+ entry.getValue());
            }
            

          }
        }
        catch(Exception e){
          System.out.println("Unexpected input format.");
          e.printStackTrace();
        }
        finally{
          myReader.close();
        }
        

        
     }

     public static Scanner readFile(String filename){
       Scanner myReader = null;
      try {
        File myObj = new File(filename);
        myReader = new Scanner(myObj);
        // myReader.close();
        
      } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
      return myReader;
     }

     

     
}