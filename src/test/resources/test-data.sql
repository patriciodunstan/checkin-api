-- Test data for integration tests (matching original database schema)

-- Insert test airplane (matching original schema)
INSERT INTO airplane (airplane_id, name) VALUES (1, 'AirNova-660');

-- Insert test seat types (required by foreign key constraints)
INSERT INTO seat_type (seat_type_id, name) VALUES 
(1, 'Economic'),
(2, 'Premium Economic'), 
(3, 'Business');

-- Insert test seats for airplane (matching original schema)
INSERT INTO seat (seat_id, airplane_id, seat_row, seat_column, seat_type_id) VALUES
(1, 1, 1, 'A', 1),
(2, 1, 1, 'B', 1),
(3, 1, 1, 'C', 1),
(4, 1, 1, 'D', 1),
(5, 1, 1, 'E', 1),
(6, 1, 1, 'F', 1),
(7, 1, 2, 'A', 1),
(8, 1, 2, 'B', 1),
(9, 1, 2, 'C', 1),
(10, 1, 2, 'D', 1);

-- Insert test flight (using Integer timestamps as per original schema)
INSERT INTO flight (flight_id, takeoff_date_time, takeoff_airport, landing_date_time, landing_airport, airplane_id) 
VALUES (1, 1688207580, 'Aeropuerto Internacional Arturo Merino Benitez, Chile', 1688221980, 'Aeropuerto Internacional Jorge Cháve, Perú', 1);

-- Insert test passengers (matching original schema)
INSERT INTO passenger (passenger_id, dni, name, age, country) VALUES
(1, '12345678', 'John Doe', 30, 'Chile'),
(2, '87654321', 'Jane Smith', 25, 'Peru'),
(3, '11111111', 'Bob Johnson', 35, 'Argentina');

-- Insert test purchases (matching original schema with purchase_date)
INSERT INTO purchase (purchase_id, purchase_date) VALUES 
(1, 1688121180),
(2, 1688121180);

-- Insert test boarding passes (matching original schema structure)
INSERT INTO boarding_pass (boarding_pass_id, purchase_id, passenger_id, seat_type_id, seat_id, flight_id) VALUES
(1, 1, 1, 1, NULL, 1),
(2, 1, 2, 1, NULL, 1),
(3, 2, 3, 1, NULL, 1);
