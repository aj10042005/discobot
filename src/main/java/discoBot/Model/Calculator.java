package discoBot.Model;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Stack;

public abstract class Calculator {

    static class NumBuffer {
        private StringBuffer num;
        private boolean isEmpty;
        public static NumBuffer instance;

        public NumBuffer() {
            num = new StringBuffer();
            isEmpty=true;
        }

        public Double pop() {
            if(this.isEmpty)return null;
            else {
                isEmpty=true;
                return Double.valueOf(num.toString());
            }
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void push(char in) {
            if(isEmpty) {isEmpty=false;
                num.delete(0,num.length()).append(in);
            } else {
                num.append(in);
            }
        }
    }
    
    private static int checkForMinus(char[] in, int i, int len, StringBuffer exp, StringBuffer aftermath) {
        Character[] ignore = {'\u0020'};
        boolean minusNeeded=true;
        for(int j = i-1; j >= 0; j-=1) {
            if(Character.isDigit(in[j])) {
                aftermath.append("+");
                break;
            } else if(in[j]!='\u0020') break;
        }
        for(i+=1;i < len;i+=1) {
            if(Character.isDigit(in[i])) {
                if(minusNeeded) exp.append("-");
                return i-1;
            }

            else if(Arrays.asList(ignore).contains(in[i]));

            else if(in[i] == '-') minusNeeded=!minusNeeded;

            else {
                System.err.println("Line " + i + ": operator after minus");
                return i-1;
            }
        }
        return len;
    }

    private static int onBracketMet(char[] in, int start, StringBuffer exp) {
        int iter = in.length;
        int bracks = 0, j = start-1;
        boolean isNegative = false;


        for(; j >= 0; j-=1) {
            if(in[j]=='-') {
                isNegative=true;
                break;
            } else if(in[j]!='\u0020') break;
        }

        for(j = start; j < iter; j++) {
            if(in[j]=='(')bracks+=1;
            else if(in[j]==')') {
                bracks-=1;
                if(bracks==0)break;
            }

        }
        if(bracks>0)System.err.println("Expression has missing closing brackets: " + bracks);
        exp.append(toReversePolishNotation(String.valueOf(in).substring(start+1, j).toCharArray()));
        if(isNegative) exp.append(" -1 * ");
        return j;
    }

    public static String toReversePolishNotation(char[] in) {
        StringBuffer exp = new StringBuffer(), aftermath = new StringBuffer();
        int iter = in.length;
        char mulDiv = '='; boolean isMul=false;
        Character[] ignore = {'-', '('};

        for (int i = 0; i < iter; i++) {
            if(Character.isDigit(in[i])||in[i]=='.') exp.append(in[i]);
            else if(in[i]=='\u0020');
            else {
                exp.append('\u0020');

                if(isMul && !Arrays.asList(ignore).contains(in[i])) {
                    exp.append(mulDiv).append('\u0020');
                    isMul = false;
                }

                switch(in[i]) {//test lines

                    case '-':
                        i = checkForMinus(in, i, iter, exp, aftermath);
                        break;
                    case '+':
                        aftermath.append('+');
                        break;
                    case '*':
                    case '/':
                        isMul = true;
                        mulDiv = in[i];
                        break;
                    case '(':
                        i = onBracketMet(in, i, exp);

                        break;
                    default:
                        System.err.println("Can't read " + in[i]);
                        break;
                }

            }
        }
        if(isMul) exp.append(mulDiv);

        return exp.append(aftermath).toString();
    }

    private static double calcRPNExp(String in) { //TODO: Make it less shitty and buggy mess than it is now
        Stack<Double> stek = new Stack<>();
        int len = in.length();
        char[] exp = in.toCharArray();
        Character[] numberParts = {'.', '-'};

        NumBuffer buffer = new NumBuffer();

        try {
            for (int i = 0; i < len; i++) {
                if (Character.isDigit(exp[i]) || Arrays.asList(numberParts).contains(exp[i])) {
                    buffer.push(exp[i]);
                } else {
                    if (!buffer.isEmpty()) stek.push(buffer.pop());

                    switch (exp[i]) {
                        case '+':
                            stek.push(stek.pop() + stek.pop());
                            break;
                        case '*':
                            stek.push(stek.pop() * stek.pop());
                            break;
                        case '/':
                            stek.push(1 / (stek.pop() / stek.pop()));
                            break;
                    }

                }
            }
            return stek.pop();

        } catch(EmptyStackException e) {
            System.err.println("Not enough operands given, returning zero");
            return 0;
        }
    }


    public static String calc(String in) {
        char[] exp = in.toCharArray();
        return Double.toString(calcRPNExp(toReversePolishNotation(exp)));
    }
}