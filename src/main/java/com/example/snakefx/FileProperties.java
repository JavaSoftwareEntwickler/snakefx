package com.example.snakefx;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileProperties {
    public String getPath(String properties){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/com/example/snakefx/file.properties"));
            return prop.getProperty(properties);
        } catch (IOException e) {
            return null;
        }
    }
}
