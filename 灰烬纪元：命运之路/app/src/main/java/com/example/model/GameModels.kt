package com.example.model

import androidx.compose.ui.graphics.Color

// Game Seasons
enum class Season(val displayName: String, val description: String) {
    SPRING("春季", "雨水丰沛，植物生机勃勃。草药采集效率+30%，自然元素伤害增加。"),
    SUMMER("夏季", "骄阳似火，酷暑炎热。火系技能伤害+15%，体力消耗加快。部分河道干涸显露密道。"),
    AUTUMN("秋季", "红叶漫山，野兽躁动。食物收集效率+50%，兽潮事件更频繁。"),
    WINTER("冬季", "大雪封山，严寒彻骨。冰系技能伤害+20%，移动点消耗增加，部分山路受阻。")
}

// Time of Day Slots (1 game day = 15 minutes of real-world time)
enum class TimeOfDay(val displayName: String, val hours: String, val viewBonus: Int, val hitModifier: Int, val glowDesc: String) {
    DAWN("黎明", "04:00 - 06:00", 0, 0, "朝阳初升，橙金色的光芒唤醒大地"),
    DAY("白天", "06:00 - 17:00", 0, 0, "烈日高照，视野清晰无极"),
    TWILIGHT("黄昏", "17:00 - 19:00", -1, -5, "残阳如血，阴影开始在远方拉长"),
    NIGHT("夜晚", "19:00 - 23:00", -2, -15, "月明星稀，篝火与火把微光环绕"),
    MIDNIGHT("深夜", "23:00 - 02:00", -3, -25, "寂静幽暗，暗夜行者与盗贼活跃"),
    EARLY_MORNING("凌晨", "02:00 - 04:00", -1, -10, "天地混沌，晨雾弥漫，万物将明")
}

// Weather Types
enum class Weather(val displayName: String, val movementPenalty: Int, val fireMod: Float, val frostMod: Float, val lightningMod: Float, val desc: String) {
    SUNNY("晴天", 0, 1.0f, 1.0f, 1.0f, "晴空万里，微风吹拂。"),
    CLOUDY("多云", 0, 1.0f, 1.0f, 1.0f, "阴云棋布，月光若隐若现。"),
    LIGHT_RAIN("小雨", 1, 0.9f, 1.0f, 1.0f, "沥沥小雨，地面变得有些湿滑。"),
    HEAVY_RAIN("大雨", 2, 0.8f, 1.0f, 1.15f, "暴雨倾盆。火伤-20%，雷伤+15%，视野模糊。"),
    STORM("暴风雨", 3, 0.7f, 0.9f, 1.30f, "电闪雷鸣，虚空中有碎雷劈落！无法进行远程射击。"),
    SOFTSNOW("小雪", 1, 1.0f, 0.9f, 1.0f, "白雪飞舞，地面覆盖一层薄雪。"),
    BLIZZARD("暴风雪", 3, 0.8f, 0.7f, 1.0f, "极寒暴雪！非抗寒装甲每回合损失HP，冰系抗性突降。"),
    FOG("浓雾", 1, 1.0f, 1.0f, 1.0f, "迷雾笼罩。视野降为1格，无法射击。")
}

