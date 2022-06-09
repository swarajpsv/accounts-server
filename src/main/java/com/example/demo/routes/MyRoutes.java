package com.example.demo.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class MyRoutes extends RouteBuilder {

    private static Logger logger = LogManager.getLogger();

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet");

        rest("")
                .post("computeInterest").to("direct:calculateInterest")
                .post("computeInterest2").to("direct:calculateInterest2")
                .post("computeFees").to("direct:calculateFees")
                .post("computeTax").to("direct:calculateTax");

        from("direct:calculateInterest2")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    JSONObject requestJSON = new JSONObject(exchange.getIn().getBody().toString());
                    double interest = 12.0 / 100.0 * requestJSON.getDouble("balance");
                    exchange.getIn().setBody(interest);
                    logger.info("Interest Calculated: " + interest);
                });

        from("direct:calculateInterest")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    JSONObject requestJSON = new JSONObject(exchange.getIn().getBody().toString());
                    logger.info(requestJSON.get("balance"));
                    double interest = 12.0 / 100.0 * requestJSON.getDouble("balance");
                    logger.info("Interest Calculated: " + interest);
                    JSONObject responseJSON = new JSONObject().put("interestAmount", interest);
                    exchange.getIn().setBody(responseJSON);
                });

        from("direct:calculateFees")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    JSONObject requestJSON = new JSONObject(exchange.getIn().getBody().toString());
                    double fees = 3.0 / 100.0 * requestJSON.getDouble("balance");
                    logger.info("Fees Calculated: " + fees);
                    JSONObject responseJSON = new JSONObject().put("feeAmount", fees);
                    exchange.getIn().setBody(responseJSON);
                });

        from("direct:calculateTax")
                .convertBodyTo(String.class)
                .process(exchange -> {
                    JSONObject requestJSON = new JSONObject(exchange.getIn().getBody().toString());
                    double tax = 5.0 / 100.0 * requestJSON.getDouble("balance");
                    logger.info("Tax Calculated: " + tax);
                    JSONObject responseJSON = new JSONObject().put("taxAmount", tax);
                    exchange.getIn().setBody(responseJSON);
                });
    }
}
