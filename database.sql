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
DROP TABLE IF EXISTS notifications CASCADE;

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
	username VARCHAR(20) UNIQUE NOT NULL,
	password INTEGER NOT NULL,
		type VARCHAR(128) NOT NULL
);

CREATE TABLE programs(
	id UUID PRIMARY KEY,
	name VARCHAR(128) NOT NULL,
	description VARCHAR(1024) NOT NULL,
	createdById UUID NOT NULL REFERENCES users(id),
	duration INTEGER NOT NULL,
	approved BOOLEAN NOT NULL,
	production INTEGER NOT NULL REFERENCES productions(id)
);

CREATE TABLE transmissions(
    programsId UUID PRIMARY KEY REFERENCES programs(id)
);

CREATE TABLE tv_series(
	id UUID PRIMARY KEY,
	name VARCHAR(128) NOT NULL,
	description VARCHAR(1024),
	createdById UUID NOT NULL REFERENCES users(id)
);

CREATE TABLE episodes(
	programsId UUID PRIMARY KEY REFERENCES programs(id),
        tvSeriesId UUID NOT NULL REFERENCES tv_series,
	episodeNo INTEGER NOT NULL,
	seasonNo INTEGER NOT NULL
);

CREATE TABLE credits(
        creditedPersonId UUID NOT NULL REFERENCES creditedPeople(id),
        role VARCHAR(128),
        programId UUID REFERENCES programs,
        PRIMARY KEY (creditedPersonId, role, programId)
);


CREATE TABLE notifications(
	id SERIAL PRIMARY KEY,
	title VARCHAR(1024) UNIQUE NOT NULL,
	seen BOOLEAN NOT NULL
);

COMMIT;

CREATE VIEW transmissionsView AS
SELECT id, name, description, createdById, duration, approved, production FROM programs, transmissions
WHERE programs.id = transmissions.programsId;

CREATE VIEW episodesView AS
SELECT id, tvSeriesId,  name, description, createdById, episodeNo, seasonNo, duration, approved, production FROM programs, episodes
WHERE programs.id = episodes.programsId;

BEGIN;
--
INSERT INTO creditedPeople (id, name) VALUES
('05a639ea-79a3-43b9-b0d7-079b6fce3e56', 'Mathias Engmark'),
('af8c0e36-3abf-4f97-99b6-3d7c6d94ce1b', 'Anton Irvold'),
('260f65f3-4110-4fd9-b21e-610fdb18a343', 'Christoffer Krath'),
('6cd868ae-2666-408b-8d06-5945bf742d6c', 'Nicklas Jensen'),
('1e3ccd36-f8d8-40fd-bd88-37b2843c8b72', 'Kasper Stokholm'),
('2685807d-a300-46fa-b087-8abd995befa6', 'Oliver Heine');

--
INSERT INTO productions(name) VALUES
('TV2'),
('Nordisk Film');

--
INSERT INTO users (id, username, password, type) VALUES
('5df26920-3da5-4b07-a394-641306d68561', 'kapper', '-1138619133', 'Producer'),
('b632b093-dc71-413d-a73e-d672b398c88c', 'producer', '-1003761774', 'Producer'),
('66789049-a663-4a07-bcd1-d4ac7e481a20', 'admin', '92668751', 'Admin');

--
INSERT INTO programs (id, name, description, createdById, duration, approved, production) VALUES
('f85a5a3a-dd0e-4943-a54c-940eead342eb', 'Everything burns', 'A hous burned to the ground', '5df26920-3da5-4b07-a394-641306d68561', 1, true,1),
('0ee8bea9-f0f6-41dc-8658-e0e8475553cc', 'De sprøjter alle sammen', 'Alle hold skal bage en lagkage, og være fællels om at sprøjte kagecrem rund på kagen', 'b632b093-dc71-413d-a73e-d672b398c88c', 1, false, 2),
('2a93e924-6638-465a-b5cd-cb8d16f00d69', 'Top Gun', 'En skyde film', 'b632b093-dc71-413d-a73e-d672b398c88c', 124, true, 1),
('ae880937-b2cd-4206-b8c8-35966165390a', 'Melodi Gran Prix', 'Et sang show', '5df26920-3da5-4b07-a394-641306d68561', 12, false, 2),
('3129a54d-2d5f-4fbc-a39a-9c478905ff11', 'Flyvende Farmor', 'En overskudsfarmor på tur', 'b632b093-dc71-413d-a73e-d672b398c88c', 234, true, 1);

--
INSERT INTO transmissions (programsId) VALUES
('2a93e924-6638-465a-b5cd-cb8d16f00d69'),
('ae880937-b2cd-4206-b8c8-35966165390a'),
('3129a54d-2d5f-4fbc-a39a-9c478905ff11');

