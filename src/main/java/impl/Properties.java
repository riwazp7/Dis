package impl;

import org.apache.commons.configuration2.Configuration;

public class Properties {

    private static Properties instance = null;

    public static Properties properties() {
        return instance == null ? instance = new Properties() : instance;
    }

    private final Configuration configuration;

    private Properties() {
        this.configuration =
    }

}
