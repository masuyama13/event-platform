# Event Platform

This is an event booking platform built with Spring Boot 4, Thymeleaf, Spring Security, and JPA.  
For local development, it uses an H2 in-memory database by default.

## Prerequisites

- Java 17
- Maven, or the included Maven Wrapper (`./mvnw`)

## Setup

1. Move to the project root.

```bash
cd event-platform
```

2. Start the application.

```bash
./mvnw spring-boot:run
```

3. Open the following URLs in your browser.

- Application: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

## Local Development Configuration

The default settings in `src/main/resources/application.properties` are:

- Database: `jdbc:h2:mem:testdb`
- H2 username: `sa`
- H2 password: empty
- Base URL: `http://localhost:8080`

You can override the following values with environment variables:

- `APP_BASE_URL`
- `SUPPORT_EMAIL`
- `STRIPE_SECRET_KEY`
- `STRIPE_WEBHOOK_SECRET`

Example:

```bash
APP_BASE_URL=http://localhost:8080 \
SUPPORT_EMAIL=support@example.com \
./mvnw spring-boot:run
```

## Seed Data

Sample data is inserted automatically on the first startup. You can log in with these test accounts:

- Customer: `customer@test.com` / `temp1234`
- Organizer: `organizer@test.com` / `temp1234`
- Organizer: `organizer2@test.com` / `temp1234`
- Admin: `admin@test.com` / `temp1234`

Sample categories, organizer profiles, plans, bookings, and invoice data are also created.

## Persistent Storage

By default, the application uses an H2 in-memory database, so all data is lost when the application stops.  
If you want to persist data locally, change the datasource setting in `src/main/resources/application.properties`:

```properties
# spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.url=jdbc:h2:file:./data/testdb
```

## Run Tests

```bash
./mvnw test
```

## Verify Stripe Payments Locally

Use this flow to verify that Stripe Checkout and the webhook integration work end to end.

1. Prepare Stripe test credentials.

- You need two different Stripe credentials for this check:
- `STRIPE_SECRET_KEY`: the Stripe test secret key used by the application to create Checkout Sessions.
- `STRIPE_WEBHOOK_SECRET`: the webhook signing secret used by the application to verify webhook events.
- To get `STRIPE_SECRET_KEY`, open the Stripe Dashboard, switch to test mode, and copy the secret key that starts with `sk_test_`. If you do not have access, ask your team for access to the Stripe test environment.
- To get `STRIPE_WEBHOOK_SECRET`, start a webhook tunnel with the Stripe CLI:

```bash
stripe listen --forward-to localhost:8080/stripe/webhook
```

- Keep this command running while you test the payment flow.
- Copy the signing secret displayed by the Stripe CLI. It starts with `whsec_`. Set that value as `STRIPE_WEBHOOK_SECRET`.

2. Start the application with Stripe enabled.

```bash
STRIPE_SECRET_KEY=sk_test_xxxxx \
STRIPE_WEBHOOK_SECRET=whsec_xxxxx \
./mvnw spring-boot:run
```
