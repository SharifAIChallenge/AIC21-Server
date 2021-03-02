package ir.sharif.aichallenge.server.engine.config;

import java.awt.*;
import java.io.File;

/**
 * Copyright (C) 2016 Hadi
 */
public class FileParam extends Param<File> {
    private String pattern;

    public FileParam(String paramName, File defaultValue, String pattern) {
        super(paramName, defaultValue);
        this.pattern = pattern;
    }

    @Override
    public File getValueFromString(String value) {
        File file = new File(value);
        return file.exists() ? file : null;
    }

    @Override
    public File getValueFromUser() {
        FileDialog fileDialog = new FileDialog((Frame) null, "Choose " + getParamName(), FileDialog.LOAD);
        fileDialog.setFilenameFilter((dir, name) -> name.matches(pattern));
        fileDialog.setMultipleMode(false);
        fileDialog.setVisible(true);
        File[] files = fileDialog.getFiles();
//        JOptionPane.showMessageDialog(null, "Parameter '" + getParamName() + "' is not specified or invalid.\nPlease select a file to continue.", "Game Parameters", JOptionPane.INFORMATION_MESSAGE);
//        JFileChooser fileChooser = new JFileChooser((String) null);
//        int result = fileChooser.showOpenDialog(null);
//        if (result != JOptionPane.YES_OPTION)
//            return null;
//        return fileChooser.getSelectedFile();
        return files.length != 1 ? null : files[0];
    }
}
