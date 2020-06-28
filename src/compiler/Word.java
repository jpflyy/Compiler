package compiler;

import java.util.ArrayList;

/**
 * @author jpf
 * 词法分析结果
 * value 文法符号名
 * type 符号类型
 * typeCOde 种别码
 * addr 符号表中所在位置
 */
public class Word implements java.io.Serializable {

    private String typeCode;
    private String type;
    private String value;
    private int addr;

    public Word(String value, String typeCode, String type, int addr) {
        this.value = value;
        this.typeCode = typeCode;
        this.type = type;
        this.addr = addr;
    }

    public static ArrayList<Word> token = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public void printToken(){
        System.out.println("{"+this.value+","+this.type+","+this.typeCode+","+this.addr+"}");
    }
}
