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

    private Confar() {
        ;
    }
    
    public static void load(File file) throws IOException {
    }
    
    public static void save(File file) throws IOException {
    }
}