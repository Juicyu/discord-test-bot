package properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private static Properties properties;

    static {
        properties = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = cl.getResourceAsStream("token.properties");
        try {
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException exception) {

        }
    }
    public static final String getProperty(String propertyName){
        return properties.getProperty(propertyName);
    }
}
