package de.potionlabs.enums

enum class Result(val code: Int) {
    /**
     * Success.
     */
    OK(1),

    /**
     * Generic failure.
     */
    FAIL(2),

    /**
     * Your Steam client doesn't have a connection to the back-end.
     */
    NO_CONNECTION(3),

    /**
     * Password/ticket is invalid.
     */
    INVALID_PASSWORD(5),

    /**
     * The user is logged in elsewhere.
     */
    LOGGED_IN_ELSEWHERE(6),

    /**
     * Protocol version is incorrect.
     */
    INVALID_PROTOCOL_VER(7),

    /**
     * A parameter is incorrect.
     */
    INVALID_PARAM(8),

    /**
     * File was not found.
     */
    FILE_NOT_FOUND(9),

    /**
     * Called method is busy - action not taken.
     */
    BUSY(10),

    /**
     * Called object was in an invalid state.
     */
    INVALID_STATE(11),

    /**
     * The name was invalid.
     */
    INVALID_NAME(12),

    /**
     * The email was invalid.
     */
    INVALID_EMAIL(13),

    /**
     * The name is not unique.
     */
    DUPLICATE_NAME(14),

    /**
     * Access is denied.
     */
    ACCESS_DENIED(15),

    /**
     * Operation timed out.
     */
    TIMEOUT(16),

    /**
     * The user is VAC2 banned.
     */
    BANNED(17),

    /**
     * Account not found.
     */
    ACCOUNT_NOT_FOUND(18),

    /**
     * The Steam ID was invalid.
     */
    INVALID_STEAM_ID(19),

    /**
     * The requested service is currently unavailable.
     */
    SERVICE_UNAVAILABLE(20),

    /**
     * The user is not logged on.
     */
    NOT_LOGGED_ON(21),

    /**
     * Request is pending, it may be in process or waiting on third party.
     */
    PENDING(22),

    /**
     * Encryption or Decryption failed.
     */
    ENCRYPTION_FAILURE(23),

    /**
     * Insufficient privilege.
     */
    INSUFFICIENT_PRIVILEGE(24),

    /**
     * Too much of a good thing.
     */
    LIMIT_EXCEEDED(25),

    /**
     * Access has been revoked (used for revoked guest passes.)*/
    REVOKED(26),

    /**
     * License/Guest pass the user is trying to access is expired.
     */
    EXPIRED(27),

    /**
     * Guest pass has already been redeemed by account, cannot be used again.
     */
    ALREADY_REDEEMED(28),

    /**
     * The request is a duplicate and the action has already occurred in the past, ignored this time.
     */
    DUPLICATE_REQUEST(29),

    /**
     * All the games in this guest pass redemption request are already owned by the user.
     */
    ALREADY_OWNED(30),

    /**
     * IP address not found.
     */
    IPNOT_FOUND(31),

    /**
     * Failed to write change to the data store.
     */
    PERSIST_FAILED(32),

    /**
     * Failed to acquire access lock for this operation.
     */
    LOCKING_FAILED(33),

    /**
     * The logon session has been replaced.
     */
    LOGON_SESSION_REPLACED(34),

    /**
     * Failed to connect.
     */
    CONNECT_FAILED(35),

    /**
     * The authentication handshake has failed.
     */
    HANDSHAKE_FAILED(36),

    /**
     * There has been a generic IO failure.
     */
    IOFAILURE(37),

    /**
     * The remote server has disconnected.
     */
    REMOTE_DISCONNECT(38),

    /**
     * Failed to find the shopping cart requested.
     */
    SHOPPING_CART_NOT_FOUND(39),

    /**
     * A user blocked the action.
     */
    BLOCKED(40),

    /**
     * The target is ignoring sender.
     */
    IGNORED(41),

    /**
     * Nothing matching the request found.
     */
    NO_MATCH(42),

    /**
     * The account is disabled.
     */
    ACCOUNT_DISABLED(43),

    /**
     * This service is not accepting content changes right now.
     */
    SERVICE_READ_ONLY(44),

