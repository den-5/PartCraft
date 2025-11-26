// Utility class for validating user data formats
export class ValidateUserData {
    static validateEmail(email: string): boolean {
        if (!email || email.trim().length === 0) return false;
        // Requires: local-part@domain.tld (TLD must be at least 2 characters)
        const regex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        return regex.test(email.trim());
    }

    static validateUsername(username: string): boolean {
        if (!username || username.trim().length === 0) return false;
        // 5-20 alphanumeric characters only
        const regex = /^[A-Za-z0-9]{5,20}$/;
        return regex.test(username.trim());
    }

    static validatePassword(password: string): boolean {
        if (!password || password.trim().length === 0) return false;
        // At least 8 chars, 1 digit, 1 lower, 1 upper, 1 special, no spaces
        const regex =
            /^(?=\S+$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$/;
        return regex.test(password.trim());
    }

    static validateUserData(
        email: string,
        username: string,
        password: string,
    ): boolean {
        return (
            this.validateEmail(email) &&
            this.validatePassword(password) &&
            this.validatePassword(password)
        );
    }
}
