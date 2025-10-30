package com.partcraft.back.unit;

import com.partcraft.back.dto.CreateUserDTO;
import com.partcraft.back.exception.ValidationException;
import com.partcraft.back.util.VerifyUserDataFormat;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VerifyUserDataFormatTest {

    @Nested
    class VerifyCreateUserDTOTests {
        @Test
        void verifyCreateUserDTO_shouldReturnTrue_whenAllFieldsAreValid() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("validUser123");
            dto.setEmail("valid@example.com");
            dto.setPassword("ValidPass123!");

            boolean result = VerifyUserDataFormat.verifyCreateUserDTO(dto);

            assertTrue(result);
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenDTOIsNull() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("no user data provided");
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenEmailIsInvalid() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("validUser123");
            dto.setEmail("invalid-email");
            dto.setPassword("ValidPass123!");

            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(dto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenUsernameIsInvalid() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("ab"); // Too short
            dto.setEmail("valid@example.com");
            dto.setPassword("ValidPass123!");

            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(dto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenPasswordIsInvalid() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("validUser123");
            dto.setEmail("valid@example.com");
            dto.setPassword("weak");

            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(dto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenEmailIsNull() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("validUser123");
            dto.setEmail(null);
            dto.setPassword("ValidPass123!");

            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(dto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email is null or empty");
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenUsernameIsNull() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername(null);
            dto.setEmail("valid@example.com");
            dto.setPassword("ValidPass123!");

            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(dto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("username is null or empty");
        }

        @Test
        void verifyCreateUserDTO_shouldThrowException_whenPasswordIsNull() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("validUser123");
            dto.setEmail("valid@example.com");
            dto.setPassword(null);

            assertThatThrownBy(() -> VerifyUserDataFormat.verifyCreateUserDTO(dto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("password is null or empty");
        }

        @Test
        void verifyCreateUserDTO_shouldAcceptValidDataWithSpecialCharacters() {
            CreateUserDTO dto = new CreateUserDTO();
            dto.setUsername("User_Name_123");
            dto.setEmail("user.name+tag@example.co.uk");
            dto.setPassword("Complex@Pass123");

            boolean result = VerifyUserDataFormat.verifyCreateUserDTO(dto);

            assertTrue(result);
        }
    }

    @Nested
    class VerifyEmailFormatTests {
        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailIsValid() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test@example.com");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailHasSubdomain() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test@mail.example.com");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailHasPlus() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test+tag@example.com");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailHasDots() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test.user@example.com");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailHasNumbers() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test123@example456.com");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailHasLongTLD() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test@example.information");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldReturnTrue_whenEmailHasHyphenInDomain() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("test@my-domain.com");

            assertTrue(result);
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailIsNull() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email is null or empty");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailIsEmpty() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat(""))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email is null or empty");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailIsWhitespace() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("   "))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email is null or empty");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailMissingAt() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("testexample.com"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailMissingDomain() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("test@"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailMissingLocalPart() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("@example.com"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailMissingTLD() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("test@example"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailHasSpaces() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("test @example.com"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenEmailHasMultipleAt() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("test@@example.com"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldThrowException_whenTLDIsTooShort() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyEmailFormat("test@example.c"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid email format");
        }

        @Test
        void verifyEmailFormat_shouldTrimWhitespace() {
            boolean result = VerifyUserDataFormat.verifyEmailFormat("  test@example.com  ");

            assertTrue(result);
        }
    }

    @Nested
    class VerifyUsernameFormatTests {
        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameIsValid() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("validUser123");

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameHasMinimumLength() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("abc");

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameHasMaximumLength() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("a1234567890123456789"); // exactly 20 chars

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameHasUnderscores() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("user_name_123");

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameIsOnlyLetters() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("username");

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameIsOnlyNumbers() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("123456");

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldReturnTrue_whenUsernameHasUpperCase() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("UserName123");

            assertTrue(result);
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameIsNull() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("username is null or empty");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameIsEmpty() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat(""))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("username is null or empty");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameIsWhitespace() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("   "))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("username is null or empty");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameTooShort() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("ab"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameTooLong() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("a123456789012345678901")) // 21 chars
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameHasSpaces() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("user name"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameHasSpecialCharacters() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("user@name"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameHasHyphen() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("user-name"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyUsernameFormat_shouldThrowException_whenUsernameHasDot() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyUsernameFormat("user.name"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid username format");
        }

        @Test
        void verifyUsernameFormat_shouldTrimWhitespace() {
            boolean result = VerifyUserDataFormat.verifyUsernameFormat("  username  ");

            assertTrue(result);
        }
    }

    @Nested
    class VerifyPasswordFormatTests {
        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordIsValid() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("ValidPass123!");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordHasMinimumLength() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("Abcd123!");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordHasMultipleSpecialChars() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("Pass@Word#123!");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordIsLong() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("VeryLongPassword123!@#");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordHasDollarSign() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("Password123$");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordHasPercent() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("Password123%");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldReturnTrue_whenPasswordHasAmpersand() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("Password123&");

            assertTrue(result);
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordIsNull() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("password is null or empty");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordIsEmpty() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat(""))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("password is null or empty");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordIsWhitespace() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("   "))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("password is null or empty");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordTooShort() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("Abc123!"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordMissingUpperCase() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("password123!"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordMissingLowerCase() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("PASSWORD123!"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordMissingNumber() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("Password!"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordMissingSpecialChar() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("Password123"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordHasSpaces() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("Pass word123!"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordOnlyLowerCase() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("password"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordOnlyUpperCase() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("PASSWORD"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldThrowException_whenPasswordOnlyNumbers() {
            assertThatThrownBy(() -> VerifyUserDataFormat.verifyPasswordFormat("12345678"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("invalid password format");
        }

        @Test
        void verifyPasswordFormat_shouldTrimWhitespace() {
            boolean result = VerifyUserDataFormat.verifyPasswordFormat("  ValidPass123!  ");

            assertTrue(result);
        }
    }
}
