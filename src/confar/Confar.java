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

import java.io.File;
import java.io.IOException;

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
    
    public static void load(File file) throws IOException {
    }
    
    public static void save(File file) throws IOException {
    }
}