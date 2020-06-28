package compiler;

import java.io.*;
import java.util.*;

/**
 * @Author: jpf
 * @Date: 2020/6/13 0013 17:08
 * LR语法分析器
 */
public class LrParser {
    /**M N X Y为附加状态*/
    public static String[] vt = {"id","Integer","Float","int","float","if","(",")","else","while",">","<","==","=","+","-","*","/",";","$"};
    public static String[] vn = {"G","P","D","S","L","C","E","T","F","M","N","X","Y"};
    /**G的增广文法*/
    public static HashMap<String, ArrayList<Rproduct>> GPLUS = new HashMap<>();
    /**项集规范族*/
    public static HashMap<Integer,HashMap<String, ArrayList<Rproduct>>> C = new HashMap<>();
    /**slr分析表*/
    public static HashMap<HashMap<Integer,String>, String> ACTION = new HashMap<>();
    public static HashMap<HashMap<Integer,String>, Integer> GOTO = new HashMap<>();
    /**归约情况*/
    public static ArrayList<String> reduce = new ArrayList<>();

    /*初始化增广文法*/
    static {
//        GPLUS.put("G",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"E"},0,0));
//        }});
//        GPLUS.put("E",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"E","+","T"},0,0));
//            add(new Rproduct(new String[]{"T"},0,1));
//        }});
//        GPLUS.put("T",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"T","*","F"},0,0));
//            add(new Rproduct(new String[]{"F"},0,1));
//        }});
//        GPLUS.put("F",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"(","E",")"},0,0));
//            add(new Rproduct(new String[]{"id"},0,1));
//        }});

//        GPLUS.put("G",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"P"},0,0));
//        }});
//        GPLUS.put("P",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"D","S"},0,0));
//        }});
//        GPLUS.put("D",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"L","id",";","D"},0,0));
//            add(new Rproduct(new String[]{"epsilon"},0,1));
//        }});
//        GPLUS.put("L",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"int"},0,0));
//            add(new Rproduct(new String[]{"float"},0,1));
//        }});
//        GPLUS.put("S",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"id","=","E",";"},0,0));
//            add(new Rproduct(new String[]{"if","(","C",")","S"},0,1));
//            add(new Rproduct(new String[]{"if","(","C",")","S","else","S"},0,2));
//            add(new Rproduct(new String[]{"while","(","C",")","S"},0,3));
//            add(new Rproduct(new String[]{"S","S"},0,4));
//        }});
//        GPLUS.put("C",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"E",">","E"},0,0));
//            add(new Rproduct(new String[]{"E","<","E"},0,1));
//            add(new Rproduct(new String[]{"E","==","E"},0,2));
//        }});
//        GPLUS.put("E",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"E","+","T"},0,0));
//            add(new Rproduct(new String[]{"E","-","T"},0,1));
//            add(new Rproduct(new String[]{"T"},0,2));
//        }});
//        GPLUS.put("T",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"F"},0,0));
//            add(new Rproduct(new String[]{"T","*","F"},0,1));
//            add(new Rproduct(new String[]{"T","/","F"},0,2));
//        }});
//        GPLUS.put("F",new ArrayList<Rproduct>(){{
//            add(new Rproduct(new String[]{"(","E",")"},0,0));
//            add(new Rproduct(new String[]{"id"},0,1));
//            add(new Rproduct(new String[]{"Integer"},0,2));
//            add(new Rproduct(new String[]{"Float"},0,3));
//        }});

        GPLUS.put("G",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"P"},0,0));
        }});
        GPLUS.put("P",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"M","D","S"},0,0));
        }});
        GPLUS.put("D",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"L","id",";","N","D"},0,0));
            add(new Rproduct(new String[]{"epsilon"},0,1));
        }});
        GPLUS.put("L",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"int"},0,0));
            add(new Rproduct(new String[]{"float"},0,1));
        }});
        /*---语义分析额外加入产生式*/
        GPLUS.put("M",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"epsilon"},0,0));
        }});
        GPLUS.put("N",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"epsilon"},0,0));
        }});
        /*-----------------------*/
        GPLUS.put("S",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"id","=","E",";"},0,0));
            add(new Rproduct(new String[]{"if","(","C",")","X","S"},0,1));
            add(new Rproduct(new String[]{"if","(","C",")","X","S","Y","else","X","S"},0,2));
            add(new Rproduct(new String[]{"while","X","(","C",")","X","S"},0,3));
            add(new Rproduct(new String[]{"S","X","S"},0,4));
        }});
        /*---语义分析额外加入产生式*/
        GPLUS.put("X",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"epsilon"},0,0));
        }});
        GPLUS.put("Y",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"epsilon"},0,0));
        }});
        /*-----------------------*/
        GPLUS.put("C",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"E",">","E"},0,0));
            add(new Rproduct(new String[]{"E","<","E"},0,1));
            add(new Rproduct(new String[]{"E","==","E"},0,2));
        }});
        GPLUS.put("E",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"E","+","T"},0,0));
            add(new Rproduct(new String[]{"E","-","T"},0,1));
            add(new Rproduct(new String[]{"T"},0,2));
        }});
        GPLUS.put("T",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"F"},0,0));
            add(new Rproduct(new String[]{"T","*","F"},0,1));
            add(new Rproduct(new String[]{"T","/","F"},0,2));
        }});
        GPLUS.put("F",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"(","E",")"},0,0));
            add(new Rproduct(new String[]{"id"},0,1));
            add(new Rproduct(new String[]{"Integer"},0,2));
            add(new Rproduct(new String[]{"Float"},0,3));
        }});
    }
    /** 打印项集*/
    public static void printHs(HashMap<String, ArrayList<Rproduct>> item){
        for(Map.Entry<String, ArrayList<Rproduct>> entry : item.entrySet()){
            String key = entry.getKey();
            ArrayList<Rproduct> value = entry.getValue();
            for (Rproduct rproduct : value) {
                System.out.print(key + " -> ");
                for (int j = 0; j < rproduct.len; j++) {
                    System.out.print(rproduct.symbol[j]);
                }
                System.out.print("   " + rproduct.pointer + "\n");
            }
        }
    }

    /**判断一组产生式是否在状态集的某个状态中，返回状态值*/
    public static int isinC(HashMap<String, ArrayList<Rproduct>> item){
        for(Map.Entry<Integer,HashMap<String, ArrayList<Rproduct>>> entry : C.entrySet()){
            int key = entry.getKey();
            HashMap<String, ArrayList<Rproduct>> value = entry.getValue();
            boolean flag = true;
            for(Map.Entry<String, ArrayList<Rproduct>> entry2 : item.entrySet()){
                String key2 = entry2.getKey();
                ArrayList<Rproduct> value2 = entry2.getValue();
                if(value.containsKey(key2)){
                    if(!value.get(key2).containsAll(value2)){
                        flag = false;
                    }
                }else {
                    flag = false;
                }
            }
            if(flag){
                return key;
            }
        }
        return -1;
    }

    /** first集*/
    public static HashSet<String> first(String x){
        HashSet<String> ret = new HashSet<>();
        Stack<String> stack = new Stack<>();
        boolean flag = false;
        if(!Arrays.asList(vn).contains(x)){
            ret.add(x);
            return ret;
        }
        if(GPLUS.containsKey(x)){
            ArrayList<Rproduct> value = GPLUS.get(x);
            if(value.contains(new Rproduct(new String[]{"epsilon"},0,0))){
                flag = true;
            }
            for(Rproduct rproduct : value){
                if(!rproduct.symbol[0].equals(x)){
                    if(Arrays.asList(vn).contains(rproduct.symbol[0])){
                        stack.push(rproduct.symbol[0]);
                    }else {
                        ret.add(rproduct.symbol[0]);
                    }
                }else if(flag && rproduct.len>1){
                    stack.push(rproduct.symbol[1]);
                }
            }
            while (!stack.empty()){
                String firstVn = stack.pop();
                ret.addAll(first(firstVn));
            }
        }
        return ret;
    }
    /** 符号串first集*/
    public static HashSet<String> firstX(ArrayList<String> x){
        HashSet<String> set = new HashSet<>();
        for (String s : x) {
            if (first(s).contains("epsilon")) {
                set.addAll(first(s));
                set.remove("epsilon");
            } else {
                set.addAll(first(s));
                return set;
            }
        }
        set.add("epsilon");
        return  set;
    }

    /**follow集*/
    public static HashSet<String> follow(String x) {
        HashSet<String> ret = new HashSet<>();
        Stack<String> stack = new Stack<>();
        if ("G".equals(x)) {
            ret.add("$");
            return ret;
        }else {
            for (Map.Entry<String, ArrayList<Rproduct>> entry : GPLUS.entrySet()) {
                String key = entry.getKey();
                for (Rproduct rproduct : entry.getValue()) {
                    for (int index = 0; index < rproduct.len; index++) {
                        if (rproduct.symbol[index].equals(x)) {
                            if (index < rproduct.len - 1) {
                                String next = rproduct.symbol[index + 1];
                                if (Arrays.asList(vt).contains(next)) {
                                    ret.add(next);
                                } else if (Arrays.asList(vn).contains(next)) {
                                    HashSet<String> set = firstX(rproduct.divideR(index + 1));
                                    if (set.contains("epsilon")) {
                                        set.remove("epsilon");
                                        ret.addAll(set);
                                        stack.add(key);
                                    } else {
                                        ret.addAll(set);
                                    }
                                }
                            } else if (index == rproduct.len - 1) {
                                stack.push(key);
                            }
                        }
                    }
                }
            }
            while (!stack.empty()){
                String left = stack.pop();
                if(!left.equals(x)){
                    ret.addAll(follow(left));
                }
            }
        }
        return ret;
    }

    /**
     * 求项集闭包
     * */
    public static HashMap<String, ArrayList<Rproduct>> getClosure(HashMap<String, ArrayList<Rproduct>> item){
        HashMap<String, ArrayList<Rproduct>> rproducts = new HashMap<>();
        boolean flag = true;
        while (flag){
            String x;
            System.out.println("新的一轮");
            printHs(item);
            /*遍历item，对item中每个产生式右部待移入的文法符号，GPLUS有以其为左部的产生式，若不在item中，则加入*/
            for (Map.Entry<String, ArrayList<Rproduct>> entry : item.entrySet()) {
                String key = entry.getKey();
                ArrayList<Rproduct> value = entry.getValue();
                System.out.println("key:" + key + "      value size:" + value.size());
                for (Rproduct rproduct : value) {
                    x = rproduct.getNext();
                    System.out.println("next:" + x + "     pointer:"+rproduct.pointer);
                    if (GPLUS.containsKey(x)) {
                        for (int i = 0; i < GPLUS.get(x).size(); i++) {
                            if(!item.containsKey(x) && !rproducts.containsKey(x)){
                                rproducts.put(x, new ArrayList<>());
                                System.out.println("新加入"+x);
                            }
                            if (!item.containsKey(x)) {
                                if(!rproducts.get(x).contains(GPLUS.get(x).get(i))){
                                    rproducts.get(x).add(GPLUS.get(x).get(i));
                                    System.out.println("add1:"+x+"->" + GPLUS.get(x).get(i).symbol[0]);
                                }
                            } else if (!item.get(x).contains(GPLUS.get(x).get(i))) {
                                if(!rproducts.containsKey(x)){
                                    rproducts.put(x, new ArrayList<>());
                                    System.out.println("新加入"+x);
                                }
                                if(!rproducts.get(x).contains(GPLUS.get(x).get(i))){
                                    rproducts.get(x).add(GPLUS.get(x).get(i));
                                    System.out.println("add2:"+x+"->"+ GPLUS.get(x).get(i).symbol[0]);
                                }
                            }
                        }
                    }
                }
            }
            if(rproducts.size()>=1){
                for(Map.Entry<String, ArrayList<Rproduct>> entry : rproducts.entrySet()){
                    String key = entry.getKey();
                    ArrayList<Rproduct> value = entry.getValue();
                    if(value.size() > 0)
                    {
                        if(!item.containsKey(key)){
                            item.put(key,new ArrayList<>());
                            System.out.println("item中放入新的左部"+key);
                        }
                        for(Rproduct rproduct : value){
                            item.get(key).add(rproduct);
                        }
                        flag = true;
                    }else {
                        flag = false;
                    }
                }
                LrParser.printHs(item);
            }else {
                flag = false;
            }
            rproducts.clear();
        }
        return item;
    }

    /**
     * 实现深度拷贝，用于完全辅助hashmap而不是赋值引用
     * */
    public static <T extends Serializable> T myClone(T obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clonedObj;
    }

    /**项目集闭包item对应于x的后继项目集闭包
     * 参数hashmap传递的是引用的拷贝，对他属性的改变会改变原值*/
    public static HashMap<String, ArrayList<Rproduct>> go(HashMap<String, ArrayList<Rproduct>> item, String x){
        HashMap<String, ArrayList<Rproduct>> j = new HashMap<>();
        HashMap<String, ArrayList<Rproduct>> itemClone = myClone(item);
        for(Map.Entry<String, ArrayList<Rproduct>> entry : itemClone.entrySet()){
            String key = entry.getKey();
            ArrayList<Rproduct> value = entry.getValue();
            for(Rproduct rproduct : value){
                if(rproduct.pointer<rproduct.len){
                    if(!j.containsKey(key) && rproduct.getNext().equals(x)){
                        j.put(key, new ArrayList<>());
                        rproduct.pointer++;
                        j.get(key).add(rproduct);
                    }else if(rproduct.getNext().equals(x)){
                        rproduct.pointer++;
                        j.get(key).add(rproduct);
                    }
                }
            }
        }
        LrParser.printHs(j);
        return LrParser.getClosure(j);
    }

    /**初始化规范项集族*/
    public static void initC(){
        HashMap<String, ArrayList<Rproduct>> item = new HashMap<>();
        item.put("G",new ArrayList<Rproduct>(){{
            add(new Rproduct(new String[]{"P"},0,0));
        }});
        int i = 0;
        Queue<Integer> pointer = new LinkedList<>();
        pointer.add(i);
        C.put(i,LrParser.getClosure(item));
        printHs(C.get(i));
        HashMap<String, ArrayList<Rproduct>> gotoix;
        while (!pointer.isEmpty()){
            int j = pointer.poll();
            HashMap<String, ArrayList<Rproduct>> cItem = C.get(j);
            HashMap<String, Integer> flag = new HashMap<>();
            System.out.println("开始处理第状态:"+j);
            LrParser.printHs(cItem);
            for(Map.Entry<String, ArrayList<Rproduct>> entry : cItem.entrySet()){
                String key = entry.getKey();
                ArrayList<Rproduct> value = entry.getValue();
                for (Rproduct rproduct : value) {
                    String x = rproduct.getNext();
                    if(x!=null){
                        flag.put(x, 1);
                    }
                    System.out.println(x);
                }
            }
            for(Map.Entry<String, Integer> entryflag : flag.entrySet()){
                String flagkey = entryflag.getKey();
                HashMap<Integer, String> source = new HashMap<>();
                if(!"epsilon".equals(flagkey) && flagkey!=null){
                    System.out.println("下个输入符号"+flagkey);
                    gotoix = LrParser.go(C.get(j),flagkey);
                    int index = LrParser.isinC(gotoix);
                    if(gotoix != null && index==-1){
                        C.put(++i,gotoix);
                        pointer.add(i);
                        source.put(j,flagkey);
                        GOTO.put((HashMap<Integer, String>) source.clone(),i);

                        System.out.println("记录i："+i);
                        System.out.println("--------------");
                        LrParser.printHs(gotoix);
                        System.out.println("--------------");
                    }else {
                        source.put(j,flagkey);
                        GOTO.put((HashMap<Integer, String>) source.clone(),index);
                        System.out.println("-------已存在-------");
                        assert gotoix != null;
                        LrParser.printHs(gotoix);
                        System.out.println("-------------------");
                    }
                    source.clear();
                }
            }
            flag.clear();
        }
    }
    /**初始化slr分析表*/
    public static void initLrTable(){
        for(Map.Entry<Integer, HashMap<String, ArrayList<Rproduct>>> entry : C.entrySet()){
            int key = entry.getKey();
            HashMap<String, ArrayList<Rproduct>> value = entry.getValue();
            for(Map.Entry<String, ArrayList<Rproduct>> entry2 : value.entrySet()){
                String lP = entry2.getKey();
                ArrayList<Rproduct> rP = entry2.getValue();
                for(int i=0; i<rP.size(); i++){
                    Rproduct rproduct = rP.get(i);
                    String variable = rproduct.getNext();
                    HashMap<Integer,String> actionKey = new HashMap<>();
                    if(variable != null && !"epsilon".equals(variable)){
                        if(Arrays.asList(vt).contains(variable)){
                            actionKey.put(key,variable);
                            int des = GOTO.get(actionKey);
                            ACTION.put((HashMap<Integer, String>) actionKey.clone(),"s"+des);
                            System.out.println(key+"-----"+variable+"-----"+"s"+des);
                        }else if(Arrays.asList(vn).contains(variable)){
                            /*在初始化规范项集族时已求出*/
                            actionKey.put(key,variable);
                            int des = GOTO.get(actionKey);
                            System.out.println(key+"-----"+variable+"-----"+des);
                        }
                    }else {
                        HashSet<String> followLp = follow(lP);
                        for(int k=0; k<vt.length; k++){
                            if(followLp.contains(vt[k])){
                                if(!"G".equals(lP)){
                                    actionKey.put(key,vt[k]);
                                    ACTION.put((HashMap<Integer, String>) actionKey.clone(),"r"+lP+rproduct.no);
                                    System.out.println(key+"-----"+vt[k]+"-----r"+lP+rproduct.no);
                                }else {
                                    actionKey.put(key,"$");
                                    ACTION.put((HashMap<Integer, String>) actionKey.clone(),"acc");
                                    System.out.println(key+"-----"+"$"+"-----"+"acc");
                                }
                            }
                            actionKey.clear();
                        }
                    }
                    actionKey.clear();
                }
            }
        }
    }
    /** slr分析器 */
    public static void slrParser(){
        reduce.clear();
        int index = 0;
        int state = 0;
        Stack<Integer> stateStack = new Stack<>();
        stateStack.push(state);
        while (true){
            state = stateStack.peek();
            Word symbol = Word.token.get(index);
            HashMap<Integer,String> s1 = new HashMap<>();
            s1.put(state,symbol.getType());
            if(ACTION.containsKey(s1)){
                String action = ACTION.get(s1);
                System.out.println("状态"+state+"  输入符号"+symbol.getType()+"  动作"+action);
                if(action.charAt(0) == 's'){
                    stateStack.push(Integer.parseInt(action.substring(1)));
                    int x = stateStack.peek();
                    System.out.println("栈顶状态"+x+"");
                    index++;
                }else if(action.charAt(0) == 'r'){
                    String leftProduct = action.substring(1,2);
                    int productNo = Integer.parseInt(action.substring(2));
                    int popLen;
                    if("epsilon".equals(GPLUS.get(leftProduct).get(productNo).symbol[0])){
                        popLen = 0;
                    }else {
                        popLen = GPLUS.get(leftProduct).get(productNo).len;
                    }
                    for(;popLen>0;popLen--){
                        stateStack.pop();
                        int x = stateStack.peek();
                        System.out.println("栈顶状态"+x+"");
                    }
                    state = stateStack.peek();
                    HashMap<Integer,String> s2 = new HashMap<>();
                    s2.put(state,leftProduct);
                    stateStack.push(GOTO.get(s2));
                    System.out.println(leftProduct+"->"+GPLUS.get(leftProduct).get(productNo).printProduct());
                    reduce.add(leftProduct+"->"+GPLUS.get(leftProduct).get(productNo).printProduct());
                    int x = stateStack.peek();
                    System.out.println("栈顶状态"+x+"");
                }else if("acc".equals(action)){
                    System.out.println("语法分析完成");
                    reduce.add("G->P 语法分析完成");
                    break;
                }else {
                    System.out.println("error");
                    reduce.add("语法错误");
                    break;
                }
            }else {
                System.out.println("error");
                reduce.add("语法错误");
                break;
            }
        }
    }

    public static void main(String[] args){

        LrParser.initC();
        for(Map.Entry<Integer,HashMap<String, ArrayList<Rproduct>>> entry : C.entrySet()) {
            int key = entry.getKey();
            System.out.println("状态I："+key);
            HashMap<String, ArrayList<Rproduct>> value = entry.getValue();
            LrParser.printHs(value);
        }
        LrParser.initLrTable();
        Scancer.input = "int a; int b; int c; int x; int y; while (a<b) if(c<5) while (x>y) z=x+1; else x=y;";
        Scancer.lexicalAnalysis();
        for (Word word : Word.token) {
            word.printToken();
        }
        LrParser.slrParser();

//        ArrayList<String> t = new ArrayList<>();
//        t.add("J");
//        t.add("else");
//        HashSet<String> test = LrParser.firstX(t);
//        for (String s : test) {
//            System.out.println(s);
//        }
//        HashSet<String> test2 = LrParser.follow("S");
//        for(Iterator iterator = test2.iterator(); iterator.hasNext();){
//            System.out.println(iterator.next());
//        }

    }
}
