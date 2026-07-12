package hr.abysalto.hiring.api.junior.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private boolean dataInitialized = false;

	public boolean isDataInitialized() {
		return this.dataInitialized;
	}

	public void initialize() {
		initTables();
		initData();
		this.dataInitialized = true;
	}

	private void initTables() {
		this.jdbcTemplate.execute("""
			 CREATE TABLE buyer (
				 buyer_id INT auto_increment PRIMARY KEY,
				 first_name varchar(100) NOT NULL,
				 last_name varchar(100) NOT NULL,
				 title varchar(100) NULL
			 );
 		""");

		this.jdbcTemplate.execute("""
			 CREATE TABLE buyer_address (
				 buyer_address_id INT auto_increment PRIMARY KEY,
				 city varchar(100) NOT NULL,
				 street varchar(100) NOT NULL,
				 home_number varchar(100) NULL
			 );
 		""");

		this.jdbcTemplate.execute("""
			 CREATE TABLE orders (
				 order_nr INT auto_increment PRIMARY KEY,
				 buyer_id int NOT NULL,
				 order_status varchar(32) NOT NULL,
				 order_time datetime NOT NULL,
				 payment_option varchar(50) NOT NULL,
				 delivery_address_id INT NOT NULL,
				 contact_number varchar(100) NULL,
				 note varchar(100) NULL,
				 currency varchar(50) NULL,
				 total_price decimal(10,2),
				 CONSTRAINT FK_order_to_buyer FOREIGN KEY (buyer_id) REFERENCES buyer (buyer_id),
				 CONSTRAINT FK_order_to_delivery_address FOREIGN KEY (delivery_address_id) REFERENCES buyer_address (buyer_address_id)
			 );
 		""");

		this.jdbcTemplate.execute("""
			 CREATE TABLE order_item (
				 order_item_id INT auto_increment PRIMARY KEY,
				 order_nr int NOT NULL,
				 item_nr smallint NOT NULL,
				 name varchar(100) NOT NULL,
				 quantity smallint NOT NULL,
				 price decimal(10,2),
				 CONSTRAINT UC_order_items UNIQUE (order_item_id, order_nr),
				 CONSTRAINT FK_order_item_to_order FOREIGN KEY (order_nr) REFERENCES orders (order_nr)
			 );
 		""");
	}

	private void initData() {
		this.jdbcTemplate.execute("INSERT INTO buyer (first_name, last_name, title) VALUES ('Jabba', 'Hutt', 'the')");
		this.jdbcTemplate.execute("INSERT INTO buyer (first_name, last_name, title) VALUES ('Anakin', 'Skywalker', NULL)");
		this.jdbcTemplate.execute("INSERT INTO buyer (first_name, last_name, title) VALUES ('Jar Jar', 'Binks', NULL)");
		this.jdbcTemplate.execute("INSERT INTO buyer (first_name, last_name, title) VALUES ('Han', 'Solo', NULL)");
		this.jdbcTemplate.execute("INSERT INTO buyer (first_name, last_name, title) VALUES ('Leia', 'Organa', 'Princess')");

		// --- ADRESE DOSTAVE (dobiju ID 1, 2, 3) ---
		this.jdbcTemplate.execute("INSERT INTO buyer_address (city, street, home_number) VALUES ('Zagreb', 'Ilica', '10')");
		this.jdbcTemplate.execute("INSERT INTO buyer_address (city, street, home_number) VALUES ('Split', 'Marmontova', '5')");
		this.jdbcTemplate.execute("INSERT INTO buyer_address (city, street, home_number) VALUES ('Rijeka', 'Korzo', '22')");

		// --- PRIMJER NARUDZBI ---
		this.jdbcTemplate.execute("""
			INSERT INTO orders (buyer_id, order_status, order_time, payment_option, delivery_address_id, contact_number, note, currency, total_price)
			VALUES (1, 'WAITING_FOR_CONFIRMATION', '2026-07-12 12:30:00', 'CASH', 1, '0911111111', 'Pozvoniti na drugi kat', 'EUR', 19.50)
		""");
		this.jdbcTemplate.execute("INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (1, 1, 'Pizza Margarita', 2, 8.50)");
		this.jdbcTemplate.execute("INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (1, 2, 'Coca-Cola 0.5', 1, 2.50)");

		this.jdbcTemplate.execute("""
			INSERT INTO orders (buyer_id, order_status, order_time, payment_option, delivery_address_id, contact_number, note, currency, total_price)
			VALUES (4, 'PREPARING', '2026-07-12 13:00:00', 'CARD_ON_DELIVERY', 2, '0922222222', NULL, 'EUR', 18.00)
		""");
		this.jdbcTemplate.execute("INSERT INTO order_item (order_nr, item_nr, name, quantity, price) VALUES (2, 1, 'Cheeseburger', 3, 6.00)");
	}
}
