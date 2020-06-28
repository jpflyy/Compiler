package compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author: jpf
 * @Date: 2020/6/17 0017 9:50
 */
public class Rproduct implements Serializable {
    /**
     * 产生式右部数据结构，pointer指明了下一个待移入文法符号
     * len表示有多少个文法符号
     * no表明该产生式编号
     * */
    public String[] symbol;
    public int pointer;
    public int len;
    public int no;

    public Rproduct(String[] symbol, int pointer, int no) {
        this.symbol = symbol;
        this.pointer = pointer;
        this.len = symbol.length;
        this.no = no;
    }
    /**将symbol第i个位置之后的字符串数组返回*/
    public ArrayList<String> divideR(int i){
        ArrayList<String> ret = new ArrayList<>();
        if(i<len){
            ret.addAll(Arrays.asList(symbol).subList(i, len));
        }
        return ret;
    }
    /**返回指针指向的符号，即待移进符号*/
    public String getNext(){
        if(pointer < len){
            return this.symbol[pointer];
        }else{
            return null;
        }
    }
    /**输出产生式右部*/
    public String printProduct(){
        String ret = "";
        for(int i=0; i<len; i++){
            ret += symbol[i]+" ";
        }
        return ret;
    }

    /**重写equals方法，symbol和pointer相同即认为相等*/
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass()!=obj.getClass()){
            return false;
        }
        Rproduct rproduct = (Rproduct) obj;
        return Arrays.equals(this.symbol, rproduct.symbol) && this.pointer == rproduct.pointer;
    }
}
