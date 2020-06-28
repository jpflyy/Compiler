package compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author jpf
 * 词法分析器
 */
public class Scancer {
    public static HashMap<String,String> code = new HashMap<>();
    public static String input;
    public static int pointer;
    public static int symbolTableIndex;
    public static ArrayList<String> message = new ArrayList<>();
    public static String[] keywords = {"if","else","then","while","do","int","float"};
    static {
        code.put("id","1");
        code.put("Integer","2");
        code.put("Float","3");
        code.put("int","4");
        code.put("float","5");
        code.put("if","6");
        code.put("then","7");
        code.put("else","8");
        code.put("while","9");
        code.put("do","10");
        code.put("+","11");
        code.put("-","12");
        code.put("*","13");
        code.put("/","14");
        code.put(">","15");
        code.put("<","16");
        code.put("=","17");
        code.put(">=","18");
        code.put("<=","19");
        code.put("!=","20");
        code.put("==","21");
        code.put("(","22");
        code.put(")","23");
        code.put(".","24");
        code.put(";","25");
        code.put("$","26");
        pointer = 0;
        symbolTableIndex = 0;
    }

    public static Word comparatorsIdentify() {
        int state = 0;
        int index = pointer;
        while (true) {
            if(index == input.length()){
                pointer = index;
                String word = Character.toString(input.charAt(pointer-1));
                return new Word(word, code.get(word), word, -1);
            }
            switch (state) {
                case 0:{
                    char x = input.charAt(index);
                    if('>' == x){
                        state = 1;
                    }else if('!' == x){
                        state = 2;
                        if(index+1 == input.length()){
                            message.add("WARNING:'!'后只能跟'=',位置("+index+"),默认忽略该'!'");
                            pointer = index+1;
                            return null;
                        }
                    }else if('<' == x){
                        state = 3;
                    }else if('=' == x){
                        state = 4;
                    }else {
                        return null;
                    }
                    index++;
                    break;
                }
                /* 状态1234为终止状态，接收>=,<=,!=,==或进入下一步处理*/
                case 1:{
                    if('=' == input.charAt(index)){
                        pointer = index + 1;
                        return new Word(">=", code.get(">="), ">=", -1);
                    }
                    state = 5;
                    break;
                }
                case 2:{
                    if('=' == input.charAt(index)){
                        pointer = index + 1;
                        return new Word("!=", code.get("!="),"!=",-1);
                    }else {
                        System.out.println("WARNING:'!'后只能跟'=',位置("+index+"),默认忽略该'!'");
                        message.add("WARNING:'!'后只能跟'=',位置("+index+"),默认忽略该'!'");
                        pointer = index;
                        state = 0;
                    }
                    break;
                }
                case 3:{
                    if('=' == input.charAt(index)){
                        pointer = index + 1;
                        return new Word("<=", code.get("<="), "<=", -1);
                    }
                    state = 5;
                    break;
                }
                case 4:{
                    if('=' == input.charAt(index)){
                        pointer = index + 1;
                        return new Word("==", code.get("=="), "==", -1);
                    }
                    state = 5;
                    break;
                }
                /* 终止状态，输出不合法信息，并自动修改*/
                case 5:{
                    char x = input.charAt(index);
                    if ('='==x || '>'==x || '<'==x || '!'==x) {
                        System.out.println("WARNING:不期望的符号,位置("+index+"),默认将其视为一个新单词首部");
                        message.add("WARNING:不期望的符号,位置("+index+"),默认将其视为一个新单词首部");
                    }
                    String word = Character.toString(input.charAt(pointer));
                    pointer = index;
                    return new Word(word, code.get(word),word, -1);
                }
                default:
            }
        }
    }

    public static Word operatorIdentify(){
        Word word;
        if(pointer == input.length()){
            return null;
        }
        switch (input.charAt(pointer)){
            case '+':{
                pointer++;
                word = new Word("+",code.get("+"), "+", -1);
                break;
            }
            case '-':{
                pointer++;
                word = new Word("-",code.get("-"), "-", -1);
                break;
            }
            case '*':{
                pointer++;
                word = new Word("*",code.get("*"), "*", -1);
                break;
            }
            case '/':{
                pointer++;
                word = new Word("/",code.get("/"), "/", -1);
                break;
            }
            default:
                return null;
        }
        return word;
    }