--
INSERT INTO tv_series (id, name, description, createdById) VALUES
('0c7bb0e9-7adb-4f3f-a8fd-6d7609c1f6cc', 'Chicago Fire', 'En serie om CPD', '5df26920-3da5-4b07-a394-641306d68561'),
('b3794996-72f6-42fa-90db-1fff32010af3', 'Den Store Bagedyst', 'Du ser folk bage - og bliver vildt sulten', 'b632b093-dc71-413d-a73e-d672b398c88c');

--
INSERT INTO episodes (programsId, tvSeriesId, episodeNo, seasonNo) VALUES
('f85a5a3a-dd0e-4943-a54c-940eead342eb', '0c7bb0e9-7adb-4f3f-a8fd-6d7609c1f6cc', 1, 2),
('0ee8bea9-f0f6-41dc-8658-e0e8475553cc', 'b3794996-72f6-42fa-90db-1fff32010af3', 2, 3);

--
INSERT INTO credits (creditedPersonId, role, programId) VALUES
('05a639ea-79a3-43b9-b0d7-079b6fce3e56', 'Casting', '2a93e924-6638-465a-b5cd-cb8d16f00d69'),
('260f65f3-4110-4fd9-b21e-610fdb18a343', 'Billed- og lydredigering', '2a93e924-6638-465a-b5cd-cb8d16f00d69'),
('af8c0e36-3abf-4f97-99b6-3d7c6d94ce1b', 'Kor', 'ae880937-b2cd-4206-b8c8-35966165390a'),
('2685807d-a300-46fa-b087-8abd995befa6', 'Efter ide af', '3129a54d-2d5f-4fbc-a39a-9c478905ff11'),
('6cd868ae-2666-408b-8d06-5945bf742d6c', 'Dronefører', 'f85a5a3a-dd0e-4943-a54c-940eead342eb'),
('1e3ccd36-f8d8-40fd-bd88-37b2843c8b72', 'Danske undertekster', 'f85a5a3a-dd0e-4943-a54c-940eead342eb');


INSERT INTO notifications (title, seen) VALUES
('Kapper created a person with the name Mathias Engmark on 13-04-2021 13:45:23', true),
('Kapper created a person with the name Anton Irvold on 13-04-2021 13:55:23', true),
('Producer created a person with the name Christoffer Krath on 13-04-2021 14:04:39', true),
('Kapper created a person with the name Nicklas Jensen on 14-04-2021 11:55:03', true),
('Producer created a person with the name Kasper Stokholm on 14-04-2021 12:13:30', true),
('Kapper created a person with the name Oliver Heine on 14-04-2021 12:15:23', true),
('Producer created a TV-series with the name Chicago Fire on 14-04-2021 13:25:23', true),
('Kapper created an episode with the title Everything burns on 14-04-2021 13:27:48', true),
('Kapper created a TV-series witht the name Den Store Bagedyst on 15-04-2021 11:45:46', true),
('Producer created an episode with the title De sprøjter alle sammen on 15-04-2021 12:00:12', true),
('producer created a transmission with the title Top Gun on 16-04-2021 14:23:56', true),
('Kapper created a transmission with the title Melodi Gran Prix on 16-04-2021 21:34:42', true),
('Producer created a transmission with the title Flyvende Farmor on 17-04-2021 23:21:34', true),
('Producer added Mathias Engmark as a credit to the program Top Gun with the role Casting on 18-04-2021 08:32:45', true),
('Producer added Christoffer Krath as a credit to the program Top Gun with the role Billed- og lydredigering on 18-04-2021 08:33:00', true),
('Kapper added Anton Irvold as a credit to the program Melodi Gran Prix with the role Kor on 18-04-2021 08:34:45', true),
('Producer added Oliver Heine as a credit to the program Flyvende Farmor with the role Efter ide af on 18-04-2021 08:42:45', true),
('Kapper added Nicklas Jensen as a credit to the program Top Gun with the role Droneføre on 18-04-2021 09:32:45', true),
('Kapper added Kasper stokhold as a credit to the program Top Gun with the role Danske undertekster on 18-04-2021 10:33:00', true),
('Admin approved a program with the UUID f85a5a3a-dd0e-4943-a54c-940eead342eb and name Everything burns on 20-04-2021 12:34:21', false),
('Admin approved a program with the UUID 2a93e924-6638-465a-b5cd-cb8d16f00d69 and name Top Gun on 20-04-2021 12:34:21', false),
('Admin approved a program with the UUID 3129a54d-2d5f-4fbc-a39a-9c478905ff11 and name Flyvende Farmor on 20-04-2021 12:34:21', false);
COMMIT;
