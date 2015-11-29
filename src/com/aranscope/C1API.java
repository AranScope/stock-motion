package com.aranscope;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class C1API {
    public static final String C1_CUSTOMER_ID = "565ab8d23921211200ef1d23";
    public static final String LSE_MERCHANT_ID = "565ab5e73921211200ef1d22";

    private Optional<C1Account> account = Optional.empty();
    private String key;

    private double balance = 5000;

    public C1API() {
        this.key = System.getenv("C1APIKEY");
        prepUnirest();
        try {
            this.account = Optional.of(
                    Unirest.post(C1AccountsUrl(C1_CUSTOMER_ID))
                    .queryString("key", key)
                    .field("type", "Credit Card")
                    .field("nickname", "Account")
                    .field("rewards", 0)
                    .field("balance", this.balance)
                    .field("account_number", this.uuid())
                    .asObject(C1Account.class)
                    .getBody()
            );
        } catch (UnirestException e) {

        }
    }

    public void sell(double price) {
        this.balance += price;
        double _balance = this.balance;

        if (account.isPresent()) {
            C1Deposit d = new C1Deposit(price);
            Unirest.post(C1DepositUrl(account.get()._id)).body(d);
        }

    }

    public void buy(double price, Function<Double,Void> cb) {
        this.balance -= price;
        double _balance = this.balance;
        if (account.isPresent()) {
            C1Purchase p = new C1Purchase(account.get()._id, price);
            Unirest.post(C1PurchaseUrl(account.get()._id)).body(p);
        }
    }

    private static String C1AccountsUrl(String cust) {
        return "http://api.reimaginebanking.com/customers/" + cust + "/accounts";
    }

    private static String C1PurchaseUrl(String accId) {
        return "http://api.reimaginebanking.com/customers/" + accId + "/purchases";
    }

    private static String C1DepositUrl(String accId) {
        return "http://api.reimaginebanking.com/customers/" + accId + "/deposits";
    }

    private String uuid() {
        Random rand = new Random();
        long accumulator = 1 + rand.nextInt(9); // ensures that the 16th digit isn't 0
        for(int i = 0; i < 15; i++) {
            accumulator *= 10L;
            accumulator += rand.nextInt(10);
        }
        return String.valueOf(accumulator);
    }

    private void prepUnirest() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private class C1Account {
        public String _id;
        public String type;
        public String nickname;
        public int rewards;
        public int balance;
        public String account_number;
        public String customer_id;
    }

    private class C1Purchase {
        public C1Purchase(String payer, double a) {
            this.payer_id = payer;
            this.amount = a;
        }
        public String type = "merchant";
        public String merchant_id = LSE_MERCHANT_ID;
        public String payer_id;
        public String purchase_date = "2015-11-29";
        public double amount;
        public String status = "pending";
        public String medium = "balance";
        public String description = "Trades";
    }

    private class C1Deposit {
        public C1Deposit(double a) {
            this.amount = a;
        }
        public String medium = "balance";
        public String transaction_date = "2015-11-29";
        public String status = "pending";
        public double amount;
        public String description = "sales";
    }
}
