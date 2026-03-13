package no.nav.foreldrepenger.los.struktur;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tester at alle migreringer følger standarder for navn og god praksis.
 */
@ExtendWith(JpaExtension.class)
class SjekkDbStrukturTest extends EntityManagerAwareTest {

    @Test
    void sjekk_at_alle_tabeller_er_dokumentert() {
        var sql = """
            SELECT t.table_name
              FROM information_schema.tables t
              LEFT JOIN pg_catalog.pg_class c ON c.relname = t.table_name
              LEFT JOIN pg_catalog.pg_description d ON d.objoid = c.oid AND d.objsubid = 0
             WHERE t.table_schema = current_schema()
               AND t.table_type = 'BASE TABLE'
               AND (d.description IS NULL OR d.description IN ('', 'MISSING COLUMN COMMENT'))
               AND upper(t.table_name) NOT LIKE '%SCHEMA_%'
               AND upper(t.table_name) NOT LIKE 'HT_%'
            """;
        var query = getEntityManager().createNativeQuery(sql, String.class);
        var avvik = query.getResultStream().toList();
        assertThat(avvik).isEmpty();
    }

    @Test
    void sjekk_at_alle_relevant_kolonner_er_dokumentert() {
        var sql = """
            SELECT t.table_name || '.' || t.column_name
              FROM information_schema.columns t
              LEFT JOIN pg_catalog.pg_class c ON c.relname = t.table_name
              LEFT JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attname = t.column_name
              LEFT JOIN pg_catalog.pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum
             WHERE t.table_schema = current_schema()
               AND (d.description IS NULL OR d.description = '')
               AND upper(t.table_name) NOT LIKE '%SCHEMA_%'
               AND upper(t.table_name) NOT LIKE 'HT_%'
               AND t.column_name NOT IN (
                 SELECT kcu.column_name
                   FROM information_schema.key_column_usage kcu
                   JOIN information_schema.table_constraints tc
                     ON tc.constraint_name = kcu.constraint_name
                    AND tc.table_schema = kcu.table_schema
                  WHERE tc.constraint_type IN ('PRIMARY KEY', 'FOREIGN KEY')
                    AND kcu.table_name = t.table_name
                    AND kcu.table_schema = t.table_schema
               )
               AND upper(t.column_name) NOT IN ('OPPRETTET_TID','ENDRET_TID','OPPRETTET_AV','ENDRET_AV','VERSJON','BESKRIVELSE','NAVN','FOM','TOM','AKTIV')
             ORDER BY t.table_name, t.column_name
            """;

        var query = getEntityManager().createNativeQuery(sql, String.class);
        var avvik = query.getResultStream().map(row -> "\n" + row).toList();

        var hjelpetekst = """
            Du har nylig lagt til en ny tabell eller kolonne som ikke er dokumentert ihht. gjeldende regler for dokumentasjon.

            Vennligst gå over SQL-skriptene og dokumenter tabellene på korrekt måte.
            """;

        assertThat(avvik).withFailMessage("Mangler dokumentasjon for %s kolonner. %s\n\n%s", avvik.size(), avvik, hjelpetekst).isEmpty();
    }

    @Test
    void sjekk_at_alle_FK_kolonner_har_fornuftig_indekser() {
        var sql = """
            SELECT
                tc.table_name,
                tc.constraint_name,
                string_agg(kcu.column_name, ',' ORDER BY kcu.ordinal_position) AS columns
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON kcu.constraint_name = tc.constraint_name
             AND kcu.table_schema = tc.table_schema
            WHERE tc.constraint_type = 'FOREIGN KEY'
              AND tc.table_schema = current_schema()
              AND EXISTS (
                SELECT kcu2.column_name
                  FROM information_schema.key_column_usage kcu2
                 WHERE kcu2.constraint_name = tc.constraint_name
                   AND kcu2.table_schema = tc.table_schema
                EXCEPT
                SELECT a.attname
                  FROM pg_catalog.pg_index ix
                  JOIN pg_catalog.pg_class t ON t.oid = ix.indrelid
                  JOIN pg_catalog.pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey)
                  JOIN pg_catalog.pg_namespace n ON n.oid = t.relnamespace
                 WHERE t.relname = tc.table_name
                   AND n.nspname = tc.table_schema
              )
            GROUP BY tc.table_name, tc.constraint_name
            ORDER BY tc.table_name
            """;

        var query = getEntityManager().createNativeQuery(sql, Object[].class);
        List<Object[]> resultList = query.getResultList();

        var tekst = resultList.stream().map(row -> Arrays.stream(row).map(String.class::cast).collect(Collectors.joining(", "))).collect(Collectors.joining("\n"));
        var manglerIndeks = "Kolonner som inngår i Foreign Keys skal ha indekser (ikke KL_ kolonner).\nMangler indekser for %s foreign keys\n%s";
        assertThat(resultList).withFailMessage(manglerIndeks, resultList.size(), tekst).isEmpty();
    }

