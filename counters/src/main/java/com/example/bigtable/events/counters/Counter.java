package com.example.bigtable.events.counters;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class Counter {
    private byte[] counterName;
    private byte[] counterFamily;

    public Counter(String counterFamily, String counterName) {
        this.counterFamily = Bytes.toBytes(counterFamily);
        this.counterName = Bytes.toBytes(counterName);
    }

    public void increment(Table table, String userId) throws IOException {
        //Table table = conn.getTable(this.tableName);
        table.incrementColumnValue(Bytes.toBytes(userId), this.counterFamily,
                this.counterName, 1);
    }

    public Long getValue(Result result) {
        if (!result.isEmpty()) {
            byte[] value = result.getValue(this.counterFamily, this.counterName);
            return value == null ? 0L : Bytes.toLong(value);
        } else {
            return 0L;
        }
    }
}
