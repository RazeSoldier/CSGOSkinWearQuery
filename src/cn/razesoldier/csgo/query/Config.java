package cn.razesoldier.csgo.query;

import java.io.*;
import java.util.Properties;

/**
 * 用于读写程序的配置文件
 * @since 1.0.1
 */
public class Config {
    /**
     * 配置文件的路径
     */
    private final static String CONFIG_FILE_PATH = Main.rootPath + "/config.xml";

    private static Config instance;

    private Properties properties;

    private OutputStream outputStream;

    private Config() {
        properties = new Properties();
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists() && configFile.length() != 0) {
            try {
                FileInputStream inputStream = new FileInputStream(configFile);
                properties.loadFromXML(inputStream);
                inputStream.close();
            } catch (FileNotFoundException e) {
                try {
                    // noinspection ResultOfMethodCallIgnored
                    configFile.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            outputStream = new FileOutputStream(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void unset(String key) {
        properties.remove(key);
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    /**
     * 将存储于内存的配置刷入本地文件
     */
    public void syncFile() {
        try {
            properties.storeToXML(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
