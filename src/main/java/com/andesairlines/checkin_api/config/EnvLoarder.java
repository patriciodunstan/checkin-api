package com.andesairlines.checkin_api.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoarder {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .filename(".env")
            .ignoreIfMissing()
            .load();

    public static String get(String key){
        return dotenv.get(key);
    }
}
