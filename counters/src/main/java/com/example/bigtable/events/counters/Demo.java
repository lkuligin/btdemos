package com.example.bigtable.events.counters;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class Demo {
    private static final byte[] TABLE_NAME = Bytes.toBytes("counters");
    private static final String COLUMN_FAMILY_NAME = "cf1";
    private static final Counter counter1 = new Counter(COLUMN_FAMILY_NAME, "c1");
    private static final Counter counter2 = new Counter(COLUMN_FAMILY_NAME, "c2");

    private static void getCounters(Table table, String userId) throws IOException {
        Result result = table.get(new Get(Bytes.toBytes(userId)));
        System.out.printf("\tCounter1 for %s is %s\n", userId, counter1.getValue(result).toString());
        System.out.printf("\tCounter2 for %s is %s\n", userId, counter2.getValue(result).toString());
    }

    private static void runDemo(String projectId, String instanceId) {
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            System.out.println("Connection opened!");
            Table countersTable = connection.getTable(TableName.valueOf(TABLE_NAME));

            System.out.println("Values before incrementing:");
            getCounters(countersTable, "user1");
            getCounters(countersTable, "user2");

            counter1.increment(countersTable, "user1");
            counter1.increment(countersTable, "user2");
            counter2.increment(countersTable, "user2");

            System.out.println("Values after incrementing:");
            getCounters(countersTable, "user1");
            getCounters(countersTable, "user2");
        }
        catch (IOException e) {
            System.out.println("Exception occured" + e.getMessage());
        }
        System.out.println("Closed the connection");
    }

    public static void main(String[] args)  {
        // Consult system properties to get project/instance
        String projectId = requiredProperty("bigtable.projectID");
        String instanceId = requiredProperty("bigtable.instanceID");

        runDemo(projectId, instanceId);
    }

    private static String requiredProperty(String prop) {
        String value = System.getProperty(prop);
        if (value == null) {
            throw new IllegalArgumentException("Missing required system property: " + prop);
        }
        return value;
    }

}
