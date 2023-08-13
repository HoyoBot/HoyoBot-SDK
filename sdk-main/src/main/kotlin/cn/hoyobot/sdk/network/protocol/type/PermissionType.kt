package cn.hoyobot.sdk.network.protocol.type

enum class PermissionType(val value: String) {
    //是否可以艾特全体成员
    MENTIONED_ALL("mention_all"),

    //允许成员能够在聊天房间中撤回任何人的消息
    RECALL_MESSAGE("recall_message"),

    //允许成员能够在聊天房间中置顶消息
    PIN_MESSAGE("pin_message"),

    //允许成员添加、删除身份组，管理身份组成员，修改身份组的权限
    MANAGE_MEMBER_ROLE("manage_member_role"),

    //允许成员编辑大别野的简介、标签、设置大别野加入条件等
    EDIT_VILLA_INFO("edit_villa_info"),

    //允许成员新建房间，新建/删除房间分组，调整房间及房间分组的排序
    MANAGE_GROUP_AND_ROOM("manage_group_and_room"),

    //允许成员能够在房间里禁言其他人
    VILLA_SILENCE("villa_silence"),

    //允许成员能够拉黑和将其他人移出大别野
    BLACK_OUT("black_out"),

    //允许成员审核大别野的加入申请
    HANDLE_APPLY("handle_apply"),

    //允许成员编辑房间信息及设置可见、发言权限
    MANAGE_CHAT_ROOM("manage_chat_room"),

    //允许成员查看大别野数据看板
    VIEW_DATA_BOARD("view_data_board"),

    //允许成员创建活动，编辑活动信息
    MANAGE_CUSTOM_EVENT("manage_custom_event"),

    //允许成员在直播房间中点播节目及控制节目播放
    LIVE_ROOM_ORDER("live_room_order"),

    //允许成员设置、移除精选消息
    MANAGE_SPOTLIGHT_COLLECTION("manage_spotlight_collection")
}