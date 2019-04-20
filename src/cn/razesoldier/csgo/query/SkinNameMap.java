package cn.razesoldier.csgo.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SkinNameMap {
    private static SkinNameMap instance;

    private HashMap<String, String> map;

    synchronized public static SkinNameMap getInstance() {
        if (instance == null) {
            instance = new SkinNameMap();
        }
        return instance;
    }

    private SkinNameMap() {
        InputStream inputStream = Main.class.getResourceAsStream("/skinnamemap.txt");
        if (inputStream == null) {
            throw new RuntimeException("Failed to read " + Main.class.getResource("/skinnamemap.txt"));
        }
        map = new HashMap<>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inputStreamReader);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                int pos = line.indexOf("=");
                if (!map.containsKey(line.substring(0, pos))) {
                    map.put(line.substring(0, pos), line.substring(pos + 1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        try {
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close skinnamemap.txt, reason: " + e.getLocalizedMessage());
        }
    }

    public String getEnName(String key) {
        return map.get(key);
    }
}
