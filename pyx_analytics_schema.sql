--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.10
-- Dumped by pg_dump version 10.1

-- Started on 2018-01-24 09:54:58

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;

--
-- TOC entry 570 (class 1247 OID 17320)
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
-- TOC entry 573 (class 1247 OID 17323)
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
-- TOC entry 576 (class 1247 OID 17326)
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
-- TOC entry 184 (class 1259 OID 17327)
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
-- TOC entry 185 (class 1259 OID 17333)
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
-- TOC entry 2281 (class 0 OID 0)
-- Dependencies: 185
-- Name: black_card_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE black_card_uid_seq OWNED BY black_card.uid;


--
-- TOC entry 186 (class 1259 OID 17335)
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
-- TOC entry 187 (class 1259 OID 17341)
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
-- TOC entry 2283 (class 0 OID 0)
-- Dependencies: 187
-- Name: deck_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE deck_uid_seq OWNED BY deck.uid;


--
-- TOC entry 188 (class 1259 OID 17343)
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
-- TOC entry 189 (class 1259 OID 17349)
-- Name: game_start__deck; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE game_start__deck (
    uid bigint NOT NULL,
    deck_uid bigint NOT NULL,
    game_id character varying NOT NULL
);


ALTER TABLE game_start__deck OWNER TO pyx;

--
-- TOC entry 190 (class 1259 OID 17355)
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
-- TOC entry 2286 (class 0 OID 0)
-- Dependencies: 190
-- Name: game_start__deck_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE game_start__deck_uid_seq OWNED BY game_start__deck.uid;


--
-- TOC entry 191 (class 1259 OID 17357)
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
-- TOC entry 2287 (class 0 OID 0)
-- Dependencies: 191
-- Name: game_start_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE game_start_uid_seq OWNED BY game_start.uid;


--
-- TOC entry 192 (class 1259 OID 17359)
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
-- TOC entry 2288 (class 0 OID 0)
-- Dependencies: 192
-- Name: COLUMN round_complete.has_any_non_stock; Type: COMMENT; Schema: public; Owner: pyx
--

COMMENT ON COLUMN round_complete.has_any_non_stock IS 'insert-time computation, not part of uniqueness. determination at query time is difficult';


--
-- TOC entry 193 (class 1259 OID 17365)
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
-- TOC entry 194 (class 1259 OID 17368)
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
-- TOC entry 2291 (class 0 OID 0)
-- Dependencies: 194
-- Name: round_complete__user_session__white_card_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE round_complete__user_session__white_card_uid_seq OWNED BY round_complete__user_session__white_card.uid;


--
-- TOC entry 195 (class 1259 OID 17370)
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
-- TOC entry 2292 (class 0 OID 0)
-- Dependencies: 195
-- Name: round_complete_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE round_complete_uid_seq OWNED BY round_complete.uid;


--
-- TOC entry 196 (class 1259 OID 17372)
-- Name: server_start; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE server_start (
    uid bigint NOT NULL,
    start_id character varying(100) NOT NULL,
    meta pyx_metadata NOT NULL
);


ALTER TABLE server_start OWNER TO pyx;

--
-- TOC entry 197 (class 1259 OID 17378)
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
-- TOC entry 2294 (class 0 OID 0)
-- Dependencies: 197
-- Name: server_start_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE server_start_uid_seq OWNED BY server_start.uid;


--
-- TOC entry 198 (class 1259 OID 17380)
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
-- TOC entry 199 (class 1259 OID 17382)
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
-- TOC entry 200 (class 1259 OID 17389)
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
-- TOC entry 201 (class 1259 OID 17391)
-- Name: user_session_end; Type: TABLE; Schema: public; Owner: pyx
--

CREATE TABLE user_session_end (
    uid bigint DEFAULT nextval('user_session_end_uid_seq'::regclass) NOT NULL,
    session_id character varying(100) NOT NULL,
    meta pyx_metadata NOT NULL
);


ALTER TABLE user_session_end OWNER TO pyx;

--
-- TOC entry 202 (class 1259 OID 17398)
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
-- TOC entry 203 (class 1259 OID 17404)
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
-- TOC entry 2298 (class 0 OID 0)
-- Dependencies: 203
-- Name: white_card_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: pyx
--

ALTER SEQUENCE white_card_uid_seq OWNED BY white_card.uid;


