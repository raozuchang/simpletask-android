/**
 * This file is part of Todo.txt Touch, an Android app for managing your todo.txt file (http://todotxt.com).
 *
 * Copyright (c) 2009-2012 Todo.txt contributors (http://todotxt.com)
 *
 * LICENSE:
 *
 * Todo.txt Touch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Todo.txt Touch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with Todo.txt Touch.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Todo.txt contributors <todotxt@yahoogroups.com>
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2009-2012 Todo.txt contributors (http://todotxt.com)
 */
package nl.mpcjanssen.simpletask.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import nl.mpcjanssen.simpletask.task.TaskCache;


/**
 * A utility class for performing Task level I/O
 *
 * @author Tim Barlotta
 */
public class TaskIo {
    private final static String TAG = TaskIo.class.getSimpleName();
    private static boolean sWindowsLineBreaks;

    public static ArrayList<String> loadTasksFromStream(InputStream is)
            throws IOException {
        ArrayList<String> items = new ArrayList<String>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(is));
            String line;
            long counter = 0L;
            while ((line = in.readLine()) != null) {
                items.add(line);
                counter++;
            }
        } finally {
            Util.closeStream(in);
            Util.closeStream(is);
        }
        return items;
    }

    private static String readLine(BufferedReader r) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean eol = false;
        int c;
        while(!eol && (c = r.read()) >= 0) {
            sb.append((char)c);
            eol = (c == '\r' || c == '\n');

            // check for \r\n
            if (c == '\r') {
                r.mark(1);
                c = r.read();
                if (c != '\n') {
                    r.reset();
                } else {
                    sWindowsLineBreaks = true;
                    sb.append((char)c);
                }
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    public static ArrayList<String> loadFromFile(File file)
            throws IOException {
        ArrayList<String> items = new ArrayList<String>();
        BufferedReader in = null;
        if (!file.exists()) {
            Log.w(TAG, file.getAbsolutePath() + " does not exist!");
        } else {
            InputStream is = new FileInputStream(file);
            try {
                in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                long counter = 0L;
                sWindowsLineBreaks = false;
                while ((line = readLine(in)) != null) {
                    line = line.trim();
                    if (line.length() > 0) {
                        items.add(line);
                    }
                    counter++;
                }
            } finally {
                Util.closeStream(in);
                Util.closeStream(is);
            }
        }
        return items;
    }


    public static void writeToFile(String contents, File file, boolean append) {
        try {
            Util.createParentDirectory(file);
            Writer fw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, append), "UTF-8"));
            fw.write(contents);
            fw.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
