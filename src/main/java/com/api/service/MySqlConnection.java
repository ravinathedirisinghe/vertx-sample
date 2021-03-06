package com.api.service;

import com.api.utils.CommonConstants;
import com.api.wrapper.PaymentRequest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class MySqlConnection extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MySqlConnection.class);

    @Override
    public void start() {
        LOG.info("Starting Mysql class");

        vertx.eventBus().consumer(CommonConstants.PAYMENT_REQUEST_ADDRESS, message -> {

            PaymentRequest paymentRequest = null;

            try {
                paymentRequest = ( PaymentRequest ) message.body();
            }

            catch ( Exception e ) {
                LOG.error("Received message: " + paymentRequest);
            }

            MySQLPool client = setMySqlConnection(vertx);

            selectData(paymentRequest.getEndUserId(), client);
            //check msisdn is in the db
            insertData(paymentRequest.getEndUserId(), paymentRequest.getChargingInformation().getAmount(), client);

            LOG.info("Sending reply: " + paymentRequest);

            message.reply(paymentRequest);

        });


    }

    //https://vertx.io/docs/3.9.1/vertx-mysql-client/java/
    public void insertData(String msisdn, String amount, MySQLPool client) {

        client
                .preparedQuery("INSERT INTO payment (User, Amount) VALUES (?, ?)")
                .execute(Tuple.of(msisdn, amount), ar -> {
                    if ( ar.succeeded() ) {
                        //RowSet < Row > rows = ar.result();
                        RowSet<Row> result1 = ar.result();
                        Row row1 = result1.iterator().next();
                        System.out.println("First result: " + row1.getString(""));

                        RowSet<Row> result2 = result1.next();
                        Row row2 = result2.iterator().next();
                        System.out.println("Second result: " + row2.getInteger(0));

                        RowSet<Row> result3 = result2.next();
                        System.out.println("Affected rows: " + result3.rowCount());
                    } else {
                        LOG.info("Failure: " + ar.cause().getMessage());
                    }

                    // Now close the pool
                    client.close();
                });
    }

    public void selectData(String msisdn, MySQLPool client) {
        try {

            client
                    .query("SELECT * FROM user WHERE user= " + msisdn)
                    .execute(ar -> {
                        if ( ar.succeeded() ) {
                            RowSet<Row> rows = ar.result();
                            //System.out.println("Got " + rows. + " rows ");
                        } else {
                            LOG.info("Failure: " + ar.cause().getMessage());
                        }

                    });

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

    }


    private MySQLPool setMySqlConnection(Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("localhost")
                .setDatabase("test")
                .setUser("root")
                .setPassword("root");

// Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

// Create the pooled client
        MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);
        return client;
    }

}
