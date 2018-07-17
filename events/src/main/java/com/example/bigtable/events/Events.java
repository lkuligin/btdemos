package com.example.bigtable.events;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.shaded.org.apache.avro.generic.GenericData;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

public class Events {
    private byte[] eventName;
    private byte[] EVENTS_CF = Bytes.toBytes("cf1");

    public Events (String eventName) {
        this.eventName = Bytes.toBytes(eventName);
    }

    public void addEvent(Table table, String userId, Long ts) throws IOException {
        Put put = new Put(Bytes.toBytes(userId));
        put.addColumn(this.EVENTS_CF, this.eventName, ts, Bytes.toBytes(true));
        table.put(put);
    }
}