--
-- TOC entry 2092 (class 2604 OID 17406)
-- Name: black_card uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY black_card ALTER COLUMN uid SET DEFAULT nextval('black_card_uid_seq'::regclass);


--
-- TOC entry 2093 (class 2604 OID 17407)
-- Name: deck uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY deck ALTER COLUMN uid SET DEFAULT nextval('deck_uid_seq'::regclass);


--
-- TOC entry 2094 (class 2604 OID 17408)
-- Name: game_start uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start ALTER COLUMN uid SET DEFAULT nextval('game_start_uid_seq'::regclass);


--
-- TOC entry 2095 (class 2604 OID 17409)
-- Name: game_start__deck uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck ALTER COLUMN uid SET DEFAULT nextval('game_start__deck_uid_seq'::regclass);


--
-- TOC entry 2096 (class 2604 OID 17410)
-- Name: round_complete uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete ALTER COLUMN uid SET DEFAULT nextval('round_complete_uid_seq'::regclass);


--
-- TOC entry 2097 (class 2604 OID 17411)
-- Name: round_complete__user_session__white_card uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card ALTER COLUMN uid SET DEFAULT nextval('round_complete__user_session__white_card_uid_seq'::regclass);


--
-- TOC entry 2098 (class 2604 OID 17412)
-- Name: server_start uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY server_start ALTER COLUMN uid SET DEFAULT nextval('server_start_uid_seq'::regclass);


--
-- TOC entry 2101 (class 2604 OID 17413)
-- Name: white_card uid; Type: DEFAULT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY white_card ALTER COLUMN uid SET DEFAULT nextval('white_card_uid_seq'::regclass);


--
-- TOC entry 2105 (class 2606 OID 17415)
-- Name: black_card black_card_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY black_card
    ADD CONSTRAINT black_card_pkey PRIMARY KEY (uid);


--
-- TOC entry 2107 (class 2606 OID 17417)
-- Name: black_card black_card_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY black_card
    ADD CONSTRAINT black_card_unique UNIQUE (text, is_custom, watermark, draw, pick);


--
-- TOC entry 2109 (class 2606 OID 17419)
-- Name: deck deck_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY deck
    ADD CONSTRAINT deck_pkey PRIMARY KEY (uid);


--
-- TOC entry 2111 (class 2606 OID 17421)
-- Name: deck deck_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY deck
    ADD CONSTRAINT deck_unique UNIQUE (name, is_custom, id, white_count, black_count);


--
-- TOC entry 2143 (class 2606 OID 17423)
-- Name: user_session_end end_session_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session_end
    ADD CONSTRAINT end_session_id_unique UNIQUE (session_id);


--
-- TOC entry 2113 (class 2606 OID 17425)
-- Name: game_start game_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start
    ADD CONSTRAINT game_id_unique UNIQUE (game_id);


--
-- TOC entry 2118 (class 2606 OID 17427)
-- Name: game_start__deck game_start__deck_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck
    ADD CONSTRAINT game_start__deck_pkey PRIMARY KEY (uid);


--
-- TOC entry 2120 (class 2606 OID 17429)
-- Name: game_start__deck game_start__deck_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck
    ADD CONSTRAINT game_start__deck_unique UNIQUE (deck_uid, game_id);


--
-- TOC entry 2115 (class 2606 OID 17431)
-- Name: game_start game_start_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start
    ADD CONSTRAINT game_start_pkey PRIMARY KEY (uid);


--
-- TOC entry 2131 (class 2606 OID 17433)
-- Name: round_complete__user_session__white_card rc_us_wc_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT rc_us_wc_unique UNIQUE (session_id, round_complete_uid, white_card_uid, white_card_index);


--
-- TOC entry 2133 (class 2606 OID 17435)
-- Name: round_complete__user_session__white_card round_complete__user_session__white_card_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT round_complete__user_session__white_card_pkey PRIMARY KEY (uid);


--
-- TOC entry 2125 (class 2606 OID 17437)
-- Name: round_complete round_complete_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete
    ADD CONSTRAINT round_complete_pkey PRIMARY KEY (uid);


--
-- TOC entry 2127 (class 2606 OID 17439)
-- Name: round_complete round_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete
    ADD CONSTRAINT round_id_unique UNIQUE (round_id);


--
-- TOC entry 2135 (class 2606 OID 17441)
-- Name: server_start server_start_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY server_start
    ADD CONSTRAINT server_start_pkey PRIMARY KEY (uid);


