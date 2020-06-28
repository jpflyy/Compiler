package compiler;

import java.util.ArrayList;

/**
 * @Author: jpf
 * @Date: 2020/6/22 0022 18:38
 * 语义分析时的符号栈
 * symbol 输入符号类型，例如A,E,S,id,if,Integer等,对应单词的类型
 * 以下属性不一定全部用到
 * type  输入符号类型。用ArrayList实现堆栈，实际上只改变栈顶指针，由于语义分析采用L属性SDT的自底向上实现，将
 *       句中的语义规则转换为一个文法符号，该文法符号推出空，且附上语义规则。栈顶指针的改变并不改变符号
 *       的一些属性值，归约时，symbol会改变，但是原位置上的属性值仍可以利用。
 *       除symbol外，其余均作为符号的综合属性
 * value id的名字，或Integer的值等
 * addr  符号表中位置
 * width 位宽
 *
 * trueList 包含跳转指令的列表，为true时控制流应该转向的指令标号就是这些跳转指令的目的标号
 * falseList 包含跳转指令的列表，为false时控制流应该转向的指令标号就是这些跳转指令的目的标号
 * nextList 包含跳转指令的列表，按照运行顺序执行完当前语句块后的指令标号就是这些跳转指令的目的标号
 * 上述三条属性在回填时完成跳转指令目的地址的填写
 * instr 指示当前指令地址
 */
public class SymbolAttribute {
    public String symbol;
    public String type;
    public String value;
    public int addr;
    public int width;
    ArrayList<Integer> trueList;
    ArrayList<Integer> falseList;
    ArrayList<Integer> nextList;
    int instr;
    /**符号栈*/
    public static ArrayList<SymbolAttribute> symbolStacks = new ArrayList<>();
    public static int top = -1;

    public SymbolAttribute(String symbol) {
        this.symbol = symbol;
        this.addr = -1;
        this.instr = -1;
    }
    /**符号栈push*/
    public static void push(String symbol){
        if(symbolStacks.size() <= top+1){
            symbolStacks.add(new SymbolAttribute(symbol));
            top++;
        }else {
            top++;
            symbolStacks.get(top).symbol = symbol;
        }
    }
    /**符号栈pop*/
    public static void pop(){
        top--;
    }
    /**makeList，用于回填技术，创建一个仅包含第i条指令的数组，意味着第i条指令需要回填*/
    public static ArrayList<Integer> makeList(int i){
        return new ArrayList<Integer>(){{add(i);}};
    }
    /**merge，用于回填技术，合并p1，p2*/
    public static ArrayList<Integer>merge(ArrayList<Integer> p1, ArrayList<Integer> p2){
        if(p1!=null && p2==null){
            return p1;
        }else if(p1==null && p2!=null){
            return p2;
        }else if(p1 != null){
            p1.addAll(p2);
            return p1;
        }else {
            return null;
        }
    }
    /**符号拓展，仅支持Integer给Float赋值或同类型赋值*/
    public static String extendType(int op1, int op2){
        if("Float".equals(Symbol.symbolsTable.get(op1).getType()) || "Float".equals(Symbol.symbolsTable.get(op2).getType())){
            return "Float";
        }else {
            return "Integer";
        }
    }

    public static void printSs(){
        for(SymbolAttribute x : symbolStacks){
            String mes = String.format("%s  %s  %s",x.symbol,x.type,x.width);
            System.out.printf("%s\n",mes);
        }
    }

}
