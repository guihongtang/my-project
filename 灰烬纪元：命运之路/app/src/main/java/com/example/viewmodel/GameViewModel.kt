package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.database.GameRepository
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

enum class ActiveScreen {
    WELCOME,
    CHARACTER_CREATE,
    MAIN_HUB
}

enum class HubTab {
    WORLD_MAP,
    TAVERN_NPC,
    COMBAT_ARENA,
    BLACKSMITH_FORGE,
    HERO_PROFILE,
    MANUAL_GUIDE
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository
    
    // Live saved slots observed from SQL DB
    val dbSaveSlots: StateFlow<List<SaveSlotEntity>>
    
    // Navigation and UI focus states
    var currentScreen by mutableStateOf(ActiveScreen.WELCOME)
    var currentTab by mutableStateOf(HubTab.WORLD_MAP)
    var selectedSlotId by mutableStateOf(1)
    
    // Game World Cycle State
    var currentSeason by mutableStateOf(Season.SPRING)
    var currentTimeOfDay by mutableStateOf(TimeOfDay.DAY)
    var currentWeather by mutableStateOf(Weather.SUNNY)
    var daysElapsed by mutableStateOf(1)
    var currentRegion by mutableStateOf(MapRegion.DAWNHAVEN)
    var oreMaterials by mutableStateOf(10) // Forge crafting iron ores
    
    // Beast Tide State
    var beastTideCountdown by mutableStateOf(12) // actions left till beast tide
    var isBeastTideActive by mutableStateOf(false)
    var activeSavedSlot by mutableStateOf<SaveSlotEntity?>(null)
    
    // Player Character Details & stats
    var heroName by mutableStateOf("未命名旅人")
    var heroRace by mutableStateOf(Race.HUMAN)
    var heroPath by mutableStateOf(StylePath.MAGIC_PATH)
    var heroPortraitIndex by mutableStateOf(0)
    var heroLevel by mutableStateOf(1)
    var heroExp by mutableStateOf(0)
    val expNeededForNextLevel: Int get() = heroLevel * 150
    var heroGold by mutableStateOf(120)
    
    // Dynamic Combat stats of player
    var playerHpMax by mutableStateOf(100)
    var playerHpCurrent by mutableStateOf(100)
    var playerMpMax by mutableStateOf(50)
    var playerMpCurrent by mutableStateOf(50)
    var attributePointsLeft by mutableStateOf(5)
    var skillPointsLeft by mutableStateOf(1)
    
    // Attributes Map
    var attributes = mutableMapOf<String, Int>(
        "STR" to 10, "DEX" to 10, "CON" to 10, "INT" to 10,
        "WIL" to 10, "SPR" to 10, "PER" to 10, "CHA" to 10
    )
    
    // Equipped items (10 slots)
    var equippedGears = mutableMapOf<GearSlot, Gear>()
    
    // Consumable inventory list
    var backpack = mutableStateOf<List<Consumable>>(emptyList())
    
    // Learned Skills
    var customSkills = mutableStateOf<List<Skill>>(emptyList())
    
    // NPC relationship dictionary
    var npcRelations = mutableMapOf<String, Int>()
    
    // Quest tracker database
    var questLogByRegion = mutableStateOf<List<Quest>>(emptyList())
    
    // Combat state variables (10x10 arena grid)
    var inCombatMode by mutableStateOf(false)
    var combatGrid = mutableStateOf<List<GridCell>>(emptyList())
    var combatLog = mutableStateOf<List<String>>(emptyList())
    var activePlayerIdx by mutableStateOf(0) // order
    var combatRoundCount by mutableStateOf(1)
    
