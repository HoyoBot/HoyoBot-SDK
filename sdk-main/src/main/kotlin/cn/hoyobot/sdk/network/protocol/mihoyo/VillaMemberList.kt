package cn.hoyobot.sdk.network.protocol.mihoyo

class VillaMemberList {

    val members: ArrayList<Member> = ArrayList()
    var nextOffsetStr = ""

    fun getMemberCount(): Int {
        return this.members.size
    }

}