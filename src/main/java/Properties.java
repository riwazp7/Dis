import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Properties {

    private static final String PROPERTIES_FILE_DIR = "dist.properties";

    private static Properties instance = null;

    public static Properties properties() throws ConfigurationException {
        return instance == null ? instance = new Properties() : instance;
    }

    private final Configuration configuration;

    private Properties() throws ConfigurationException {
        File propertiesFile = new File(PROPERTIES_FILE_DIR);
        Parameters parameters = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> fileBasedConfigurationBuilder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(parameters.fileBased().setFile(propertiesFile));
        configuration = fileBasedConfigurationBuilder.getConfiguration();
    }

    public String getProperty(String key) {
        return configuration.getProperty(key).toString(); // ?
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Properties.properties().getProperty("portA"));
    }
}
