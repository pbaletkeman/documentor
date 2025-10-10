package com.documentor.util;

/**
 * Application constants used across the application
 * Created to reduce duplication and improve code readability
 */
public final class ApplicationConstants {
    // Private constructor to prevent instantiation
    private ApplicationConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /** Minimum number of parts required for parsing function definitions */
    public static final int MINIMUM_PARTS_FOR_PARSING = 3;
    
    /** Array index where function prefix starts in the parsed output */
    public static final int FUNCTION_DEF_PREFIX_LENGTH = 1;
    
    /** Array index for function parameters in the parsed output */
    public static final int PARAMETERS_ARRAY_INDEX = 2;
    
    /** Default timeout for external process execution (in seconds) */
    public static final int DEFAULT_PROCESS_TIMEOUT_SECONDS = 30;
    
    /** Maximum number of lines to process in a single batch */
    public static final int MAX_LINES_PER_BATCH = 1000;
}