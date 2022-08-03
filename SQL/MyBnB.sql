DROP SCHEMA IF EXISTS mybnb;
CREATE SCHEMA mybnb;
USE mybnb;

DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
	uid SERIAL,
    username VARCHAR(25) NOT NULL,
    password VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    address VARCHAR(100) NOT NULL,
    occupation VARCHAR(50) NOT NULL,
    sin INT NOT NULL,
    
    PRIMARY	KEY (uid),
    UNIQUE (username)
);

DROP TABLE IF EXISTS creditCards;
CREATE TABLE creditCards (
	cardID SERIAL,
    card_num BIGINT NOT NULL,
    card_type ENUM('Visa', 'MasterCard', 'American Express', 'Discover') NOT NULL,
	expiry_date DATE NOT NULL,
    renterId BIGINT UNSIGNED NOT NULL,
    
    PRIMARY KEY (cardID),
    FOREIGN KEY (renterId) REFERENCES accounts(uid) ON DELETE CASCADE
);

DROP TABLE IF EXISTS listings;
CREATE TABLE listings (
	lid SERIAL,
    listing_type ENUM('Entire Place', 'Hotel Rooms', 'Private Rooms', 'Shared Rooms', 'Other') NOT NULL DEFAULT 'Other',
    title VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    num_bedrooms SMALLINT UNSIGNED NOT NULL,
    num_bathrooms SMALLINT UNSIGNED NOT NULL,
    price REAL NOT NULL,
    hostId BIGINT UNSIGNED,
    longitude DECIMAL(10, 5) NOT NULL,
    latitude DECIMAL(10, 5) NOT NULL,
    
    PRIMARY KEY (lid),
    FOREIGN KEY (hostId) REFERENCES accounts(uid) ON DELETE SET NULL
);

DROP TABLE IF EXISTS available_on;
CREATE TABLE available_on (
	lid BIGINT UNSIGNED NOT NULL,
    date DATE NOT NULL,
    
    PRIMARY KEY (lid, date),
    FOREIGN KEY (lid) REFERENCES listings(lid) ON DELETE CASCADE
);

DROP TABLE IF EXISTS has_amenities;
CREATE TABLE has_amenities (
	lid BIGINT UNSIGNED NOT NULL,
    amenity ENUM('Kitchen', 'Internet', 'TV', 'Essentials', 'Heating', 
		'Air Conditioning', 'Washer', 'Dryer', 'Free Parking', 'Wireless', 
		'Breakfast', 'Pets', 'Family Friendly', 'Suitable for Events',
		'Smoking', 'Wheelchair Accessible', 'Elevator', 'Fireplace', 'Buzzer', 
		'Doorman', 'Pool', 'Hot Tub', 'Gym', '24 Hours Check-In', 'Hangers', 
		'Iron', 'Hair Dryer', 'Laptop-friendly Workspace',
		'Carbon Monoxide Detector', 'First Aid Kit', 'Smoke Detector') NOT NULL,
	
    PRIMARY KEY (lid, amenity),
    FOREIGN KEY (lid) REFERENCES listings(lid) ON DELETE CASCADE
);

DROP TABLE IF EXISTS addresses;
CREATE TABLE addresses (
	aid SERIAL,
    country VARCHAR(25) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street_address VARCHAR(100) NOT NULL,
    postal VARCHAR(10) NOT NULL,
    
    PRIMARY KEY (aid)
);

DROP TABLE IF EXISTS at;
CREATE TABLE at (
	listing BIGINT UNSIGNED NOT NULL,
    address BIGINT UNSIGNED,
    
    PRIMARY KEY (listing, address),
	FOREIGN KEY (listing) REFERENCES listings(lid) ON DELETE CASCADE,
    FOREIGN KEY (address) REFERENCES addresses(aid) ON DELETE CASCADE
);

DROP TABLE IF EXISTS bookings;
CREATE TABLE bookings (
	bid SERIAL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    cost REAL NOT NULL,
    is_cancelled BOOLEAN NOT NULL,
    book_date DATE NOT NULL,
    listing BIGINT UNSIGNED,
    renterId BIGINT UNSIGNED,
    
    PRIMARY KEY (bid),
    FOREIGN KEY (listing) REFERENCES listings(lid) ON DELETE SET NULL,
    FOREIGN KEY (renterId) REFERENCES accounts(uid) ON DELETE SET NULL
);

DROP TABLE IF EXISTS cancellations;
CREATE TABLE cancellations (
	cid SERIAL,
    booking BIGINT UNSIGNED NOT NULL,
    canceller BIGINT UNSIGNED,
    cancel_date DATE NOT NULL,
    
    PRIMARY KEY (cid),
    FOREIGN KEY (booking) REFERENCES bookings(bid) ON DELETE CASCADE,
    FOREIGN KEY (canceller) REFERENCES accounts(uid) ON DELETE SET NULL
);

DROP TABLE IF EXISTS comments;
CREATE TABLE comments (
	commentID SERIAL,
    comment TEXT NOT NULL,
    rating INT NOT NULL,
    sender BIGINT UNSIGNED,
    receiver BIGINT UNSIGNED NOT NULL,
    on_listing BIGINT UNSIGNED,
    
    PRIMARY KEY (commentID),
    FOREIGN KEY (sender) REFERENCES accounts(uid) ON DELETE CASCADE,
    FOREIGN KEY (receiver) REFERENCES accounts(uid) ON DELETE CASCADE,
    FOREIGN KEY (on_listing) REFERENCES listings(lid) ON DELETE SET NULL,
    CHECK (rating >= 0 AND rating <=5),
    CHECK (sender <> receiver)
);