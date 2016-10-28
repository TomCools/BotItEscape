import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    private static Map<Character, Character> brackets = new HashMap<>();
    private static String opening = "{[(<";
    private static String closing = "}])>";

    public static void init() {
        brackets.put('{', '}');
        brackets.put('[', ']');
        brackets.put('(', ')');
        brackets.put('<', '>');
    }

    public static void main(String args[]) {
        init();
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        in.nextLine();

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String expression = in.nextLine();
            strings.add(expression);
        }
        for (int i = 0; i < N; i++) {
            System.out.println(flippable(strings.get(i)));
        }
    }

    public static boolean flippable(String s) {
        if (isValid(s)) {
            System.err.println("Base string already correct: " + s);
            return true;
        }
        boolean result = flippableRecursive(s.toCharArray(), 0);
        System.err.println(s + " is flippable?:" + result);
        return result;
    }

    static boolean flippableRecursive(char[] array, int index) {
        boolean result = false;
        for (int i = index; i < array.length && !result; i++) {
            char currentChar = array[i];
            char[] a = array.clone();
            if (isOpeningBracket(currentChar) || isClosingBracket(currentChar)) {
                a[i] = flip(currentChar);
                result |= isValid(new String(a));
                if (!result) {
                    result |= flippableRecursive(a, index + 1);
                }
            }
        }
        return result;
    }

    static char flip(char c) {
        if (isOpeningBracket(c)) {
            return closing.charAt(opening.indexOf(c));
        } else {
            return opening.charAt(closing.indexOf(c));
        }
    }

    static boolean isValid(String s) {
        Stack<Character> characters = new Stack<>();
        for (char c : s.toCharArray()) {
            if (isOpeningBracket(c)) {
                characters.push(c);
            } else if (isClosingBracket(c)) {
                if (characters.isEmpty()) {
                    return false;
                }
                Character matchingChar = characters.pop();
                if (brackets.get(matchingChar) != c) {
                    return false;
                }
            }
        }
        return characters.isEmpty();
    }


    public static boolean isOpeningBracket(char c) {
        return opening.indexOf(c) != -1;
    }

    public static boolean isClosingBracket(char c) {
        return closing.indexOf(c) != -1;
    }
}