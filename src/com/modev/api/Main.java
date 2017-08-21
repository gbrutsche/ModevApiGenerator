package com.modev.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String struct = "";

    public static void main(final String[] args) {
        final String[] lines = struct
                .replace(", ", ",")
                .replace(" cql", "cql")
                .split("\n");

        for (final String line : lines) {
            final String[] parts = line.trim().split("\\s+");

            if (parts.length >= 3) {
                System.out.println("@ApiField\nprivate " + getConvertedType(parts[1]) + " " + getFieldName(parts[2]) + ";\n");
            } else if (parts.length == 2) {
                System.out.println("@ApiField\nprivate " + getConvertedType(parts[0]) + " " + getFieldName(parts[1]) + ";\n");
            }
        }
    }

    private static final Map<String, String> typeConversion = new HashMap<String, String>() {{
        put("gocql.UUID", "String");
        put("string", "String");
        put("int64", "Long");
        put("int32", "Integer");
        put("int", "Integer");
        put("byte", "Byte");
        put("time.Time", "DateString");
        put("float64", "Double");
        put("bool", "Boolean");
        put("classifications.Classification", "Classification");
        put("aide.Groups", "List<String>");
        put("AVR", "Avr");
    }};

    private static final Pattern mapMatch = Pattern.compile("\\[(.+)](.+)");

    private static String getConvertedType(String type) {
        if (type.startsWith("*")) {
            type = type.substring(1);
        }

        if (type.startsWith("[]")) {
            return "List<" + getType(type.substring(2)) + ">";
        } else if (type.startsWith("map[")) {
            final String[] types = getMatches(mapMatch, type);
            return "Map<" + getType(types[0]) + ", " + getType(types[1]) + ">";
        } else {
            return getType(type);
        }
    }

    private static String getType(final String type) {
        final String actualType = typeConversion.get(type);

        return actualType == null ? type : actualType;
    }

    private static final Pattern fieldMatch = Pattern.compile("`json:\"(.+?)[,\"]");

    private static String getFieldName(final String jsonPart) {
        return getMatches(fieldMatch, jsonPart)[0];
    }

    private static String[] getMatches(final Pattern pattern, final String stringToSearch) {
        final List<String> list = new ArrayList<>();
        Matcher m = pattern.matcher(stringToSearch);
        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                list.add(m.group(i));
            }
        }
        return list.toArray(new String[list.size()]);
    }
}