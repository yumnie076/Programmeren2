package test.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.stage.Stage;
import main.java.Cursist;

public class CursistTest {

    @BeforeAll
    public static void setUp() {
        Platform.startup(() -> new Stage());
    }

    @Test
    public void testCursistConstructor() {
        String naam = "John Doe";
        String email = "john.doe@example.com";
        String geslacht = "Man";
        Date geboortedatum = Date.valueOf(LocalDate.of(1990, 1, 1));
        String adres = "123 Main St";
        String postcode = "1234AE";
        String woonplaats = "City";
        String land = "Country";

        Cursist cursist = new Cursist(naam, email, geslacht, geboortedatum, adres, postcode, woonplaats, land);

        assertEquals(naam, cursist.getNaam());
        assertEquals(email, cursist.getEmail());
        assertEquals(geslacht, cursist.getGeslacht());
        assertEquals(geboortedatum, cursist.getGeboortedatum());
        assertEquals(adres, cursist.getAdres());
        assertEquals(postcode, cursist.getPostcode());
        assertEquals(woonplaats, cursist.getWoonplaats());
        assertEquals(land, cursist.getLand());
    }

    @Test
    public void testFormatPostcode() {
        String inputPostcode = "1234aa";
        String expectedFormattedPostcode = "1234 aa";

        String formattedPostcode = Cursist.formatPostcode(inputPostcode);

        assertEquals(expectedFormattedPostcode, formattedPostcode);
    }

    @Test
    public void testFormatPostcodeWithSpaces() {
        String inputPostcode = "123456";
        String expectedFormattedPostcode = "1234 56";

        String formattedPostcode = Cursist.formatPostcode(inputPostcode);

        assertEquals(expectedFormattedPostcode, formattedPostcode);
    }

    @Test
    public void testFormatPostcodeWithNonNumericCharacters() {
        String inputPostcode = "12-3456";
        String expectedFormattedPostcode = "1234 56";

        String formattedPostcode = Cursist.formatPostcode(inputPostcode);

        assertEquals(expectedFormattedPostcode, formattedPostcode);
    }

    @Test
    void isValidEmail_ValidEmail_ReturnsTrue() {

        String validEmail = "test@example.com";

        boolean result = Cursist.isValidEmail(validEmail);

        assertTrue(result);
    }

    @Test
    void isValidEmail_InvalidEmail_ReturnsFalse() {

        String invalidEmail = "invalid.email";

        boolean result = Cursist.isValidEmail(invalidEmail);

        assertFalse(result);
    }

    @Test
    void isValidEmail_NullEmail_ReturnsFalse() {

        String nullEmail = null;

        boolean result = Cursist.isValidEmail(nullEmail);

        assertFalse(result);
    }

    @Test
    void isValidEmail_EmptyEmail_ReturnsFalse() {

        String emptyEmail = "";

        boolean result = Cursist.isValidEmail(emptyEmail);

        assertFalse(result);
    }
}