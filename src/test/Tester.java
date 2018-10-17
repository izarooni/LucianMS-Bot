package test;

import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class Tester {

    public static void main(String[] args) {
        String content = "izarooni";
        if (!content.matches(Pattern.compile("[a-zA-Z0-9]{4,}").pattern())) {
            System.out.println("fail");
        } else {
            System.out.println("Success");
        }
    }
}
