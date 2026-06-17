/*
 * Copyright (C) 2026 onebitwonder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package confar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author onebitwonder
 */
public class Confar {
    
    public interface TypeParser<T> {
        
        public T parse(String raw) throws  IllegalArgumentException;
    }

    public static class Setting<T> {
        
        public Setting(String name, T defaultValue, TypeParser<T> typeParser) throws IllegalArgumentException {

            if ((null == name) || name.isBlank()) {
                throw new IllegalArgumentException("Setting must have a name");
            }
            
            if (null == defaultValue) {
                throw new IllegalArgumentException("Default value must not be null");
            }
            
            if (null == typeParser) {
                throw new IllegalArgumentException("TypeParser must not me null");
            }
            
            this.name = name;
            this.value = defaultValue;
            this.required = false;
            this.typeParser = typeParser;
        }

        public Setting(String name, TypeParser<T> typeParser) throws IllegalArgumentException {

            if ((null == name) || name.isBlank()) {
                throw new IllegalArgumentException("Setting must have a name");
            }
            
            if (null == typeParser) {
                throw new IllegalArgumentException("TypeParser must not me null");
            }

            this.name = name;
            this.required = true;
            this.typeParser = typeParser;
        }
        
        private final String name;
        
        public String getName() {
            return name;
        }

        private final boolean required;
        
        public boolean isRequired() {
            return required;
        }
        
        private T value = null;
        
        private void parse(String value) throws IllegalArgumentException {
            this.value = typeParser.parse(value);
        }
        
        public T get() throws IllegalStateException {
            if (null == value) {
                throw new IllegalStateException("Setting '" + name + "' has no value. Use Confar.load() to initialise settings before calling get() on them");
            }
            
            return value;
        }
        
        public void set(T value) {
            if (null == value) {
                throw new IllegalStateException("Setting '" + name + "' cannot be assigned a value of 'null'");
            }

            this.value = value;
        }
        
        private final TypeParser<T> typeParser;
        
        public TypeParser<T> getTypeParser() {
            return typeParser;
        }
        
        private static final String DEFAULT_GROUP  = "";
        
        private String group = DEFAULT_GROUP;
        
        private String getGroup() {
            return group;
        }
        
        private void setGroup(String name) {
            group = name;
        }
    }
    
    private Confar() {
        ;
    }
    
    public static void load(File file, Map<String, Setting<?>> declaredSettings) throws IOException, IllegalArgumentException, IllegalStateException {
        
        if (null == file) {
            throw new IOException("File must not be null");
        }
        
        if ((null == declaredSettings) || declaredSettings.isEmpty()) {
            throw new IllegalArgumentException("Settings list must not be null or empty");
        }
        
        try (BufferedReader configFile = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            int lines = 0;
            String currentLine;
            String group = "";
            List<String> parsedSettings = new ArrayList<>();
            
            while (null != (currentLine = configFile.readLine())) {
                lines++;

                currentLine = currentLine.trim();

                if (currentLine.isBlank()) {
                    continue;
                } else if (currentLine.startsWith("#")) {
                    continue;
                } else if (currentLine.startsWith(";")) {
                    continue;
                } else if (currentLine.startsWith("[")) {

                    if (!currentLine.endsWith("]")) {
                        throw new IllegalArgumentException("Invalid group header at line " + lines);
                    }
                    
                    group = currentLine.substring(1, currentLine.length() - 1).trim();
                    
                    if (group.isBlank()) {
                        throw new IllegalArgumentException("Invalid group name in line " + lines);
                    }
                    
                    continue;
                } else  {

                    int index = currentLine.indexOf('=');

                    if (-1 == index) {
                        throw new IllegalArgumentException("Invalid line " + lines + ": expected key=value");
                    }

                    String key = currentLine.substring(0, index).trim();
                    String val = currentLine.substring(index + 1).trim();

                    if (parsedSettings.contains(key)) {
                        throw new IllegalStateException("Duplicate setting '" + key + "' at line " + lines);
                    }
                    
                    Setting setting = declaredSettings.get(key);
                    
                    if (null == setting) {
                        throw new IllegalArgumentException("Unknown setting '" + key + "' at line " + lines);
                    }
                    
                    setting.parse(val);
                    setting.setGroup(group); // soft setting, no hard mapping
                    
                    parsedSettings.add(key);
                }
            }
            
            for (String key : declaredSettings.keySet()) {
                if (!parsedSettings.contains(key) && declaredSettings.get(key).isRequired()) {
                    throw new IllegalStateException("Missing required setting '" + key + "'");
                }
            }
        }
    }
    
    public static void save(File file) throws IOException {
    }
}