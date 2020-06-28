package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @Author: jpf
 * @Date: 2020/6/22 0022 17:23
 * 语义分析器，根据语义规则执行相关操作
 * 给出一些错误提示
 * 自底向上的语法制导翻译方案
 */
public class SemanticAnalysis {

    public static ArrayList<String> errorMes = new ArrayList<>();

    /*以下规则均包含一定的错误提示*/
    /*以下是声明语句的语义规则*/

    public static void statement1(){
        Symbol.offset = 0;
    }
    /**归约N->epsilon时语义规则,归约完后符号栈为D->L id; N */
    public static void statement2(){
        int index = SymbolAttribute.symbolStacks.get(SymbolAttribute.top-2).addr;
        if(Symbol.symbolsTable.get(index).getType() != null){
            errorMes.add("ERROR:重复的定义:"+Symbol.symbolsTable.get(index).getName()+"不可重复定义");
        }else {
            Symbol.symbolsTable.get(index).setType(SymbolAttribute.symbolStacks.get(SymbolAttribute.top-3).type);
            Symbol.symbolsTable.get(index).setAddr(Symbol.offset);
            Symbol.offset += SymbolAttribute.symbolStacks.get(SymbolAttribute.top-3).width;
        }
    }
    /**归约L->Integer时语义规则*/
    public static void statement3(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).type = "Integer";
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).width = 4;
    }
    /**归约L->Float时语义规则*/
    public static void statement4(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).type = "Float";
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).width = 8;
    }
    /*以下是赋值语句的语义规则*/
    /**归约S->id=E;时语义规则*/
    public static void assignment1(){
        int result = SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr;
        int op2 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr;
        if(Symbol.symbolsTable.get(result).getType() == null) {
            errorMes.add("ERROR:未定义的变量不可使用:"+SymbolAttribute.symbolStacks.get(SymbolAttribute.top).value+"未声明");
        }else if(Symbol.symbolsTable.get(op2).getType() != null){
            if(Symbol.typeCheck(result,op2)){
                Tac.generate("=",-1,op2,result);
            }else {
                errorMes.add("ERROR:Float类型变量不可以给Integer类型变量赋值:"+SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).value+"不可以给"+SymbolAttribute.symbolStacks.get(SymbolAttribute.top).value+"赋值");
            }

        }
    }
    /**归约E->E+T时语义规则*/
    public static void assignment2(){
        int op1 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr;
        int op2 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr;
        int result = Symbol.newTemp(SymbolAttribute.extendType(op1,op2));
        Tac.generate("+",op1,op2,result);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = result;
    }
    /**归约E->E-T时语义规则*/
    public static void assignment3(){
        int op1 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr;
        int op2 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr;
        int result = Symbol.newTemp(SymbolAttribute.extendType(op1,op2));
        Tac.generate("-",op1,op2,result);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = result;
    }
    /**归约T->T*F时语义规则*/
    public static void assignment4(){
        int op1 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr;
        int op2 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr;
        int result = Symbol.newTemp(SymbolAttribute.extendType(op1,op2));
        Tac.generate("*",op1,op2,result);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = result;
    }
    /**归约T->T/F时语义规则*/
    public static void assignment5(){
        int op1 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr;
        int op2 = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr;
        int result = Symbol.newTemp(SymbolAttribute.extendType(op1,op2));
        Tac.generate("/",op1,op2,result);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = result;
    }
    /**归约F->(E)时语义规则*/
    public static void assignment6(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+1).addr;
    }
    /**归约F->id时语义规则*/
    public static void assignment7(){
        //SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = Symbol.wordLocate(SymbolAttribute.symbolStacks.get(SymbolAttribute.top).value);
        if(Symbol.symbolsTable.get(SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr).getType() == null){
            errorMes.add("ERROR:未定义的变量不可使用:"+SymbolAttribute.symbolStacks.get(SymbolAttribute.top).value+"未声明");
        }
    }
    /*以下是控制流语句的语义规则，仅实现了>、<、==，其他原理类似，采用自底向上翻译，回填技术*/
    /**归约C->E1>E2时语义规则*/
    public static void control1(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).trueList = SymbolAttribute.makeList(Tac.nextInstr());
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).falseList = SymbolAttribute.makeList(Tac.nextInstr()+1);
        Tac.generate("j>",SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr,-1);
        Tac.generate("j",-1,-1,-1);
    }
    /**归约C->E1<E2时语义规则*/
    public static void control2(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).trueList = SymbolAttribute.makeList(Tac.nextInstr());
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).falseList = SymbolAttribute.makeList(Tac.nextInstr()+1);
        Tac.generate("j<",SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr,-1);
        Tac.generate("j",-1,-1,-1);
    }
    /**归约C->E1==E2时语义规则*/
    public static void control3(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).trueList = SymbolAttribute.makeList(Tac.nextInstr());
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).falseList = SymbolAttribute.makeList(Tac.nextInstr()+1);
        Tac.generate("j==",SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).addr,-1);
        Tac.generate("j",-1,-1,-1);
    }
    /**归约X->epsilon*/
    public static void control4(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).instr = Tac.nextInstr();
    }
    /**归约Y->epsilon*/
    public static void control5(){
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).nextList = SymbolAttribute.makeList(Tac.nextInstr());
        Tac.generate("j",-1,-1,-1);
    }
    /**归约S->if (C) X S1的语义规则*/
    public static void control6(){
        Tac.backpatch(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).trueList,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+4).instr);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).nextList =
                SymbolAttribute.merge(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).falseList,
                        SymbolAttribute.symbolStacks.get(SymbolAttribute.top+5).nextList);
    }
    /**归约S -> if(C) X1 S1 Y else X2 S2*/
    public static void control7(){
        Tac.backpatch(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).trueList,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+4).instr);
        Tac.backpatch(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).falseList,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+8).instr);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).nextList =
                SymbolAttribute.merge(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+9).nextList,
                                      SymbolAttribute.merge(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+6).nextList, SymbolAttribute.symbolStacks.get(SymbolAttribute.top+5).nextList));
    }
    /**归约S->while X1 (C) X2 S1的语义规则*/
    public static void control8(){
        Tac.backpatch(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+6).nextList,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+1).instr);
        Tac.backpatch(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+3).trueList,SymbolAttribute.symbolStacks.get(SymbolAttribute.top+5).instr);
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).nextList = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+3).falseList;
        Tac.generate("j",-1,-1, SymbolAttribute.symbolStacks.get(SymbolAttribute.top+1).instr);

    }
    /**归约S->S1 X S2的语义规则*/
    public static void control9(){
        Tac.backpatch(SymbolAttribute.symbolStacks.get(SymbolAttribute.top).nextList,(SymbolAttribute.symbolStacks.get(SymbolAttribute.top+1).instr));
        SymbolAttribute.symbolStacks.get(SymbolAttribute.top).nextList = SymbolAttribute.symbolStacks.get(SymbolAttribute.top+2).nextList;
    }
    /**根据归约所用的产生式执行相应的语义规则*/
    public static void midDeal(String symbol){
        switch (symbol){
            case "M0":{
                statement1();
                break;
            }
            case "N0":{
                statement2();
                break;
            }
            case "L0":{
                statement3();
                break;
            }
            case "L1":{
                statement4();
                break;
            }
            case "S0":{
                assignment1();
                break;
            }
            case "E0":{
                assignment2();
                break;
            }
            case "E1":{
                assignment3();
                break;
            }
            case "T1":{
                assignment4();
                break;
            }
            case "T2":{
                assignment5();
                break;
            }
            case "F0":{
                assignment6();
                break;
            }
            case "F1":{
                assignment7();
                break;
            }
            case "C0":{
                control1();
                break;
            }
            case "C1":{
                control2();
                break;
            }
            case "C3":{
                control3();
                break;
            }
            case "X0":{
                control4();
                break;
            }
            case "Y0":{
                control5();
                break;
            }
            case "S1":{
                control6();
                break;
            }
            case "S2":{
                control7();
                break;
            }
            case "S3":{
                control8();
                break;
            }
            case "S4":{
                control9();
                break;
            }
            default:
        }
    }
    /**语义分析器，在slr分析过程中完成，*/
    public static void midCode(){
        int index = 0;
        int state = 0;
        Stack<Integer> stateStack = new Stack<>();
        stateStack.push(state);
        SymbolAttribute.push("$");
        while (true){
            /*获取栈顶状态*/
            state = stateStack.peek();
            /*下一个读入的符号*/
            Word symbol = Word.token.get(index);
            HashMap<Integer,String> s1 = new HashMap<>();
            s1.put(state,symbol.getType());
            /*查ACTION表*/
            if(LrParser.ACTION.containsKey(s1)){
                String action = LrParser.ACTION.get(s1);
                System.out.println("状态"+state+"  输入符号"+symbol.getType()+"  动作"+action);
                if(action.charAt(0) == 's'){
                    /*移入操作*/
                    stateStack.push(Integer.parseInt(action.substring(1)));
                    /*入符号栈，符号值，符号类型，符号在符号表中地址*/
                    SymbolAttribute.push(symbol.getType());
                    SymbolAttribute.symbolStacks.get(SymbolAttribute.top).value = symbol.getValue();
                    SymbolAttribute.symbolStacks.get(SymbolAttribute.top).type = symbol.getType();
                    SymbolAttribute.symbolStacks.get(SymbolAttribute.top).addr = symbol.getAddr();
                    int x = stateStack.peek();
                    System.out.println("栈顶状态"+x+"");
                    index++;
                }else if(action.charAt(0) == 'r'){
                    /*归约操作*/
                    String leftProduct = action.substring(1,2);
                    int productNo = Integer.parseInt(action.substring(2));
                    int popLen;
                    if("epsilon".equals(LrParser.GPLUS.get(leftProduct).get(productNo).symbol[0])){
                        popLen = 0;
                    }else {
                        popLen = LrParser.GPLUS.get(leftProduct).get(productNo).len;
                    }
                    for(;popLen>0;popLen--){
                        stateStack.pop();
                        SymbolAttribute.pop();
                        int x = stateStack.peek();
                        System.out.println("栈顶状态"+x+"");
                    }
                    state = stateStack.peek();
                    HashMap<Integer,String> s2 = new HashMap<>();
                    s2.put(state,leftProduct);
                    stateStack.push(LrParser.GOTO.get(s2));
                    SymbolAttribute.push(leftProduct+productNo);
                    /*输出归约采用的产生式*/
                    System.out.println(leftProduct+"->"+LrParser.GPLUS.get(leftProduct).get(productNo).printProduct());
                    int x = stateStack.peek();
                    System.out.println("栈顶状态"+x+"");
                    /*仅在归约时执行语义规则*/
                    midDeal(SymbolAttribute.symbolStacks.get(SymbolAttribute.top).symbol);
                }else if("acc".equals(action)){
                    System.out.println("语义分析完成");
                    errorMes.add("语义分析完成");
                    break;
                }else {
                    System.out.println("error");
                    break;
                }
            }else {
                System.out.println("error");
                break;
            }
        }
    }

    public static void main(String[] args){
        LrParser.initC();
        for(Map.Entry<Integer,HashMap<String, ArrayList<Rproduct>>> entry : LrParser.C.entrySet()) {
            int key = entry.getKey();
            System.out.println("状态I："+key);
            HashMap<String, ArrayList<Rproduct>> value = entry.getValue();
            LrParser.printHs(value);
        }
        LrParser.initLrTable();
        Scancer.input = "";
        Scancer.lexicalAnalysis();
        midCode();
        Symbol.printSt();
        Tac.printTac();
        Tac.printQuaternary();
        for(String error : errorMes){
            System.out.println(error);
        }
    }

}
