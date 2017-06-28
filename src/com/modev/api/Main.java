package com.modev.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String struct = "\tOwners                         []string             `json:\"owners\" cql:\"owners\"`\n" +
            "\tFileID                         gocql.UUID           `json:\"fileID\" cql:\"file_id\"`\n" +
            "\tSearchID                       gocql.UUID           `json:\"searchID\" cql:\"searchid\"`\n" +
            "\tMD5Hash                        string               `json:\"md5Hash\" cql:\"md5hash\"`\n" +
            "\tFileName                       string               `json:\"fileName\" cql:\"file_name\"`\n" +
            "\tFileType                       string               `json:\"fileType\" cql:\"file_type\"`\n" +
            "\tFileSize                       int64                `json:\"fileSize\" cql:\"file_size\"`\n" +
            "\tGist                           string               `json:\"gist\" cql:\"gist\"`\n" +
            "\tThumbnailURL                   string               `json:\"thumbnailurl\" cql:\"thumbnail_url\"`\n" +
            "\tThumbnail                      []byte               `json:\"thumbnail\" cql:\"thumbnail\"`\n" +
            "\tCreated                        time.Time            `json:\"created\" cql:\"created\"`\n" +
            "\tLastModified                   time.Time            `json:\"lastModified\" cql:\"last_modified\"`\n" +
            "\tLastOpened                     time.Time            `json:\"lastOpened\" cql:\"last_opened\"`\n" +
            "\tLatitude                       float64              `json:\"latitude\" cql:\"latitude\"`\n" +
            "\tLongitude                      float64              `json:\"longitude\" cql:\"longitude\"`\n" +
            "\tShape                          string               `json:\"shape\" cql:\"shape\"`\n" +
            "\tAuthor                         string               `json:\"author\" cql:\"author\"`\n" +
            "\tFileText                       string               `json:\"fileText\" cql:\"file_text\"`\n" +
            "\tDataString                     map[string]string    `json:\"dataString,omitempty\"`\n" +
            "\tDataText                       map[string]string    `json:\"dataText,omitempty\"`\n" +
            "\tDataDouble                     map[string]float64   `json:\"dataDouble,omitempty\"`\n" +
            "\tDataInt                        map[string]int64     `json:\"dataInt,omitempty\"`\n" +
            "\tDataBool                       map[string]bool      `json:\"dataBool,omitempty\"`\n" +
            "\tDataTime                       map[string]time.Time `json:\"dataTime,omitempty\"`\n" +
            "\tVersion                        gocql.UUID           `json:\"version,omitempty\" cql:\"version\"`\n" +
            "\tAction                         []string             `json:\"action,omitempty\" cql:\"action\"`\n" +
            "\tPermissions                    int                  `json:\"permissions\" cql:\"permissions\"`\n" +
            "\tStarred                        bool                 `json:\"starred\"`\n" +
            "\tRestorePath                    string               `json:\"restorePath,omitempty\" cql:\"restore_path\"`\n" +
            "\taide.Groups                    `json:\"groups\" cql:\"groups\"`\n" +
            "\tclassifications.Classification `json:\"classification\" cql:\"classification\"`";

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
        put("int", "Integer");
        put("byte", "Byte");
        put("time.Time", "DateString");
        put("float64", "Double");
        put("bool", "Boolean");
        put("classifications.Classification", "Classification");
        put("aide.Groups", "List<String>");
    }};

    private static final Pattern mapMatch = Pattern.compile("\\[(.+)](.+)");

    private static String getConvertedType(final String type) {
        if (type.startsWith("[]")) {
            return "List<" + typeConversion.get(type.substring(2)) + ">";
        } else if (type.startsWith("map[")) {
            final String[] types = getMatches(mapMatch, type);
            return "Map<" + typeConversion.get(types[0]) + ", " + typeConversion.get(types[1]) + ">";
        } else {
            return typeConversion.get(type);
        }
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