    /**
     * Account doesn't have value, so this feature isn't available.
     */
    ACCOUNT_NOT_FEATURED(45),

    /**
     * Allowed to take this action, but only because requester is admin.
     */
    ADMINISTRATOR_OK(46),

    /**
     * A Version mismatch in content transmitted within the Steam protocol.
     */
    CONTENT_VERSION(47),

    /**
     * The current CM can't service the user making a request, user should try another.
     */
    TRY_ANOTHER_CM(48),

    /**
     * You are already logged in elsewhere, this cached credential login has failed.
     */
    PASSWORD_REQUIRED_TO_KICK_SESSION(49),

    /**
     * The user is logged in elsewhere. (Use Logged_in_elsewhere instead!)*/
    ALREADY_LOGGED_IN_ELSEWHERE(50),

    /**
     * Long running operation has suspended/paused. (eg. content download.)*/
    SUSPENDED(51),

    /**
     * Operation has been canceled, typically by user. (eg. a content download.)*/
    CANCELLED(52),

    /**
     * Operation canceled because data is ill formed or unrecoverable.
     */
    DATA_CORRUPTION(53),

    /**
     * Operation canceled - not enough disk space.
     */
    DISK_FULL(54),

    /**
     * The remote or IPC call has failed.
     */
    REMOTE_CALL_FAILED(55),

    /**
     * Password could not be verified as it's unset server side.
     */
    PASSWORD_UNSET(56),

    /**
     * External account (PSN, Facebook...) is not linked to a Steam account.
     */
    EXTERNAL_ACCOUNT_UNLINKED(57),

    /**
     * PSN ticket was invalid.
     */
    PSNTICKET_INVALID(58),

    /**
     * External account (PSN, Facebook...) is already linked to some other account, must explicitly request to replace/delete the link first.
     */
    EXTERNAL_ACCOUNT_ALREADY_LINKED(59),

    /**
     * The sync cannot resume due to a conflict between the local and remote files.
     */
    REMOTE_FILE_CONFLICT(60),

    /**
     * The requested new password is not allowed.
     */
    ILLEGAL_PASSWORD(61),

    /**
     * New value is the same as the old one. This is used for secret question and answer.
     */
    SAME_AS_PREVIOUS_VALUE(62),

    /**
     * Account login denied due to 2nd factor authentication failure.
     */
    ACCOUNT_LOGON_DENIED(63),

    /**
     * The requested new password is not legal.
     */
    CANNOT_USE_OLD_PASSWORD(64),

    /**
     * Account login denied due to auth code invalid.
     */
    INVALID_LOGIN_AUTH_CODE(65),

    /**
     * Account login denied due to 2nd factor auth failure - and no mail has been sent.
     */
    ACCOUNT_LOGON_DENIED_NO_MAIL(66),

    /**
     * The users hardware does not support Intel's Identity Protection Technology (IPT).
     */
    HARDWARE_NOT_CAPABLE_OF_IPT(67),

    /**
     * Intel's Identity Protection Technology (IPT) has failed to initialize.
     */
    IPTINIT_ERROR(68),

    /**
     * Operation failed due to parental control restrictions for current user.
     */
    PARENTAL_CONTROL_RESTRICTED(69),

    /**
     * Facebook query returned an error.
     */
    FACEBOOK_QUERY_ERROR(70),

    /**
     * Account login denied due to an expired auth code.
     */
    EXPIRED_LOGIN_AUTH_CODE(71),

    /**
     * The login failed due to an IP restriction.
     */
    IPLOGIN_RESTRICTION_FAILED(72),

    /**
     * The current users account is currently locked for use. This is likely due to a hijacking and pending ownership verification.
     */
    ACCOUNT_LOCKED_DOWN(73),

    /**
     * The logon failed because the accounts email is not verified.
     */
    ACCOUNT_LOGON_DENIED_VERIFIED_EMAIL_REQUIRED(74),

    /**
     * There is no URL matching the provided values.
     */
    NO_MATCHING_URL(75),

