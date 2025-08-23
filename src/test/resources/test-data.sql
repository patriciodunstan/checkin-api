-- Test data for integration tests

-- Insert test airplane
INSERT INTO airplane (airplane_id, brand, model) VALUES (100, 'Boeing', '737-800');

-- Insert test seats for airplane
INSERT INTO seat (seat_id, airplane_id, seat_row, seat_column, seat_type_id) VALUES
(1, 100, 1, 'A', 1),
(2, 100, 1, 'B', 1),
(3, 100, 1, 'C', 1),
(4, 100, 1, 'D', 1),
(5, 100, 1, 'E', 1),
(6, 100, 1, 'F', 1),
(7, 100, 2, 'A', 1),
(8, 100, 2, 'B', 1),
(9, 100, 2, 'C', 1),
(10, 100, 2, 'D', 1);

-- Insert test flight
INSERT INTO flight (flight_id, takeoff_date_time, takeoff_airport, landing_date_time, landing_airport, airplane_id) 
VALUES (1, '2024-12-25 10:00:00', 'SCL', '2024-12-25 14:00:00', 'LIM', 100);

-- Insert test passengers
INSERT INTO passenger (passenger_id, dni, name, age, country) VALUES
(1, '12345678', 'John Doe', 30, 'Chile'),
(2, '87654321', 'Jane Smith', 25, 'Peru'),
(3, '11111111', 'Bob Johnson', 35, 'Argentina');

-- Insert test boarding passes
INSERT INTO boarding_pass (boarding_pass_id, flight_id, passenger_id, purchase_id, seat_type_id, seat_id) VALUES
(1, 1, 1, 1, 1, NULL),
(2, 1, 2, 1, 1, NULL),
(3, 1, 3, 2, 1, NULL);
