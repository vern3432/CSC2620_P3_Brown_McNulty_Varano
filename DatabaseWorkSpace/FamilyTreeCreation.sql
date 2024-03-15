CREATE TABLE IF NOT EXISTS Clients (
  client_id INTEGER PRIMARY KEY,
  client_name TEXT
);

CREATE TABLE IF NOT EXISTS FamilyMembers (
  member_id INTEGER PRIMARY KEY,
  name TEXT,
  birth_date DATE,
  death_date DATE,
  is_deceased BOOLEAN,
  current_residence TEXT,
  client_id INTEGER,
  FOREIGN KEY(client_id) REFERENCES Clients(client_id)
);

CREATE TABLE IF NOT EXISTS Relationships (
  relationship_id INTEGER PRIMARY KEY,
  member_id INTEGER,
  related_member_id INTEGER,
  relation_type TEXT,
  FOREIGN KEY(member_id) REFERENCES FamilyMembers(member_id),
  FOREIGN KEY(related_member_id) REFERENCES FamilyMembers(member_id)
);

CREATE TABLE IF NOT EXISTS Addresses (
  address_id INTEGER PRIMARY KEY,
  city TEXT,
  member_id INTEGER,
  FOREIGN KEY(member_id) REFERENCES FamilyMembers(member_id)
);

CREATE TABLE IF NOT EXISTS Events (
  event_id INTEGER PRIMARY KEY,
  event_date DATE,
  event_type TEXT,
  member_id INTEGER,
  FOREIGN KEY(member_id) REFERENCES FamilyMembers(member_id)
);
