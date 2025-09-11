package com.nearpick.app.common.exception


// 사용자 관련 예외
class UserNotFoundException(userId: String) : BaseException(
    code = "USER_NOT_FOUND",
    message = "해당 사용자를 찾을 수 없습니다. (userId=$userId)",
    category = ErrorCategory.NOT_FOUND
)

class EmailAlreadyExistsException(email: String) : BaseException(
    code = "EMAIL_ALREADY_EXISTS",
    message = "이미 사용 중인 이메일입니다. (email=$email)",
    category = ErrorCategory.VALIDATION
)

class NicknameAlreadyExistsException(nickname: String) : BaseException(
    code = "NICKNAME_ALREADY_EXISTS",
    message = "이미 사용 중인 닉네임입니다. (nickname=$nickname)",
    category = ErrorCategory.VALIDATION
)

class InvalidFormatNicknameException(nickname: String?) : BaseException(
    code = "INVALID_FORMAT_NICKNAME",
    message = "올바르지 않은 닉네임 형식입니다. ($nickname)",
    category = ErrorCategory.VALIDATION
)

class InvalidFormatEmailException(email: String?) : BaseException(
    code = "INVALID_FORMAT_EMAIL",
    message = "올바르지 않은 이메일 형식입니다. ($email)",
    category = ErrorCategory.VALIDATION
)

class EmailVerificationApiException(cause: String?) : BaseException(
    code = "EMAIL_VERIFICATION_INTERNAL_ERROR",
    message = "이메일 인증 번호 전송 중 오류가 발생했습니다 ($cause)",
    category = ErrorCategory.INTERNAL
)

class InvalidEmailVerificationRequestException(email: String?) : BaseException(
    code = "INVALID_EMAIL_VERIFICATION_REQUEST",
    message = "이메일 인증 인증 요청 기록이 없습니다. ($email)",
    category = ErrorCategory.VALIDATION
)

class InvalidEmailVerificationTimeException(email: String?) : BaseException(
    code = "INVALID_EMAIL_VERIFICATION_TIME",
    message = "이메일 인증이 만료되었습니다. ($email)",
    category = ErrorCategory.VALIDATION
)

class InvalidEmailVerificationException(email: String?) : BaseException(
    code = "INVALID_EMAIL_VERIFICATION",
    message = "이메일 인증이 유효하지 않습니다. ($email)",
    category = ErrorCategory.VALIDATION
)

class InvalidEmailVerificationTokenException(email: String?, emailVerificationToken: String?) : BaseException(
    code = "INVALID_EMAIL_VERIFICATION_TOKEN",
    message = "올바르지 않은 이메일 인증 토큰입니다. ($email, $emailVerificationToken)",
    category = ErrorCategory.VALIDATION
)

class InvalidRoleException(role: String?) : BaseException(
    code = "INVALID_ROLE",
    message = "올바르지 않은 Role입니다. (role = $role)",
    category = ErrorCategory.VALIDATION
)

// 인증 관련 예외
class InvalidEmailException() : BaseException(
    code = "INVALID_EMAIL",
    message = "이메일이 일치하지 않습니다.",
    category = ErrorCategory.AUTH
)

class InvalidPasswordException() : BaseException(
    code = "INVALID_PASSWORD",
    message = "비밀번호가 일치하지 않습니다.",
    category = ErrorCategory.AUTH
)

// 사용자 주소 관련 예외
class UserAddressNotFoundException(addressId: String, userId: String) : BaseException(
    code = "USER_ADDRESS_NOT_FOUND",
    message = "사용자의 주소 정보가 잘못되었습니다. (userId=$userId, addressId=$addressId)",
    category = ErrorCategory.NOT_FOUND
)

// 주소 관련 예외
class ExternalApiException(cause: String?) : BaseException(
    code = "ADDRESS_REST_API_INTERNAL_ERROR",
    message = "카카오 주소 검색 중 오류가 발생했습니다 ($cause)",
    category = ErrorCategory.INTERNAL
)

class InvalidPhoneNumberException(phoneNumber: String?) : BaseException(
    code = "INVALID_FORMAT_PHONE_NUMBER",
    message = "올바르지 않은 핸드폰 번호 형식입니다. ($phoneNumber)",
    category = ErrorCategory.VALIDATION
)

// 가게 정보 관련 예외
class InvalidBusinessRegistrationNumberException(businessRegistrationNumberNumber: String?) : BaseException(
    code = "INVALID_BUSINESS_REGISTRATION_NUMBER",
    message = "올바르지 않은 사업자 등록 번호 형식입니다. ($businessRegistrationNumberNumber)",
    category = ErrorCategory.VALIDATION
)

class BrandNotFoundException(brandId: String?, userId: String?) : BaseException(
    code = "BRAND_NOT_FOUND",
    message = "사용자의 가게 정보가 잘못되었습니다. (userId=$userId, brandId=$brandId)",
    category = ErrorCategory.NOT_FOUND
)

class BrandAlreadyExistsException(brand: String) : BaseException(
    code = "BRAND_ALREADY_EXISTS",
    message = "이미 등록된 사업자 등록 번호입니다. (brand=$brand)",
    category = ErrorCategory.VALIDATION
)

// 상품 정보 관련 예외
class ProductNotFoundException(productId: String?, userId: String?) : BaseException(
    code = "PRODUCT_NOT_FOUND",
    message = "사용자의 상품 정보가 잘못되었습니다. (productId=$productId)",
    category = ErrorCategory.NOT_FOUND
)