    @Test
    void skal_ha_primary_key_i_hver_tabell_som_begynner_med_PK() {
        var sql = """
            SELECT t.table_name
              FROM information_schema.tables t
             WHERE t.table_schema = current_schema()
               AND t.table_type = 'BASE TABLE'
               AND upper(t.table_name) NOT LIKE '%SCHEMA_%'
               AND upper(t.table_name) NOT LIKE 'HT_%'
               AND t.table_name NOT IN (
                 SELECT tc.table_name
                   FROM information_schema.table_constraints tc
                  WHERE tc.constraint_type = 'PRIMARY KEY'
                    AND tc.table_schema = current_schema()
                    AND tc.constraint_name LIKE 'pk_%'
               )
            """;

        var query = getEntityManager().createNativeQuery(sql, String.class);
        var avvik = query.getResultList();
        var tekst = avvik.stream().collect(Collectors.joining("\n"));
        var sz = avvik.size();
        var feilTekst = "Feil eller mangelende definisjon av primary key (skal hete 'pk_<tabell navn>'). Antall feil = %s \n\nTabell:\n%s";
        assertThat(avvik).withFailMessage(feilTekst, sz, tekst).isEmpty();
    }

    @Test
    void skal_ha_alle_foreign_keys_begynne_med_FK() {
        var sql = """
            SELECT tc.table_name, tc.constraint_name
              FROM information_schema.table_constraints tc
             WHERE tc.constraint_type = 'FOREIGN KEY'
               AND tc.table_schema = current_schema()
               AND tc.constraint_name NOT LIKE 'fk_%'
            """;

        var query = getEntityManager().createNativeQuery(sql, Object[].class);
        List<Object[]> rowList = query.getResultList();
        var tekst = rowList.stream().map(row -> Arrays.stream(row).map(String.class::cast).collect(Collectors.joining(", "))).collect(Collectors.joining("\n"));
        var feilTekst = "Feil eller mangelende definisjon av foreign key (skal hete 'fk_<tabell navn>_<løpenummer>'). Antall feil = %s\n\nTabell, Foreign Key\n%s";
        assertThat(rowList).withFailMessage(feilTekst, rowList.size(), tekst).isEmpty();
    }

    @Test
    void skal_ha_korrekt_index_navn() {
        var sql = """
            SELECT t.relname AS table_name, i.relname AS index_name, a.attname AS column_name
              FROM pg_catalog.pg_index ix
              JOIN pg_catalog.pg_class t ON t.oid = ix.indrelid
              JOIN pg_catalog.pg_class i ON i.oid = ix.indexrelid
              JOIN pg_catalog.pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey)
              JOIN pg_catalog.pg_namespace n ON n.oid = t.relnamespace
             WHERE n.nspname = current_schema()
               AND i.relname NOT LIKE 'pk_%'
               AND i.relname NOT LIKE 'idx_%'
               AND i.relname NOT LIKE 'uidx_%'
               AND upper(t.relname) NOT LIKE '%SCHEMA_%'
               AND NOT ix.indisprimary
            """;

        var query = getEntityManager().createNativeQuery(sql, Object[].class);
        List<Object[]> rowList = query.getResultList();
        var tekst = rowList.stream().map(row -> Arrays.stream(row).map(String.class::cast).collect(Collectors.joining(", "))).collect(Collectors.joining("\n"));

        var feilTekst = "Feil navngiving av index. Primary Keys skal ha prefiks pk_, andre unike indekser prefiks uidx_, vanlige indekser prefiks idx_. Antall feil = %s\n\nTabell, Index, Kolonne\n%s";
        assertThat(rowList).withFailMessage(feilTekst, rowList.size(), tekst).isEmpty();
    }

