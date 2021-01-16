package ir.sharif.aichallenge.server.engine.config;

/**
 * Copyright (C) 2016 Hadi
 */
public class BooleanParam extends Param<Boolean> {
    public BooleanParam(String paramName, Boolean defaultValue) {
        super(paramName, defaultValue);
    }

    @Override
    public Boolean getValueFromString(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ignore) {
            return null;
        }
    }
}
