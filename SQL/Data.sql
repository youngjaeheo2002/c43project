USE mybnb;
-- insert accounts:
INSERT INTO accounts(username, password, last_name, first_name, dob, address, occupation, sin) VALUES ("renter1", "pwd", "One", "Renter", '2002-08-08', "User Street", "Renter", "000000001");  -- uid: 1
INSERT INTO accounts(username, password, last_name, first_name, dob, address, occupation, sin) VALUES ("renter2", "pwd", "Two", "Renter", '2002-08-08', "User Street", "Renter", "000000002");    -- uid: 2
INSERT INTO accounts(username, password, last_name, first_name, dob, address, occupation, sin) VALUES ("host1", "pwd", "One", "Host", '2002-08-08', "User Street", "Host", "000000003");        -- uid: 3
INSERT INTO accounts(username, password, last_name, first_name, dob, address, occupation, sin) VALUES ("host2", "pwd", "Two", "Host", '2002-08-08', "User Street", "Host", "000000004");        -- uid: 4

INSERT INTO creditCards(card_num, card_type, expiry_date, renterId) VALUES(100000000001, "Visa", '2025-08-08', 1);
INSERT INTO creditCards(card_num, card_type, expiry_date, renterId) VALUES(100000000002, "MasterCard", '2025-08-08', 2);

-- insert listings:
INSERT INTO listings(listing_type, title, description, num_bedrooms, num_bathrooms, price, hostId, longitude, latitude, posted_date) VALUES("Entire Place", "Full house", "Rent an entire house", 4, 4, 170, 3, 100, 100, '2022-08-01');   -- lid: 1
INSERT INTO listings(listing_type, title, description, num_bedrooms, num_bathrooms, price, hostId, longitude, latitude, posted_date) VALUES("Hotel Rooms", "Big hotel room", "Rent a hotel room", 2, 2, 100, 3, 110, 110, '2022-08-01');   -- lid: 2
INSERT INTO listings(listing_type, title, description, num_bedrooms, num_bathrooms, price, hostId, longitude, latitude, posted_date) VALUES("Private Rooms", "A humble room", "Rent a private room", 1, 1, 80, 4, 100, 90, '2022-08-01');  -- lid: 3
INSERT INTO listings(listing_type, title, description, num_bedrooms, num_bathrooms, price, hostId, longitude, latitude, posted_date) VALUES("Shared Rooms", "A shared romo", "Rent a shared room", 1, 1, 60, 4, 90, 120, '2022-08-01');   -- lid: 4

INSERT INTO addresses(country, city, street_address, postal) VALUES ("Country1", "City1", "Street11", "101 010");  -- aid: 1
INSERT INTO addresses(country, city, street_address, postal) VALUES ("Country1", "City1", "Street12", "101 101");  -- aid: 2
INSERT INTO addresses(country, city, street_address, postal) VALUES ("Country2", "City2", "Street21", "111 010");  -- aid: 3
INSERT INTO addresses(country, city, street_address, postal) VALUES ("Country2", "City2", "Street22", "111 101");  -- aid: 4

INSERT INTO at(listing, address) VALUES (1, 1);
INSERT INTO at(listing, address) VALUES (2, 2);
INSERT INTO at(listing, address) VALUES (3, 3);
INSERT INTO at(listing, address) VALUES (4, 4);

INSERT INTO has_amenities(lid, amenity) VALUES (1, "Hot Tub");
INSERT INTO has_amenities(lid, amenity) VALUES (1, "Heating");
INSERT INTO has_amenities(lid, amenity) VALUES (2, "24 Hours Check-In");
INSERT INTO has_amenities(lid, amenity) VALUES (2, "Gym");
INSERT INTO has_amenities(lid, amenity) VALUES (2, "Sauna");
INSERT INTO has_amenities(lid, amenity) VALUES (3, "Air Conditioning");
INSERT INTO has_amenities(lid, amenity) VALUES (3, "Internet");
INSERT INTO has_amenities(lid, amenity) VALUES (4, "TV");

-- insert bookings:
INSERT INTO bookings(start_date, end_date, cost, is_cancelled, book_date, listing, renterId) VALUES ('2022-08-10', '2022-08-12', 170, false, '2022-08-3', 1, 1);  -- bid: 1
INSERT INTO bookings(start_date, end_date, cost, is_cancelled, book_date, listing, renterId) VALUES ('2022-08-10', '2022-08-12', 100, false, '2022-08-3', 2, 1);  -- bid: 2
INSERT INTO bookings(start_date, end_date, cost, is_cancelled, book_date, listing, renterId) VALUES ('2022-08-13', '2022-08-15', 170, false, '2022-08-3', 1, 2);   -- bid: 3
INSERT INTO bookings(start_date, end_date, cost, is_cancelled, book_date, listing, renterId) VALUES ('2022-08-10', '2022-08-12', 60, false, '2022-08-3', 4, 2);   -- bid: 4
INSERT INTO bookings(start_date, end_date, cost, is_cancelled, book_date, listing, renterId) VALUES ('2022-08-10', '2022-08-12', 80, true, '2022-08-3', 3, 1);   -- bid: 5

-- insert cancellation:
INSERT INTO cancellations(booking, canceller, cancel_date) VALUES(5, 1, '2022-08-3');

-- insert comments
INSERT INTO comments(comment, rating, sender, receiver, on_listing, is_sender_renter) VALUES ("The place is very nice and the bedroom is very comfortable.", 4, 1, 3, 1, true);
INSERT INTO comments(comment, rating, sender, receiver, on_listing, is_sender_renter) VALUES ("The place is big but the kitchen is small", 3, 2, 3, 1, true);
