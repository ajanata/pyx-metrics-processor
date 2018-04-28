--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.7
-- Dumped by pg_dump version 10.1

-- Started on 2018-02-25 21:51:31

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12427)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2318 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 502 (class 1247 OID 17484)
-- Name: browser; Type: TYPE; Schema: public; Owner: pyx
--

CREATE TYPE browser AS (
	name character varying COLLATE pg_catalog."C.UTF-8",
	type character varying COLLATE pg_catalog."C.UTF-8",
	os character varying COLLATE pg_catalog."C.UTF-8",
	language character varying COLLATE pg_catalog."C.UTF-8"
);


ALTER TYPE browser OWNER TO pyx;

--
-- TOC entry 532 (class 1247 OID 17487)
-- Name: geo; Type: TYPE; Schema: public; Owner: pyx
--

CREATE TYPE geo AS (
	city character varying COLLATE pg_catalog."C.UTF-8",
	country character varying COLLATE pg_catalog."C.UTF-8",
	represented_country character varying COLLATE pg_catalog."C.UTF-8",
	subdivisions character varying[] COLLATE pg_catalog."C.UTF-8",
	postal character varying COLLATE pg_catalog."C.UTF-8"
);


ALTER TYPE geo OWNER TO pyx;

--
-- TOC entry 587 (class 1247 OID 17490)
-- Name: pyx_metadata; Type: TYPE; Schema: public; Owner: pyx
--

CREATE TYPE pyx_metadata AS (
	"timestamp" timestamp without time zone,
	build character varying COLLATE pg_catalog."C.UTF-8"
);


ALTER TYPE pyx_metadata OWNER TO pyx;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 188 (class 1259 OID 17491)
-- Name: black_card; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE black_card (
    uid bigint NOT NULL,
    text character varying(1000) NOT NULL,
    is_custom boolean NOT NULL,
    watermark character varying(20) NOT NULL,
    draw smallint NOT NULL,
    pick smallint NOT NULL
);


ALTER TABLE black_card OWNER TO pyx;

--
-- TOC entry 189 (class 1259 OID 17497)
-- Name: black_card_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE black_card_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE black_card_uid_seq OWNER TO pyx;

--
-- TOC entry 2319 (class 0 OID 0)
-- Dependencies: 189
-- Name: black_card_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE black_card_uid_seq OWNED BY black_card.uid;


--
-- TOC entry 209 (class 1259 OID 17681)
-- Name: card_dealt; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE card_dealt (
    uid bigint NOT NULL,
    game_id character varying(100) NOT NULL,
    session_id character varying(100) NOT NULL,
    white_card_uid bigint NOT NULL,
    deal_seq bigint NOT NULL,
    meta pyx_metadata NOT NULL
);


ALTER TABLE card_dealt OWNER TO pyx;

--
-- TOC entry 208 (class 1259 OID 17679)
-- Name: card_dealt_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE card_dealt_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE card_dealt_uid_seq OWNER TO pyx;

--
-- TOC entry 2320 (class 0 OID 0)
-- Dependencies: 208
-- Name: card_dealt_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE card_dealt_uid_seq OWNED BY card_dealt.uid;


--
-- TOC entry 190 (class 1259 OID 17499)
-- Name: deck; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE deck (
    uid bigint NOT NULL,
    name character varying(1000) NOT NULL,
    is_custom boolean NOT NULL,
    id bigint NOT NULL,
    white_count smallint NOT NULL,
    black_count smallint NOT NULL
);


ALTER TABLE deck OWNER TO pyx;

--
-- TOC entry 191 (class 1259 OID 17505)
-- Name: deck_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE deck_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE deck_uid_seq OWNER TO pyx;

--
-- TOC entry 2321 (class 0 OID 0)
-- Dependencies: 191
-- Name: deck_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE deck_uid_seq OWNED BY deck.uid;


--
-- TOC entry 192 (class 1259 OID 17507)
-- Name: game_start; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE game_start (
    uid bigint NOT NULL,
    blanks_in_deck smallint NOT NULL,
    max_players smallint NOT NULL,
    score_goal smallint NOT NULL,
    has_password boolean NOT NULL,
    meta pyx_metadata NOT NULL,
    game_id character varying(100) NOT NULL
);


