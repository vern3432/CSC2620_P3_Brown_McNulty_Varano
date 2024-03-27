-- Table Clients

CREATE TABLE Clients (
  client_id INTEGER PRIMARY KEY,
  client_name TEXT
);

-- Table FamilyMembers
CREATE TABLE FamilyMembers (
  member_id INTEGER PRIMARY KEY,
  name TEXT,
  birth_date DATE,
  death_date DATE,
  is_deceased BOOLEAN,
  current_residence TEXT,
  client_id INTEGER,
  FOREIGN KEY (client_id) REFERENCES Clients(client_id)
);

-- Table Relationships
CREATE TABLE Relationships (
  relationship_id INTEGER PRIMARY KEY,
  member_id INTEGER,
  related_member_id INTEGER,
  relation_type TEXT,
  FOREIGN KEY (member_id) REFERENCES FamilyMembers(member_id),
  FOREIGN KEY (related_member_id) REFERENCES FamilyMembers(member_id)
);

-- Table Addresses
CREATE TABLE Addresses (
  address_id INTEGER PRIMARY KEY,
  city TEXT,
  member_id INTEGER,
  FOREIGN KEY (member_id) REFERENCES FamilyMembers(member_id)
);

-- Table Event
CREATE TABLE Event (
  event_id INTEGER PRIMARY KEY,
  event_date DATE,
  event_type TEXT
);

-- Table EventAttendee
CREATE TABLE EventAttendee (
  event_id INTEGER,
  member_id INTEGER,
  FOREIGN KEY (event_id) REFERENCES Event(event_id),
  FOREIGN KEY (member_id) REFERENCES FamilyMembers(member_id)
);-- Sample inserts
INSERT INTO Clients (client_id, client_name) VALUES
(1, 'John Doe'),
(2, 'Jane Smith');

INSERT INTO FamilyMembers (member_id, name, birth_date, death_date, is_deceased, current_residence, client_id) VALUES
(1, 'John Doe', '1980-01-01', NULL, 0, 'New York', 1),
(2, 'Jane Smith', '1975-05-15', NULL, 0, 'Los Angeles', 2);

INSERT INTO Relationships (relationship_id, member_id, related_member_id, relation_type) VALUES
(1, 1, 2, 'Spouse');

INSERT INTO Addresses (address_id, city, member_id) VALUES
(1, 'New York', 1),
(2, 'Los Angeles', 2);

INSERT INTO Event (event_id, event_date, event_type) VALUES
(1, '2022-01-01', 'Wedding');

INSERT INTO EventAttendee (event_id, member_id) VALUES
(1, 1),
(1, 2);

-- Sample inserts with diverse data
INSERT INTO FamilyMembers (member_id, name, birth_date, death_date, is_deceased, current_residence, client_id) VALUES
(3, 'Emily Johnson', '1990-03-10', '2020-05-20', 1, 'Chicago', 1),
(4, 'Michael Brown', '1978-07-25', '2015-12-10', 1, 'Miami', 1),
(5, 'Emma Wilson', '2000-11-08', '2021-08-30', 1, 'San Francisco', 2),
(6, 'William Miller', '1985-02-15', '2019-04-05', 1, 'Seattle', 2),
(7, 'Olivia Garcia', '1995-09-20', NULL, 0, 'Dallas', 1),
(8, 'Daniel Martinez', '1970-06-12', NULL, 0, 'Houston', 2),
(9, 'Sophia Lee', '1982-04-18', NULL, 0, 'Atlanta', 2),
(10, 'Alexander Taylor', '1998-12-30', NULL, 0, 'Boston', 1),
(11, 'Isabella Lopez', '1976-08-05', NULL, 0, 'Denver', 1),
(12, 'Ethan Hernandez', '1992-01-25', NULL, 0, 'Las Vegas', 2),
(13, 'Mia Adams', '1989-07-14', NULL, 0, 'Phoenix', 1),
(14, 'James Rodriguez', '1980-03-20', NULL, 0, 'Detroit', 2),
(15, 'Ava Thomas', '2005-05-28', NULL, 0, 'Portland', 2),
(16, 'Benjamin King', '1973-11-12', NULL, 0, 'San Diego', 1),
(17, 'Charlotte White', '1993-09-03', NULL, 0, 'Philadelphia', 1),
(18, 'Samuel Clark', '1974-04-22', NULL, 0, 'Orlando', 2),
(19, 'Amelia Hall', '2001-10-15', NULL, 0, 'Minneapolis', 1),
(20, 'Henry Scott', '1987-06-08', NULL, 0, 'Salt Lake City', 2);
-- Additional sample inserts
INSERT INTO FamilyMembers (member_id, name, birth_date, death_date, is_deceased, current_residence, client_id) VALUES
(21, 'Grace Moore', '1996-02-28', '2020-10-15', 1, 'Austin', 2),
(22, 'Jack Anderson', '1979-10-09', NULL, 0, 'New Orleans', 1),
(23, 'Lily Wilson', '1983-06-17', '2017-07-22', 1, 'Kansas City', 1),
(24, 'Logan Baker', '2002-09-11', NULL, 0, 'Raleigh', 2),
(25, 'Madison Turner', '1990-04-05', '2018-11-30', 1, 'Tampa', 1),
(26, 'Oliver Wright', '1975-11-23', NULL, 0, 'San Antonio', 2),
(27, 'Natalie Perez', '2008-07-19', NULL, 0, 'Honolulu', 1),
(28, 'Elijah Cooper', '1988-01-14', '2016-03-18', 1, 'Memphis', 2),
(29, 'Avery Sanchez', '2004-03-30', NULL, 0, 'Charlotte', 2),
(30, 'Harper Rivera', '1972-05-27', NULL, 0, 'Albuquerque', 1);


-- Additional sample inserts for Relationships table
INSERT INTO Relationships (relationship_id, member_id, related_member_id, relation_type) VALUES
(6, 3, 4, 'Parent'),
(7, 4, 5, 'Child'),
(8, 5, 6, 'Sibling'),
(9, 7, 8, 'Spouse'),
(10, 8, 9, 'Sibling');


-- Additional sample inserts for Events table
INSERT INTO Event (event_id, event_date, event_type) VALUES
(6, '2020-01-15', 'Birthday'),
(7, '2019-11-25', 'Anniversary'),
(8, '2018-07-04', 'Graduation'),
(9, '2017-09-30', 'Family Reunion'),
(10, '2016-12-25', 'Christmas');
