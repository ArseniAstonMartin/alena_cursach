# Loyalty Platform

Course project for distributed information systems: a loyalty platform with personalized offers based on purchase history.

## Structure

- `backend` - Spring Boot 3 REST API, Java 21, PostgreSQL, Redis, Flyway, JWT, OAuth2 client setup, OpenAPI.
- `frontend` - React + TypeScript + Vite SPA with Zustand and TanStack Query.
- `docker-compose.yml` - local runtime for PostgreSQL, Redis, backend and frontend.

## Quick start

```bash
cp .env.example .env
docker compose up --build
```

Services:

- Frontend: <http://localhost:5173>
- Backend API: <http://localhost:8080/api/v1>
- OpenAPI UI: <http://localhost:8080/swagger-ui.html>

Demo credentials:

- Admin: `admin@loyalty.local` / `admin12345`
- Customer: `alice@example.com` / `password123`

## Implemented high-level use cases

1. Customer registration.
2. JWT login.
3. Refresh token rotation.
4. View current customer profile.
5. Admin customer search with pagination.
6. List merchants with pagination.
7. List products with filtering and sorting.
8. Create a purchase with items.
9. View purchase history.
10. View loyalty balance.
11. List personalized offers.
12. Claim an offer.
13. Redeem loyalty points.
14. Recalculate customer segment.
15. View recommendations from external integrations.
16. Admin view registered external integrations.
17. View reward transaction history.
18. View loyalty program rules by category and segment.
19. Receive explained reward accruals after purchases.
20. Redeem points into discount certificates with confirmation codes.
21. View certificate wallet and certificate statuses.
22. Apply an active certificate to a purchase.

## Loyalty business rules

- Category cashback is stored in `reward_rules` and can differ per product category.
- Segment multipliers are stored in `customer_segment_thresholds`.
- Activated offers add fixed bonus points when their category matches a purchase.
- Personalized offer ranking is based on purchase history: favorite categories, category spend, purchase count, average check, total spend, frequency and last purchase date.
- The customer purchase profile endpoint exposes the behavior report used by the personalization module.
- Each offer response includes status (`AVAILABLE`, `CLAIMED`, `USED`), next-step instructions and a personalization reason, so the UI can show why it was recommended and how it will be applied.
- Every purchase reward is stored in `purchase_reward_breakdowns` with an explanation.
- Points can be redeemed through `redemption_orders`; the current rule is 10 points = 1 discount unit, minimum 50 points.
- Redeemed certificates are category-bound by purchase history: by default, a certificate is issued for the customer's strongest category.
- Redeemed certificates are stored in the customer's wallet with `ACTIVE`, `USED` or `EXPIRED` status.
- A purchase can apply one active certificate only to a matching category; the purchase stores gross amount, discount amount, final amount and certificate code.

## Architecture notes

The backend follows a modular hexagonal style:

- `domain` contains entities, value objects and domain services.
- `application` contains command/query markers, DTO records, ports and use-case services.
- `adapters.in.web` exposes versioned REST controllers.
- `application.port` declares persistence ports implemented by Spring Data at runtime.
- `infrastructure` contains security, configuration, domain event observers and REST integrations.

Patterns beyond Repository/Unit of Work:

- Strategy - personalized offer scoring rules.
- Factory - offer strategy factory.
- Facade - loyalty facade used by controllers.
- Observer - Spring events for purchase rewards.
- Builder-style command objects are represented by immutable Java records for request DTO construction.

Java/OOP requirements included: generics (`Command<R>`, `AbstractUseCase<I,O>`), abstract class, custom interfaces, lambdas/Stream API, DTO records, Optional repositories, custom exceptions, custom annotation plus Reflection API in `IntegrationRegistry`.

Database schema has more than eight related 3NF tables: customers, roles, customer_roles, merchants, products, purchases, purchase_items, loyalty_accounts, reward_transactions, offers, claimed_offers, refresh_tokens, reward_rules, redemption_orders, purchase_reward_breakdowns and customer_segment_thresholds. Flyway migrations are split into V1-V8.
