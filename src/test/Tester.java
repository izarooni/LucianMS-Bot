package test;

import com.lucianms.io.Defaults;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author izarooni
 */
public class Tester {

    public static void main(String[] args) {
        try {
            Defaults.createDefault("", "config.json");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
