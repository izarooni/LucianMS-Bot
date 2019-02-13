package test;

import com.lucianms.io.Defaults;

import java.io.IOException;

/**
 * @author izarooni
 */
public class Tester {

    public static void main(String[] args) throws IOException {
        Defaults.createDefault("", "config.ini");
    }
}
