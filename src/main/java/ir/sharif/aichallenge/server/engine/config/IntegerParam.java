package ir.sharif.aichallenge.server.engine.config;

/**
 * Copyright (C) 2016 Hadi
 */
public class IntegerParam extends Param<Integer> {
    public IntegerParam(String paramName, Integer defaultValue) {
        super(paramName, defaultValue);
    }

    @Override
    public Integer getValueFromString(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignore) {
            return null;
        }
    }
}
