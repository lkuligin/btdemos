# Cloud BigTable mutations example

This is a simple demonstration of storing raw events in Big Table column families.
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

    ```create 'events', {NAME => 'cf1', TTL => 86400}```

3. Have a look at the initial values ```scan 'events'```

## Test mutations

1. Run the example:
    ```
    cd events
    mvn package
    mvn exec:java -Dbigtable.projectID=kuligin-sandbox \
    -Dbigtable.instanceID=demo-instance
    ```
    
2. Make sure that countes have been incremented: ```scan 'events'```