ALTER TABLE game_start OWNER TO pyx;

--
-- TOC entry 193 (class 1259 OID 17513)
-- Name: game_start__deck; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE game_start__deck (
    uid bigint NOT NULL,
    deck_uid bigint NOT NULL,
    game_id character varying NOT NULL
);


ALTER TABLE game_start__deck OWNER TO pyx;

--
-- TOC entry 194 (class 1259 OID 17519)
-- Name: game_start__deck_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE game_start__deck_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE game_start__deck_uid_seq OWNER TO pyx;

--
-- TOC entry 2322 (class 0 OID 0)
-- Dependencies: 194
-- Name: game_start__deck_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE game_start__deck_uid_seq OWNED BY game_start__deck.uid;


--
-- TOC entry 195 (class 1259 OID 17521)
-- Name: game_start_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE game_start_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE game_start_uid_seq OWNER TO pyx;

--
-- TOC entry 2323 (class 0 OID 0)
-- Dependencies: 195
-- Name: game_start_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE game_start_uid_seq OWNED BY game_start.uid;


--
-- TOC entry 196 (class 1259 OID 17523)
-- Name: round_complete; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE round_complete (
    uid bigint NOT NULL,
    game_id character varying(100) NOT NULL,
    round_id character varying(100) NOT NULL,
    judge_session_id character varying(100) NOT NULL,
    winner_session_id character varying(100) NOT NULL,
    black_card_uid bigint NOT NULL,
    meta pyx_metadata NOT NULL,
    has_any_non_stock boolean NOT NULL
);


ALTER TABLE round_complete OWNER TO pyx;

--
-- TOC entry 2324 (class 0 OID 0)
-- Dependencies: 196
-- Name: COLUMN round_complete.has_any_non_stock; Type: COMMENT; Schema: public; Owner: pyx
--

COMMENT ON COLUMN round_complete.has_any_non_stock IS 'insert-time computation, not part of uniqueness. determination at query time is difficult';


--
-- TOC entry 197 (class 1259 OID 17529)
-- Name: round_complete__user_session__white_card; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE round_complete__user_session__white_card (
    uid bigint NOT NULL,
    session_id character varying(100) NOT NULL,
    round_complete_uid bigint NOT NULL,
    white_card_uid bigint NOT NULL,
    white_card_index smallint NOT NULL
);


ALTER TABLE round_complete__user_session__white_card OWNER TO pyx;

--
-- TOC entry 198 (class 1259 OID 17532)
-- Name: round_complete__user_session__white_card_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE round_complete__user_session__white_card_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE round_complete__user_session__white_card_uid_seq OWNER TO pyx;

--
-- TOC entry 2325 (class 0 OID 0)
-- Dependencies: 198
-- Name: round_complete__user_session__white_card_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE round_complete__user_session__white_card_uid_seq OWNED BY round_complete__user_session__white_card.uid;


--
-- TOC entry 199 (class 1259 OID 17534)
-- Name: round_complete_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE round_complete_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE round_complete_uid_seq OWNER TO pyx;

--
-- TOC entry 2326 (class 0 OID 0)
-- Dependencies: 199
-- Name: round_complete_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE round_complete_uid_seq OWNED BY round_complete.uid;


--
-- TOC entry 200 (class 1259 OID 17536)
-- Name: server_start; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE server_start (
    uid bigint NOT NULL,
    start_id character varying(100) NOT NULL,
    meta pyx_metadata NOT NULL
);


ALTER TABLE server_start OWNER TO pyx;

--
-- TOC entry 201 (class 1259 OID 17542)
-- Name: server_start_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE server_start_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE server_start_uid_seq OWNER TO pyx;

--
-- TOC entry 2327 (class 0 OID 0)
-- Dependencies: 201
-- Name: server_start_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE server_start_uid_seq OWNED BY server_start.uid;


--
-- TOC entry 202 (class 1259 OID 17544)
-- Name: user_session_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE user_session_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_session_uid_seq OWNER TO pyx;

