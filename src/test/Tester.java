package test;

import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class Tester {

    public static void main(String[] args) {
        String content = "aries";
        String pattern = "aries";

        boolean matches = Pattern.matches(Pattern.compile(pattern).pattern(), content);
        System.out.println(matches);
    }
}
