package compiler;

import java.util.ArrayList;

/**
 * @Author: jpf
 * @Date: 2020/6/22 0022 11:19
 * 三地址码 code存储三地址码对应的四元式，提供将四元式输出为三地址码的函数
 */
public class Tac {
    public String op;
    public int arg1;
    public int arg2;
    public int result;

    public Tac(String op, int arg1, int arg2, int result) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    public static ArrayList<Tac> code = new ArrayList<>();
    /**产生一条三地址码*/
    public static void generate(String op, int arg1, int arg2, int result){
        code.add(new Tac(op,arg1,arg2,result));
    }
    /**回填所用写回函数*/
    public static void backpatch(ArrayList<Integer> p, int i){
        if(p != null){
            for(int ins : p){
                code.get(ins).result = i;
            }
        }
    }
    /**返回下条指令地址*/
    public static int nextInstr(){
        return code.size();
    }
    /**输出四元式形式*/
    public static void printQuaternary(){
        int i=0;
        for(Tac tac : code){
            System.out.println("Instr"+i+"  {"+"op:"+tac.op+"   arg1:"+tac.arg1+"   arg2:"+tac.arg2+"   result:"+tac.result+"}");
            i++;
        }
    }
    /**输出三地址码形式*/
    public static ArrayList<String> printTac(){
        ArrayList<String> outputTac = new ArrayList<>();
        String tac = null;
        int i = 0;
        for (Tac value : code) {
            switch (value.op) {
                case "+": {
                    String result = Symbol.symbolsTable.get(value.result).getName();
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = result + " = " + op1 + "+" + op2;
                    break;
                }
                case "-": {
                    String result = Symbol.symbolsTable.get(value.result).getName();
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = result + " = " + op1 + "-" + op2;
                    break;
                }
                case "*": {
                    String result = Symbol.symbolsTable.get(value.result).getName();
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = result + " = " + op1 + "*" + op2;
                    break;
                }
                case "/": {
                    String result = Symbol.symbolsTable.get(value.result).getName();
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = result + " = " + op1 + "/" + op2;
                    break;
                }
                case "=": {
                    String result = Symbol.symbolsTable.get(value.result).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = result + " = " + op2;
                    break;
                }
                case "j>": {
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = "if " + op1 + ">" + op2 + " goto " + value.result;
                    break;
                }
                case "j<": {
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = "if " + op1 + "<" + op2 + " goto " + value.result;
                    break;
                }
                case "j==": {
                    String op1 = Symbol.symbolsTable.get(value.arg1).getName();
                    String op2 = Symbol.symbolsTable.get(value.arg2).getName();
                    tac = "if " + op1 + "==" + op2 + " goto " + value.result;
                    break;
                }
                case "j": {
                    tac = "goto " + value.result;
                    break;
                }
                default:
            }
            String mes = String.format("%4d   %s",i,tac);
            System.out.printf("%s\n",mes);
            outputTac.add(mes);
            i++;
        }
        return outputTac;
    }

}
