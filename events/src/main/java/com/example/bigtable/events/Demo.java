package com.example.bigtable.events;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class Demo {
    private static final byte[] TABLE_NAME = Bytes.toBytes("events");
    private static final Events event1 = new Events("e1");
    private static final Events event2 = new Events("e2");

    private static Map<String, List<Long>> getEvents(Table table, String userId) throws IOException {
        long currentTs= System.currentTimeMillis() / 1000L;

        Get get = new Get(Bytes.toBytes(userId));
        get = get.setMaxVersions().setTimeRange(currentTs-1L*24*3600, currentTs); //1day history
        Result result = table.get(get);
        if (!result.isEmpty()) {
            Map<String, List<Long>> events = new HashMap<>();
            for (Cell cell: result.rawCells()) {
                String event = Bytes.toString(cell.getQualifierArray());
                List<Long> tsList = events.get(event);
                if (tsList == null) {
                    tsList = new ArrayList<>();
                    events.put(event, tsList);
                }
                tsList.add(cell.getTimestamp());
            }
            return events;
        }
        else {
            return Collections.emptyMap();
        }
    }

    private static void printEvents(Table table, String userId) throws IOException {
        Map<String, List<Long>> events = getEvents(table, userId);
        if (!events.isEmpty()) {
            List<Long> d = new ArrayList<>();
            String e1 = events.getOrDefault("e1", d).stream().map(r -> r.toString()).reduce("", (r, c) -> r += c + "; ");
            String e2 = events.getOrDefault("e2", d).stream().map(r -> r.toString()).reduce("", (r, c) -> r += c + "; ");
            System.out.printf("\tEvent1 for %s is %s\n", userId, e1);
            System.out.printf("\tEvent2 for %s is %s\n", userId, e2);
        }
        else {
            System.out.printf("\tThere are no events for %s available\n", userId);
        }
    }

    private static void runDemo(String projectId, String instanceId) {
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            System.out.println("Connection opened!");
            Table eventsTable = connection.getTable(TableName.valueOf(TABLE_NAME));

            System.out.println("Events before adding timestamps:");
            printEvents(eventsTable, "user1");
            printEvents(eventsTable, "user2");

            Long currentTs = System.currentTimeMillis() / 1000L;
            event1.addEvent(eventsTable, "user1", currentTs);
            event1.addEvent(eventsTable, "user2", currentTs-1800L);
            event2.addEvent(eventsTable, "user1", currentTs-3600L);
            event1.addEvent(eventsTable, "user1", currentTs-3600L*24*2);
            event1.addEvent(eventsTable, "user2", currentTs-700L);
            event2.addEvent(eventsTable, "user1", currentTs-800L);

            System.out.println("Events after adding timestamps:");
            printEvents(eventsTable, "user1");
            printEvents(eventsTable, "user2");
        }
        catch (IOException e) {
            System.out.println("Exception occured" + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.out.println(sw.toString());
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
