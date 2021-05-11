DROP TABLE IF EXISTS programs CASCADE;
DROP TABLE IF EXISTS productions CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS transmissions CASCADE;
DROP TABLE IF EXISTS episodes CASCADE;
DROP TABLE IF EXISTS credits CASCADE;
DROP TABLE IF EXISTS tv_series CASCADE;
DROP TABLE IF EXISTS admins CASCADE;
DROP TABLE IF EXISTS producers CASCADE;
DROP TABLE IF EXISTS creditedPeople CASCADE;

BEGIN;
CREATE TABLE creditedPeople(
  id UUID PRIMARY KEY,
  name VARCHAR(128) NOT NULL
);

CREATE TABLE productions(
  id SERIAL PRIMARY KEY,
  name VARCHAR(128) NOT NULL
);

CREATE TABLE users(
  id UUID PRIMARY KEY,
  type VARCHAR(128) NOT NULL,
  username VARCHAR(20) NOT NULL,
  password INTEGER NOT NULL
);

CREATE TABLE programs(
  id UUID PRIMARY KEY,
  createdById UUID NOT NULL REFERENCES users(id),
  production INTEGER NOT NULL REFERENCES productions(id),
  name VARCHAR(128) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  duration INTEGER NOT NULL,
  approved BOOLEAN NOT NULL
);

CREATE TABLE transmissions(
  programsId UUID PRIMARY KEY REFERENCES programs(id)
);

CREATE TABLE episodes(
  programsId UUID PRIMARY KEY REFERENCES programs(id),
  tvSeriesId UUID NOT NULL,
  episodeNo INTEGER NOT NULL,
  seasonNo INTEGER NOT NULL
);

CREATE TABLE credits(
  creditedPersonId UUID NOT NULL REFERENCES creditedPeople(id),
  role VARCHAR(128) NOT NULL,
  programId UUID NOT NULL,
  PRIMARY KEY (creditedPersonId, role, programId)
);

CREATE TABLE tv_series(
  id UUID PRIMARY KEY,
  createdById UUID NOT NULL REFERENCES users(id),
  name VARCHAR(128) NOT NULL,
  description VARCHAR(1024)
);

COMMIT;

CREATE VIEW transmissionsView AS
  SELECT id, createdById, production, name, description, duration, approved FROM programs, transmissions
  WHERE programs.id = transmissions.programsId;

CREATE VIEW episodesView AS
  SELECT id, createdById, production, name, description, duration, approved, tvSeriesId, episodeNo, seasonNo FROM programs, episodes
  WHERE programs.id = episodes.programsId;

INSERT INTO creditedPeople (id, name) VALUES
  ('05a639ea-79a3-43b9-b0d7-079b6fce3e56', 'Mathias Engmark'),
  ('af8c0e36-3abf-4f97-99b6-3d7c6d94ce1b', 'Anton Irvold'),
  ('260f65f3-4110-4fd9-b21e-610fdb18a343', 'Christoffer Krath'),
  ('6cd868ae-2666-408b-8d06-5945bf742d6c', 'Nicklas Jensen'),
  ('1e3ccd36-f8d8-40fd-bd88-37b2843c8b72', 'Kasper Stokholm'),
  ('2685807d-a300-46fa-b087-8abd995befa6', 'Oliver Heine');

INSERT INTO productions(name) VALUES
  ('TV2'),
  ('Nordisk Film');

INSERT INTO users (id, type, username, password) VALUES
  ('5df26920-3da5-4b07-a394-641306d68561', 'Producer', 'kapper', '-1138619133'),
  ('b632b093-dc71-413d-a73e-d672b398c88c', 'Producer', 'producer', '-1003761774'),
  ('66789049-a663-4a07-bcd1-d4ac7e481a20', 'Admin', 'admin', '92668751');

INSERT INTO programs (id, createdById, production, name, description, duration, approved) VALUES
  ('f85a5a3a-dd0e-4943-a54c-940eead342eb', '5df26920-3da5-4b07-a394-641306d68561', 1, 'Everything burns', 'A hous burned to the ground', 1, true),
  ('0ee8bea9-f0f6-41dc-8658-e0e8475553cc', 'b632b093-dc71-413d-a73e-d672b398c88c', 2, 'De sprøjter alle sammen', 'Alle hold skal bage en lagkage, og være fællels om at sprøjte kagecrem rund på kagen', 1, false),
  ('2a93e924-6638-465a-b5cd-cb8d16f00d69', 'b632b093-dc71-413d-a73e-d672b398c88c', 1, 'Top Gun', 'En skyde film', 124, true),
  ('ae880937-b2cd-4206-b8c8-35966165390a', '5df26920-3da5-4b07-a394-641306d68561', 2, 'Melodi Gran Prix', 'Et sang show', 12, false),
  ('3129a54d-2d5f-4fbc-a39a-9c478905ff11', 'b632b093-dc71-413d-a73e-d672b398c88c', 1, 'Flyvende Farmor', 'En overskudsfarmor på tur', 234, true);

INSERT INTO transmissions (programsId) VALUES
  ('2a93e924-6638-465a-b5cd-cb8d16f00d69'),
  ('ae880937-b2cd-4206-b8c8-35966165390a'),
  ('3129a54d-2d5f-4fbc-a39a-9c478905ff11');

INSERT INTO tv_series (id, createdById, name, description) VALUES
  ('0c7bb0e9-7adb-4f3f-a8fd-6d7609c1f6cc', '5df26920-3da5-4b07-a394-641306d68561', 'Chicago Fire', 'En serie om CPD'),
  ('b3794996-72f6-42fa-90db-1fff32010af3', 'b632b093-dc71-413d-a73e-d672b398c88c', 'Den Store Bagedyst', 'Du ser folk bage - og bliver vildt sulten');

INSERT INTO episodes (programsId, tvSeriesId, episodeNo, seasonNo) VALUES
  ('f85a5a3a-dd0e-4943-a54c-940eead342eb', '0c7bb0e9-7adb-4f3f-a8fd-6d7609c1f6cc', 1, 2),
  ('0ee8bea9-f0f6-41dc-8658-e0e8475553cc', 'b3794996-72f6-42fa-90db-1fff32010af3', 2, 3);

INSERT INTO credits (creditedPersonId, role, programId) VALUES
  ('05a639ea-79a3-43b9-b0d7-079b6fce3e56', 'Casting', '2a93e924-6638-465a-b5cd-cb8d16f00d69'),
  ('260f65f3-4110-4fd9-b21e-610fdb18a343', 'Billed- og lydredigering', '2a93e924-6638-465a-b5cd-cb8d16f00d69'),
  ('af8c0e36-3abf-4f97-99b6-3d7c6d94ce1b', 'Kor', 'ae880937-b2cd-4206-b8c8-35966165390a'),
  ('2685807d-a300-46fa-b087-8abd995befa6', 'Efter ide af', '3129a54d-2d5f-4fbc-a39a-9c478905ff11'),
  ('6cd868ae-2666-408b-8d06-5945bf742d6c', 'Dronefører', 'f85a5a3a-dd0e-4943-a54c-940eead342eb'),
  ('1e3ccd36-f8d8-40fd-bd88-37b2843c8b72', 'Danske undertekster', 'f85a5a3a-dd0e-4943-a54c-940eead342eb');

SELECT * FROM creditedPeople;
