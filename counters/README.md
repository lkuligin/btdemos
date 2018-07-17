# Cloud BigTable mutations example

This is a simple demonstration of using mutations to implement incremental counters with BigTable.

## Setup
You will need the following prerequisites:
1. Setup a running BT cluster:
    
    ```
    gcloud beta bigtable instances create demo-instance \
        --cluster=demo-instance \
        --cluster-zone=europe-west1-b \
        --display-name=demo-instance \
        --cluster-storage-type=SSD \
        --instance-type=DEVELOPMENT
   ```
        
2. Start a HBase shell as described [here](https://cloud.google.com/bigtable/docs/installing-hbase-shell).

    ```create 'counters', 'cf1'```

3. Have a look at the initial values ```scan 'counters'```

## Test mutations

1. Run the example:
    ```
    cd mutations
    mvn package
    mvn exec:java -Dbigtable.projectID=kuligin-sandbox \
    -Dbigtable.instanceID=demo-instance
    ```
    
2. Make sure that countes have been incremented: ```scan 'counters'```
