package cn.hoyobot.sdk.network.protocol.mihoyo

object MihoyoAPI {

    private const val API = "https://bbs-api.miyoushe.com"
    const val API_VILLA = "$API/vila/api/bot/platform/getVilla"
    const val API_MEMBER = "$API/vila/api/bot/platform/getMember"
    const val API_MEMBER_LIST = "$API/vila/api/bot/platform/getVillaMembers"
    const val API_MEMBER_KICK = "$API/vila/api/bot/platform/deleteVillaMember"
    const val API_MESSAGE = "$API/vila/api/bot/platform/sendMessage"
    const val API_ROOM = "$API/vila/api/bot/platform/getRoom"
    const val API_TRANSFER_IMAGE = "$API/vila/api/bot/platform/transferImage"
    const val API_PIN_MESSAGE = "$API/vila/api/bot/platform/pinMessage"
    const val API_RECALL_MESSAGE = "$API/vila/api/bot/platform/recallMessage"

}