--
-- TOC entry 203 (class 1259 OID 17546)
-- Name: user_session; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE user_session (
    uid bigint DEFAULT nextval('user_session_uid_seq'::regclass) NOT NULL,
    persistent_id character varying(100) NOT NULL,
    session_id character varying(100) NOT NULL,
    browser browser NOT NULL,
    geo geo NOT NULL,
    meta pyx_metadata NOT NULL
);


ALTER TABLE user_session OWNER TO pyx;

--
-- TOC entry 204 (class 1259 OID 17553)
-- Name: user_session_end_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE user_session_end_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_session_end_uid_seq OWNER TO pyx;

--
-- TOC entry 205 (class 1259 OID 17555)
-- Name: user_session_end; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE user_session_end (
    uid bigint DEFAULT nextval('user_session_end_uid_seq'::regclass) NOT NULL,
    session_id character varying(100) NOT NULL,
    meta pyx_metadata NOT NULL
);


ALTER TABLE user_session_end OWNER TO pyx;

--
-- TOC entry 206 (class 1259 OID 17562)
-- Name: white_card; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE white_card (
    uid bigint NOT NULL,
    text character varying(1000) NOT NULL,
    is_custom boolean NOT NULL,
    is_write_in boolean NOT NULL,
    watermark character varying(20) NOT NULL
);


ALTER TABLE white_card OWNER TO pyx;

--
-- TOC entry 207 (class 1259 OID 17568)
-- Name: white_card_uid_seq; Type: SEQUENCE; Schema: public; Owner: pyx
--

CREATE SEQUENCE white_card_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE white_card_uid_seq OWNER TO pyx;

--
-- TOC entry 2328 (class 0 OID 0)
-- Dependencies: 207
-- Name: white_card_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE white_card_uid_seq OWNED BY white_card.uid;


--
-- TOC entry 2121 (class 2604 OID 17570)
-- Name: black_card uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY black_card ALTER COLUMN uid SET DEFAULT nextval('black_card_uid_seq'::regclass);


--
-- TOC entry 2131 (class 2604 OID 17684)
-- Name: card_dealt uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY card_dealt ALTER COLUMN uid SET DEFAULT nextval('card_dealt_uid_seq'::regclass);


--
-- TOC entry 2122 (class 2604 OID 17571)
-- Name: deck uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY deck ALTER COLUMN uid SET DEFAULT nextval('deck_uid_seq'::regclass);


--
-- TOC entry 2123 (class 2604 OID 17572)
-- Name: game_start uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start ALTER COLUMN uid SET DEFAULT nextval('game_start_uid_seq'::regclass);


--
-- TOC entry 2124 (class 2604 OID 17573)
-- Name: game_start__deck uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck ALTER COLUMN uid SET DEFAULT nextval('game_start__deck_uid_seq'::regclass);


--
-- TOC entry 2125 (class 2604 OID 17574)
-- Name: round_complete uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete ALTER COLUMN uid SET DEFAULT nextval('round_complete_uid_seq'::regclass);


--
-- TOC entry 2126 (class 2604 OID 17575)
-- Name: round_complete__user_session__white_card uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card ALTER COLUMN uid SET DEFAULT nextval('round_complete__user_session__white_card_uid_seq'::regclass);


--
-- TOC entry 2127 (class 2604 OID 17576)
-- Name: server_start uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY server_start ALTER COLUMN uid SET DEFAULT nextval('server_start_uid_seq'::regclass);


--
-- TOC entry 2130 (class 2604 OID 17577)
-- Name: white_card uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY white_card ALTER COLUMN uid SET DEFAULT nextval('white_card_uid_seq'::regclass);


--
-- TOC entry 2135 (class 2606 OID 17579)
-- Name: black_card black_card_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY black_card
    ADD CONSTRAINT black_card_pkey PRIMARY KEY (uid);


--
-- TOC entry 2137 (class 2606 OID 17581)
-- Name: black_card black_card_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY black_card
    ADD CONSTRAINT black_card_unique UNIQUE (text, is_custom, draw, pick);


--
-- TOC entry 2187 (class 2606 OID 17689)
-- Name: card_dealt card_dealt_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY card_dealt
    ADD CONSTRAINT card_dealt_pkey PRIMARY KEY (uid);