--
-- TOC entry 2139 (class 2606 OID 17443)
-- Name: user_session session_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session
    ADD CONSTRAINT session_id_unique UNIQUE (session_id);


--
-- TOC entry 2137 (class 2606 OID 17445)
-- Name: server_start start_id_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY server_start
    ADD CONSTRAINT start_id_unique UNIQUE (start_id);


--
-- TOC entry 2145 (class 2606 OID 17447)
-- Name: user_session_end user_session_end_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session_end
    ADD CONSTRAINT user_session_end_pkey PRIMARY KEY (uid);


--
-- TOC entry 2141 (class 2606 OID 17449)
-- Name: user_session user_session_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY user_session
    ADD CONSTRAINT user_session_pkey PRIMARY KEY (uid);


--
-- TOC entry 2153 (class 2606 OID 17451)
-- Name: white_card white_card_pkey; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY white_card
    ADD CONSTRAINT white_card_pkey PRIMARY KEY (uid);


--
-- TOC entry 2155 (class 2606 OID 17453)
-- Name: white_card white_card_unique; Type: CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY white_card
    ADD CONSTRAINT white_card_unique UNIQUE (text, is_custom, is_write_in, watermark);


--
-- TOC entry 2102 (class 1259 OID 17454)
-- Name: black_card_is_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX black_card_is_custom_idx ON black_card USING btree (is_custom) WHERE (is_custom = true);


--
-- TOC entry 2103 (class 1259 OID 17455)
-- Name: black_card_is_not_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX black_card_is_not_custom_idx ON black_card USING btree (is_custom) WHERE (is_custom = false);


--
-- TOC entry 2128 (class 1259 OID 17456)
-- Name: fki_rc_us_rc_white_card_uid; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX fki_rc_us_rc_white_card_uid ON round_complete__user_session__white_card USING btree (white_card_uid);


--
-- TOC entry 2129 (class 1259 OID 17457)
-- Name: fki_rc_us_wc_round_complete_uid; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX fki_rc_us_wc_round_complete_uid ON round_complete__user_session__white_card USING btree (round_complete_uid);


--
-- TOC entry 2121 (class 1259 OID 17458)
-- Name: fki_round_complete_black_card_uid; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX fki_round_complete_black_card_uid ON round_complete USING btree (black_card_uid);


--
-- TOC entry 2116 (class 1259 OID 17459)
-- Name: game_start__deck_game_id_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX game_start__deck_game_id_idx ON game_start__deck USING btree (game_id);


--
-- TOC entry 2122 (class 1259 OID 17460)
-- Name: round_complete_has_any_non_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX round_complete_has_any_non_stock_idx ON round_complete USING btree (has_any_non_stock) WHERE (has_any_non_stock = true);


--
-- TOC entry 2123 (class 1259 OID 17461)
-- Name: round_complete_is_only_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX round_complete_is_only_stock_idx ON round_complete USING btree (has_any_non_stock) WHERE (has_any_non_stock = false);


--
-- TOC entry 2146 (class 1259 OID 17462)
-- Name: white_card_is_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_custom_idx ON white_card USING btree (is_custom) WHERE (is_custom = true);


--
-- TOC entry 2147 (class 1259 OID 17463)
-- Name: white_card_is_not_custom_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_not_custom_idx ON white_card USING btree (is_custom) WHERE (is_custom = false);


--
-- TOC entry 2148 (class 1259 OID 17464)
-- Name: white_card_is_not_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_not_stock_idx ON white_card USING btree (uid) WHERE ((is_custom = true) OR (is_write_in = true));


--
-- TOC entry 2149 (class 1259 OID 17465)
-- Name: white_card_is_not_write_in_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_not_write_in_idx ON white_card USING btree (is_write_in) WHERE (is_write_in = false);


--
-- TOC entry 2150 (class 1259 OID 17466)
-- Name: white_card_is_stock_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_stock_idx ON white_card USING btree (uid) WHERE ((is_custom = false) AND (is_write_in = false));


--
-- TOC entry 2151 (class 1259 OID 17467)
-- Name: white_card_is_write_in_idx; Type: INDEX; Schema: public; Owner: pyx
--

CREATE INDEX white_card_is_write_in_idx ON white_card USING btree (is_write_in) WHERE (is_write_in = true);


