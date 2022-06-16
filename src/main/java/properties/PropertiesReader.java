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
            assert inputStream != null;
            inputStream.close();
        } catch (IOException exception) {

        }
    }
    public static String getProperty(String propertyName){
        return properties.getProperty(propertyName);
    }
}
