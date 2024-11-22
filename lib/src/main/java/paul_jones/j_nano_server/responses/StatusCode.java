package paul_jones.j_nano_server.responses;

public enum StatusCode {
    // 1XX
    Continue(100),
    SwitchingProtocols(101),
    Processing(102),
    EarlyHints(103),

    // 2XX
    Ok(200),
    Created(201),
    Accepted(202),
    NonAuthoritiveInformation(203),
    NoContent(204),
    ResetContent(205),
    PartialContent(206),
    MultiStatus(207),
    AlreadyReported(208),
    IMUsed(226),

    // 3XX
    MultipleChoices(300),
    MovedPermamently(301),
    Found(302),
    SeeOther(303),
    NotModified(304),
    // 305 & 306 are deprecated
    TemporaryRedirect(307),
    PermamentRedirect(308),

    // 4XX
    BadRequest(400),
    Unauthorized(401),
    PaymentRequired(402),
    Forbidden(403),
    NotFound(404),
    MethodNotAllowed(405),
    NotAcceptable(406),
    ProxyAuthenticationRequired(407),
    RequestTimeout(408),
    Conflict(409),
    Gone(410),
    LengthRequired(411),
    PreconditionFailed(412),
    PayloadTooLarge(413),
    URITooLong(414),
    UnsupportedMediaType(415),
    RangeNotSatisfiable(416),
    ExpectationFailed(417),
    ImATeapot(418),
    MisdirectedRequest(421),
    UnprocessableContent(422),
    Locked(423),
    FailedDependency(424),
    UpgradeRequired(426),
    PreconditionRequired(428),
    TooManyRequests(429),
    RequestHeaderFieldsTooLarge(431),
    UnavailableForLegalReasons(451),

    // 5XX
    InternalServerError(500),
    NotImplemented(501),
    BadGateway(502),
    ServiceUnavailable(503),
    GatewayTimeout(504),
    HTTPVersionNotSupported(505),
    VariantAlsoNegotiates(506),
    InsufficientStorage(507),
    LoopDetected(508),
    NotExtended(510),
    NetworkAuthenticationRequired(511),
    ;

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