// Races (4 races)
enum class Race(
    val displayName: String,
    val description: String,
    val basicClothes: String,
    val strBonus: Int,
    val dexBonus: Int,
    val conBonus: Int,
    val intBonus: Int,
    val wilBonus: Int,
    val sprBonus: Int,
    val perBonus: Int,
    val chaBonus: Int,
    val traits: List<String>
) {
    HUMAN(
        "人类",
        "艾伦加德帝国的后裔，凭借无限的适应潜能和外交智慧在百年黑潮尘埃中顽强重建。他们长于组织，属性均衡。",
        "亚麻猎人衬衫 + 皮质绑腿 + 旅人皮靴",
        2, 2, 2, 2, 2, 2, 2, 4,
        listOf("适应之力 (升级多拿点数)", "语言通晓 (高额外对话选项)", "领袖光环 (范围命中率上升)", "求生本能 (血量极低暴增回避)", "交易天赋 (商店买打折卖增值)")
    ),
    DWARF(
        "矮人",
        "铁脊山脉格罗姆尼尔深城中的磐石子民。他们身型粗壮、性格古板，与生俱来对重甲、美酒以及锻造无比狂热。",
        "铆钉锁片皮甲 + 矿工耐重靴 + 饰钉工具腰包",
        4, 1, 4, 1, 3, 2, 2, 1,
        listOf("岩石之躯 (物理防御+20%)", "锻造大师 (铁砧修理打半折)", "地下直觉 (暗处陷阱高发现率)", "战吼·山崩 (范围击晕敌寇降防)", "痛饮烈酒 (灌酒解百愁加伤减命中)")
    ),
    ORC(
        "兽人",
        "赤荒大平原上狂野游牧的荣耀氏族。拥有尖锐獠牙与血怒天赋，追求力量并在萨满指引中贯彻古老而高贵的战斗意志。",
        "巨兽獠骨护肩 + 兽皮裙甲 + 赤荒麻凉草鞋",
        5, 2, 3, 1, 2, 1, 2, 1,
        listOf("血怒天赋 (血损攻击反向暴增)", "野性直觉 (先攻加15野地闪避)", "战争践踏 (崩裂大地移动减倍)", "嗜血狂暴 (扣血25%换狂暴状态)", "族群纽带 (队伍多兽人战力上升)")
    ),
    ELF(
        "精灵",
        "翡翠迷境深处傲然长存的优雅古树子民。生来带有星月魔力纹路，超凡脱俗，能够与植物和元素脉动产生自然共鸣。",
        "金丝刺绣短衫 + 精灵编织轻林披风 + 软鹿皮长筒靴",
        1, 4, 1, 4, 2, 4, 3, 3,
        listOf("自然共鸣 (林地自动回复生命魔法)", "元素亲和 (元素法术威能高消耗低)", "月神庇佑 (无视控制兼致命免死)", "精灵箭术 (武器长射程兼高致命率)", "岁月智慧 (额外获得两点技能点)")
    )
}

// 3 Core Styling Paths 
enum class StylePath(
    val displayName: String,
    val subTitle: String,
    val description: String,
    val primaryAttr: String,
    val secondaryAttr: String,
    val specialties: List<String>
) {
    MAGIC_PATH(
        "奥法之路 (Magic)",
        "高爆发 · 广域控场",
        "追寻世间狂暴的魔法元素与黑潮深渊中的虚空律动，擅长以法杖、魔导书施放雷火元素的大型法术与圣光壁垒治疗。",
        "智力 (INT)",
        "意志 (WIL)",
        listOf("元素毁灭者 (连锁爆裂、暴风之眼)", "暗影编织者 (阴影碎步、虚空猎取)", "神圣守护者 (圣意驱散、天使壁垒)")
    ),
    CULTIVATION_PATH(
        "天道修行 (Cultivation)",
        "中距离混伤 · 真气连携",
        "吐纳天地灵秀气化作紫府真元，御剑长枪流仙扇，挥洒间阴阳逆转、气场环绕、毒丹炼制、重塑乾坤。",
        "灵力 (SPR)",
        "体魄 (CON)",
        listOf("御剑天尊 (万剑归宗、跨障飞行)", "太极玄师 (以柔克刚、阴阳反转)", "炼丹药师 (丹药精益、起死回生)")
    ),
    BODY_TEMPERING_PATH(
        "钢铁之躯 (Body)",
        "近战肉盾 · 坚不可摧",
        "淬炼肉身至不坏，负千斤怒气斩裂深渊。装备重剑重锤，身段如重铁。战斗中姿态切换灵动，攻防自如。",
        "力量 (STR)",
        "体质 (CON)",
        listOf("铁壁战神 (不动如山、群体嘲讽)", "狂战杀神 (连环死斩、灭世狂怒)", "驯兽猎王 (召唤野兽战宠、捕兽陷阱)")
    )
}

// Specific Skills
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val type: String, // "主动" / "被动"
    val resourceCost: String, // e.g., "15 AP", "20 MP", "50 怒气", "30 真气"
    val apCost: Int,
    val cooldown: Int,
    var level: Int = 1,
    var xp: Int = 0,
    val unlockLevel: Int
)

