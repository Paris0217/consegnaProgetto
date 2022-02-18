-- This script was generated by a beta version of the ERD tool in pgAdmin 4.
-- Please log an issue at https://redmine.postgresql.org/projects/pgadmin4/issues/new if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE public.ids_ac_flights
(
    id integer NOT NULL,
    sid character varying(255),
    airport_id integer NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    departure_country character varying(3) NOT NULL,
    departure_city character varying(255) NOT NULL,
    arrival_country character varying(3) NOT NULL,
    arrival_city character varying(255) NOT NULL,
    price double precision NOT NULL,
    seats integer NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.ids_ac_tickets
(
    id integer NOT NULL,
    sid character varying(255) NOT NULL,
    active boolean NOT NULL,
    flight_id integer,
    PRIMARY KEY (id)
);

ALTER TABLE public.ids_ac_tickets
    ADD FOREIGN KEY (flight_id)
    REFERENCES public.ids_ac_flights (id)
    NOT VALID;

END;