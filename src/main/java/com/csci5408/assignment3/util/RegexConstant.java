package com.csci5408.assignment3.util;

/* Regex constants for filtering content in this application. */
public class RegexConstant {

    public static final String EMOJI_SPECIAL_CHAR_FILTER = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
    public static final String URL_FILTER = "(http|https).*?\\s";
    public static final String ALPHANUMERIC_FILTER = "[^A-Za-z0-9]+";
    public static final String ALPHANUMERIC_SPECIAL_CHAR_FILTER = "[^A-Za-z0-9.,/\"'&-]+";

}