--
-- TOC entry 2189 (class 2606 OID 17691)
-- Name: card_dealt card_dealt_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY card_dealt
    ADD CONSTRAINT card_dealt_unique UNIQUE (game_id, session_id, white_card_uid, deal_seq);


--
-- TOC entry 2139 (class 2606 OID 17583)
-- Name: deck deck_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY deck
    ADD CONSTRAINT deck_pkey PRIMARY KEY (uid);


--
-- TOC entry 2141 (class 2606 OID 17585)
-- Name: deck deck_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY deck
    ADD CONSTRAINT deck_unique UNIQUE (name, is_custom, id, white_count, black_count);


--
-- TOC entry 2173 (class 2606 OID 17587)
-- Name: user_session_end end_session_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session_end
    ADD CONSTRAINT end_session_id_unique UNIQUE (session_id);


--
-- TOC entry 2143 (class 2606 OID 17589)
-- Name: game_start game_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start
    ADD CONSTRAINT game_id_unique UNIQUE (game_id);


--
-- TOC entry 2148 (class 2606 OID 17591)
-- Name: game_start__deck game_start__deck_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck
    ADD CONSTRAINT game_start__deck_pkey PRIMARY KEY (uid);


--
-- TOC entry 2150 (class 2606 OID 17593)
-- Name: game_start__deck game_start__deck_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck
    ADD CONSTRAINT game_start__deck_unique UNIQUE (deck_uid, game_id);


--
-- TOC entry 2145 (class 2606 OID 17595)
-- Name: game_start game_start_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start
    ADD CONSTRAINT game_start_pkey PRIMARY KEY (uid);


--
-- TOC entry 2161 (class 2606 OID 17597)
-- Name: round_complete__user_session__white_card rc_us_wc_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT rc_us_wc_unique UNIQUE (session_id, round_complete_uid, white_card_uid, white_card_index);


--
-- TOC entry 2163 (class 2606 OID 17599)
-- Name: round_complete__user_session__white_card round_complete__user_session__white_card_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT round_complete__user_session__white_card_pkey PRIMARY KEY (uid);


--
-- TOC entry 2155 (class 2606 OID 17601)
-- Name: round_complete round_complete_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete
    ADD CONSTRAINT round_complete_pkey PRIMARY KEY (uid);


--
-- TOC entry 2157 (class 2606 OID 17603)
-- Name: round_complete round_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete
    ADD CONSTRAINT round_id_unique UNIQUE (round_id);


--
-- TOC entry 2165 (class 2606 OID 17605)
-- Name: server_start server_start_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY server_start
    ADD CONSTRAINT server_start_pkey PRIMARY KEY (uid);


--
-- TOC entry 2169 (class 2606 OID 17607)
-- Name: user_session session_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session
    ADD CONSTRAINT session_id_unique UNIQUE (session_id);


--
-- TOC entry 2167 (class 2606 OID 17609)
-- Name: server_start start_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY server_start
    ADD CONSTRAINT start_id_unique UNIQUE (start_id);


--
-- TOC entry 2175 (class 2606 OID 17611)
-- Name: user_session_end user_session_end_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session_end
    ADD CONSTRAINT user_session_end_pkey PRIMARY KEY (uid);


--
-- TOC entry 2171 (class 2606 OID 17613)
-- Name: user_session user_session_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session
    ADD CONSTRAINT user_session_pkey PRIMARY KEY (uid);


--
-- TOC entry 2183 (class 2606 OID 17615)
-- Name: white_card white_card_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY white_card
    ADD CONSTRAINT white_card_pkey PRIMARY KEY (uid);


--
-- TOC entry 2185 (class 2606 OID 17617)
-- Name: white_card white_card_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY white_card
    ADD CONSTRAINT white_card_unique UNIQUE (text, is_custom, is_write_in);


--
-- TOC entry 2132 (class 1259 OID 17618)
-- Name: black_card_is_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX black_card_is_custom_idx ON black_card USING btree (is_custom) WHERE (is_custom = true);


--
-- TOC entry 2133 (class 1259 OID 17619)
-- Name: black_card_is_not_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX black_card_is_not_custom_idx ON black_card USING btree (is_custom) WHERE (is_custom = false);


