package com.alibaba.android.arouter.compiler.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * Created by zhangluya on 2018/1/18.
 */

public class RoutersMappingGenerator {

    public static final String ASSETS_DEF_PATH = "src/main/assets";

    public static void createRouterMapping(Logger logger, Set<String> needLoadClassList, String assetPath, String moduleNameNoFormat) throws IOException {
        if (!needLoadClassList.isEmpty()) {
            logger.info(">>> Start create routers config. <<< ");
            String realAssetsPath = assetPath == null || "".equals(assetPath) ? ASSETS_DEF_PATH : assetPath;
            File file = new File(moduleNameNoFormat + File.separator + realAssetsPath);
            if (!file.exists()) {
                boolean mkdir = file.mkdir();
                if (!mkdir) {
                    logger.error(">>> Failed to create Assets folder. <<<");
                    throw new FileNotFoundException("Failed to create Assets folder. Check 'assetsPath' is correct.");
                }
            }
            File routersFile = new File(file, moduleNameNoFormat + "_routers.rt");
            logger.info(">>> routers file name " + routersFile.getAbsolutePath() + " <<<");
            if (routersFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(routersFile);
                Properties properties = new Properties();
                properties.load(new BufferedInputStream(fileInputStream));

                String routers = properties.getProperty("routers");
                String newValue = arrayToString(needLoadClassList);
                if (newValue.equals(routers)) {
                    return;
                }

                if (newValue.length() > routers.length() && newValue.contains(routers)) {
                    routers += "," + newValue.replace(routers, "");
                } else if (newValue.length() < routers.length() && routers.contains(newValue)) {
                    routers = newValue;
                } else {
                    routers += "," + newValue;
                }
                properties.put("routers", routers);
                FileOutputStream fileOutputStream = new FileOutputStream(routersFile);
                properties.store(fileOutputStream, null);
                fileInputStream.close();
                fileOutputStream.close();
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream(routersFile);
                Properties properties = new Properties();

                properties.put("routers", arrayToString(needLoadClassList));
                properties.store(fileOutputStream, null);
                fileOutputStream.close();
            }


        }
    }

    private static String arrayToString(Set<String> routers) {
        StringBuilder sb = new StringBuilder();
        for (String item : routers) {
            sb.append(item).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();

    }
}