    public static Word delimiterIdentify(){
        Word word;
        if(pointer == input.length()){
            return null;
        }
        switch (input.charAt(pointer)){
            case ';':{
                pointer++;
                word = new Word(";",code.get(";"), ";", -1);
                break;
            }
            case '(':{
                if(pointer == input.length()-1) {
                    System.out.println("WARNING:输入最后一个符号为'(',位置("+pointer+"),默认忽略");
                    message.add("WARNING:输入最后一个符号为'(',位置("+pointer+"),默认忽略");
                    pointer++;
                    return null;
                }else {
                    pointer++;
                    word = new Word("(",code.get("("), "(", -1);
                    break;
                }
            }
            case ')':{
                pointer++;
                word = new Word(")",code.get(")"), ")", -1);
                break;
            }
            default:
                return null;
        }
        return word;
    }

    public static Word digitIdentify(){
        int state = 0;
        int index = pointer;
        int start = pointer;
        String float10 = "";
        while (true) {
            switch (state) {
                /*初始状态*/
                case 0:{
                    if(index<input.length() && Character.isDigit(input.charAt(index))){
                        state = 1;
                        index++;
                    }else {
                        return null;
                    }
                    break;
                }
                /*终止状态，识别出'.'后转识别float10，只接受一个'.'*/
                case 1:{
                    if(index<input.length() && Character.isDigit(input.charAt(index))){
                        index++;
                    }else if(index<input.length() && input.charAt(index)=='.'){
                        if(index+1 == input.length()){
                            System.out.println("WARNING:'.'作为最后一个字符无意义，忽略，词法分析完毕");
                            message.add("WARNING:'.'作为最后一个字符无意义，忽略，词法分析完毕");
                            pointer = index+1;
                            return new Word(input.substring(start,index),code.get("Integer"), "Integer", -1);
                        }else {
                            state = 2;
                            float10 = input.substring(start,index)+".";
                            index++;
                            start = index;
                        }
                    } else {
                        pointer = index;
                        return new Word(input.substring(start,index),code.get("Integer"), "Integer", -1);
                    }
                    break;
                }
                case 2:{
                    if(index<input.length() && Character.isDigit(input.charAt(index))){
                        index++;
                    }else if(index<input.length() && input.charAt(index)=='.'){
                        System.out.println("WARNING:float只包含一个小数点'.'，位置("+index+")处'.'多余，忽略");
                        message.add("WARNING:float只包含一个小数点'.'，位置("+index+")处'.'多余，忽略");
                        float10 = float10 + input.substring(start,index);
                        index++;
                        start = index;
                    }else {
                        float10 = float10 + input.substring(start,index);
                        pointer = index;
                        return new Word(float10,code.get("Float"), "Float", -1);
                    }
                }
                default:
            }
        }
    }

    public static Word identifierIdentify(){
        int state = 0;
        int index = pointer;
        int start = pointer;
        while (true){
            switch (state){
                case 0:{
                    if(index<input.length() && Character.isLetter(input.charAt(index))){
                        state = 1;
                        index++;
                    }else {
                        return null;
                    }
                    break;
                }
                case 1:{
                    if(index<input.length() && Character.isLetterOrDigit(input.charAt(index))){
                        index++;
                    }else {
                        pointer = index;
                        return new Word(input.substring(start,index),code.get("id"), "id", -1);
                    }
                }
                default:
            }
        }
    }

