/**
 * An interface for validating email addresses.
 *
 * This interface provides a contract for implementing different email validation strategies.
 * Implementations of this interface should define the logic for verifying whether a given
 * email address is valid.
 */
interface EmailValidator {
    /**
     * Checks if the provided email address is valid.
     *
     * @param email The email address to validate.
     * @return `true` if the email address is valid, `false` otherwise.
     */
    fun isValid(email: String): Boolean
}

/**
 * Default implementation of [EmailValidator] using Android's built-in email pattern matcher.
 *
 * This implementation leverages `android.util.Patterns.EMAIL_ADDRESS` to check whether an
 * email address matches the standard email format. It should be used in real application
 * environments where Android APIs are available.
 *
 * **Note:** This implementation should not be used in unit tests, as `Patterns.EMAIL_ADDRESS`
 * is an Android framework-dependent utility. For testing, a mocked or custom implementation
 * of `EmailValidator` should be used instead.
 */
object DefaultEmailValidator : EmailValidator {
    /**
     * Validates an email address using `Patterns.EMAIL_ADDRESS`.
     *
     * @param email The email address to validate.
     * @return `true` if the email format is correct, `false` otherwise.
     */
    override fun isValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
