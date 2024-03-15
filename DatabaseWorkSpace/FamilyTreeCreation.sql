-- Table Clients
CREATE TABLE Clients (
  client_id SERIAL PRIMARY KEY,
  client_name TEXT
);

-- Table FamilyMembers
CREATE TABLE FamilyMembers (
  member_id SERIAL PRIMARY KEY,
  name TEXT,
  birth_date DATE,
  death_date DATE,
  is_deceased BOOLEAN,
  current_residence TEXT,
  client_id INTEGER REFERENCES Clients(client_id)
);

-- Table Relationships
CREATE TABLE Relationships (
  relationship_id SERIAL PRIMARY KEY,
  member_id INTEGER REFERENCES FamilyMembers(member_id),
  related_member_id INTEGER REFERENCES FamilyMembers(member_id),
  relation_type TEXT
);

-- Table Addresses
CREATE TABLE Addresses (
  address_id SERIAL PRIMARY KEY,
  city TEXT,
  member_id INTEGER REFERENCES FamilyMembers(member_id)
);

-- Table Event
CREATE TABLE Events (
  event_id SERIAL PRIMARY KEY,
  event_date DATE,
  event_type TEXT,
  member_id INTEGER REFERENCES FamilyMembers(member_id)
);

-- Table EventAttendee
CREATE TABLE EventAttendee (
  event_date DATE,
  event_type TEXT,
  event_id INTEGER REFERENCES Events(event_id),
  member_id INTEGER REFERENCES FamilyMembers(member_id),
  PRIMARY KEY (event_date, event_type, member_id)
);