--
-- TOC entry 2158 (class 1259 OID 17620)
-- Name: fki_rc_us_rc_white_card_uid; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX fki_rc_us_rc_white_card_uid ON round_complete__user_session__white_card USING btree (white_card_uid);


--
-- TOC entry 2159 (class 1259 OID 17621)
-- Name: fki_rc_us_wc_round_complete_uid; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX fki_rc_us_wc_round_complete_uid ON round_complete__user_session__white_card USING btree (round_complete_uid);


--
-- TOC entry 2151 (class 1259 OID 17622)
-- Name: fki_round_complete_black_card_uid; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX fki_round_complete_black_card_uid ON round_complete USING btree (black_card_uid);


--
-- TOC entry 2146 (class 1259 OID 17623)
-- Name: game_start__deck_game_id_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX game_start__deck_game_id_idx ON game_start__deck USING btree (game_id);


--
-- TOC entry 2152 (class 1259 OID 17624)
-- Name: round_complete_has_any_non_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX round_complete_has_any_non_stock_idx ON round_complete USING btree (has_any_non_stock) WHERE (has_any_non_stock = true);


--
-- TOC entry 2153 (class 1259 OID 17625)
-- Name: round_complete_is_only_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX round_complete_is_only_stock_idx ON round_complete USING btree (has_any_non_stock) WHERE (has_any_non_stock = false);


--
-- TOC entry 2176 (class 1259 OID 17626)
-- Name: white_card_is_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_custom_idx ON white_card USING btree (is_custom) WHERE (is_custom = true);


--
-- TOC entry 2177 (class 1259 OID 17627)
-- Name: white_card_is_not_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_not_custom_idx ON white_card USING btree (is_custom) WHERE (is_custom = false);


--
-- TOC entry 2178 (class 1259 OID 17628)
-- Name: white_card_is_not_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_not_stock_idx ON white_card USING btree (uid) WHERE ((is_custom = true) OR (is_write_in = true));


--
-- TOC entry 2179 (class 1259 OID 17629)
-- Name: white_card_is_not_write_in_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_not_write_in_idx ON white_card USING btree (is_write_in) WHERE (is_write_in = false);


--
-- TOC entry 2180 (class 1259 OID 17630)
-- Name: white_card_is_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_stock_idx ON white_card USING btree (uid) WHERE ((is_custom = false) AND (is_write_in = false));


--
-- TOC entry 2181 (class 1259 OID 17631)
-- Name: white_card_is_write_in_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_write_in_idx ON white_card USING btree (is_write_in) WHERE (is_write_in = true);


--
-- TOC entry 2194 (class 2606 OID 17692)
-- Name: card_dealt card_dealt__white_card_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY card_dealt
    ADD CONSTRAINT card_dealt__white_card_uid FOREIGN KEY (white_card_uid) REFERENCES white_card(uid);


--
-- TOC entry 2190 (class 2606 OID 17632)
-- Name: game_start__deck game_start__deck__deck_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck
    ADD CONSTRAINT game_start__deck__deck_uid FOREIGN KEY (deck_uid) REFERENCES deck(uid);


--
-- TOC entry 2192 (class 2606 OID 17637)
-- Name: round_complete__user_session__white_card rc_us_rc_white_card_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT rc_us_rc_white_card_uid FOREIGN KEY (white_card_uid) REFERENCES white_card(uid);


--
-- TOC entry 2193 (class 2606 OID 17642)
-- Name: round_complete__user_session__white_card rc_us_wc_round_complete_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT rc_us_wc_round_complete_uid FOREIGN KEY (round_complete_uid) REFERENCES round_complete(uid);


--
-- TOC entry 2191 (class 2606 OID 17647)
-- Name: round_complete round_complete_black_card_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete
    ADD CONSTRAINT round_complete_black_card_uid FOREIGN KEY (black_card_uid) REFERENCES black_card(uid);


-- Completed on 2018-02-25 21:51:31

--
-- PostgreSQL database dump complete
--

CREATE INDEX round_complete_game_id_idx ON round_complete USING btree (game_id);
