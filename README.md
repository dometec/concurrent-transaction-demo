# ACID Transaction and Transaction Isolation Level DEMO

This is a very little quarkus project, you can use it to experiment with database transaction.

Detailed explanation in this video (Italian): https://youtu.be/Awojpl2un6M

## Start the demo

You need a machine with docker and just type, quarkus dev service will start a MySQL database for you:
```bash
$ ./mvnw quarkus:dev
```
There are three method that you can use to generate a problem on Database:

### Deadlock

You can generate a deadlock invoking this operation:
```bash
 $ curl -X POST localhost:8080/hello/deadlock
```
 In this case there are two thread that start two transactions. One thread update an entity (A) and after a second update anoter entity (B). The other thread do the same but in a different order  (B and after A). The row is the same on enach entity. 


### Lock timeout

You can generate a deadlock invoking this operation:
```bash
 $ curl -X POST localhost:8080/hello/locktimeout -v
```
 In this case there is a single thread that modify an entity (A) and try to start another transaction to modify the same entity before to close the first transaction. MySQL wait for 5 seconds (configured in src/main/resources/testcontainers/mysql_conf/extra.cnf, _innodb-lock-wait-timeout_) and after send back the lock timeout error to quarkus. Please check that the error causa the rollback also of the first transaction.

 ### Transaction isolation level test

You can generate a deadlock invoking this operation:
```bash
 $ curl -X POST localhost:8080/hello/trasisolation -v
```

## MySQL configuration

In src/main/resources/testcontainers/mysql_conf/extra.cnf there are some variables that are used to start MySQL:

### innodb_lock_wait_timeout

I set a timeout of 5 seconds just to wait a littet time instead of 50 seconds of default for Lock timeout test.

### general_log, general_log_file

I want to see all SQL statement send from quarkus, so if you open the /var/log/mysql_general.log that is inside the MySQL container you can see all the queries.

### transaction_isolation

This is the Transaction Isolation Level configured in MySQL. You can follow the guide in https://dev.mysql.com/doc/refman/8.0/en/server-options.html#option_mysqld_transaction-isolation and change this value, and verify what happen invoking the trassaction isolation test.

## Useful MySQL COMMANDS to check what happens

You can check the MySQL's variables with a sql like:

```sql
show variables like 'innodb_lock_wait_timeout';
```
This Performance Schema table indicates which transactions are waiting for a given lock:
```sql
SELECT * FROM performance_schema.data_lock_waits
```
Doc: 

https://dev.mysql.com/doc/refman/8.3/en/innodb-information-schema-transactions.html

https://dev.mysql.com/doc/refman/8.3/en/innodb-information-schema-understanding-innodb-locking.html

https://dev.mysql.com/doc/refman/8.3/en/performance-schema-data-lock-waits-table.html

---
Check the current innodb thread:

```sql
select * from  information_schema.innodb_trx 
```
---
Check the current innodb thread that are alive for > 3 seconds:

```sql
select * from  information_schema.innodb_trx where TIME_TO_SEC(TIMEDIFF(current_timestamp, trx_wait_started)) > 3
```
---
See the last deadlock occurred:

```sql
SHOW ENGINE INNODB STATUS
```
---
Operations currently being performed by the set of threads executing within the server:
```sql
SHOW FULL PROCESSLIST
```
Doc: https://dev.mysql.com/doc/refman/8.3/en/processlist-access.html


