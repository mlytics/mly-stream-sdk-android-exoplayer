package com.mlytics.mlysdk.util

//import Foundation
enum class StatusCodes(rawValue: Int) {
    OK(200), FORBIDDEN(403), NOT_FOUND(404), SERVICE_UNAVAILABLE(526), INTERNAL_SERVER_ERROR(550), BAD_REQUEST(
        601
    )
}
typealias MessageCodeObject = MessageCode