    var combatPlayer by mutableStateOf<Combatant?>(null)
    var combatEnemy by mutableStateOf<Combatant?>(null)
    var selectedTargetPartIdx by mutableStateOf(1) // 0=Head, 1=Torso, 2=R.Arm, 3=L.Arm, 4=R.Leg, 5=L.Leg, 6=Waist
    var lastCombatRewardSummary by mutableStateOf("")

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())
        dbSaveSlots = repository.allSaveSlots.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        // Setup initial default inventory
        resetBackpack()
        initializeDefaultNpcRelations()
        initializeDefaultQuests()
    }

    private fun resetBackpack() {
        backpack.value = listOf(
            Consumable("pot_hp_small", "微效生命药水", "立刻恢复30%头部或躯干血量", 35, 0, goldWorth = 15, count = 3),
            Consumable("pot_mp_small", "微效法力药水", "立刻恢复30点MP与真气", 0, 30, goldWorth = 15, count = 2),
            Consumable("dwarf_ale", "格罗姆尼尔黑啤", "触发矮人种族烈酒天赋，大增攻击但降命中", 20, 10, "DRUNK", count = 1, goldWorth = 10)
        )
    }

    private fun initializeDefaultNpcRelations() {
        npcRelations["alaric"] = 10
        npcRelations["moira"] = 5
        npcRelations["thrall"] = 0
        npcRelations["elenwe"] = 15
    }

    private fun initializeDefaultQuests() {
        questLogByRegion.value = listOf(
            Quest("q_dawn_1", "晨曦之星的呼唤", QuestType.MAIN, "拜访晨曦镇村长了解百年黑潮裂痕的秘密。", "与村长对话", 0, 1, 100, 50, "dawnhaven", "未接取"),
            Quest("q_silver_1", "银冠领主之影", QuestType.MAIN, "银冠城邦中出现勾结黑市盗贼的帝国骑士，铲除祸源。", "在银冠城逮捕密谋骑士", 0, 1, 300, 150, "silvercrown", "未接取"),
            Quest("q_iron_mine", "矮人符文古刻", QuestType.SIDE, "在格罗姆尼尔矿井击败熔岩蠕虫，寻回丢失的锻铁符文。", "夺回矿井古刻符文", 0, 1, 200, 100, "ironspine", "未接取"),
            Quest("q_orc_beast", "赤荒猛犸狩猎", QuestType.SIDE, "在猛犸墓园平息被黑潮污染的发狂巨剑熊人。", "击杀巨兽狂熊", 0, 1, 250, 120, "redwaste", "未接取"),
            Quest("q_daily_gather", "药剂草药收集", QuestType.DAILY, "收集迷林附近的翡翠灵泉水与星辰草一株。", "递交材料", 0, 1, 80, 40, "emerald_labyrinth", "未接取")
        )
    }

    // Load Save Slot Data
    fun loadGameSlot(slotId: Int) {
        selectedSlotId = slotId
        viewModelScope.launch {
            val entity = repository.getSaveSlotById(slotId)
            if (entity != null) {
                withContext(Dispatchers.Main) {
                    activeSavedSlot = entity
                    heroName = entity.characterName
                    heroRace = Race.valueOf(entity.raceName)
                    heroPath = StylePath.valueOf(entity.pathName)
                    heroPortraitIndex = entity.portraitIndex
                    heroLevel = entity.level
                    heroExp = entity.exp
                    heroGold = entity.gold
                    
                    attributes["STR"] = entity.strength
                    attributes["DEX"] = entity.agility
                    attributes["CON"] = entity.constitution
                    attributes["INT"] = entity.intelligence
                    attributes["WIL"] = entity.will
                    attributes["SPR"] = entity.spirit
                    attributes["PER"] = entity.perception
                    attributes["CHA"] = entity.charisma
                    
                    attributePointsLeft = entity.attributePointsLeft
                    skillPointsLeft = entity.skillPointsLeft
                    playerHpCurrent = entity.currentHp
                    playerHpMax = entity.maxHp
                    playerMpCurrent = entity.currentMp
                    playerMpMax = entity.maxMp
                    
                    currentRegion = MapRegion.values().find { it.id == entity.currentRegionId } ?: MapRegion.DAWNHAVEN
                    currentTimeOfDay = TimeOfDay.values()[entity.timeOfDayOrdinal]
                    currentWeather = Weather.values()[entity.weatherOrdinal]
                    currentSeason = Season.values()[entity.seasonOrdinal]
                    daysElapsed = entity.daysElapsed
                    
                    // Unpack simple custom values
                    unpackInventory(entity.serializedInventory)
                    unpackGear(entity.serializedGear)
                    unpackSkills(entity.serializedSkills)
                    unpackQuests(entity.serializedQuests)
                    unpackNpcRelations(entity.serializedNpcRelations)
                    
                    currentScreen = ActiveScreen.MAIN_HUB
                    currentTab = HubTab.WORLD_MAP
                }
            } else {
                // Trigger Character creation for empty slot
                withContext(Dispatchers.Main) {
                    currentScreen = ActiveScreen.CHARACTER_CREATE
                }
            }
        }
    }

    // Save Game State to DB
    fun saveGameProgress() {
        val activeSlot = selectedSlotId
        viewModelScope.launch {
            val entity = SaveSlotEntity(
                slotId = activeSlot,
                characterName = heroName,
                level = heroLevel,
                exp = heroExp,
                gold = heroGold,
                raceName = heroRace.name,
                pathName = heroPath.name,
                portraitIndex = heroPortraitIndex,
                
                strength = attributes["STR"] ?: 10,
                agility = attributes["DEX"] ?: 10,
                constitution = attributes["CON"] ?: 10,
                intelligence = attributes["INT"] ?: 10,
                will = attributes["WIL"] ?: 10,
                spirit = attributes["SPR"] ?: 10,
                perception = attributes["PER"] ?: 10,
                charisma = attributes["CHA"] ?: 10,
                attributePointsLeft = attributePointsLeft,
                skillPointsLeft = skillPointsLeft,
                
                currentHp = playerHpCurrent,
                maxHp = playerHpMax,
                currentMp = playerMpCurrent,
                maxMp = playerMpMax,
                
                currentRegionId = currentRegion.id,
                timeOfDayOrdinal = currentTimeOfDay.ordinal,
                weatherOrdinal = currentWeather.ordinal,
                seasonOrdinal = currentSeason.ordinal,
                daysElapsed = daysElapsed,
                lastSaveTime = System.currentTimeMillis(),
                
                serializedGear = packGear(),
                serializedInventory = packInventory(),
                serializedSkills = packSkills(),
                serializedQuests = packQuests(),
                serializedNpcRelations = packNpcRelations()
            )
            repository.saveSlot(entity)
            withContext(Dispatchers.Main) {
                activeSavedSlot = entity
            }
        }
    }

    // Fast serialization helpers
    private fun packGear(): String {
        return equippedGears.map { "${it.key.name}:${it.value.name}:${it.value.rarity.name}:${it.value.baseValue}:${it.value.upgradeLevel}" }.joinToString(";")
    }

    private fun unpackGear(data: String) {
        equippedGears.clear()
        if (data.isEmpty()) {
            equippedGears[GearSlot.WEAPON] = Gear("weapon1", "新手铁剑", GearSlot.WEAPON, GearRarity.COMMON, 12, "锋利", "勇者")
            equippedGears[GearSlot.TORSO] = Gear("armor1", "粗糙旅人衫", GearSlot.TORSO, GearRarity.DAMAGED, 5, "碎旧", "行者")
            return
        }
        try {
            data.split(";").forEach { item ->
                val tokens = item.split(":")
                if (tokens.size >= 5) {
                    val slot = GearSlot.valueOf(tokens[0])
                    val gearName = tokens[1]
                    val rarity = GearRarity.valueOf(tokens[2])
                    val baseValue = tokens[3].toInt()
                    val upgradeLevel = tokens[4].toInt()
                    equippedGears[slot] = Gear("gear_${slot.name}", gearName, slot, rarity, baseValue, upgradeLevel = upgradeLevel)
                }
            }
        } catch (e: Exception) {
            // fallback
            equippedGears[GearSlot.WEAPON] = Gear("weapon1", "新手铁剑", GearSlot.WEAPON, GearRarity.COMMON, 12)
        }
    }

    private fun packInventory(): String {
        return backpack.value.map { "${it.id}:${it.name}:${it.desc}:${it.hpRestore}:${it.mpRestore}:${it.bonusEffect}:${it.count}:${it.goldWorth}" }.joinToString(";")
    }

    private fun unpackInventory(data: String) {
        if (data.isEmpty()) {
            resetBackpack()
            return
        }
        try {
            val list = mutableListOf<Consumable>()
            data.split(";").forEach { item ->
                val tokens = item.split(":")
                if (tokens.size >= 8) {
                    list.add(Consumable(tokens[0], tokens[1], tokens[2], tokens[3].toInt(), tokens[4].toInt(), tokens[5], tokens[6].toInt(), tokens[7].toInt()))
                }
            }
            backpack.value = list
        } catch (e: Exception) {
            resetBackpack()
        }
    }

    private fun packSkills(): String {
        return customSkills.value.map { "${it.id}:${it.name}:${it.description}:${it.type}:${it.resourceCost}:${it.apCost}:${it.cooldown}:${it.level}:${it.xp}:${it.unlockLevel}" }.joinToString(";")
    }

    private fun unpackSkills(data: String) {
        if (data.isEmpty()) {
            setupInitialSkills()
            return
        }
        try {
            val list = mutableListOf<Skill>()
            data.split(";").forEach { item ->
                val tokens = item.split(":")
                if (tokens.size >= 10) {
                    list.add(Skill(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], tokens[5].toInt(), tokens[6].toInt(), tokens[7].toInt(), tokens[8].toInt(), tokens[9].toInt()))
                }
            }
            customSkills.value = list
        } catch (e: Exception) {
            setupInitialSkills()
        }
    }

    private fun packQuests(): String {
        return questLogByRegion.value.map { "${it.id}:${it.title}:${it.type.name}:${it.desc}:${it.objective}:${it.progress}:${it.maxProgress}:${it.expReward}:${it.goldReward}:${it.location}:${it.status}" }.joinToString(";")
    }

    private fun unpackQuests(data: String) {
        if (data.isEmpty()) return
        try {
            val list = mutableListOf<Quest>()
            data.split(";").forEach { item ->
                val tokens = item.split(":")
                if (tokens.size >= 11) {
                    list.add(Quest(tokens[0], tokens[1], QuestType.valueOf(tokens[2]), tokens[3], tokens[4], tokens[5].toInt(), tokens[6].toInt(), tokens[7].toInt(), tokens[8].toInt(), tokens[9], tokens[10]))
                }
            }
            questLogByRegion.value = list
        } catch (e: Exception) {
            // leave default
        }
    }

    private fun packNpcRelations(): String {
        return npcRelations.map { "${it.key}:${it.value}" }.joinToString(";")
    }

    private fun unpackNpcRelations(data: String) {
        if (data.isEmpty()) return
        try {
            data.split(";").forEach { item ->
                val tokens = item.split(":")
                if (tokens.size == 2) {
                    npcRelations[tokens[0]] = tokens[1].toInt()
                }
            }
        } catch (e: Exception) {
            // leave defaultes
        }
    }

    fun setupInitialSkills() {
        val list = mutableListOf<Skill>()
        when (heroPath) {
            StylePath.MAGIC_PATH -> {
                list.add(Skill("spell_fire", "爆炎豪火术", "元素毁灭者：远射爆裂。引燃草地5x5范围造成巨大火属性法伤，击碎障碍。", "主动", "15 MP", 6, 2, unlockLevel = 1))
                list.add(Skill("spell_shadow", "暗影瞬息步", "暗影编织者：无消耗位移，并极大提升下回合自身的斩杀致命概率。", "主动", "0 MP", 4, 3, unlockLevel = 1))
                list.add(Skill("spell_divine", "神圣天使壁垒", "神圣守护者：提供意志x3的防御吸收波，持续3回合，解除所有残废状态。", "主动", "20 MP", 5, 4, unlockLevel = 1))
            }
            StylePath.CULTIVATION_PATH -> {
                list.add(Skill("cult_sword", "天地极皇剑", "御剑天尊：灌满真元，贯通直线4格物理加真元混伤，并标记万剑归宗。", "主动", "20 真气", 6, 1, unlockLevel = 1))
                list.add(Skill("cult_taiji", "阴阳玄极逆转", "太极玄师：转换自身35%物理攻击为反伤盾，反弹一切头部/躯干受到的打击。", "主动", "15 真气", 5, 2, unlockLevel = 1))
                list.add(Skill("cult_alchemy", "灵芝起死丹", "炼丹药师：服用万灵金丹重塑生命状态，并对敌方武器附加致命烈毒层。", "主动", "10 真气", 4, 3, unlockLevel = 1))
            }
            StylePath.BODY_TEMPERING_PATH -> {
                list.add(Skill("body_guard", "不动万岳壁障", "铁壁战神：完全抵挡下一次攻击，强制吸引全网格内敌人优先对其嘲讽。", "主动", "30 怒气", 4, 2, unlockLevel = 1))
                list.add(Skill("body_fury", "灭世狂斩", "狂战杀神：挥舞重刃，基础伤害额外附加自己损失物理血量比值的爆发段。", "主动", "40 怒气", 6, 1, unlockLevel = 1))
                list.add(Skill("body_tame", "荒兽万鹰召唤", "驯兽猎王：在相邻空格生成威风凛凛的鹰宠猎狼，协同玩家独立发起咬杀击腿。", "主动", "20 怒气", 5, 3, unlockLevel = 1))
            }
        }
        customSkills.value = list
    }

    // Creating initial characters
    fun createNewCharacter(name: String, race: Race, path: StylePath, portraitIdx: Int) {
        heroName = name.ifEmpty { "命定之子" }
        heroRace = race
        heroPath = path
        heroPortraitIndex = portraitIdx
        heroLevel = 1
        heroExp = 0
        heroGold = 250
        daysElapsed = 1
        
        // Initializing Attribute points base values
        attributes["STR"] = 10 + race.strBonus
        attributes["DEX"] = 10 + race.dexBonus
        attributes["CON"] = 10 + race.conBonus
        attributes["INT"] = 10 + race.intBonus
        attributes["WIL"] = 10 + race.wilBonus
        attributes["SPR"] = 10 + race.sprBonus
        attributes["PER"] = 10 + race.perBonus
        attributes["CHA"] = 10 + race.chaBonus
        
        attributePointsLeft = 5
        skillPointsLeft = 2
        
        // Form Health and MP maxima
        playerHpMax = 70 + (attributes["CON"] ?: 10) * 8
        playerHpCurrent = playerHpMax
        playerMpMax = 30 + (attributes["INT"] ?: 10) * 5
        playerMpCurrent = playerMpMax
        
        currentRegion = MapRegion.DAWNHAVEN
        currentTimeOfDay = TimeOfDay.DAY
        currentWeather = Weather.SUNNY
        currentSeason = Season.SPRING
        oreMaterials = 8
        beastTideCountdown = 12
        isBeastTideActive = false
        
        equippedGears.clear()
        // Starter Weapons
        when (path) {
            StylePath.MAGIC_PATH -> {
                equippedGears[GearSlot.WEAPON] = Gear("staff_new", "旧象牙法杖", GearSlot.WEAPON, GearRarity.COMMON, 14, "咒法", "学者")
                equippedGears[GearSlot.TORSO] = Gear("robes_new", "学徒编织布质长袍", GearSlot.TORSO, GearRarity.COMMON, 4, "轻盈", "法徒")
            }
            StylePath.CULTIVATION_PATH -> {
                equippedGears[GearSlot.WEAPON] = Gear("sword_new", "桃木飞羽长剑", GearSlot.WEAPON, GearRarity.COMMON, 15, "除魔", "剑客")
                equippedGears[GearSlot.TORSO] = Gear("cloth_new", "苍翠流仙袍", GearSlot.TORSO, GearRarity.COMMON, 6, "流光", "散修")
            }
            StylePath.BODY_TEMPERING_PATH -> {
                equippedGears[GearSlot.WEAPON] = Gear("axe_new", "破柴铁板双刃斧", GearSlot.WEAPON, GearRarity.COMMON, 18, "裂石", "力士")
                equippedGears[GearSlot.TORSO] = Gear("armor_new", "厚皮革钉铁片战甲", GearSlot.TORSO, GearRarity.COMMON, 10, "坚固", "精兵")
            }
        }
        
        resetBackpack()
        initializeDefaultNpcRelations()
        initializeDefaultQuests()
        setupInitialSkills()
        
        // Save slots
        saveGameProgress()
        currentScreen = ActiveScreen.MAIN_HUB
        currentTab = HubTab.WORLD_MAP
    }

    // World Map - Travelling between regions
    fun travelToRegion(region: MapRegion) {
        if (currentRegion == region) return
        
        currentRegion = region
        incrementTimeStep(Random.nextInt(2, 5)) // Travel takes time
        processGatheringLogic()
        
        // Quest checks
        questLogByRegion.value = questLogByRegion.value.map { q ->
            if (q.status == "进行中" && q.location == region.id) {
                // progressive logic
                q
            } else q
        }
        
        saveGameProgress()
    }

    private fun processGatheringLogic() {
        if (Random.nextFloat() < 0.45f) {
            val amount = Random.nextInt(1, 4)
            oreMaterials += amount
            triggerLog("你在旅途中采集到了 $amount 份玄铁原石⚒️！")
        }
        if (Random.nextFloat() < 0.30f) {
            backpack.value = backpack.value.map { item ->
                if (item.id == "pot_hp_small") {
                    item.count += 1
                }
                item
            }
            triggerLog("行至草地深处，你顺手采获并炼制了1瓶 生命药水！")
        }
    }

    // Weather and Calendar Progression rules
    fun incrementTimeStep(steps: Int = 1) {
        var nextHourIdx = currentTimeOfDay.ordinal + steps
        val loops = nextHourIdx / TimeOfDay.values().size
        
        if (loops > 0) {
            daysElapsed += loops
            // Reduce beast tide count limit
            beastTideCountdown -= loops
            if (beastTideCountdown <= 0) {
                isBeastTideActive = true
                beastTideCountdown = Random.nextInt(6, 12)
            }
            
            // Season progression
            if (daysElapsed % 4 == 0) { // Every 4 days = 1 year season cycle
                val nextSeasonIdx = (currentSeason.ordinal + 1) % Season.values().size
                currentSeason = Season.values()[nextSeasonIdx]
                triggerLog("季节更替！世界跨入了深奥的【${currentSeason.displayName}】。")
            }
        }
        
        currentTimeOfDay = TimeOfDay.values()[nextHourIdx % TimeOfDay.values().size]
        
        // Shift weather code
        if (Random.nextFloat() < 0.35f) {
            val applicableWeathers = getApplicableWeathersForSeason(currentSeason)
            currentWeather = applicableWeathers.random()
            triggerLog("云海涌动，天气转变为【${currentWeather.displayName}】。")
        }
    }

    private fun getApplicableWeathersForSeason(season: Season): List<Weather> {
        return when (season) {
            Season.SPRING -> listOf(Weather.SUNNY, Weather.CLOUDY, Weather.LIGHT_RAIN, Weather.LIGHT_RAIN, Weather.FOG)
            Season.SUMMER -> listOf(Weather.SUNNY, Weather.SUNNY, Weather.CLOUDY, Weather.HEAVY_RAIN, Weather.STORM)
            Season.AUTUMN -> listOf(Weather.SUNNY, Weather.CLOUDY, Weather.CLOUDY, Weather.FOG, Weather.LIGHT_RAIN)
            Season.WINTER -> listOf(Weather.SOFTSNOW, Weather.SOFTSNOW, Weather.BLIZZARD, Weather.CLOUDY, Weather.LIGHT_RAIN)
        }
    }

    fun triggerLog(string: String) {
        val currentLogs = combatLog.value.toMutableList()
        currentLogs.add(0, string) // Insert at top
        if (currentLogs.size > 20) currentLogs.removeLast()
        combatLog.value = currentLogs
    }

    // Gifting to NPCs
    fun giftToNpc(npcId: String, giftName: String) {
        val currentRep = npcRelations[npcId] ?: 0
        val isNpcLoved = giftName == "烈酒" && npcId == "moira" || giftName == "魔法石" && npcId == "elenwe" || giftName == "饰铠" && npcId == "alaric"
        val isNpcDisliked = giftName == "草药" && npcId == "thrall"
        
        val change = if (isNpcLoved) 25 else if (isNpcDisliked) -10 else 10
        val newRep = (currentRep + change).coerceIn(-100, 100)
        npcRelations[npcId] = newRep
        
        // Consumables logic
        backpack.value = backpack.value.mapNotNull { item ->
            if (item.name == giftName) {
                item.count -= 1
                if (item.count <= 0) null else item
            } else item
        }
        
        triggerLog("你向 NPC 馈赠了 [${giftName}]！好感改变了 ${if(change>0) "+$change" else "$change"}。")
        saveGameProgress()
    }

    // Smithy Forge logic
    fun repairGears() {
        if (heroGold < 40) {
            triggerLog("金币严重不足！需要 40 枚金币进行全套铁砧保养。")
            return
        }
        heroGold -= 40
        equippedGears.forEach { (slot, gear) ->
            equippedGears[slot] = gear.copy(upgradeLevel = gear.upgradeLevel) // trigger refresh
        }
        triggerLog("格罗姆尼尔铁砧火星四溅，你的全套佩戴装备修理一新！")
        saveGameProgress()
    }

    fun upgradeGear(slot: GearSlot) {
        val gear = equippedGears[slot]
        if (gear == null) {
            triggerLog("该槽位未悬挂任何装备。")
            return
        }
        if (gear.upgradeLevel >= 5) {
            triggerLog("该古器已达到最大强化极限 (+5)。")
            return
        }
        val costOre = 2 + gear.upgradeLevel * 2
        val costGold = 50 + gear.upgradeLevel * 50
        
        if (oreMaterials < costOre) {
            triggerLog("锻造原石储备不足！需要 $costOre 颗原石，现有 $oreMaterials 颗。")
            return
        }
        if (heroGold < costGold) {
            triggerLog("金币不足！需要 $costGold 金币，现有 $heroGold。")
            return
        }
        
        oreMaterials -= costOre
        heroGold -= costGold
        
        // Success chance drops with tier
        val chance = 0.95f - (gear.upgradeLevel * 0.15f)
        if (Random.nextFloat() <= chance) {
            gear.upgradeLevel += 1
            equippedGears[slot] = gear
            triggerLog("强化成功！【${gear.name}】淬火进阶至 +${gear.upgradeLevel} 🚀！修正面板翻倍。")
        } else {
            if (gear.upgradeLevel > 0) gear.upgradeLevel -= 1
            equippedGears[slot] = gear
            triggerLog("⚒️叮！碎裂！强化失败。锻力回火，导致极意减退，降至 +${gear.upgradeLevel}。")
        }
        saveGameProgress()
    }

    // Stats allocation
    fun allocateStat(attrKey: String) {
        if (attributePointsLeft <= 0) return
        val currentVal = attributes[attrKey] ?: 10
        attributes[attrKey] = currentVal + 1
        attributePointsLeft -= 1
        
        // Recalculate caps
        playerHpMax = 70 + (attributes["CON"] ?: 10) * 8
        playerMpMax = 30 + (attributes["INT"] ?: 10) * 5
        
        triggerLog("已分配1点自由属性至 [$attrKey]！物理法力池重新灌注。")
        saveGameProgress()
    }

    // Breakthrough trial (every 10 level)
    fun attemptBreakthrough() {
        val neededXp = expNeededForNextLevel
        if (heroExp < neededXp) {
            triggerLog("经验元气不坚固，未满足境界提升。还需 ${neededXp - heroExp} 点历练。")
            return
        }
        // Success breakthrough
        heroExp -= neededXp
        heroLevel += 1
        attributePointsLeft += 5
        skillPointsLeft += 1
        
        playerHpMax = 70 + (attributes["CON"] ?: 10) * 8
        playerHpCurrent = playerHpMax
        playerMpMax = 30 + (attributes["INT"] ?: 10) * 5
        playerMpCurrent = playerMpMax
        
        triggerLog("💥乾坤通达！恭喜突破境界，你已升至第 【$heroLevel】 级！获得5自由点及天赋技能点。")
        saveGameProgress()
    }

    // Accept / Complete Quests
    fun toggleQuestStatus(quest: Quest) {
        val list = questLogByRegion.value.toMutableList()
        val index = list.indexOfFirst { it.id == quest.id }
        if (index != -1) {
            val q = list[index]
            when (q.status) {
                "未接取" -> {
                    q.status = "进行中"
                    triggerLog("接取任务：【${q.title}】！请前往指定区域执剑行事。")
                }
                "可交付", "进行中" -> {
                    // Claim awards immediately to make gameplay satisfying
                    q.status = "已完成"
                    q.progress = q.maxProgress
                    heroExp += q.expReward
                    heroGold += q.goldReward
                    triggerLog("完成任务！【${q.title}】交付结案！获得经验 +${q.expReward}，金币 +${q.goldReward}💰。")
                    saveGameProgress()
                }
            }
            questLogByRegion.value = list
        }
    }

    // Combat Setup Flow (Standard 10x10 turn combat)
    fun buildTacticalCombat(isBossSelection: Boolean = false) {
        inCombatMode = true
        combatRoundCount = 1
        combatLog.value = emptyList()
        triggerLog("⚔️ 发现敌踪！战棋等距网格 10x10 领域展开。")
        
        // generate layout cells
        val cells = mutableListOf<GridCell>()
        for (y in 0 until 10) {
            for (x in 0 until 10) {
                val terrain = when {
                    (x == 4 && y == 4) || (x == 5 && y == 4) -> TerrainType.WALL_ROCK
                    x == 0 || x == 9 || y == 9 -> TerrainType.SWAMP
                    y in 2..3 && isBossSelection -> TerrainType.LAVA_FIRE
                    y in 5..6 && currentRegion == MapRegion.IRONSPINE -> TerrainType.ICE_FLOE
                    else -> TerrainType.PLAIN
                }
                val elev = if (terrain == TerrainType.WALL_ROCK) 2 else if (x in 1..2 && y in 3..4) 1 else 0
                cells.add(GridCell(x, y, terrain, elevation = elev))
            }
        }
        combatGrid.value = cells

        // Character build
        val playerAttack = 10 + (attributes["STR"] ?: 10) + (equippedGears[GearSlot.WEAPON]?.getEffectiveStat() ?: 0)
        val playerDef = 2 + (attributes["CON"] ?: 10)/2 + (equippedGears[GearSlot.TORSO]?.getEffectiveStat() ?: 0)
        
        val playerParts = BodyPart.values().map { part ->
            val maxHP = when (part) {
                BodyPart.HEAD -> (playerHpMax * 0.15f)
                BodyPart.TORSO -> (playerHpMax * 0.30f)
                BodyPart.R_ARM, BodyPart.L_ARM, BodyPart.R_LEG, BodyPart.L_LEG -> (playerHpMax * 0.12f)
                BodyPart.WAIST -> (playerHpMax * 0.07f)
            }
            BodyPartHP(part, maxHP, maxHP)
        }
        
        val enemyName = if (isBossSelection || isBeastTideActive) {
            when (currentRegion) {
                MapRegion.DAWNHAVEN -> "野猪王"
                MapRegion.SILVERCROWN -> "帝国重装逆叛骑士 🛡️ [BOSS]"
                MapRegion.IRONSPINE -> "极域冰霜远古巨龙 🐉 [BOSS]"
                MapRegion.REDWASTE -> "赤荒炎焰怒火萨满 🌋 [BOSS]"
                MapRegion.EMERALD_LABYRINTH -> "永恒腐化万年树人 🌲 [BOSS]"
                MapRegion.BLACK_WAVE_WASTE -> "深渊终末黑潮主宰 👁️ [BOSS]"
            }
        } else {
            listOf("利齿哥布林", "荒原骷髅弓手", "赤荒小狼崽", "深渊爬行食尸兽").random()
        }

        val enemyColor = if (isBossSelection || isBeastTideActive) Color(0xFFFF4D4D) else Color(0xFFFFB300)
        val enemyHpMultiplier = if (isBossSelection || isBeastTideActive) 3f else 1f
        val enemyMaxHp = (110 + currentRegion.dangerLevel * 6 * enemyHpMultiplier).toInt()
        
        val enemyParts = BodyPart.values().map { part ->
            val maxHP = when (part) {
                BodyPart.HEAD -> (enemyMaxHp * 0.15f)
                BodyPart.TORSO -> (enemyMaxHp * 0.30f)
                BodyPart.R_ARM, BodyPart.L_ARM, BodyPart.R_LEG, BodyPart.L_LEG -> (enemyMaxHp * 0.12f)
                BodyPart.WAIST -> (enemyMaxHp * 0.07f)
            }
            BodyPartHP(part, maxHP, maxHP)
        }

        combatPlayer = Combatant(
            name = heroName,
            isPlayer = true,
            x = 4, y = 8, // bottom center
            race = heroRace,
            path = heroPath,
            maxHp = playerHpMax,
            currentHp = playerHpCurrent,
            maxMp = playerMpMax,
            currentMp = playerMpCurrent,
            defense = playerDef,
            attackRating = playerAttack,
            bodyParts = playerParts,
            spriteColor = Color(0xFFFF8C00)
        )

        combatEnemy = Combatant(
            name = enemyName,
            isPlayer = false,
            x = 5, y = 1, // top center
            race = null,
            path = null,
            maxHp = enemyMaxHp,
            currentHp = enemyMaxHp,
            maxMp = enemyMaxHp/2,
            currentMp = enemyMaxHp/2,
            defense = currentRegion.dangerLevel + 2,
            attackRating = 12 + currentRegion.dangerLevel,
            bodyParts = enemyParts,
            spriteColor = enemyColor,
            aiType = if (isBossSelection || isBeastTideActive) "战术型" else listOf("激进型", "防御型").random()
        )
        
        // If there's an ongoing beast tide and we fought, clear it
        if (isBeastTideActive) {
            isBeastTideActive = false
        }
        
        activePlayerIdx = if (Random.nextFloat() <= 0.5f) 0 else 1
        triggerLog("${if (activePlayerIdx == 0) "【我方】" else "【敌方】"} 先攻！各显命运图腾之光。")
    }

    // Tactical combat action execution
    fun moveCombatPlayer(targetX: Int, targetY: Int) {
        val player = combatPlayer ?: return
        if (player.ap < 2) {
            triggerLog("⚠️ AP 不足！移动1格最少需要 2点AP（困难地形多扣）。")
            return
        }
        
        val cell = combatGrid.value.find { it.x == targetX && it.y == targetY }
        if (cell == null || cell.terrain == TerrainType.WALL_ROCK) {
            triggerLog("阻挡石壁或虚空无法通行。")
            return
        }
        
        // distance check (exactly adjacent check to avoid teleporting)
        val dist = Math.abs(player.x - targetX) + Math.abs(player.y - targetY)
        if (dist > 1) {
            triggerLog("战棋仅能每次挪动 1格 格位。")
            return
        }
        
        val apCost = cell.terrain.apCost
        player.ap -= apCost
        player.x = targetX
        player.y = targetY
        triggerLog("玩家挪移至 ($targetX, $targetY) 清理落脚，扣除 $apCost AP。")
        
        // terrain interaction
        if (cell.terrain == TerrainType.LAVA_FIRE) {
            player.currentHp -= 12
            triggerLog("🔥踩中炽热熔岩！【我方】受到 12 点溅射火属性伤。")
        }

        checkCombatWinLossState()
    }

    fun passTurn() {
        val player = combatPlayer ?: return
        player.ap = 0
        triggerLog("【玩家】宣布防御整休，本回合AP归零。")
        executeEnemyAiDecisionTurn()
    }

    fun executeCombatPhysAttack() {
        val player = combatPlayer ?: return
        val enemy = combatEnemy ?: return
        
        if (player.ap < 6) {
            triggerLog("AP 不足！发起普通攻击需要消耗 6 点 AP。")
            return
        }
        
        player.ap -= 6
        
        // Range check
        val dist = Math.abs(player.x - enemy.x) + Math.abs(player.y - enemy.y)
        if (dist > 2 && heroPath != StylePath.MAGIC_PATH) {
            triggerLog("超射程了！近战武技无法击中远于2格的敌人。")
            return
        }
        
        // Targeted body part calculation
        val targetPartEnum = BodyPart.values()[selectedTargetPartIdx]
        val targetPartObj = enemy.bodyParts[selectedTargetPartIdx]
        
        val hitRate = 75 + ((attributes["DEX"] ?: 10) - 10) * 2 - (if (targetPartEnum == BodyPart.HEAD) 20 else 0)
        
        if (Random.nextInt(0, 100) < hitRate) {
            // Success
            val baseDamage = player.attackRating - enemy.defense
            val damageMultiplier = when (targetPartEnum) {
                BodyPart.HEAD -> 1.8f
                BodyPart.TORSO -> 1.0f
                BodyPart.R_ARM, BodyPart.L_ARM, BodyPart.R_LEG, BodyPart.L_LEG -> 0.85f
                BodyPart.WAIST -> 1.1f
            }
            val damage = (baseDamage * damageMultiplier).toInt().coerceAtLeast(1)
            
            // Apply damage to body part
            targetPartObj.currentHp = (targetPartObj.currentHp - damage).coerceAtLeast(0f)
            enemy.currentHp = (enemy.currentHp - damage).coerceAtLeast(0)
            
            var breakDesc = ""
            if (targetPartObj.currentHp <= 0f && !targetPartObj.isCrippled) {
                targetPartObj.isCrippled = true
                breakDesc = " ⚠️部位破碎！敌人受到了【残废限制】！"
                if (targetPartEnum == BodyPart.R_ARM || targetPartEnum == BodyPart.L_ARM) {
                    enemy.attackRating = (enemy.attackRating * 0.5f).toInt()
                }
            }
            
            triggerLog("🎯命中！【我方】重重砸中了敌方的【${targetPartEnum.displayName}】，造成 $damage 点伤害！$breakDesc")
        } else {
            triggerLog("💨闪避！敌方一个敏捷滚翻，我方重击落空失之毫厘。")
        }
        
        checkCombatWinLossState()
    }

    fun executeCombatSkill(skill: Skill) {
        val player = combatPlayer ?: return
        val enemy = combatEnemy ?: return
        
        if (player.ap < skill.apCost) {
            triggerLog("AP严重枯竭！需要 ${skill.apCost} AP 施展 ${skill.name}。")
            return
        }
        
        player.ap -= skill.apCost
        player.currentMp = (player.currentMp - 15).coerceAtLeast(0)
        
        val randDmg = (player.attackRating * 1.5f).toInt() + Random.nextInt(5, 10)
        enemy.currentHp = (enemy.currentHp - randDmg).coerceAtLeast(0)
        
        // Random body part collateral damage inside RPG design
        val hitPartIdx = Random.nextInt(0, 7)
        val part = enemy.bodyParts[hitPartIdx]
        part.currentHp = (part.currentHp - randDmg/2).coerceAtLeast(0f)
        
        triggerLog("💥怒吼！我方施放了【${skill.name}】！轰击对敌方全身造成毁坏性法术/真元大伤 $randDmg 点！敌方【${part.part.displayName}】受到余震侵蚀。")
        
        // level skill xp
        skill.xp += 30
        if (skill.xp >= skill.level * 100 && skill.level < 5) {
            skill.level += 1
            skill.xp = 0
            triggerLog("⚡ 绝世熟练度提升！技能【${skill.name}】已晋升为等级 ${skill.level}！威力威能极大回馈。")
        }

        checkCombatWinLossState()
    }

    fun drinkCombatPotion() {
        val player = combatPlayer ?: return
        
        val hpPot = backpack.value.find { it.id == "pot_hp_small" }
        if (hpPot == null || hpPot.count <= 0) {
            triggerLog("药快捷包空了！")
            return
        }
        
        hpPot.count -= 1
        player.ap = (player.ap - 4).coerceAtLeast(0)
        player.currentHp = (player.currentHp + 45).coerceAtMost(player.maxHp)
        playerHpCurrent = player.currentHp
        
        // Reset bleeding body parts
        player.bodyParts.forEach {
            it.currentHp = it.maxHp
            it.isCrippled = false
            it.isBleeding = false
        }
        
        backpack.value = backpack.value.filter { it.count > 0 }
        triggerLog("🩹畅饮！我方喝下了 生命秘露，生命恢复 45，并瞬间止血弥合物理各处骨裂！")
        
        checkCombatWinLossState()
    }

    private fun checkCombatWinLossState() {
        val player = combatPlayer ?: return
        val enemy = combatEnemy ?: return
        
        if (enemy.currentHp <= 0) {
            // Victory
            val gainedGold = 25 + currentRegion.dangerLevel * 4 + Random.nextInt(10, 30)
            val gainedExp = 40 + currentRegion.dangerLevel * 6
            val gainedOre = if (Random.nextFloat() <= 0.65f) 2 else 0
            
            heroGold += gainedGold
            heroExp += gainedExp
            oreMaterials += gainedOre
            playerHpCurrent = player.currentHp
            
            // Quest completion checking
            questLogByRegion.value = questLogByRegion.value.map { q ->
                if (q.status == "进行中" && q.location == currentRegion.id) {
                    q.progress = (q.progress + 1).coerceAtMost(q.maxProgress)
                    if (q.progress == q.maxProgress) {
                        q.status = "可交付"
                    }
                }
                q
            }
            
            lastCombatRewardSummary = "【战斗大捷】！敌方轰然倒地。你获得了 历练 +$gainedExp XP，战利金币 +$gainedGold，铁匠玄石原材 +$gainedOre ⚒️！"
            inCombatMode = false
            saveGameProgress()
            return
        }
        
        if (player.currentHp <= 0) {
            // Defeated fallback - Respawn in Dawnhaven safely
            playerHpCurrent = (playerHpMax * 0.35f).toInt()
            currentRegion = MapRegion.DAWNHAVEN
            lastCombatRewardSummary = "【命运遗恨】！你力竭战败，在重伤之余，被晨曦镇的温顺山野猎人背回了安全营区。所幸没有死亡，修养了身体。损耗部分药剂。"
            inCombatMode = false
            saveGameProgress()
            return
        }
        
        // Turn progression check
        if (player.ap <= 0) {
            executeEnemyAiDecisionTurn()
        }
    }

    // AI actions simulation based on behavior patterns
    private fun executeEnemyAiDecisionTurn() {
        val player = combatPlayer ?: return
        val enemy = combatEnemy ?: return
        
        combatRoundCount += 1
        enemy.ap = 10
        triggerLog("⌛回合更替 --> 敌手【${enemy.name}】开始执子行动。")
        
        // Deciding AI actions
        val dist = Math.abs(player.x - enemy.x) + Math.abs(player.y - enemy.y)
        
        if (dist > 2) {
            // Move closer
            val dx = (player.x - enemy.x).coerceIn(-1, 1)
            val dy = (player.y - enemy.y).coerceIn(-1, 1)
            enemy.x += dx
            enemy.y += dy
            triggerLog("敌手【${enemy.name}】依靠荒野直觉伏步移动，拉近战局至 (${enemy.x}, ${enemy.y})。")
        } else {
            // Targeted hit physically on player
            val randPartIdx = Random.nextInt(0, 7)
            val playerPart = player.bodyParts[randPartIdx]
            
            val hitRate = 70 + currentRegion.dangerLevel/2 - (if (randPartIdx == 0) 15 else 0)
            if (Random.nextInt(0, 100) < hitRate) {
                val dmg = (enemy.attackRating - player.defense).coerceAtLeast(2)
                playerPart.currentHp = (playerPart.currentHp - dmg).coerceAtLeast(0f)
                player.currentHp = (player.currentHp - dmg).coerceAtLeast(0)
                playerHpCurrent = player.currentHp
                
                var statusCripple = ""
                if (playerPart.currentHp <= 0f && !playerPart.isCrippled) {
                    playerPart.isCrippled = true
                    statusCripple = " 😭部位断折残废！"
                }
                triggerLog("💥遭袭！敌方瞄准我方【${playerPart.part.displayName}】发起狠辣凿砸，对我方造成 $dmg 物理伤！$statusCripple")
            } else {
                triggerLog("️🛡️格挡！我方利用盾牌/护腕，强防卸力挡下了敌足攻势！")
            }
        }
        
        // Reset player action points for next turn
        player.ap = 10
        checkCombatWinLossState()
    }

    // Trigger complete reset (for deleting save profiles)
    fun deleteSaveSlot(slotId: Int) {
        viewModelScope.launch {
            repository.deleteSlotById(slotId)
        }
    }
}
