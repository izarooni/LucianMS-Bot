package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author izarooni
 */
public class Tester {

    public static void main(String[] args) {
        String content = "asdjfalkfAries";
        String regex = "aries";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        System.out.println(matcher.find());
    }
}
