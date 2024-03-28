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
);





























