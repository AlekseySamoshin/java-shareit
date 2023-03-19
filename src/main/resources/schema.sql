DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items_bookings;
DROP TABLE IF EXISTS users_items;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
	 id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	 description VARCHAR(1024) NOT NULL,
	 requestor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	 CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	name VARCHAR(255) NOT NULL,
	description VARCHAR NOT NULL,
	available boolean,
	owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	request_id BIGINT REFERENCES requests(id) ON DELETE CASCADE,
	CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	start_date TIMESTAMP WITHOUT TIME ZONE,
	end_date TIMESTAMP WITHOUT TIME ZONE,
	item_id bigint NOT NULL REFERENCES items(id) ON DELETE CASCADE,
	booker_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	status VARCHAR(16) NOT NULL,
	CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items_bookings (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	item_id BIGINT REFERENCES items(id),
	booking_id BIGINT REFERENCES bookings(id),
	CONSTRAINT pk_items_bookings PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users_items (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	user_id BIGINT REFERENCES users(id),
	item_id BIGINT REFERENCES items(id),
	CONSTRAINT pk_users_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	text VARCHAR(1024) NOT NULL,
	item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
	author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
	created TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT pk_comment PRIMARY KEY (id)
);