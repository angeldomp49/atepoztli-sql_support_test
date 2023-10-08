
package org.makechtec.software.sql_support.testing;

import org.makechtec.software.sql_support.ConnectionInformation;
import org.makechtec.software.sql_support.postgres.PostgresEngine;
import org.makechtec.software.sql_support.query_call_mechanism.ProducerByCall;
import org.makechtec.software.sql_support.query_process.statement.ParamType;

import java.sql.SQLException;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        insertingToRemotePostgresDatabase();
        readingFromRemotePostgresDatabase();
    }

    private static void readingFromRemotePostgresDatabase() {
        var connectionInformation = new ConnectionInformation(
                "sql_support",
                "testing",
                "159.65.190.139",
                "5432",
                "tomcat_dev_database"
        );

        var postgresEngine = new PostgresEngine<KitchenUtensil>(connectionInformation);
        var results = new ArrayList<KitchenUtensil>();

        ProducerByCall<KitchenUtensil> producer =
                resultSet -> {

                    KitchenUtensil dto = null;
                    while (resultSet.next()) {
                        dto = new KitchenUtensil(
                                resultSet.getString("name"),
                                ""
                        );

                        results.add(dto);
                    }

                    return dto;
                };

        try {
            var result =
                    postgresEngine.queryString("SELECT * FROM sql_support__testing__schema.connection_testing;")
                            .run(producer);

            results.stream().map(KitchenUtensil::name).forEach(System.out::println);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void insertingToRemotePostgresDatabase(){
        var connectionInformation = new ConnectionInformation(
                "sql_support",
                "testing",
                "159.65.190.139",
                "5432",
                "tomcat_dev_database"
        );

        var postgresEngine = new PostgresEngine<KitchenUtensil>(connectionInformation);

        try {
                postgresEngine.isPrepared()
                            .addParamAtPosition(1, "example", ParamType.TYPE_STRING)
                            .queryString("INSERT INTO sql_support__testing__schema.connection_testing(name) VALUES(?);")
                            .update();

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