    /**
     * Bad Response due to a Parse failure, missing field, etc.
     */
    BAD_RESPONSE(76),

    /**
     * The user cannot complete the action until they re-enter their password.
     */
    REQUIRE_PASSWORD_RE_ENTRY(77),

    /**
     * The value entered is outside the acceptable range.
     */
    VALUE_OUT_OF_RANGE(78),

    /**
     * Something happened that we didn't expect to ever happen.
     */
    UNEXPECTED_ERROR(79),

    /**
     * The requested service has been configured to be unavailable.
     */
    DISABLED(80),

    /**
     * The files submitted to the CEG server are not valid.
     */
    INVALID_CEGSUBMISSION(81),

    /**
     * The device being used is not allowed to perform this action.
     */
    RESTRICTED_DEVICE(82),

    /**
     * The action could not be complete because it is region restricted.
     */
    REGION_LOCKED(83),

    /**
     * Temporary rate limit exceeded, try again later, different from Limit_exceeded which may be permanent.
     */
    RATE_LIMIT_EXCEEDED(84),

    /**
     * Need two-factor code to login.
     */
    ACCOUNT_LOGIN_DENIED_NEED_TWO_FACTOR(85),

    /**
     * The thing we're trying to access has been deleted.
     */
    ITEM_DELETED(86),

    /**
     * Login attempt failed, try to throttle response to possible attacker.
     */
    ACCOUNT_LOGIN_DENIED_THROTTLE(87),

    /**
     * Two factor authentication (Steam Guard) code is incorrect.
     */
    TWO_FACTOR_CODE_MISMATCH(88),

    /**
     * The activation code for two-factor authentication (Steam Guard) didn't match.
     */
    TWO_FACTOR_ACTIVATION_CODE_MISMATCH(89),

    /**
     * The current account has been associated with multiple partners.
     */
    ACCOUNT_ASSOCIATED_TO_MULTIPLE_PARTNERS(90),

    /**
     * The data has not been modified.
     */
    NOT_MODIFIED(91),

    /**
     * The account does not have a mobile device associated with it.
     */
    NO_MOBILE_DEVICE(92),

    /**
     * The time presented is out of range or tolerance.
     */
    TIME_NOT_SYNCED(93),

    /**
     * SMS code failure - no match, none pending, etc.
     */
    SMS_CODE_FAILED(94),

    /**
     * Too many accounts access this resource.
     */
    ACCOUNT_LIMIT_EXCEEDED(95),

    /**
     * Too many changes to this account.
     */
    ACCOUNT_ACTIVITY_LIMIT_EXCEEDED(96),

    /**
     * Too many changes to this phone.
     */
    PHONE_ACTIVITY_LIMIT_EXCEEDED(97),

    /**
     * Cannot refund to payment method, must use wallet.
     */
    REFUND_TO_WALLET(98),

    /**
     * Cannot send an email.
     */
    EMAIL_SEND_FAILURE(99),

    /**
     * Can't perform operation until payment has settled.
     */
    NOT_SETTLED(100),

    /**
     * The user needs to provide a valid captcha.
     */
    NEED_CAPTCHA(101),

    /**
     * A game server login token owned by this token's owner has been banned.
     */
    GSLTDENIED(102),

    /**
     * Game server owner is denied for some other reason such as account locked, community ban, vac ban, missing phone, etc.
     */
    GSOWNER_DENIED(103),

    /**
     * The type of thing we were requested to act on is invalid.
     */
    INVALID_ITEM_TYPE(104),

    /**
     * The IP address has been banned from taking this action.
     */
    IPBANNED(105),

    /**
     * This Game Server Login Token (GSLT) has expired from disuse; it can be reset for use.
     */
    GSLTEXPIRED(106),

    /**
     * user doesn't have enough wallet funds to complete the action*/
    INSUFFICIENT_FUNDS(107),

    /**
     * There are too many of this thing pending already.
     */
    TOO_MANY_PENDING(108);

    companion object {
        fun getByCode(code: Int): Result? = entries.firstOrNull { it.code == code }
    }
}