// Equipment Rarity Colors and Multipliers
enum class GearRarity(val displayName: String, val color: Color, val multiplier: Float) {
    DAMAGED("破损", Color(0xFF888888), 0.7f),
    COMMON("普通", Color(0xFFFFFFFF), 1.0f),
    EXCELLENT("优良", Color(0xFF4CAF50), 1.2f),
    RARE("稀有", Color(0xFF2196F3), 1.4f),
    EPIC("史诗", Color(0xFF9C27B0), 1.6f),
    LEGENDARY("传说", Color(0xFFFF9800), 1.8f),
    MYTHICAL("神话", Color(0xFFF44336), 2.2f)
}

// 10 Gear slots
enum class GearSlot(val displayName: String) {
    HEAD("头部"),
    TORSO("躯干"),
    ARMS("手臂"),
    LEGS("腿部"),
    WEAPON("主手武器"),
    OFFHAND("副手/盾"),
    NECK("项链饰品"),
    RING("戒指饰品"),
    BELT("腰带披风")
}

// Equipments Definition
data class Gear(
    val id: String,
    val name: String,
    val slot: GearSlot,
    val rarity: GearRarity,
    val baseValue: Int, // Armor or Attack rating
    val prefix: String = "",
    val suffix: String = "",
    val magicEffect: String = "",
    var upgradeLevel: Int = 0 // +1 to +5
) {
    fun getEffectiveStat(): Int {
        val rarityMultiplier = rarity.multiplier
        val upgradeMultiplier = 1.0f + (upgradeLevel * 0.15f)
        return ((baseValue * rarityMultiplier) * upgradeMultiplier).toInt()
    }

    fun getFullName(): String {
        val upStr = if (upgradeLevel > 0) " +$upgradeLevel" else ""
        val preStr = if (prefix.isNotEmpty()) "$prefix-" else ""
        val sufStr = if (suffix.isNotEmpty()) "-之$suffix" else ""
        return "${preStr}${name}${sufStr}${upStr}"
    }
}

// Inventory Consumables
data class Consumable(
    val id: String,
    val name: String,
    val desc: String,
    val hpRestore: Int,
    val mpRestore: Int,
    val bonusEffect: String = "",
    var count: Int = 1,
    val goldWorth: Int = 10
)

// Areas on Map (6 regions)
enum class MapRegion(
    val id: String,
    val displayName: String,
    val description: String,
    val coordinates: Pair<Int, Int>,
    val dangerLevel: Int,
    val themeColor: Color
) {
    DAWNHAVEN(
        "dawnhaven", "晨曦之镇", "温暖祥和的新手聚落，被金色麦田与古老松树合抱，没有任何怪物。",
        Pair(9, 11), 1, Color(0xFFC0A060)
    ),
    SILVERCROWN(
        "silvercrown", "银冠城邦", "宏伟的中世纪尖顶城堡与繁忙的骑士训练营，繁华背后面临贵族内斗与盗贼滋扰。",
        Pair(5, 7), 15, Color(0xFF698F9E)
    ),
    IRONSPINE(
        "ironspine", "铁脊山脉", "巍峨挺拔的极寒雪峰。深藏格罗姆尼尔矮人要塞及热火烫锻的晶体铁索熔岩地。",
        Pair(8, 3), 28, Color(0xFF7D725C)
    ),
    REDWASTE(
        "redwaste", "赤荒平原", "由鲜血祭坛、图腾篝火和猛犸骨骷构成的巨野碎砂荒地，野性图腾萨满狂野咆哮。",
        Pair(14, 8), 35, Color(0xFFAB473E)
    ),
    EMERALD_LABYRINTH(
        "emerald_labyrinth", "翡翠迷境", "上古森林巨树搭起的自然奇观，月影泉水流经德鲁伊图腾，隐藏狂兽蛛丝。",
        Pair(13, 14), 42, Color(0xFF2E6334)
    ),
    BLACK_WAVE_WASTE(
        "black_wave_waste", "黑潮废土", "位于大北方终年不见天日的古战场，焦土散溢深渊黑气，炎龙与食尸鬼横行。",
        Pair(8, 0), 50, Color(0xFF4A148C)
    )
}