--
-- TOC entry 2156 (class 2606 OID 17468)
-- Name: game_start__deck game_start__deck__deck_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY game_start__deck
    ADD CONSTRAINT game_start__deck__deck_uid FOREIGN KEY (deck_uid) REFERENCES deck(uid);


--
-- TOC entry 2158 (class 2606 OID 17473)
-- Name: round_complete__user_session__white_card rc_us_rc_white_card_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT rc_us_rc_white_card_uid FOREIGN KEY (white_card_uid) REFERENCES white_card(uid);


--
-- TOC entry 2159 (class 2606 OID 17478)
-- Name: round_complete__user_session__white_card rc_us_wc_round_complete_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete__user_session__white_card
    ADD CONSTRAINT rc_us_wc_round_complete_uid FOREIGN KEY (round_complete_uid) REFERENCES round_complete(uid);


--
-- TOC entry 2157 (class 2606 OID 17483)
-- Name: round_complete round_complete_black_card_uid; Type: FK CONSTRAINT; Schema: public; Owner: pyx
--

ALTER TABLE ONLY round_complete
    ADD CONSTRAINT round_complete_black_card_uid FOREIGN KEY (black_card_uid) REFERENCES black_card(uid);


--
-- TOC entry 2279 (class 0 OID 0)
-- Dependencies: 7
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2280 (class 0 OID 0)
-- Dependencies: 184
-- Name: black_card; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE black_card FROM PUBLIC;
REVOKE ALL ON TABLE black_card FROM pyx;
GRANT ALL ON TABLE black_card TO pyx;


--
-- TOC entry 2282 (class 0 OID 0)
-- Dependencies: 186
-- Name: deck; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE deck FROM PUBLIC;
REVOKE ALL ON TABLE deck FROM pyx;
GRANT ALL ON TABLE deck TO pyx;


--
-- TOC entry 2284 (class 0 OID 0)
-- Dependencies: 188
-- Name: game_start; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE game_start FROM PUBLIC;
REVOKE ALL ON TABLE game_start FROM pyx;
GRANT ALL ON TABLE game_start TO pyx;


--
-- TOC entry 2285 (class 0 OID 0)
-- Dependencies: 189
-- Name: game_start__deck; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE game_start__deck FROM PUBLIC;
REVOKE ALL ON TABLE game_start__deck FROM pyx;
GRANT ALL ON TABLE game_start__deck TO pyx;


--
-- TOC entry 2289 (class 0 OID 0)
-- Dependencies: 192
-- Name: round_complete; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE round_complete FROM PUBLIC;
REVOKE ALL ON TABLE round_complete FROM pyx;
GRANT ALL ON TABLE round_complete TO pyx;


--
-- TOC entry 2290 (class 0 OID 0)
-- Dependencies: 193
-- Name: round_complete__user_session__white_card; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE round_complete__user_session__white_card FROM PUBLIC;
REVOKE ALL ON TABLE round_complete__user_session__white_card FROM pyx;
GRANT ALL ON TABLE round_complete__user_session__white_card TO pyx;


--
-- TOC entry 2293 (class 0 OID 0)
-- Dependencies: 196
-- Name: server_start; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE server_start FROM PUBLIC;
REVOKE ALL ON TABLE server_start FROM pyx;
GRANT ALL ON TABLE server_start TO pyx;


--
-- TOC entry 2295 (class 0 OID 0)
-- Dependencies: 199
-- Name: user_session; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE user_session FROM PUBLIC;
REVOKE ALL ON TABLE user_session FROM pyx;
GRANT ALL ON TABLE user_session TO pyx;


--
-- TOC entry 2296 (class 0 OID 0)
-- Dependencies: 201
-- Name: user_session_end; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE user_session_end FROM PUBLIC;
REVOKE ALL ON TABLE user_session_end FROM pyx;
GRANT ALL ON TABLE user_session_end TO pyx;


--
-- TOC entry 2297 (class 0 OID 0)
-- Dependencies: 202
-- Name: white_card; Type: ACL; Schema: public; Owner: pyx
--

REVOKE ALL ON TABLE white_card FROM PUBLIC;
REVOKE ALL ON TABLE white_card FROM pyx;
GRANT ALL ON TABLE white_card TO pyx;


-- Completed on 2018-01-24 09:54:58

--
-- PostgreSQL database dump complete
--

