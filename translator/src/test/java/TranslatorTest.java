import com.example.Translator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslatorTest {

    @Test
    void testTranslateSentence() {

        assertAll(
                () -> assertEquals("Majoritatea dintre voi iubiti merele rosii gustoase si frumoase.",
                        Translator.translateSentence("English", "Romanian",
                                "The majority of you love red tasty and beautiful apples.")
                ),
                () -> assertEquals("Ea este un sofer rapid!",
                        Translator.translateSentence("English", "Romanian", "She is a fast driver!")
                ));
    }

    @Test
    void testTranslateFile() {

        assertAll(() -> assertEquals("Toti dintre voi vreti merele gustoase si frumoase. Eu iubesc doar merele rosii! Marul este gustos.",
                        Translator.translateFile("English", "Romanian", "src/main/resources/files-to-translate/firstFileToConvert.txt")
                ),
                () -> assertEquals("Eu vreau zece mere gustoase. Merele sunt foarte bune pentru sanatatea noastra! Soferul conduce rapid. Ea este rapida.",
                        Translator.translateFile("English", "Romanian",
                                "src/main/resources/files-to-translate/secondFileToConvert.txt")
                ));
    }

}

