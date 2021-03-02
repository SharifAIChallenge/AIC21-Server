package ir.sharif.aichallenge.server.engine.config;

import com.google.gson.JsonObject;
import ir.sharif.aichallenge.server.common.util.Log;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Copyright (C) 2016 Hadi
 */

public abstract class Param<T> {
    private static final ArrayList<Param> allParameters = new ArrayList<Param>();

    public static Param[] getAllParameters() {
        return allParameters.toArray(new Param[allParameters.size()]);
    }

    private final String paramName;
    private final T defaultValue;
    private T value;
    private boolean cached = false;

    public Param(String paramName, T defaultValue) {
        allParameters.add(this);
        this.paramName = paramName;
        this.defaultValue = defaultValue;
    }

    public String getParamName() {
        return paramName;
    }

    public T getValue() {
        if (value != null || cached)
            return value;
        if ((value = getValueFromEnv()) != null) {
            Log.i("PARAM", paramName + "=" + value);
            return value;
        }
        if ((value = getValueFromJsonObject(Configs.getConfigFile())) != null) {
            Log.i("PARAM", paramName + "=" + value);
            return value;
        }
        if ((value = getDefaultValue()) != null) {
            if (this != Configs.PARAM_AIC_DEPLOY && Configs.PARAM_AIC_DEPLOY.getValue() == Boolean.TRUE)
                Log.w("PARAM", "Using default value for parameter " + paramName + ".");
            Log.i("PARAM", paramName + "=" + value);
            return value;
        }
        if (Configs.PARAM_AIC_DEPLOY.getValue() == Boolean.TRUE)
            throw new RuntimeException("Config '" + paramName + "' not found.");
        value = getValueFromUser();
        Log.i("PARAM", paramName + "=" + value);
        cached = true;
        return value;
    }

    public T getValueFromEnv() {
        try {
            String value = System.getenv("AIC" + paramName);
            if (value == null)
                return null;
            return getValueFromString(value);
        } catch (Exception ignore) {
            return null;
        }
    }

    public T getValueFromJsonObject(JsonObject object) {
        try {
            String value = object.get(paramName).getAsString();
            return getValueFromString(value);
        } catch (Exception ignore) {
            return null;
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValueFromUser() {
        if (value == null) {
            try {
                String result = JOptionPane.showInputDialog(null, "Parameter '" + paramName + "' is not specified. Please enter a value to continue.", "Game Parameters", JOptionPane.INFORMATION_MESSAGE);
                value = getValueFromString(result);
            } catch (Exception ignore) {
                return null;
            }
        }
        return value;
    }

    public abstract T getValueFromString(String value);

    @Override
    public String toString() {
        return String.format("%s[%s]", paramName, getClass().getSimpleName());
    }
}
