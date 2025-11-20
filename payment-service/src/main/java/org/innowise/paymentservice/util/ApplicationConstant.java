package org.innowise.paymentservice.util;

public class ApplicationConstant {
    private ApplicationConstant() {
    }

    public static final String TOPIC_CREATE_PAYMENT = "CREATE_PAYMENT";
    public static final String TOPIC_CREATE_ORDER = "CREATE_ORDER";
    public static final int PARTITION_COUNT = 1;
    public static final short REPLICATION_FACTOR = 1;
    public static final String GENERATE_NUMBER_QUERY = "%s?min=%d&max=%d&count=1";
    public static final int DEFAULT_FAILED_RANDOM_VALUE = 1;
}
