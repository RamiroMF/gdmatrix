-- Grant reference privileges on "<SCHEMA_GENESYS5>".Tables

-- Replace <SCHEMA_GENESYS5> for the Genesys schema name.
-- Replace <SCHEMA_MATRIX> for the Matrix schema name.
-- Needs GRANT privileges on <SCHEMA_GENESYS5>
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."NCL_CARRER" TO "<SCHEMA_MATRIX>";
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."NCL_DOMICILI" TO "<SCHEMA_MATRIX>";
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."NCL_MUNICIPI" TO "<SCHEMA_MATRIX>";
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."NCL_PAIS" TO "<SCHEMA_MATRIX>";
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."NCL_PROVINCIA" TO "<SCHEMA_MATRIX>";
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."NCL_PERSONA" TO "<SCHEMA_MATRIX>";
GRANT REFERENCES ON "<SCHEMA_GENESYS5>"."ORG_USUARI" TO "<SCHEMA_MATRIX>";

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_PERSONA
(
  PERSCOD NUMBER(8, 0),
  PAISCOD NUMBER(3, 0),
  PROVCOD NUMBER(2, 0),
  MUNICOD NUMBER(3, 0),
  PERSNOM VARCHAR2(70),
  PERSCOG1 VARCHAR2(25),
  PERSCOG2 VARCHAR2(25),
  PERSPAR1 CHAR(6),
  PERSPAR2 CHAR(6),
  NIFNUM CHAR(8),
  NIFDC CHAR(1),
  NIFSW CHAR(1),
  PERSDCONNIF CHAR(8),
  PERSDCANNIF CHAR(8),
  PERSNACIONA NUMBER(3, 0),
  PERSPASSPORT CHAR(20),
  PERSNDATA CHAR(8),
  PERSPARE CHAR(20),
  PERSMARE CHAR(20),
  PERSSEXE CHAR(1),
  PERSSW CHAR(1),
  IDIOCOD CHAR(1),
  BAIXASW CHAR(1),
  PERSVNUM NUMBER(2, 0),
  STDAPLADD CHAR(5),
  STDAPLMOD CHAR(5),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  CONTVNUM NUMBER(4, 0),
  NIFNUMP CHAR(10),
  NIFORIG CHAR(10),
  PERSCODOLD VARCHAR2(30)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_TELECO
(
  PERSCOD NUMBER(8, 0),
  NUMORDRE NUMBER(2, 0),
  TIPCONTACTE CHAR(4),
  CONTACTE VARCHAR2(50),
  OBSERVACIONS VARCHAR2(50),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_PERSDOM
(
  PERSCOD NUMBER(8, 0),
  DOMCOD NUMBER(8, 0),
  PERSND NUMBER(6, 0),
  STDAPLADD CHAR(5),
  STDAPLMOD CHAR(5),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  BAIXASW CHAR(1)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_DOMICILI
(
  PROVCOD NUMBER(2, 0),
  MUNICOD NUMBER(3, 0),
  DOMCOD NUMBER(8, 0) NOT NULL,
  CARCOD NUMBER(5, 0),
  PSEUDOCOD NUMBER(5, 0),
  GISCOD CHAR(16),
  DOMNUM CHAR(4),
  DOMBIS CHAR(1),
  DOMNUM2 CHAR(4),
  DOMBIS2 CHAR(1),
  DOMBLOC CHAR(2),
  DOMPTAL CHAR(2),
  DOMESC CHAR(2),
  DOMPIS CHAR(3),
  DOMPTA CHAR(4),
  DOMKM NUMBER(4, 0),
  DOMCP NUMBER(10, 0),
  DOMOBS VARCHAR2(256),
  DOMTIP CHAR(4),
  BAIXASW CHAR(1),
  STDAPLADD CHAR(5),
  STDAPLMOD CHAR(5),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  PAISCOD NUMBER(3, 0),
  DOMHM NUMBER(2, 0),
  DOMTLOC CHAR(1),
  APCORREUS NUMBER(6, 0)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_CARRER
(
  PAISCOD NUMBER(3, 0) NOT NULL,
  PROVCOD NUMBER(2, 0) NOT NULL,
  MUNICOD NUMBER(3, 0) NOT NULL,
  CARCOD NUMBER(5, 0) NOT NULL,
  CARSIG CHAR(5),
  CARPAR CHAR(6),
  CARDESC VARCHAR2(50) NOT NULL,
  CARDESC2 CHAR(25),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  BAIXASW CHAR(1),
  ORGCOD CHAR(4),
  ORGDATA CHAR(8),
  ORGOBS VARCHAR2(1024),
  PLACA VARCHAR2(255),
  OBSERVACIONS VARCHAR2(2047)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_MUNICIPI
(
  PAISCOD NUMBER(3, 0) NOT NULL,
  PROVCOD NUMBER(2, 0) NOT NULL,
  MUNICOD NUMBER(3, 0) NOT NULL,
  MUNIDESC VARCHAR2(50) NOT NULL,
  MUNIDESC2 CHAR(25),
  MUNIDC CHAR(1),
  MUNIVNUM NUMBER(4, 0),
  ILLAVNUM NUMBER(6, 0),
  COMCOD NUMBER(2, 0),
  TRAMVNUM NUMBER(6, 0),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  BAIXASW CHAR(1),
  MUNICPS VARCHAR2(400)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_PROVINCIA
(
  PAISCOD NUMBER(3, 0) NOT NULL,
  PROVCOD NUMBER(2, 0) NOT NULL,
  PROVDESC VARCHAR2(50) NOT NULL,
  PROVVNUM NUMBER(3, 0),
  AUTOCOD NUMBER(2, 0),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  BAIXASW CHAR(1)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_PAIS
(
  PAISCOD NUMBER(3, 0) NOT NULL,
  PAISDESC VARCHAR2(50) NOT NULL,
  PAISVNUM NUMBER(3, 0),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6),
  VALDATA CHAR(8),
  BAIXASW CHAR(1)
);

CREATE TABLE "<SCHEMA_GENESYS5>".ORG_USUARI
(
  USRCOD CHAR(20) NOT NULL,
  PERSCOD NUMBER(8, 0) NOT NULL,
  USRDESC CHAR(20),
  USRPASS CHAR(8),
  BLOQUEJAT CHAR(1),
  USRIND CHAR(10),
  USRLOG CHAR(2),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDHGR CHAR(6),
  STDDMOD CHAR(8),
  STDHMOD CHAR(6),
  MODO CHAR(2)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_CLAU
(
  CLAUPREF CHAR(8) NOT NULL,
  CLAUCOD CHAR(8) NOT NULL,
  CLAUORIGEN CHAR(4) NOT NULL,
  CLAUDESC VARCHAR2(50) NOT NULL,
  CLAUVALF VARCHAR2(255),
  CLAUVNUM NUMBER(10, 0),
  STDUGR CHAR(20),
  STDUMOD CHAR(20),
  STDDGR CHAR(8),
  STDDMOD CHAR(8),
  STDHGR CHAR(6),
  STDHMOD CHAR(6)
);

CREATE TABLE "<SCHEMA_GENESYS5>".NCL_TAULA
(
  TCCODI0 CHAR(4) NOT NULL ENABLE,
	TCCODI1 CHAR(10) NOT NULL ENABLE, 
	TCCODI2 CHAR(10), 
	TCDESC VARCHAR2(50) NOT NULL ENABLE, 
	TCDESC2 VARCHAR2(255), 
	TCQUAL CHAR(2), 
	TCVNUM NUMBER(10,0), 
	STDUGR CHAR(20), 
	STDUMOD CHAR(20), 
	STDDGR CHAR(8), 
	STDDMOD CHAR(8), 
	STDHGR CHAR(6), 
	STDHMOD CHAR(6), 
  CONSTRAINT PK_NCL_TAULA PRIMARY KEY ("TCCODI0", "TCCODI1")
);

CREATE TABLE "<SCHEMA_GENESYS5>"."NCL_REPRESENTANT"
(
	PERSCOD NUMBER(8,0) NOT NULL ENABLE,
	REPRCOD NUMBER(8,0) NOT NULL ENABLE,
	REPRNOTARI VARCHAR2(50),
	REPRPROTOCOL CHAR(10),
	REPRTIP CHAR(1),
	STDUGR CHAR(20),
	STDUMOD CHAR(20),
	STDDGR CHAR(8),
	STDDMOD CHAR(8),
	STDHGR CHAR(6),
	STDHMOD CHAR(6),
	VALDATA CHAR(8),
	CONSTRAINT "PK_NCL_REPRESENTANT" PRIMARY KEY ("PERSCOD", "REPRCOD")
);

CREATE INDEX "<SCHEMA_GENESYS5>".NCL_REPRESENTANT_FK1 ON "<SCHEMA_GENESYS5>".NCL_REPRESENTANT (PERSCOD);

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_DOMICILI
ADD CONSTRAINT PK_NCL_DOMICILI PRIMARY KEY
(
DOMCOD
)
 ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_CARRER
ADD CONSTRAINT PK_NCL_CARRER PRIMARY KEY
(
PAISCOD,
PROVCOD,
MUNICOD,
CARCOD
)
 ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_MUNICIPI
ADD CONSTRAINT PK_NCL_MUNICIPI PRIMARY KEY
(
PAISCOD,
PROVCOD,
MUNICOD
)
 ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_PROVINCIA
ADD CONSTRAINT PK_NCL_PROVINCIA PRIMARY KEY
(
PAISCOD,
PROVCOD
)
 ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_PAIS
ADD CONSTRAINT PK_NCL_PAIS PRIMARY KEY
(
PAISCOD
)
 ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_DOMICILI
ADD CONSTRAINT FK_NCL_DOMI_NCL_R_CAR_NCL_CARR FOREIGN KEY
(
PAISCOD,
PROVCOD,
MUNICOD,
CARCOD
)
REFERENCES "<SCHEMA_GENESYS5>".NCL_CARRER
(
PAISCOD,
PROVCOD,
MUNICOD,
CARCOD
) ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_CARRER
ADD CONSTRAINT FK_NCL_CARR_RELATION__NCL_MUNI FOREIGN KEY
(
PAISCOD,
PROVCOD,
MUNICOD
)
REFERENCES "<SCHEMA_GENESYS5>".NCL_MUNICIPI
(
PAISCOD,
PROVCOD,
MUNICOD
) ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_MUNICIPI
ADD CONSTRAINT FK_NCL_MUNI_RELATION__NCL_PROV FOREIGN KEY
(
PAISCOD,
PROVCOD
)
REFERENCES "<SCHEMA_GENESYS5>".NCL_PROVINCIA
(
PAISCOD,
PROVCOD
) ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_PROVINCIA
ADD CONSTRAINT FK_NCL_PROV_NCL_R_PAI_NCL_PAIS FOREIGN KEY
(
PAISCOD
)
REFERENCES "<SCHEMA_GENESYS5>".NCL_PAIS
(
PAISCOD
) ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".ORG_USUARI
ADD CONSTRAINT PK_ORG_USUARI PRIMARY KEY
(
USRCOD
)
 ENABLE
;

ALTER TABLE "<SCHEMA_GENESYS5>".NCL_CLAU
ADD CONSTRAINT PK_NCL_CLAU PRIMARY KEY
(
CLAUPREF,
CLAUCOD,
CLAUORIGEN
)
 ENABLE
;

CREATE INDEX "<SCHEMA_GENESYS5>".NCL_DOMICILI_FK1 ON "<SCHEMA_GENESYS5>".NCL_DOMICILI (PSEUDOCOD ASC, MUNICOD ASC, PROVCOD ASC, PAISCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".I_NCL_DOMICILI_ORD ON "<SCHEMA_GENESYS5>".NCL_DOMICILI (CARCOD ASC, DOMNUM ASC, DOMBIS ASC, DOMESC ASC, DOMPIS ASC, DOMPTA ASC, DOMBLOC ASC, DOMPTAL ASC, DOMKM ASC, DOMHM ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".NCL_DOMICILI_FK2 ON "<SCHEMA_GENESYS5>".NCL_DOMICILI (CARCOD ASC, MUNICOD ASC, PROVCOD ASC, PAISCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".NCL_CARRER_FK2 ON "<SCHEMA_GENESYS5>".NCL_CARRER (CARSIG ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".NCL_CARRER_FK1 ON "<SCHEMA_GENESYS5>".NCL_CARRER (MUNICOD ASC, PROVCOD ASC, PAISCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".I_NCL_CARRER_CARPAR ON "<SCHEMA_GENESYS5>".NCL_CARRER (CARPAR ASC, PROVCOD ASC, MUNICOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".I_NCL_MUNICIPI_PROV_MUNI_CARR ON "<SCHEMA_GENESYS5>".NCL_CARRER (CARCOD ASC, MUNICOD ASC, PROVCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".NCL_CARRER_FK3 ON "<SCHEMA_GENESYS5>".NCL_CARRER (ORGCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".I_NCL_MUNICIPI_MUNIDESC ON "<SCHEMA_GENESYS5>".NCL_MUNICIPI (MUNIDESC ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".NCL_MUNICIPI_FK1 ON "<SCHEMA_GENESYS5>".NCL_MUNICIPI (PAISCOD ASC, PROVCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".NCL_PROVINCIA_FK1 ON "<SCHEMA_GENESYS5>".NCL_PROVINCIA (PAISCOD ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".I_NCL_PROVINCIA_PROVDESC ON "<SCHEMA_GENESYS5>".NCL_PROVINCIA (PROVDESC ASC);
CREATE INDEX "<SCHEMA_GENESYS5>".I_ORG_USUARI_PERSCOD ON "<SCHEMA_GENESYS5>".ORG_USUARI (PERSCOD ASC);
