--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 15.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: annonce; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.annonce (
    id integer NOT NULL,
    description text,
    datedebut date,
    datefin date,
    nomposte character varying(100) NOT NULL,
    iddepartement integer,
    idprofil integer,
    idtypeannonce integer,
    datepublication date
);


ALTER TABLE public.annonce OWNER TO postgres;

--
-- Name: annonce_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.annonce_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.annonce_id_seq OWNER TO postgres;

--
-- Name: annonce_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.annonce_id_seq OWNED BY public.annonce.id;


--
-- Name: candidat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.candidat (
    id integer NOT NULL,
    nom character varying(100) NOT NULL,
    prenom character varying(100) NOT NULL,
    datenaissance date,
    adresse character varying(200),
    cv text,
    idannonce integer,
    idstatut integer,
    idcomptecandidat integer
);


ALTER TABLE public.candidat OWNER TO postgres;

--
-- Name: candidat_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.candidat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.candidat_id_seq OWNER TO postgres;

--
-- Name: candidat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.candidat_id_seq OWNED BY public.candidat.id;


--
-- Name: candidatemploye; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.candidatemploye (
    id integer NOT NULL,
    idcandidat integer,
    idemploye integer
);


ALTER TABLE public.candidatemploye OWNER TO postgres;

--
-- Name: candidatemploye_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.candidatemploye_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.candidatemploye_id_seq OWNER TO postgres;

--
-- Name: candidatemploye_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.candidatemploye_id_seq OWNED BY public.candidatemploye.id;


--
-- Name: candidaturecritere; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.candidaturecritere (
    id integer NOT NULL,
    idcandidat integer,
    idannonce integer,
    idcritere integer,
    valeurdouble numeric(10,2),
    valeurvarchar character varying(200),
    valeurbool boolean,
    iddiplome integer
);


ALTER TABLE public.candidaturecritere OWNER TO postgres;

--
-- Name: candidaturecritere_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.candidaturecritere_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.candidaturecritere_id_seq OWNER TO postgres;

--
-- Name: candidaturecritere_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.candidaturecritere_id_seq OWNED BY public.candidaturecritere.id;


--
-- Name: comptecandidat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comptecandidat (
    id integer NOT NULL,
    email character varying(100) NOT NULL,
    motdepasse character varying(255) NOT NULL
);


ALTER TABLE public.comptecandidat OWNER TO postgres;

--
-- Name: comptecandidat_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.comptecandidat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comptecandidat_id_seq OWNER TO postgres;

--
-- Name: comptecandidat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.comptecandidat_id_seq OWNED BY public.comptecandidat.id;


--
-- Name: contrat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contrat (
    id integer NOT NULL,
    idemploye integer,
    datedebut date,
    nombremois integer,
    typecontrat character varying(50)
);


ALTER TABLE public.contrat OWNER TO postgres;

--
-- Name: contrat_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.contrat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.contrat_id_seq OWNER TO postgres;

--
-- Name: contrat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.contrat_id_seq OWNED BY public.contrat.id;


--
-- Name: critere; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.critere (
    id integer NOT NULL,
    nom character varying(100) NOT NULL,
    idtypechamp integer
);


ALTER TABLE public.critere OWNER TO postgres;

--
-- Name: critere_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.critere_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.critere_id_seq OWNER TO postgres;

--
-- Name: critere_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.critere_id_seq OWNED BY public.critere.id;


--
-- Name: critereprofil; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.critereprofil (
    id integer NOT NULL,
    idprofil integer,
    idcritere integer,
    valeurdouble numeric(10,2),
    valeurvarchar character varying(200),
    valeurbool boolean,
    estobligatoire boolean DEFAULT true
);


ALTER TABLE public.critereprofil OWNER TO postgres;

--
-- Name: critereprofil_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.critereprofil_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.critereprofil_id_seq OWNER TO postgres;

--
-- Name: critereprofil_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.critereprofil_id_seq OWNED BY public.critereprofil.id;


--
-- Name: departement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.departement (
    id integer NOT NULL,
    nom character varying(100) NOT NULL
);


ALTER TABLE public.departement OWNER TO postgres;

--
-- Name: departement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.departement_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.departement_id_seq OWNER TO postgres;

--
-- Name: departement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.departement_id_seq OWNED BY public.departement.id;


--
-- Name: diplome; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diplome (
    id integer NOT NULL,
    nom character varying(100) NOT NULL
);


ALTER TABLE public.diplome OWNER TO postgres;

--
-- Name: diplome_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.diplome_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.diplome_id_seq OWNER TO postgres;

--
-- Name: diplome_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.diplome_id_seq OWNED BY public.diplome.id;


--
-- Name: employe; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employe (
    id integer NOT NULL,
    nom character varying(100) NOT NULL,
    prenom character varying(100) NOT NULL,
    adresse character varying(200),
    iddept integer
);


