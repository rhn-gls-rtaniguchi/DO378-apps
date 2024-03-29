package org.acme.rest.json;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.persistence.Entity;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Expense extends PanacheEntity {

    enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD,
    }

    public UUID uuid;
    public String name;

    public PaymentMethod paymentMethod;
    public BigDecimal amount;

    public Expense() {
    }

    public Expense(UUID uuid, String name, PaymentMethod paymentMethod, String amount) {
        this.uuid = uuid;
        this.name = name;
        this.paymentMethod = paymentMethod;
        this.amount = new BigDecimal(amount);
    }

    public Expense(String name, PaymentMethod paymentMethod, String amount) {
        this(UUID.randomUUID(), name, paymentMethod, amount);
    }

    @JsonbCreator
    public static Expense of(String name, PaymentMethod paymentMethod, String amount) {
        return new Expense(name, paymentMethod, amount);
    }

    public static void update(final Expense expense) {
        Optional<Expense> previous = Expense.findByIdOptional(expense.id);

        previous.ifPresentOrElse((update) -> {
            update.uuid = expense.uuid;
            update.name = expense.name;
            update.amount = expense.amount;
            update.paymentMethod = expense.paymentMethod;

            update.persist();
        }, () -> {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        });
    }

}