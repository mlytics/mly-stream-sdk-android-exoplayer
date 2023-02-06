package com.mlytics.mlysdk.util

enum class MessageCode(
    var code: StatusCodes,
    var logCode: String,
    var metaCode: String,
    var metaContent: String,
    var logContent: String
) {
    WMV400(
        StatusCodes.BAD_REQUEST, "WMV400", "400", "Restful view: Bad request.", "Bad request."
    ),
    WMV403(
        StatusCodes.FORBIDDEN, "WMV403", "403", "Restful view: Forbidden.", "Forbidden."
    ),
    WMV404(
        StatusCodes.NOT_FOUND, "WMV404", "404", "Restful view: Not found.", "Not found."
    ),
    EMV500(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMV500",
        "500",
        "Restful view: Internal server error.",
        "Internal server error."
    ),
    EMU000(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU000",
        "520",
        "Auxiliary util: Internal error.",
        "Backoff operation has been exhausted."
    ),
    EMU010(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU010",
        "520",
        "Auxiliary util: Internal error.",
        "Future operation timeout exceeded."
    ),
    EMU011(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU011",
        "520",
        "Auxiliary util: Internal error.",
        "Future condition has been done."
    ),
    EMU012(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU012",
        "520",
        "Auxiliary util: Internal error.",
        "Future condition has never been done."
    ),
    EMU013(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU013",
        "520",
        "Auxiliary util: Internal error.",
        "Future precondition has been done."
    ),
    EMU014(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU014",
        "520",
        "Auxiliary util: Internal error.",
        "Future task has been cancelled."
    ),
    EMU015(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU015",
        "520",
        "Auxiliary util: Internal error.",
        "Future task loop has been cancelled."
    ),
    EMU016(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU016",
        "520",
        "Auxiliary util: Internal error.",
        "Future task retry exceeded."
    ),
    EMU017(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU017",
        "520",
        "Auxiliary util: Internal error.",
        "Future task manager has been aborted."
    ),
    EMU018(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU018",
        "520",
        "Auxiliary util: Internal error.",
        "Future lock has been released."
    ),
    EMU019(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU019",
        "520",
        "Auxiliary util: Internal error.",
        "Future channel has been closed."
    ),
    EMU050(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU050",
        "520",
        "Auxiliary util: Internal error.",
        "Flow key to a value must be in flow storage."
    ),
    EMU060(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU060",
        "520",
        "Auxiliary util: Internal error.",
        "Graceful processor received some process errors."
    ),
    EMU070(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "EMU070",
        "525",
        "Auxiliary util: Internal error.",
        "Request carrier encountered an error before response."
    ),
    EMU071(
        StatusCodes.SERVICE_UNAVAILABLE,
        "EMU071",
        "526",
        "Auxiliary util: External API error.",
        "Request carrier received unexpected failure response."
    ),
    WMU072(
        StatusCodes.BAD_REQUEST,
        "WMU072",
        "527",
        "Auxiliary util: External API error.",
        "Request carrier received validation failure response."
    ),

    ESC000(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC000",
        "550",
        "Kernel core: Internal error.",
        "File request has been aborted."
    ),
    ESC001(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC001",
        "550",
        "Kernel core: Internal error.",
        "File downloader has been aborted."
    ),
    ESC010(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC010",
        "550",
        "Kernel core: Internal error.",
        "HTTP downloader proxy request failed."
    ),
    ESC011(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC011",
        "550",
        "Kernel core: Internal error.",
        "HTTP downloader proxy cannot handle the response."
    ),
    ESC012(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC012",
        "550",
        "Kernel core: Internal error.",
        "MCDN downloader proxy is not available within timeout."
    ),
    ESC020(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC020",
        "550",
        "Kernel core: Internal error.",
        "Node downloader proxy cannot fetch available node daemon."
    ),
    ESC021(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC021",
        "550",
        "Kernel core: Internal error.",
        "Node downloader proxy is detected with no progress."
    ),
    ESC030(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC030",
        "550",
        "Kernel core: Internal error.",
        "User downloader proxy cannot handle the unshareable resource."
    ),
    ESC031(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC031",
        "550",
        "Kernel core: Internal error.",
        "User downloader proxy cannot handle the urgent resource."
    ),
    ESC032(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC032",
        "550",
        "Kernel core: Internal error.",
        "User downloader proxy is not chosen this time."
    ),
    ESC033(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC033",
        "550",
        "Kernel core: Internal error.",
        "User downloader proxy cannot fetch available user daemon."
    ),
    ESC034(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESC034",
        "550",
        "Kernel core: Internal error.",
        "User downloader proxy is detected with no progress."
    ),
    ISP200(StatusCodes.OK, "ISP200", "200", "Kernel protocol: OK.", "OK."), ESP000(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESP000",
        "560",
        "Kernel protocol: External API error.",
        "An error response is received."
    ),
    ESP001(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESP001",
        "562",
        "Kernel protocol: Internal error.",
        "An error occurred while processing a request."
    ),
    ESP010(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESP010",
        "560",
        "Kernel protocol: Internal error.",
        "Peer broker cannot be found."
    ),
    ESP011(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESP011",
        "560",
        "Kernel protocol: Internal error.",
        "Node broker cannot be found."
    ),
    ESP012(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESP012",
        "560",
        "Kernel protocol: Internal error.",
        "Tracker broker cannot be found."
    ),
    WSP020(
        StatusCodes.BAD_REQUEST,
        "WSP020",
        "561",
        "Kernel protocol: Invalid parameter.",
        "Resource cache cannot be found."
    ),
    ESS000(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS000",
        "570",
        "Kernel service: Internal error.",
        "Peer broker has been closed."
    ),
    ESS001(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS001",
        "570",
        "Kernel service: Internal error.",
        "Peer daemon has exited."
    ),
    WSS002(
        StatusCodes.BAD_REQUEST,
        "WSS002",
        "571",
        "Kernel service: Invalid parameter.",
        "Peer daemon cannot handle the action."
    ),
    ESS003(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS003",
        "570",
        "Kernel service: Internal error.",
        "Peer daemon cannot handle the command."
    ),
    ESS005(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS005",
        "572",
        "Kernel service: Internal error.",
        "User manager has reached max peer connections."
    ),
    ESS006(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS006",
        "572",
        "Kernel service: Internal error.",
        "User manager rejected a peer which had failed."
    ),
    ESS007(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS007",
        "572",
        "Kernel service: Internal error.",
        "User manager rejected a peer which is in use."
    ),
    ESS010(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS010",
        "570",
        "Kernel service: Internal error.",
        "Swarm daemon cannot be found."
    ),
    ESS011(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS011",
        "570",
        "Kernel service: Internal error.",
        "Swarm daemon cannot handle the command."
    ),
    ESS020(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS020",
        "570",
        "Kernel service: Internal error.",
        "Tracker broker has been closed."
    ),
    ESS021(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS021",
        "570",
        "Kernel service: Internal error.",
        "Tracker daemon has exited."
    ),
    ESS022(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESS022",
        "570",
        "Kernel service: Internal error.",
        "Tracker launcher cannot handle the feedback."
    ),
    ESB000(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESB000",
        "450",
        "System booter: Internal error.",
        "System component activate failed."
    ),
    WSB010(
        StatusCodes.BAD_REQUEST,
        "WSB010",
        "460",
        "System booter: Invalid parameter.",
        "Kernel parameters cannot be validated successfully."
    ),
    WSB011(
        StatusCodes.BAD_REQUEST,
        "WSB011",
        "460",
        "System booter: Invalid parameter.",
        "Kernel parameter must be configured."
    ),
    WSB012(
        StatusCodes.BAD_REQUEST,
        "WSB012",
        "460",
        "System booter: Invalid parameter.",
        "Kernel parameter cannot be configured again."
    ),
    WSV000(
        StatusCodes.BAD_REQUEST,
        "WSV000",
        "601",
        "Driver essential: Invalid environ.",
        "Driver manager cannot activate without WebRTC support."
    ),
    WSV001(
        StatusCodes.BAD_REQUEST,
        "WSV001",
        "601",
        "Driver essential: Invalid procedure.",
        "Driver manager cannot activate before properly configured."
    ),
    WSV002(
        StatusCodes.BAD_REQUEST,
        "WSV002",
        "601",
        "Driver essential: Invalid procedure.",
        "Driver manager cannot be operated before it is activated."
    ),
    WSV050(
        StatusCodes.BAD_REQUEST,
        "WSV050",
        "621",
        "Driver integration: Invalid parameter.",
        "HLS loader cannot handle the response type."
    ),
    ESV051(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESV051",
        "621",
        "Driver integration: Internal error.",
        "HLS controller cannot buffer a play segment which is aborted."
    ),
    ESV100(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESV100",
        "650",
        "Driver peripheral: Internal error.",
        "Video.js plugin method has never been implemented."
    ),
    ESV101(
        StatusCodes.INTERNAL_SERVER_ERROR,
        "ESV101",
        "650",
        "Driver peripheral: Internal error.",
        "Video.js source handler method has never been implemented."
    ),
    WSV110(
        StatusCodes.BAD_REQUEST,
        "WSV110",
        "651",
        "Driver peripheral: Invalid environ.",
        "Video.js HLS plugin cannot be registered because its version is incompatible."
    ),
    WSV111(
        StatusCodes.BAD_REQUEST,
        "WSV111",
        "651",
        "Driver peripheral: Invalid environ.",
        "Video.js HLS plugin cannot be registered because HLS protocol is not supported."
    )
}