ALTER TABLE public.employe OWNER TO postgres;

--
-- Name: employe_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.employe_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.employe_id_seq OWNER TO postgres;

--
-- Name: employe_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.employe_id_seq OWNED BY public.employe.id;


--
-- Name: entretien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.entretien (
    id integer NOT NULL,
    idcandidat integer,
    dateheure timestamp without time zone,
    idstatut integer,
    idresultat integer
);


ALTER TABLE public.entretien OWNER TO postgres;

--
-- Name: entretien_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.entretien_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.entretien_id_seq OWNER TO postgres;

--
-- Name: entretien_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.entretien_id_seq OWNED BY public.entretien.id;


--
-- Name: historiquecandidature; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.historiquecandidature (
    id integer NOT NULL,
    idcandidat integer,
    idstatut integer,
    datechangement timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.historiquecandidature OWNER TO postgres;

--
-- Name: historiquecandidature_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.historiquecandidature_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.historiquecandidature_id_seq OWNER TO postgres;

--
-- Name: historiquecandidature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.historiquecandidature_id_seq OWNED BY public.historiquecandidature.id;


--
-- Name: historiqueentretien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.historiqueentretien (
    id integer NOT NULL,
    identretien integer,
    idstatut integer,
    datechangement timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.historiqueentretien OWNER TO postgres;

--
-- Name: historiqueentretien_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.historiqueentretien_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.historiqueentretien_id_seq OWNER TO postgres;

--
-- Name: historiqueentretien_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.historiqueentretien_id_seq OWNED BY public.historiqueentretien.id;


--
-- Name: profil; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.profil (
    id integer NOT NULL,
    nom character varying(100) NOT NULL
);


ALTER TABLE public.profil OWNER TO postgres;

--
-- Name: profil_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.profil_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profil_id_seq OWNER TO postgres;

--
-- Name: profil_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.profil_id_seq OWNED BY public.profil.id;


--
-- Name: profildiplome; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.profildiplome (
    id integer NOT NULL,
    idprofil integer NOT NULL,
    iddiplome integer NOT NULL
);


ALTER TABLE public.profildiplome OWNER TO postgres;

--
-- Name: profildiplome_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.profildiplome_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profildiplome_id_seq OWNER TO postgres;

--
-- Name: profildiplome_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.profildiplome_id_seq OWNED BY public.profildiplome.id;


--
-- Name: qcmchoix; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.qcmchoix (
    id integer NOT NULL,
    idquestion integer NOT NULL,
    texte character varying(500) NOT NULL,
    estcorrect boolean DEFAULT false
);


ALTER TABLE public.qcmchoix OWNER TO postgres;

--
-- Name: qcmchoix_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.qcmchoix_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.qcmchoix_id_seq OWNER TO postgres;

--
-- Name: qcmchoix_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.qcmchoix_id_seq OWNED BY public.qcmchoix.id;


--
-- Name: qcmquestion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.qcmquestion (
    id integer NOT NULL,
    idtest integer NOT NULL,
    numero integer NOT NULL,
    question text NOT NULL,
    points integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.qcmquestion OWNER TO postgres;

--
-- Name: qcmquestion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.qcmquestion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.qcmquestion_id_seq OWNER TO postgres;

--
-- Name: qcmquestion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.qcmquestion_id_seq OWNED BY public.qcmquestion.id;


--
-- Name: qcmreponse; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.qcmreponse (
    id integer NOT NULL,
    idcandidat integer NOT NULL,
    idtest integer NOT NULL,
    idquestion integer NOT NULL,
    idchoix integer,
    pointsobtenus integer DEFAULT 0,
    datereponse date
);


ALTER TABLE public.qcmreponse OWNER TO postgres;

--
-- Name: qcmreponse_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.qcmreponse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.qcmreponse_id_seq OWNER TO postgres;

--
-- Name: qcmreponse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.qcmreponse_id_seq OWNED BY public.qcmreponse.id;


--
-- Name: qcmtest; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.qcmtest (
    id integer NOT NULL,
    nom character varying(150),
    idprofil integer
);


ALTER TABLE public.qcmtest OWNER TO postgres;

--
-- Name: qcmtest_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.qcmtest_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.qcmtest_id_seq OWNER TO postgres;

--
-- Name: qcmtest_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.qcmtest_id_seq OWNED BY public.qcmtest.id;


--
-- Name: resultat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.resultat (
    id integer NOT NULL,
    note integer,
    appreciation character varying(200)
);


ALTER TABLE public.resultat OWNER TO postgres;

--
-- Name: resultat_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.resultat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.resultat_id_seq OWNER TO postgres;

--
-- Name: resultat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.resultat_id_seq OWNED BY public.resultat.id;


--
-- Name: statutcandidat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.statutcandidat (
    id integer NOT NULL,
    nom character varying(50) NOT NULL
);


ALTER TABLE public.statutcandidat OWNER TO postgres;

--
-- Name: statutcandidat_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.statutcandidat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.statutcandidat_id_seq OWNER TO postgres;

--
-- Name: statutcandidat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.statutcandidat_id_seq OWNED BY public.statutcandidat.id;


--
-- Name: statutentretien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.statutentretien (
    id integer NOT NULL,
    nom character varying(50) NOT NULL
);


ALTER TABLE public.statutentretien OWNER TO postgres;

--
-- Name: statutentretien_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.statutentretien_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.statutentretien_id_seq OWNER TO postgres;

--
-- Name: statutentretien_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.statutentretien_id_seq OWNED BY public.statutentretien.id;


--
-- Name: testannonce; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.testannonce (
    id integer NOT NULL,
    idtest integer NOT NULL,
    idannonce integer NOT NULL
);


ALTER TABLE public.testannonce OWNER TO postgres;

--
-- Name: testannonce_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.testannonce_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.testannonce_id_seq OWNER TO postgres;

--
-- Name: testannonce_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.testannonce_id_seq OWNED BY public.testannonce.id;


--
-- Name: typeannonce; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.typeannonce (
    id integer NOT NULL,
    libelle character varying(50) NOT NULL
);


ALTER TABLE public.typeannonce OWNER TO postgres;

--
-- Name: typeannonce_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.typeannonce_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.typeannonce_id_seq OWNER TO postgres;

--
-- Name: typeannonce_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.typeannonce_id_seq OWNED BY public.typeannonce.id;


--
-- Name: typechamp; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.typechamp (
    id integer NOT NULL,
    libelle character varying(50) NOT NULL
);


ALTER TABLE public.typechamp OWNER TO postgres;

--
-- Name: typechamp_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.typechamp_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.typechamp_id_seq OWNER TO postgres;

--
-- Name: typechamp_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.typechamp_id_seq OWNED BY public.typechamp.id;


--
-- Name: utilisateurs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utilisateurs (
    id integer NOT NULL,
    email character varying(150) NOT NULL,
    motdepasse character varying(200) NOT NULL,
    idemploye integer
);


ALTER TABLE public.utilisateurs OWNER TO postgres;

--
-- Name: utilisateurs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.utilisateurs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.utilisateurs_id_seq OWNER TO postgres;

--
-- Name: utilisateurs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utilisateurs_id_seq OWNED BY public.utilisateurs.id;


--
-- Name: annonce id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annonce ALTER COLUMN id SET DEFAULT nextval('public.annonce_id_seq'::regclass);


--
-- Name: candidat id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidat ALTER COLUMN id SET DEFAULT nextval('public.candidat_id_seq'::regclass);


--
-- Name: candidatemploye id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidatemploye ALTER COLUMN id SET DEFAULT nextval('public.candidatemploye_id_seq'::regclass);


--
-- Name: candidaturecritere id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidaturecritere ALTER COLUMN id SET DEFAULT nextval('public.candidaturecritere_id_seq'::regclass);


--
-- Name: comptecandidat id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comptecandidat ALTER COLUMN id SET DEFAULT nextval('public.comptecandidat_id_seq'::regclass);


--
-- Name: contrat id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contrat ALTER COLUMN id SET DEFAULT nextval('public.contrat_id_seq'::regclass);


--
-- Name: critere id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critere ALTER COLUMN id SET DEFAULT nextval('public.critere_id_seq'::regclass);


--
-- Name: critereprofil id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critereprofil ALTER COLUMN id SET DEFAULT nextval('public.critereprofil_id_seq'::regclass);


--
-- Name: departement id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departement ALTER COLUMN id SET DEFAULT nextval('public.departement_id_seq'::regclass);


--
-- Name: diplome id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diplome ALTER COLUMN id SET DEFAULT nextval('public.diplome_id_seq'::regclass);


--
-- Name: employe id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employe ALTER COLUMN id SET DEFAULT nextval('public.employe_id_seq'::regclass);


--
-- Name: entretien id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entretien ALTER COLUMN id SET DEFAULT nextval('public.entretien_id_seq'::regclass);


--
-- Name: historiquecandidature id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiquecandidature ALTER COLUMN id SET DEFAULT nextval('public.historiquecandidature_id_seq'::regclass);


--
-- Name: historiqueentretien id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiqueentretien ALTER COLUMN id SET DEFAULT nextval('public.historiqueentretien_id_seq'::regclass);


--
-- Name: profil id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profil ALTER COLUMN id SET DEFAULT nextval('public.profil_id_seq'::regclass);


--
-- Name: profildiplome id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profildiplome ALTER COLUMN id SET DEFAULT nextval('public.profildiplome_id_seq'::regclass);


--
-- Name: qcmchoix id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmchoix ALTER COLUMN id SET DEFAULT nextval('public.qcmchoix_id_seq'::regclass);


--
-- Name: qcmquestion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmquestion ALTER COLUMN id SET DEFAULT nextval('public.qcmquestion_id_seq'::regclass);


--
-- Name: qcmreponse id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmreponse ALTER COLUMN id SET DEFAULT nextval('public.qcmreponse_id_seq'::regclass);


--
-- Name: qcmtest id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmtest ALTER COLUMN id SET DEFAULT nextval('public.qcmtest_id_seq'::regclass);


--
-- Name: resultat id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resultat ALTER COLUMN id SET DEFAULT nextval('public.resultat_id_seq'::regclass);


--
-- Name: statutcandidat id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statutcandidat ALTER COLUMN id SET DEFAULT nextval('public.statutcandidat_id_seq'::regclass);


--
-- Name: statutentretien id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statutentretien ALTER COLUMN id SET DEFAULT nextval('public.statutentretien_id_seq'::regclass);


--
-- Name: testannonce id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.testannonce ALTER COLUMN id SET DEFAULT nextval('public.testannonce_id_seq'::regclass);


--
-- Name: typeannonce id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.typeannonce ALTER COLUMN id SET DEFAULT nextval('public.typeannonce_id_seq'::regclass);


--
-- Name: typechamp id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.typechamp ALTER COLUMN id SET DEFAULT nextval('public.typechamp_id_seq'::regclass);


--
-- Name: utilisateurs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateurs ALTER COLUMN id SET DEFAULT nextval('public.utilisateurs_id_seq'::regclass);


--
-- Data for Name: annonce; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.annonce (id, description, datedebut, datefin, nomposte, iddepartement, idprofil, idtypeannonce, datepublication) FROM stdin;
2	Besoin urgent d’un comptable confirmé	2025-08-15	2025-09-15	Comptablee	3	2	1	\N
1	Recrutement développeur Java junior	2025-09-01	2025-09-30	Développeur Kotlin	1	1	1	2025-09-18
5	CDI hoa	2025-09-18	\N	Développeur Java	1	2	2	2025-09-18
3	Annonce pour le profil Développeur Java	2025-09-17	2025-10-26	Développeur Java	1	2	1	\N
\.


--
-- Data for Name: candidat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.candidat (id, nom, prenom, datenaissance, adresse, cv, idannonce, idstatut, idcomptecandidat) FROM stdin;
1	Rakoto	Hery	1995-04-12	Antananarivo	Expérience Java 2 ans	1	1	\N
2	Rasoanaivo	Lova	1993-06-25	Fianarantsoa	Comptable senior	2	2	\N
3	Randrianarisoa	Tiana	1998-01-10	Toamasina	Stage en RH	2	4	\N
4	chc	vh	2025-09-18	gvh	khbj	3	\N	\N
5	Dil	Dil	2025-09-18	dskjb	dnj	3	\N	1
6	Dil	Dil	2025-09-19	123	bscjq	1	\N	2
7	Dil	Dil	2025-09-19	123	bschj	3	1	2
8	kn,dslkqsdn	ndskjds	2025-09-19	qsdbhj	bjds	3	\N	2
9	; q;nh	dnbvgh	2025-09-19	dnbs	sq;qdjb	1	\N	2
\.


--
-- Data for Name: candidatemploye; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.candidatemploye (id, idcandidat, idemploye) FROM stdin;
1	1	1
2	2	2
\.


--
-- Data for Name: candidaturecritere; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.candidaturecritere (id, idcandidat, idannonce, idcritere, valeurdouble, valeurvarchar, valeurbool, iddiplome) FROM stdin;
2	2	2	2	\N	Licence Comptabilité	\N	\N
3	3	2	2	\N	Stage RH	\N	\N
1	1	1	1	2.00	\N	\N	1
4	4	3	2	15.00	\N	\N	\N
5	4	3	1	\N	2	\N	\N
6	4	3	3	\N	oui	\N	\N
7	5	3	2	5.00	\N	\N	\N
8	5	3	1	\N	3	\N	\N
9	5	3	3	\N	oui	\N	\N
10	6	1	2	15.00	\N	\N	\N
11	6	1	1	\N	2	\N	\N
12	6	1	3	\N	oui	\N	\N
13	7	3	2	2.00	\N	\N	\N
14	7	3	1	\N	2	\N	\N
15	7	3	3	\N	oui	\N	\N
16	8	3	2	1.00	\N	\N	\N
17	8	3	1	\N	5	\N	\N
18	8	3	3	\N	oui	\N	\N
19	9	1	2	12.00	\N	\N	\N
20	9	1	1	\N	1	\N	\N
21	9	1	3	\N	oui	\N	\N
\.


--
-- Data for Name: comptecandidat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.comptecandidat (id, email, motdepasse) FROM stdin;
1	candidat@gmail.com	candidat
2	di@gmail.com	pass123
\.


--
-- Data for Name: contrat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contrat (id, idemploye, datedebut, nombremois, typecontrat) FROM stdin;
1	1	2025-10-01	12	CDI
2	2	2025-09-01	6	CDD
\.


--
-- Data for Name: critere; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.critere (id, nom, idtypechamp) FROM stdin;
2	Diplôme requis	2
4	Langue étrangère	4
5	Full Stack	5
3	Compétence technique	3
1	Expérience (années)	1
\.


--
-- Data for Name: critereprofil; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.critereprofil (id, idprofil, idcritere, valeurdouble, valeurvarchar, valeurbool, estobligatoire) FROM stdin;
5	3	2	\N	Master RH	\N	f
6	4	3	\N	Réseaux sociaux	\N	t
11	2	2	\N	\N	\N	t
1	1	1	2.00	\N	\N	t
3	1	3	\N	Java	\N	t
2	1	2	\N	Licence Informatique	\N	t
\.


--
-- Data for Name: departement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.departement (id, nom) FROM stdin;
1	Informatique
2	Ressources Humaines
3	Comptabilité
4	Marketing
\.


--
-- Data for Name: diplome; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.diplome (id, nom) FROM stdin;
1	Licence Informatique
2	Master RH
3	Licence Comptabilité
\.


--
-- Data for Name: employe; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employe (id, nom, prenom, adresse, iddept) FROM stdin;
1	Rabe	Soa	Antananarivo	1
2	Rakotobe	Feno	Mahajanga	3
\.


--
-- Data for Name: entretien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.entretien (id, idcandidat, dateheure, idstatut, idresultat) FROM stdin;
1	1	2025-09-20 10:00:00	1	1
2	2	2025-09-18 14:00:00	3	2
\.


--
-- Data for Name: historiquecandidature; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.historiquecandidature (id, idcandidat, idstatut, datechangement) FROM stdin;
1	1	1	2025-09-05 08:00:00
2	2	2	2025-09-10 11:00:00
3	3	4	2025-09-12 13:00:00
\.


--
-- Data for Name: historiqueentretien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.historiqueentretien (id, identretien, idstatut, datechangement) FROM stdin;
1	1	1	2025-09-15 09:00:00
2	2	3	2025-09-18 15:30:00
\.


--
-- Data for Name: profil; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.profil (id, nom) FROM stdin;
2	Comptable
3	Chargé RH
4	Community Manager
1	Développeur Java
\.


--
-- Data for Name: profildiplome; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.profildiplome (id, idprofil, iddiplome) FROM stdin;
2	1	1
\.


--
-- Data for Name: qcmchoix; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.qcmchoix (id, idquestion, texte, estcorrect) FROM stdin;
1	1	Un modèle définissant objets et méthodes	t
2	1	Un fichier de configuration XML	f
3	2	Permet d’utiliser une méthode sans instance	t
4	2	Permet d’indiquer un commentaire	f
\.


--
-- Data for Name: qcmquestion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.qcmquestion (id, idtest, numero, question, points) FROM stdin;
1	1	1	Qu’est-ce qu’une classe en Java ?	2
2	1	2	À quoi sert le mot clé static ?	1
\.


--
-- Data for Name: qcmreponse; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.qcmreponse (id, idcandidat, idtest, idquestion, idchoix, pointsobtenus, datereponse) FROM stdin;
\.


--
-- Data for Name: qcmtest; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.qcmtest (id, nom, idprofil) FROM stdin;
2	Test Comptabilité générale	2
1	Test Java niveau junior	2
\.


--
-- Data for Name: resultat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.resultat (id, note, appreciation) FROM stdin;
1	3	Bon niveau
2	1	Insuffisant
\.


--
-- Data for Name: statutcandidat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.statutcandidat (id, nom) FROM stdin;
1	En attente
2	Entretien
3	Accepté
4	Refusé
\.


--
-- Data for Name: statutentretien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.statutentretien (id, nom) FROM stdin;
1	Planifié
2	En cours
3	Terminé
\.


--
-- Data for Name: testannonce; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.testannonce (id, idtest, idannonce) FROM stdin;
1	1	1
2	2	2
\.


--
-- Data for Name: typeannonce; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.typeannonce (id, libelle) FROM stdin;
1	CDD
2	CDI
\.


--
-- Data for Name: typechamp; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.typechamp (id, libelle) FROM stdin;
1	text
2	number
3	radio
4	select
5	checkbox
6	date
7	textarea
\.


--
-- Data for Name: utilisateurs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateurs (id, email, motdepasse, idemploye) FROM stdin;
1	soa.rabe@entreprise.mg	pass123	1
2	feno.rakotobe@entreprise.mg	pass456	2
3	dil@gmail.com	123456	1
\.


--
-- Name: annonce_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.annonce_id_seq', 5, true);


--
-- Name: candidat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.candidat_id_seq', 9, true);


--
-- Name: candidatemploye_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.candidatemploye_id_seq', 2, true);


--
-- Name: candidaturecritere_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.candidaturecritere_id_seq', 21, true);


--
-- Name: comptecandidat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.comptecandidat_id_seq', 2, true);


--
-- Name: contrat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.contrat_id_seq', 2, true);


--
-- Name: critere_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.critere_id_seq', 5, true);


--
-- Name: critereprofil_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.critereprofil_id_seq', 11, true);


--
-- Name: departement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.departement_id_seq', 4, true);


--
-- Name: diplome_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.diplome_id_seq', 3, true);


--
-- Name: employe_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.employe_id_seq', 2, true);


--
-- Name: entretien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.entretien_id_seq', 2, true);


--
-- Name: historiquecandidature_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.historiquecandidature_id_seq', 3, true);


--
-- Name: historiqueentretien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.historiqueentretien_id_seq', 2, true);


--
-- Name: profil_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.profil_id_seq', 4, true);


--
-- Name: profildiplome_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.profildiplome_id_seq', 2, true);


--
-- Name: qcmchoix_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.qcmchoix_id_seq', 4, true);


--
-- Name: qcmquestion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.qcmquestion_id_seq', 2, true);


--
-- Name: qcmreponse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.qcmreponse_id_seq', 8, true);


--
-- Name: qcmtest_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.qcmtest_id_seq', 2, true);


--
-- Name: resultat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.resultat_id_seq', 2, true);


--
-- Name: statutcandidat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.statutcandidat_id_seq', 4, true);


--
-- Name: statutentretien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.statutentretien_id_seq', 3, true);


--
-- Name: testannonce_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.testannonce_id_seq', 2, true);


--
-- Name: typeannonce_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.typeannonce_id_seq', 2, true);


--
-- Name: typechamp_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.typechamp_id_seq', 7, true);


--
-- Name: utilisateurs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utilisateurs_id_seq', 3, true);


--
-- Name: annonce annonce_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annonce
    ADD CONSTRAINT annonce_pkey PRIMARY KEY (id);


--
-- Name: candidat candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidat
    ADD CONSTRAINT candidat_pkey PRIMARY KEY (id);


--
-- Name: candidatemploye candidatemploye_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidatemploye
    ADD CONSTRAINT candidatemploye_pkey PRIMARY KEY (id);


--
-- Name: candidaturecritere candidaturecritere_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidaturecritere
    ADD CONSTRAINT candidaturecritere_pkey PRIMARY KEY (id);


--
-- Name: comptecandidat comptecandidat_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comptecandidat
    ADD CONSTRAINT comptecandidat_email_key UNIQUE (email);


--
-- Name: comptecandidat comptecandidat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comptecandidat
    ADD CONSTRAINT comptecandidat_pkey PRIMARY KEY (id);


--
-- Name: contrat contrat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contrat
    ADD CONSTRAINT contrat_pkey PRIMARY KEY (id);


--
-- Name: critere critere_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critere
    ADD CONSTRAINT critere_pkey PRIMARY KEY (id);


--
-- Name: critereprofil critereprofil_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critereprofil
    ADD CONSTRAINT critereprofil_pkey PRIMARY KEY (id);


--
-- Name: departement departement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.departement
    ADD CONSTRAINT departement_pkey PRIMARY KEY (id);


--
-- Name: diplome diplome_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diplome
    ADD CONSTRAINT diplome_pkey PRIMARY KEY (id);


--
-- Name: employe employe_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT employe_pkey PRIMARY KEY (id);


--
-- Name: entretien entretien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entretien
    ADD CONSTRAINT entretien_pkey PRIMARY KEY (id);


--
-- Name: historiquecandidature historiquecandidature_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiquecandidature
    ADD CONSTRAINT historiquecandidature_pkey PRIMARY KEY (id);


--
-- Name: historiqueentretien historiqueentretien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiqueentretien
    ADD CONSTRAINT historiqueentretien_pkey PRIMARY KEY (id);


--
-- Name: profil profil_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profil
    ADD CONSTRAINT profil_pkey PRIMARY KEY (id);


--
-- Name: profildiplome profildiplome_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profildiplome
    ADD CONSTRAINT profildiplome_pkey PRIMARY KEY (id);


--
-- Name: qcmchoix qcmchoix_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmchoix
    ADD CONSTRAINT qcmchoix_pkey PRIMARY KEY (id);


--
-- Name: qcmquestion qcmquestion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmquestion
    ADD CONSTRAINT qcmquestion_pkey PRIMARY KEY (id);


--
-- Name: qcmreponse qcmreponse_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmreponse
    ADD CONSTRAINT qcmreponse_pkey PRIMARY KEY (id);


--
-- Name: qcmtest qcmtest_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmtest
    ADD CONSTRAINT qcmtest_pkey PRIMARY KEY (id);


--
-- Name: resultat resultat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.resultat
    ADD CONSTRAINT resultat_pkey PRIMARY KEY (id);


--
-- Name: statutcandidat statutcandidat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statutcandidat
    ADD CONSTRAINT statutcandidat_pkey PRIMARY KEY (id);


--
-- Name: statutentretien statutentretien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statutentretien
    ADD CONSTRAINT statutentretien_pkey PRIMARY KEY (id);


--
-- Name: testannonce testannonce_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.testannonce
    ADD CONSTRAINT testannonce_pkey PRIMARY KEY (id);


--
-- Name: typeannonce typeannonce_libelle_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.typeannonce
    ADD CONSTRAINT typeannonce_libelle_key UNIQUE (libelle);


--
-- Name: typeannonce typeannonce_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.typeannonce
    ADD CONSTRAINT typeannonce_pkey PRIMARY KEY (id);


--
-- Name: typechamp typechamp_libelle_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.typechamp
    ADD CONSTRAINT typechamp_libelle_key UNIQUE (libelle);


--
-- Name: typechamp typechamp_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.typechamp
    ADD CONSTRAINT typechamp_pkey PRIMARY KEY (id);


--
-- Name: profildiplome ukckas5mm8lojl7lqmvt7xamdu5; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profildiplome
    ADD CONSTRAINT ukckas5mm8lojl7lqmvt7xamdu5 UNIQUE (idprofil, iddiplome);


--
-- Name: utilisateurs utilisateurs_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateurs
    ADD CONSTRAINT utilisateurs_email_key UNIQUE (email);


--
-- Name: utilisateurs utilisateurs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateurs
    ADD CONSTRAINT utilisateurs_pkey PRIMARY KEY (id);


--
-- Name: annonce annonce_iddepartement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annonce
    ADD CONSTRAINT annonce_iddepartement_fkey FOREIGN KEY (iddepartement) REFERENCES public.departement(id);


--
-- Name: annonce annonce_idprofil_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annonce
    ADD CONSTRAINT annonce_idprofil_fkey FOREIGN KEY (idprofil) REFERENCES public.profil(id);


--
-- Name: candidat candidat_idannonce_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidat
    ADD CONSTRAINT candidat_idannonce_fkey FOREIGN KEY (idannonce) REFERENCES public.annonce(id);


--
-- Name: candidat candidat_idstatut_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidat
    ADD CONSTRAINT candidat_idstatut_fkey FOREIGN KEY (idstatut) REFERENCES public.statutcandidat(id);


--
-- Name: candidatemploye candidatemploye_idcandidat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidatemploye
    ADD CONSTRAINT candidatemploye_idcandidat_fkey FOREIGN KEY (idcandidat) REFERENCES public.candidat(id);


--
-- Name: candidatemploye candidatemploye_idemploye_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidatemploye
    ADD CONSTRAINT candidatemploye_idemploye_fkey FOREIGN KEY (idemploye) REFERENCES public.employe(id);


--
-- Name: candidaturecritere candidaturecritere_idannonce_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidaturecritere
    ADD CONSTRAINT candidaturecritere_idannonce_fkey FOREIGN KEY (idannonce) REFERENCES public.annonce(id);


--
-- Name: candidaturecritere candidaturecritere_idcandidat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidaturecritere
    ADD CONSTRAINT candidaturecritere_idcandidat_fkey FOREIGN KEY (idcandidat) REFERENCES public.candidat(id);


--
-- Name: candidaturecritere candidaturecritere_idcritere_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidaturecritere
    ADD CONSTRAINT candidaturecritere_idcritere_fkey FOREIGN KEY (idcritere) REFERENCES public.critere(id);


--
-- Name: contrat contrat_idemploye_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contrat
    ADD CONSTRAINT contrat_idemploye_fkey FOREIGN KEY (idemploye) REFERENCES public.employe(id);


--
-- Name: critere critere_idtypechamp_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critere
    ADD CONSTRAINT critere_idtypechamp_fkey FOREIGN KEY (idtypechamp) REFERENCES public.typechamp(id);


--
-- Name: critereprofil critereprofil_idcritere_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critereprofil
    ADD CONSTRAINT critereprofil_idcritere_fkey FOREIGN KEY (idcritere) REFERENCES public.critere(id);


--
-- Name: critereprofil critereprofil_idprofil_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.critereprofil
    ADD CONSTRAINT critereprofil_idprofil_fkey FOREIGN KEY (idprofil) REFERENCES public.profil(id);


--
-- Name: employe employe_iddept_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT employe_iddept_fkey FOREIGN KEY (iddept) REFERENCES public.departement(id);


--
-- Name: entretien entretien_idcandidat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entretien
    ADD CONSTRAINT entretien_idcandidat_fkey FOREIGN KEY (idcandidat) REFERENCES public.candidat(id);


--
-- Name: entretien entretien_idresultat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entretien
    ADD CONSTRAINT entretien_idresultat_fkey FOREIGN KEY (idresultat) REFERENCES public.resultat(id);


--
-- Name: entretien entretien_idstatut_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.entretien
    ADD CONSTRAINT entretien_idstatut_fkey FOREIGN KEY (idstatut) REFERENCES public.statutentretien(id);


--
-- Name: annonce fk_annonce_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.annonce
    ADD CONSTRAINT fk_annonce_type FOREIGN KEY (idtypeannonce) REFERENCES public.typeannonce(id);


--
-- Name: candidat fk_candidat_comptecandidat; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidat
    ADD CONSTRAINT fk_candidat_comptecandidat FOREIGN KEY (idcomptecandidat) REFERENCES public.comptecandidat(id);


--
-- Name: candidaturecritere fk_candidaturecritere_diplome; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.candidaturecritere
    ADD CONSTRAINT fk_candidaturecritere_diplome FOREIGN KEY (iddiplome) REFERENCES public.diplome(id);


--
-- Name: historiquecandidature historiquecandidature_idcandidat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiquecandidature
    ADD CONSTRAINT historiquecandidature_idcandidat_fkey FOREIGN KEY (idcandidat) REFERENCES public.candidat(id);


--
-- Name: historiquecandidature historiquecandidature_idstatut_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiquecandidature
    ADD CONSTRAINT historiquecandidature_idstatut_fkey FOREIGN KEY (idstatut) REFERENCES public.statutcandidat(id);


--
-- Name: historiqueentretien historiqueentretien_identretien_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiqueentretien
    ADD CONSTRAINT historiqueentretien_identretien_fkey FOREIGN KEY (identretien) REFERENCES public.entretien(id);


--
-- Name: historiqueentretien historiqueentretien_idstatut_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historiqueentretien
    ADD CONSTRAINT historiqueentretien_idstatut_fkey FOREIGN KEY (idstatut) REFERENCES public.statutentretien(id);


--
-- Name: profildiplome profildiplome_iddiplome_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profildiplome
    ADD CONSTRAINT profildiplome_iddiplome_fkey FOREIGN KEY (iddiplome) REFERENCES public.diplome(id);


--
-- Name: profildiplome profildiplome_idprofil_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.profildiplome
    ADD CONSTRAINT profildiplome_idprofil_fkey FOREIGN KEY (idprofil) REFERENCES public.profil(id);


--
-- Name: qcmchoix qcmchoix_idquestion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmchoix
    ADD CONSTRAINT qcmchoix_idquestion_fkey FOREIGN KEY (idquestion) REFERENCES public.qcmquestion(id);


--
-- Name: qcmquestion qcmquestion_idtest_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmquestion
    ADD CONSTRAINT qcmquestion_idtest_fkey FOREIGN KEY (idtest) REFERENCES public.qcmtest(id);


--
-- Name: qcmreponse qcmreponse_idcandidat_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmreponse
    ADD CONSTRAINT qcmreponse_idcandidat_fkey FOREIGN KEY (idcandidat) REFERENCES public.candidat(id);


--
-- Name: qcmreponse qcmreponse_idchoix_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmreponse
    ADD CONSTRAINT qcmreponse_idchoix_fkey FOREIGN KEY (idchoix) REFERENCES public.qcmchoix(id);


--
-- Name: qcmreponse qcmreponse_idquestion_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmreponse
    ADD CONSTRAINT qcmreponse_idquestion_fkey FOREIGN KEY (idquestion) REFERENCES public.qcmquestion(id);


--
-- Name: qcmreponse qcmreponse_idtest_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmreponse
    ADD CONSTRAINT qcmreponse_idtest_fkey FOREIGN KEY (idtest) REFERENCES public.qcmtest(id);


--
-- Name: qcmtest qcmtest_idprofil_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.qcmtest
    ADD CONSTRAINT qcmtest_idprofil_fkey FOREIGN KEY (idprofil) REFERENCES public.profil(id);


--
-- Name: testannonce testannonce_idannonce_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.testannonce
    ADD CONSTRAINT testannonce_idannonce_fkey FOREIGN KEY (idannonce) REFERENCES public.annonce(id);


--
-- Name: testannonce testannonce_idtest_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.testannonce
    ADD CONSTRAINT testannonce_idtest_fkey FOREIGN KEY (idtest) REFERENCES public.qcmtest(id);


--
-- Name: utilisateurs utilisateurs_idemploye_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateurs
    ADD CONSTRAINT utilisateurs_idemploye_fkey FOREIGN KEY (idemploye) REFERENCES public.employe(id);


--
-- PostgreSQL database dump complete
--

