package ir.sharif.aichallenge.server.engine.config;

import com.google.gson.JsonObject;
import ir.sharif.aichallenge.server.common.network.Json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Copyright (C) 2016 Hadi
 */
public class Configs {
    public static String DEFAULT_CONFIG_PATH = "game.conf";
    private static JsonObject configFile = null;

    // General Configs
    public static final BooleanParam PARAM_AIC_DEPLOY = new BooleanParam("Deploy", false);

    // Client Configs
    public static final IntegerParam PARAM_CLIENTS_PORT = new IntegerParam("ClientsPort", 7099);
    public static final IntegerParam PARAM_CLIENTS_CONNECTIONS_TIMEOUT = new IntegerParam("ClientsConnectionTimeout", Integer.MAX_VALUE);
    public static final ArrayList<ClientConfig> CLIENT_CONFIGS = new ArrayList<ClientConfig>();

    // UI Configs
    public static final BooleanParam PARAM_UI_ENABLE = new BooleanParam("UIEnable", false);
    public static final StringParam PARAM_UI_TOKEN = new StringParam("UIToken", "00000000000000000000000000000000");
    public static final IntegerParam PARAM_UI_PORT = new IntegerParam("UIPort", 7000);
    public static final IntegerParam PARAM_UI_CONNECTIONS_TIMEOUT = new IntegerParam("UIConnectionTimeout", Integer.MAX_VALUE);

    // Output Controller Configs
    // Indicates that data will be sent to given {@link engine.network.UINetwork UINetwork} instance or not
    public static final BooleanParam PARAM_OC_SEND_TO_UI = new BooleanParam("OCSendToUI", false);
    // Indicates that a log of output will be saved in the given {@link java.io.File java.io.File} or not
    public static final BooleanParam PARAM_OC_SEND_TO_FILE = new BooleanParam("OCSendToFile", false);
    // The given {@link java.io.File java.io.File} to save data within
    public static final StringParam PARAM_OC_FILE_PATH = new StringParam("OCFilePath", "./game.log");

    private static void setConfigFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String content = new String(bytes, Charset.forName("UTF-8"));
            configFile = Json.GSON.fromJson(content, JsonObject.class);
        } catch (Exception ignore) {
            configFile = null;
        }
    }

    public static JsonObject getConfigFile() {
        return configFile;
    }

    public static void handleCMDArgs(String[] args) {
        if (args.length == 0)
            return;
        String[] split = args[0].split("=");
        if (split.length != 2)
            return;
        switch (split[0]) {
            case "--config": {
                File configFile = new File(split[1]);
                if (!configFile.exists())
                    configFile = new File(DEFAULT_CONFIG_PATH);
                setConfigFile(configFile);
                break;
            }
            case "--generate-config": {
                try {
                    File configFile = new File(split[1]);
                    Param[] params = Param.getAllParameters();
                    PrintWriter out = new PrintWriter(new FileOutputStream(configFile));
                    out.println("{");
                    for (int i = 0; i < params.length; i++) {
                        out.printf("\t\"%s\": \"%s\"%s\n", params[i].getParamName(), params[i].getDefaultValue() == null
                                ? "" : params[i].getDefaultValue().toString(), i == params.length - 1 ? "" : ",");
                    }
                    out.println("}");
                    out.close();
                    System.exit(0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