    public static Word keyIdentify(){
        if(pointer+2<input.length() && keywords[0].equals(input.substring(pointer,pointer+2)) && !Character.isLetter(input.charAt(pointer+2))){
            pointer += 2;
            return new Word(keywords[0],code.get(keywords[0]), keywords[0], -1);
        }else if(pointer+4<input.length() && keywords[1].equals(input.substring(pointer,pointer+4)) && !Character.isLetter(input.charAt(pointer+4))){
            pointer += 4;
            return new Word(keywords[1],code.get(keywords[1]),keywords[1],-1);
        }else if(pointer+4<input.length() && keywords[2].equals(input.substring(pointer,pointer+4)) && !Character.isLetter(input.charAt(pointer+4))){
            pointer += 4;
            return new Word(keywords[2],code.get(keywords[2]),keywords[2],-1);
        }else if(pointer+5<input.length() && keywords[3].equals(input.substring(pointer,pointer+5)) && !Character.isLetter(input.charAt(pointer+5))){
            pointer += 5;
            return new Word(keywords[3],code.get(keywords[3]),keywords[3],-1);
        }else if(pointer+2<input.length() && keywords[4].equals(input.substring(pointer,pointer+2)) && !Character.isLetter(input.charAt(pointer+2))){
            pointer += 2;
            return new Word(keywords[4],code.get(keywords[4]),keywords[4],-1);
        }else if(pointer+3<input.length() && keywords[5].equals(input.substring(pointer,pointer+3)) && !Character.isLetter(input.charAt(pointer+3))){
            pointer += 3;
            return new Word(keywords[5],code.get(keywords[5]),keywords[5],-1);
        }if(pointer+5<input.length() && keywords[6].equals(input.substring(pointer,pointer+5)) && !Character.isLetter(input.charAt(pointer+5))){
            pointer += 5;
            return new Word(keywords[6],code.get(keywords[6]),keywords[6],-1);
        }else {
            return null;
        }
    }

    public static void lexicalAnalysis(){
        /*初始化编译器*/
        pointer = 0;
        symbolTableIndex = 0;
        Word.token.clear();
        Symbol.symbolsTable.clear();
        Symbol.temp = 0;
        message.clear();
        Tac.code.clear();
        SemanticAnalysis.errorMes.clear();
        SymbolAttribute.symbolStacks.clear();
        SymbolAttribute.top = -1;
        /*开始词法分析*/
        Word word;
        System.out.println(input.length());
        while (pointer < input.length()){
            /*忽略空格，回车，换行*/
            System.out.println("pointer:"+pointer);
            if(input.charAt(pointer) == ' ' || input.charAt(pointer) == 0xd || input.charAt(pointer) == 0xa){
                System.out.println("可忽略的符号,位置("+pointer+")");
                pointer++;
            }else if(Character.isLetter(input.charAt(pointer))){
                System.out.println(input.charAt(pointer)+" letter");
                if((word = Scancer.keyIdentify()) != null){
                    Word.token.add(word);
                }else if((word = Scancer.identifierIdentify()) != null){
                    int addr = Symbol.wordLocate(word.getValue());
                    if(addr != -1){
                        word.setAddr(addr);
                    }else {
                        word.setAddr(symbolTableIndex);
                        Symbol symbol = new Symbol(word.getValue());
                        Symbol.symbolsTable.add(symbol);
                        symbolTableIndex++;
                    }
                    Word.token.add(word);
                }else {
                    message.add("错误的符号,位置:"+pointer);
                    pointer++;
                }
            }else if(Character.isDigit(input.charAt(pointer))){
                System.out.println(input.charAt(pointer)+" digit");
                if((word = Scancer.digitIdentify()) != null) {
                    word.setAddr(symbolTableIndex);
                    Word.token.add(word);
                    Symbol symbol = new Symbol(word.getValue());
                    symbol.setType(word.getType());
                    Symbol.symbolsTable.add(symbol);
                    symbolTableIndex++;
                }else {
                    message.add("错误的符号,位置:"+pointer);
                    pointer++;
                }
            }else {
                System.out.println(input.charAt(pointer)+" symbol");
                if((word = Scancer.comparatorsIdentify()) != null){
                    Word.token.add(word);
                }else if((word = Scancer.operatorIdentify()) != null){
                    Word.token.add(word);
                }else if((word = Scancer.delimiterIdentify()) != null){
                    Word.token.add(word);
                }else {
                    message.add("错误的符号,位置:"+pointer);
                    pointer++;
                }
            }
        }
        Word.token.add(new Word("$","26","$",-1));
        message.add("词法分析完成");
    }

    public static void main(String[] args) throws IOException {
        Scancer.input = "int a; int b; int c; int x; int y; while (a<b) if(c<5) while (x>y) z=x+1; else x=y;"+0xd;
        Scancer.lexicalAnalysis();
        for (Word word : Word.token) {
            word.printToken();
        }
        for (Symbol symbol : Symbol.symbolsTable){
            System.out.println(symbol.getName());
        }
    }
}