    @Test
    void skal_ha_samme_data_type_for_begge_sider_av_en_FK() {
        var sql = """
            SELECT
              tc.table_name                                AS table_name,
              kcu.column_name                              AS kol_a,
              col_a.data_type                              AS kol_a_data_type,
              col_a.character_maximum_length::text         AS kol_a_char_length,
              col_b.column_name                            AS kol_b,
              col_b.data_type                              AS kol_b_data_type,
              col_b.character_maximum_length::text         AS kol_b_char_length
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON kcu.constraint_name = tc.constraint_name AND kcu.table_schema = tc.table_schema
            JOIN information_schema.referential_constraints rc
              ON rc.constraint_name = tc.constraint_name AND rc.constraint_schema = tc.table_schema
            JOIN information_schema.key_column_usage kcu_ref
              ON kcu_ref.constraint_name = rc.unique_constraint_name
             AND kcu_ref.ordinal_position = kcu.ordinal_position
            JOIN information_schema.columns col_a
              ON col_a.table_name = kcu.table_name AND col_a.column_name = kcu.column_name AND col_a.table_schema = tc.table_schema
            JOIN information_schema.columns col_b
              ON col_b.table_name = kcu_ref.table_name AND col_b.column_name = kcu_ref.column_name AND col_b.table_schema = tc.table_schema
            WHERE tc.constraint_type = 'FOREIGN KEY'
              AND tc.table_schema = current_schema()
              AND (col_a.data_type <> col_b.data_type
                   OR coalesce(col_a.character_maximum_length, -1) <> coalesce(col_b.character_maximum_length, -1))
            ORDER BY tc.table_name, kcu.column_name
            """;

        var query = getEntityManager().createNativeQuery(sql, Object[].class);
        List<Object[]> rowList = query.getResultList();

        var tekst = rowList.stream()
            .map(row -> Arrays.stream(row).map(column -> column instanceof Character c ? c.toString() : (String) column).collect(Collectors.joining(", ")))
            .collect(Collectors.joining("\n"));

        var feilTekst = "Forskjellig datatype for kolonne på hver side av en FK. Kan være deklarert feil. Antall feil = %s%s%s";
        var cols = ".\n\nTABELL, KOL_A, KOL_A_DATA_TYPE, KOL_A_CHAR_LENGTH, KOL_B, KOL_B_DATA_TYPE, KOL_B_CHAR_LENGTH\n";
        assertThat(rowList).withFailMessage(feilTekst, rowList.size(), cols, tekst).isEmpty();
    }

    @Test
    void skal_ikke_bruke_FLOAT_eller_DOUBLE() {
        var sql = """
            SELECT table_name,
                   column_name,
                   data_type
              FROM information_schema.columns
             WHERE table_schema = current_schema()
               AND data_type IN ('real', 'double precision')
             ORDER BY table_name, column_name
            """;

        var query = getEntityManager().createNativeQuery(sql, Object[].class);
        List<Object[]> rowList = query.getResultList();

        var tekst = rowList.stream()
            .map(row -> Arrays.stream(row).map(String.class::cast).collect(Collectors.joining(", ")))
            .collect(Collectors.joining("\n"));

        var feilTekst = "Feil bruk av datatype, skal ikke ha FLOAT eller DOUBLE (bruk NUMERIC for alle desimaltall, spesielt der penger representeres). Antall feil = %s\n\nTabell, Kolonne, Datatype\n%s";
        assertThat(rowList).withFailMessage(feilTekst, rowList.size(), tekst).isEmpty();
    }

    @Test
    void sjekk_at_status_verdiene_i_prosess_task_tabellen_er_også_i_pollingSQL() {
        var sql = """
                SELECT (regexp_matches(
                           pg_get_expr(c.conbin, c.conrelid),
                           '''([A-Z_]+)''',
                           'g'
                       ))[1]
              FROM pg_catalog.pg_constraint c
              JOIN pg_catalog.pg_class t ON t.oid = c.conrelid
              JOIN pg_catalog.pg_namespace n ON n.oid = t.relnamespace
             WHERE t.relname = 'prosess_task'
               AND c.conname = 'chk_prosess_task_status'
               AND n.nspname = current_schema()
            """;

        @SuppressWarnings("unchecked")
        List<String> statuserFraConstraint = getEntityManager().createNativeQuery(sql).getResultList();
        var feilTekst = "Ved innføring av ny stauser må sqlen i TaskManager_pollTask.sql må oppdateres.";
        assertThat(statuserFraConstraint).withFailMessage(feilTekst)
            .containsExactlyInAnyOrder("KLAR", "FEILET", "VENTER_SVAR", "SUSPENDERT", "VETO", "FERDIG", "KJOERT");
    }
}
