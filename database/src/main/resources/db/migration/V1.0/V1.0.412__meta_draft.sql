-- CREATE TABLE "meta_draft" -----------------------------------
CREATE TABLE "public"."meta_draft" ( 
	"id" BIGSERIAL NOT NULL,
	"type" Character Varying( 2044 ) NOT NULL,
	"value" Bytea NOT NULL,
	"name" Character Varying( 100 ),
	"created_at" Timestamp With Time Zone NOT NULL,
	"created_by" Character Varying( 100 ) NOT NULL,
	"updated_at" Timestamp With Time Zone,
	"updated_by" Character Varying( 100 ),
	"version" Integer,
	"active" Boolean DEFAULT false NOT NULL,
	CONSTRAINT "meta_draft_unique_id" UNIQUE( "id" ) );
 ;
-- -------------------------------------------------------------