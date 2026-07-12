# Java Junior assignment – Sustav za narudžbe restorana

Mali REST API za upravljanje narudžbama restorana, napravljen unutar zadanog
kostura projekta (Spring Boot). Omogućuje dodavanje narudžbi, pregled, promjenu
statusa, izračun ukupnog iznosa i sortiranje narudžbi po iznosu. Podaci se
spremaju u bazu.

## Tehnologije

- Java 17
- Spring Boot 3.4.3 (Web, Security, Data JDBC)
- H2 baza (in-memory – živi dok aplikacija radi)
- Swagger / OpenAPI (za isprobavanje endpointa)
- Maven (koristi se priloženi wrapper `mvnw`)

## Pokretanje

Iz korijena projekta:



Aplikacija se digne na http://localhost:8080

## Prijava (basic auth)

Svi endpointi (osim Swaggera) traže prijavu (definirano u `SecurityConfig`):

- korisnik: `user`
- lozinka: `password`

## Prvi korak: inicijalizacija baze

Baza se puni ručno, jednim pozivom (kreira tablice + primjere podataka):



Dok se to ne pozove, ostali endpointi vraćaju HTTP 412.

## Swagger

Sve se najlakše isproba kroz Swagger UI:
http://localhost:8080/swagger-ui/index.html

## Endpointi za narudžbe

| Metoda | Putanja | Opis |
|--------|---------|------|
| POST | `/order/` | Dodaj novu narudžbu |
| GET | `/order/` | Sve narudžbe (opcionalno `?sort=asc` / `?sort=desc`) |
| GET | `/order/{nr}` | Jedna narudžba |
| PATCH | `/order/{nr}/status` | Promijeni status |
| GET | `/order/{nr}/total` | Ukupni iznos računa |

Status: `WAITING_FOR_CONFIRMATION`, `PREPARING`, `DONE`.
Način plaćanja: `CASH`, `CARD_UPFRONT`, `CARD_ON_DELIVERY`.

### Primjer – nova narudžba



Ukupni iznos se ne šalje – server ga sam izračuna iz stavki (ovdje 22.50).

## Kako je kod organiziran

- `controller` – REST endpointi
- `manager` – logika (interface + implementacija)
- `repository` – pristup bazi (Spring Data JDBC)
- `model` – entiteti (Order, OrderItem, Buyer...)
- `dto` – objekti za ulaz/izlaz API-ja, odvojeni od modela baze

## Napomene

- Tablicu narudžbi sam nazvao `orders` (ne `order`) jer je `order` rezervirana
  riječ u SQL-u i stvara probleme kad Spring sam generira upite.
- Iznosi koriste `BigDecimal` jer je za novac točniji od `double`.
- H2 je in-memory pa se podaci gube kad se aplikacija ugasi – zato postoji
  `/init-data/`.
- Što bih dodao s više vremena: validaciju ulaza (Hibernate Validator), pravu
  bazu umjesto in-memory i automatske testove.