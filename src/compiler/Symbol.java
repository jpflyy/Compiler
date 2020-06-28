package compiler;

import java.util.ArrayList;
/**
 * @Author: jpf
 * @Date: 2020/6/12 0012 15:25
 * 符号表
 * name 变量名 如id的值，digit的值
 * type 变量类型，若是变量名，type取integer，若是int数，type取int10
 * val 变量值，暂为null
 * addr 相对地址
 *
 * index仅用于图形界面显示标号
 */
public class Symbol implements java.io.Serializable {
    private int index;

    private String name;
    private String type;
    private int val;
    private int addr;

    public static int offset;
    public static int temp = 0;
    public static ArrayList<Symbol> symbolsTable = new ArrayList<>();

    public Symbol(String name) {
        this.name = name;
        this.index = symbolsTable.size();
        this.val = -1;
    }

    public static int wordLocate(String word){
        for(int i=0; i<symbolsTable.size(); i++){
            if(symbolsTable.get(i).name.equals(word)){
                return i;
            }
        }
        return -1;
    }

    /**新建一个临时变量，返回其位置*/
    public static int newTemp(String type){
        symbolsTable.add(new Symbol("&"+temp));
        symbolsTable.get(symbolsTable.size()-1).type = type;
        symbolsTable.get(symbolsTable.size()-1).addr = offset;
        if("Integer".equals(type)){
            offset += 4;
        }else if("Float".equals(type)){
            offset += 8;
        }
        temp++;
        return symbolsTable.size()-1;
    }
    /**类型判断，即只能Integer给Integer和Float赋值，而不能反过来*/
    public static boolean typeCheck(int addr1, int addr2){
        return symbolsTable.get(addr1).type.equals(symbolsTable.get(addr2).type) || "Float".equals(symbolsTable.get(addr1).type);
    }

    public static void printSt(){
        for(Symbol x : symbolsTable){
            String mes = String.format("%2d %5s  %s  %3s",x.index,x.name,x.type,x.addr);
            System.out.printf("%s\n",mes);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