// 7 Body parts state for combat (復刻 Stoneshard 部位血量及残废机制)
enum class BodyPart(val displayName: String, val thresholdHpRatio: Float, val weight: Float) {
    HEAD("头部", 0.15f, 0.10f),
    TORSO("躯干", 0.30f, 0.35f),
    R_ARM("右臂", 0.12f, 0.15f),
    L_ARM("左臂", 0.12f, 0.15f),
    R_LEG("右腿", 0.12f, 0.12f),
    L_LEG("左腿", 0.12f, 0.12f),
    WAIST("腰腹", 0.07f, 0.01f)
}

data class BodyPartHP(
    val part: BodyPart,
    var currentHp: Float,
    var maxHp: Float,
    var isBleeding: Boolean = false,
    var isCrippled: Boolean = false
) {
    fun getStatusDesc(): String {
        return when {
            currentHp <= 0 -> "已断裂残废❌"
            currentHp < maxHp * 0.25f -> "重伤危急⚠️"
            currentHp < maxHp * 0.5f -> "中度受损🩹"
            currentHp < maxHp -> "轻度出血"
            else -> "完好✨"
        }
    }
}

// Combat grid item representation
enum class TerrainType(val displayName: String, val apCost: Int, val description: String, val color: Color) {
    PLAIN("草地", 2, "平坦的草地，容易引燃", Color(0xFF4A5D3E)),
    DESERT("沙石", 3, "崎岖的碎石与沙地", Color(0xFF7A6B53)),
    SWAMP("泥泞", 4, "落脚深陷，闪避率-15%", Color(0xFF354432)),
    ICE_FLOE("冰面", 3, "极度滑溜，行走易摔倒", Color(0xFF88A9B2)),
    CRACK("废土碎岩", 4, "锋利碎面，行动加耗AP", Color(0xFF564C4D)),
    LAVA_FIRE("烈焰荒焦", 2, "踏入引火焚身，受火毒伤害", Color(0xFF8A301D)),
    SHALLOW_WATER("浅水地", 4, "大水没胫。雷伤+30%，火防提升", Color(0xFF395166)),
    WALL_ROCK("巨岩柱石", 999, "隔断视线与飞矢，无法通行", Color(0xFF2C2521)),
}

data class GridCell(
    val x: Int,
    val y: Int,
    val terrain: TerrainType,
    var hasFlame: Boolean = false,
    var hasToxicFog: Boolean = false,
    var elevation: Int = 0 // 0=平地, 1=矮台, 2=高崖
)

// Active Combatant (Combat System)
data class Combatant(
    val name: String,
    val isPlayer: Boolean,
    var x: Int,
    var y: Int,
    val race: Race?,
    val path: StylePath?,
    var maxHp: Int,
    var currentHp: Int,
    var maxMp: Int,
    var currentMp: Int,
    var ap: Int = 10,
    var rage: Int = 0,
    var qi: Int = 0,
    var defense: Int,
    var attackRating: Int,
    val bodyParts: List<BodyPartHP>,
    var isStunned: Int = 0, // Round count
    var targetPartIdx: Int = 1, // Torso by default
    val spriteColor: Color,
    val aiType: String = "激进"
)

// Quest definitions
enum class QuestType {
    MAIN, SIDE, DAILY, EVENT
}

data class Quest(
    val id: String,
    val title: String,
    val type: QuestType,
    val desc: String,
    val objective: String,
    var progress: Int,
    val maxProgress: Int,
    val expReward: Int,
    val goldReward: Int,
    val location: String,
    var status: String = "未接取" // "未接取", "进行中", "可交付", "已完成"
)

// NPC configuration
data class NPC(
    val id: String,
    val name: String,
    val title: String,
    val race: Race,
    val initialOpinion: Int,
    val location: String,
    val voiceLines: List<String>,
    val lovedGifts: List<String>,
    val dislikedGifts: List<String>,
    var reputation: Int = 0, // -100 to 100
    val activeTimes: String = "全天",
    var status: String = "中立" // 敌对 -> 冷淡 -> 中立 -> 友善 -> 亲密 -> 挚友 -> 灵魂伴侣
)
