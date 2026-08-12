package com.wannianli

import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.floor

/**
 * 农历、干支、节气、星座、宜忌等计算工具
 */
object LunarCalendar {

    private val heavenlyStems = arrayOf("甲","乙","丙","丁","戊","己","庚","辛","壬","癸")
    private val earthlyBranches = arrayOf("子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥")
    private val zodiacAnimals = arrayOf("鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪")
    private val lunarMonths = arrayOf("正","二","三","四","五","六","七","八","九","十","冬","腊")
    private val lunarDays = arrayOf(
        "初一","初二","初三","初四","初五","初六","初七","初八","初九","初十",
        "十一","十二","十三","十四","十五","十六","十七","十八","十九","二十",
        "廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九","三十"
    )

    // 1900-2100 农历数据，每年一个整数（3字节）
    // bits 0-3：闰月月份（0 表示无闰月）
    // bits 4-15：农历 1-12 月大小，bit15=正月，bit4=腊月（1=30天，0=29天）
    // bit 16：闰月大小（1=30天，0=29天）
    private val lunarInfo = intArrayOf(
        0x04bd8,0x04ae0,0x0a570,0x054d5,0x0d260,0x0d950,0x16554,0x056a0,0x09ad0,0x055d2,
        0x04ae0,0x0a5b6,0x0a4d0,0x0d250,0x1d255,0x0b540,0x0d6a0,0x0ada2,0x095b0,0x14977,
        0x04970,0x0a4b0,0x0b4b5,0x06a50,0x06d40,0x1ab54,0x02b60,0x09570,0x052f2,0x04970,
        0x06566,0x0d4a0,0x0ea50,0x06e95,0x05ad0,0x02b60,0x186e3,0x092e0,0x1c8d7,0x0c950,
        0x0d4a0,0x1d8a6,0x0b550,0x056a0,0x1a5b4,0x025d0,0x092d0,0x0d2b2,0x0a950,0x0b557,
        0x06ca0,0x0b550,0x15355,0x04da0,0x0a5b0,0x14573,0x052b0,0x0a9a8,0x0e950,0x06aa0,
        0x0aea6,0x0ab50,0x04b60,0x0aae4,0x0a570,0x05260,0x0f263,0x0d950,0x05b57,0x056a0,
        0x096d0,0x04dd5,0x04ad0,0x0a4d0,0x0d4d4,0x0d250,0x0d558,0x0b540,0x0b6a0,0x195a6,
        0x095b0,0x049b0,0x0a974,0x0a4b0,0x0b27a,0x06a50,0x06d40,0x0af46,0x0ab60,0x09570,
        0x04af5,0x04970,0x064b0,0x074a3,0x0ea50,0x06b58,0x05ac0,0x0ab60,0x096d5,0x092e0,
        0x0c960,0x0d954,0x0d4a0,0x0da50,0x07552,0x056a0,0x0abb7,0x025d0,0x092d0,0x0cab5,
        0x0a950,0x0b4a0,0x0baa4,0x0ad50,0x055d9,0x04ba0,0x0a5b0,0x15176,0x052b0,0x0a930,
        0x07954,0x06aa0,0x0ad50,0x05b52,0x04b60,0x0a6e6,0x0a4e0,0x0d260,0x0ea65,0x0d530,
        0x05aa0,0x076a3,0x096d0,0x04afb,0x04ad0,0x0a4d0,0x1d0b6,0x0d250,0x0d520,0x0dd45,
        0x0b5a0,0x056d0,0x055b2,0x049b0,0x0a577,0x0a4b0,0x0aa50,0x1b255,0x06d20,0x0ada0,
        0x14b63,0x09370,0x049f8,0x04970,0x064b0,0x168a6,0x0ea50,0x06b20,0x1a6c4,0x0aae0,
        0x0a2e0,0x0d2e3,0x0c960,0x0d557,0x0d4a0,0x0da50,0x05d55,0x056a0,0x0a6d0,0x055d4,
        0x052d0,0x0a9b8,0x0a950,0x0b4a0,0x0b6a6,0x0ad50,0x055a0,0x0aba4,0x0a5b0,0x052b0,
        0x0b273,0x06930,0x07337,0x06aa0,0x0ad50,0x14b55,0x04b60,0x0a570,0x054e4,0x0d160,
        0x0e968,0x0d520,0x0daa0,0x16aa6,0x056d0,0x04ae0,0x0a9d4,0x0a2d0,0x0d150,0x0f252,
        0x0d520
    )

    private const val BASE_YEAR = 1900
    private val baseDate = GregorianCalendar(BASE_YEAR, 0, 31) // 1900-01-31 春节

    // 24 节气名称
    private val solarTerms = arrayOf(
        "小寒","大寒","立春","雨水","惊蛰","春分","清明","谷雨","立夏","小满","芒种","夏至",
        "小暑","大暑","立秋","处暑","白露","秋分","寒露","霜降","立冬","小雪","大雪","冬至"
    )

    // 节气数据：每个节气对应1900-2100的C值（简化算法）
    // 实际上使用天文公式计算
    // 立春 C = 4.6295, 雨水 19.4599 等
    private val termCs = doubleArrayOf(
        6.11,20.84,4.63,19.46,6.11,21.04,5.34,20.45,6.12,21.31,5.61,21.94,
        7.38,23.08,7.61,23.41,8.44,24.06,8.73,23.97,8.35,22.83,7.93,22.07
    )

    // 干支时辰（子时 23-1 开始）
    private val shichen = arrayOf(
        "子时","丑时","寅时","卯时","辰时","巳时","午时","未时","申时","酉时","戌时","亥时"
    )

    // 彭祖百忌（天干10条，地支12条）
    private val pzTianGan = arrayOf(
        "甲不开仓财物耗散","乙不栽植千株不长","丙不修灶必见灾殃","丁不剃头头必生疮",
        "戊不受田田主不祥","己不破券二比并亡","庚不经络织机虚张","辛不合酱主人不尝",
        "壬不汲水更难提防","癸不词讼理弱敌强"
    )
    private val pzDiZhi = arrayOf(
        "子不问卜自惹祸殃","丑不冠带主不还乡","寅不祭祀神鬼不尝","卯不穿井水泉不香",
        "辰不哭泣必主重丧","巳不远行财物伏藏","午不苫盖屋主更张","未不服药毒气入肠",
        "申不安床鬼祟入房","酉不宴客醉坐颠狂","戌不吃犬作怪上床","亥不嫁娶不利新郎"
    )

    // 胎神占方（地支12条）
    private val taiShen = arrayOf(
        "占门碓外东南","碓磨厕外东南","炉灶炉外正南","大门外正南","房床厕外正东",
        "占门床外正南","占碓磨外正南","厨灶厕外西南","仓库炉外西南","房床门外西南",
        "门鸡栖外西南","碓磨床外西南"
    )

    // 五行：年纳音（60甲子）
    private val naYin = arrayOf(
        "海中金","海中金","炉中火","炉中火","大林木","大林木","路旁土","路旁土","剑锋金","剑锋金",
        "山头火","山头火","涧下水","涧下水","城头土","城头土","白蜡金","白蜡金","杨柳木","杨柳木",
        "泉中水","泉中水","屋上土","屋上土","霹雳火","霹雳火","松柏木","松柏木","长流水","长流水",
        "沙中金","沙中金","山下火","山下火","平地木","平地木","壁上土","壁上土","金箔金","金箔金",
        "覆灯火","覆灯火","天河水","天河水","大驿土","大驿土","钗钏金","钗钏金","桑柘木","桑柘木",
        "大溪水","大溪水","沙中土","沙中土","天上火","天上火","石榴木","石榴木","大海水","大海水"
    )

    // 60 甲子索引表
    private val ganzhiIndex: Map<String, Int> by lazy {
        val map = mutableMapOf<String, Int>()
        for (i in 0 until 60) {
            val gan = heavenlyStems[i % 10]
            val zhi = earthlyBranches[i % 12]
            map["$gan$zhi"] = i
        }
        map
    }

    // 天干五行、地支五行
    private val tgWuXing = arrayOf("木","木","火","火","土","土","金","金","水","水")
    private val dzWuXing = arrayOf("水","土","木","木","土","火","火","土","金","金","土","水")

    // 二十八宿（按日排列，从正月初一开始）
    private val xiu28 = arrayOf(
        "角","亢","氐","房","心","尾","箕","斗","牛","女","虚","危","室","壁","奎","娄","胃","昴","毕","觜","参","井","鬼","柳","星","张","翼","轸"
    )
    private val xiuAnimals = mapOf(
        "角" to "角木蛟","亢" to "亢金龙","氐" to "氐土貉","房" to "房日兔","心" to "心月狐",
        "尾" to "尾火虎","箕" to "箕水豹","斗" to "斗木獬","牛" to "牛金牛","女" to "女土蝠",
        "虚" to "虚日鼠","危" to "危月燕","室" to "室火猪","壁" to "壁水貐","奎" to "奎木狼",
        "娄" to "娄金狗","胃" to "胃土雉","昴" to "昴日鸡","毕" to "毕月乌","觜" to "觜火猴",
        "参" to "参水猿","井" to "井木犴","鬼" to "鬼金羊","柳" to "柳土獐","星" to "星日马",
        "张" to "张月鹿","翼" to "翼火蛇","轸" to "轸水蚓"
    )

    // 六曜
    private val liuYao = arrayOf("大安","留连","速喜","赤口","小吉","空亡")

    // 十二神（建除十二神）
    private val shiErShen = arrayOf("建","除","满","平","定","执","破","危","成","收","开","闭")

    // 十二建星对应月地支偏移
    // 正月建寅 => 寅月从寅日开始建

    // 地支相冲
    private val chongMap = mapOf(
        "子" to "午","丑" to "未","寅" to "申","卯" to "酉","辰" to "戌","巳" to "亥",
        "午" to "子","未" to "丑","申" to "寅","酉" to "卯","戌" to "辰","亥" to "巳"
    )

    // 地支三合煞
    private val shaMap = mapOf(
        "寅" to "北","午" to "北","戌" to "北",
        "申" to "南","子" to "南","辰" to "南",
        "亥" to "东","卯" to "东","未" to "东",
        "巳" to "西","酉" to "西","丑" to "西"
    )

    // 伊斯兰历粗略转换（平伊历354天）
    private const val HIJRI_EPOCH_DAYS = -492148L // 622-07-16 Julian

    /**
     * 公历转农历信息
     */
    data class LunarDate(
        val lunarYear: Int,
        val lunarMonth: Int, // 1-12
        val lunarDay: Int,
        val isLeapMonth: Boolean,
        val isBigMonth: Boolean,
        val zodiac: String,
        val yearGanZhi: String,
        val monthGanZhi: String,
        val dayGanZhi: String,
        val lunarMonthName: String,
        val lunarDayName: String,
        val festival: String?,
        val solarTerm: String?
    )

    enum class RestType { HOLIDAY, WEEKEND, WORKDAY, NORMAL }

    data class DayInfo(
        val solarYear: Int,
        val solarMonth: Int,
        val solarDay: Int,
        val weekDay: String,
        val lunar: LunarDate,
        val solarTerm: String?, // 当天或最近节气
        val nextSolarTerm: Pair<String, Int>?, // 距下一节气
        val yi: String,
        val ji: String,
        val constellation: String,
        val pzBaiJi: String,
        val taiShen: String,
        val yearNaYin: String,
        val monthWuXing: String,
        val dayWuXing: String,
        val xiu: String,
        val season: String,
        val julianDay: Double,
        val buddhaYear: String,
        val hijri: String,
        val chong: String,
        val sha: String,
        val liuYao: String,
        val shiErShen: String,
        val restType: RestType
    )

    fun getDayInfo(year: Int, month: Int, day: Int): DayInfo {
        val cal = GregorianCalendar(year, month - 1, day)
        val weekDays = arrayOf("星期日","星期一","星期二","星期三","星期四","星期五","星期六")
        val weekDay = weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1]
        val isSaturday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        val isSunday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        val lunar = solarToLunar(year, month, day)
        val restType = getRestType(year, month, day, isSaturday, isSunday)
        val solarTerm = getSolarTerm(year, month, day)
        val nextSolarTerm = getNextSolarTerm(year, month, day)
        val (yi, ji) = getYiJi(lunar)
        val gz = lunar.dayGanZhi
        val dayGan = gz[0].toString()
        val dayZhi = gz[1].toString()
        val monthZhi = lunar.monthGanZhi[1].toString()
        return DayInfo(
            solarYear = year,
            solarMonth = month,
            solarDay = day,
            weekDay = weekDay,
            lunar = lunar,
            solarTerm = solarTerm,
            nextSolarTerm = nextSolarTerm,
            yi = yi,
            ji = ji,
            constellation = getConstellation(month, day),
            pzBaiJi = pzTianGan[heavenlyStems.indexOf(dayGan)] + "，" + pzDiZhi[earthlyBranches.indexOf(dayZhi)],
            taiShen = taiShen[earthlyBranches.indexOf(dayZhi)],
            yearNaYin = getYearNaYin(lunar.lunarYear),
            monthWuXing = getNaYin(lunar.monthGanZhi),
            dayWuXing = getNaYin(lunar.dayGanZhi),
            xiu = get28Xiu(lunar.lunarYear, lunar.lunarMonth, lunar.lunarDay),
            season = getSeason(month, day),
            julianDay = getJulianDay(year, month, day),
            buddhaYear = "${year + 544}年",
            hijri = solarToHijri(year, month, day),
            chong = "${zodiacAnimals[(earthlyBranches.indexOf(dayZhi) + 6) % 12]}（${getChongGanZhi(dayGan, dayZhi)}）",
            sha = shaMap[dayZhi] ?: "",
            liuYao = getLiuYao(year, month, day),
            shiErShen = getShiErShen(lunar.lunarYear, lunar.lunarMonth, dayZhi),
            restType = restType
        )
    }

    /**
     * 判断当天休息日类型：法定假日/周末休息/调休上班/普通工作日
     */
    fun getRestType(solarYear: Int, solarMonth: Int, solarDay: Int, isSaturday: Boolean, isSunday: Boolean): RestType {
        if (legalHolidays[solarYear]?.contains(Pair(solarMonth, solarDay)) == true) return RestType.HOLIDAY
        if (isSaturday || isSunday) {
            return if (adjustedWorkdays[solarYear]?.contains(Pair(solarMonth, solarDay)) == true) RestType.WORKDAY else RestType.WEEKEND
        }
        return RestType.NORMAL
    }

    // 法定假日日期集合（公历，已包含调休连休的周末）
    private val legalHolidays = mapOf(
        2025 to setOf(
            Pair(1,1),
            Pair(1,28), Pair(1,29), Pair(1,30), Pair(1,31),
            Pair(2,1), Pair(2,2), Pair(2,3),
            Pair(4,4), Pair(4,5), Pair(4,6),
            Pair(5,1), Pair(5,2), Pair(5,3), Pair(5,4), Pair(5,5),
            Pair(5,31), Pair(6,1), Pair(6,2),
            Pair(10,1), Pair(10,2), Pair(10,3), Pair(10,4), Pair(10,5), Pair(10,6), Pair(10,7), Pair(10,8)
        ),
        2026 to setOf(
            Pair(1,1), Pair(1,2), Pair(1,3),
            Pair(2,17), Pair(2,18), Pair(2,19), Pair(2,20), Pair(2,21), Pair(2,22), Pair(2,23),
            Pair(4,4), Pair(4,5), Pair(4,6),
            Pair(5,1), Pair(5,2), Pair(5,3), Pair(5,4), Pair(5,5),
            Pair(6,19), Pair(6,20), Pair(6,21),
            Pair(9,25), Pair(9,26), Pair(9,27),
            Pair(10,1), Pair(10,2), Pair(10,3), Pair(10,4), Pair(10,5), Pair(10,6), Pair(10,7)
        ),
        2027 to setOf(
            Pair(1,1), Pair(1,2), Pair(1,3),
            Pair(2,6), Pair(2,7), Pair(2,8), Pair(2,9), Pair(2,10), Pair(2,11), Pair(2,12),
            Pair(4,3), Pair(4,4), Pair(4,5),
            Pair(5,1), Pair(5,2), Pair(5,3), Pair(5,4), Pair(5,5),
            Pair(6,9), Pair(6,10), Pair(6,11),
            Pair(9,15), Pair(9,16), Pair(9,17),
            Pair(10,1), Pair(10,2), Pair(10,3), Pair(10,4), Pair(10,5), Pair(10,6), Pair(10,7)
        )
    )

    // 调休上班日期（周末被调整为工作日）
    private val adjustedWorkdays = mapOf(
        2025 to setOf(
            Pair(1,26), Pair(2,8),
            Pair(4,27),
            Pair(9,28), Pair(10,11)
        ),
        2026 to setOf(
            Pair(2,14), Pair(2,24),
            Pair(4,11),
            Pair(4,26), Pair(5,9),
            Pair(6,27),
            Pair(9,20),
            Pair(10,10)
        ),
        2027 to setOf(
            Pair(2,20),
            Pair(4,10),
            Pair(4,25), Pair(5,8),
            Pair(6,12),
            Pair(9,19),
            Pair(9,26), Pair(10,9)
        )
    )

    private fun getChongGanZhi(gan: String, zhi: String): String {
        val ganIdx = heavenlyStems.indexOf(gan)
        val zhiIdx = earthlyBranches.indexOf(zhi)
        val chongGanIdx = (ganIdx + 6) % 10
        val chongZhiIdx = (zhiIdx + 6) % 12
        return heavenlyStems[chongGanIdx] + earthlyBranches[chongZhiIdx]
    }

    private fun getYearNaYin(year: Int): String {
        val gzIndex = (year - 4) % 60
        return naYin[gzIndex]
    }

    private fun getNaYin(ganZhi: String): String {
        val idx = ganzhiIndex[ganZhi] ?: 0
        return naYin[idx]
    }

    private fun getSeason(month: Int, day: Int): String {
        // 以立春、立夏、立秋、立冬为季节分界（简化按月份）
        return when(month){
            3,4,5 -> "春季"
            6,7,8 -> "夏季"
            9,10,11 -> "秋季"
            else -> "冬季"
        }
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
        val a = floor((14 - month) / 12.0)
        val y = year + 4800 - a.toInt()
        val m = month + 12 * a.toInt() - 3
        return day + floor((153 * m + 2) / 5.0) + 365 * y + floor(y / 4.0) - floor(y / 100.0) + floor(y / 400.0) - 32045.0
    }

    private fun solarToHijri(year: Int, month: Int, day: Int): String {
        val jd = getJulianDay(year, month, day)
        val daysSinceEpoch = (jd - 1948439.5).toLong() // 伊斯兰历纪元儒略日
        val lunarYears = daysSinceEpoch / 354
        val remaining = daysSinceEpoch % 354
        val hijriYear = 1 + lunarYears.toInt()
        val hijriMonth = 1 + (remaining / 29.5).toInt()
        val hijriDay = 1 + (remaining % 29.5).toInt()
        return "${hijriYear}年${hijriMonth}月${hijriDay}日"
    }

    private fun getLiuYao(year: Int, month: Int, day: Int): String {
        // 简化：以当年正月初一为起点，按日序对6取模
        val lunar = solarToLunar(year, month, day)
        val daysFromSpring = daysBetween(baseDate, GregorianCalendar(year, month - 1, day))
        return liuYao[(((daysFromSpring % 6) + 6) % 6).toInt()]
    }

    private fun getShiErShen(lunarYear: Int, lunarMonth: Int, dayZhi: String): String {
        // 正月建寅，月地支对应正月寅、二月卯...
        val monthZhiIndex = (lunarMonth + 1) % 12 // 寅=2, 卯=3...
        val dayZhiIndex = earthlyBranches.indexOf(dayZhi)
        var offset = (dayZhiIndex - monthZhiIndex + 12) % 12
        // 建日偏移0
        return shiErShen[offset]
    }

    private fun get28Xiu(lunarYear: Int, lunarMonth: Int, lunarDay: Int): String {
        // 简化算法：从1900年春节起算，按日序循环28宿
        val cal = lunarToSolar(lunarYear, lunarMonth, lunarDay, false)
        val days = daysBetween(baseDate, cal)
        val idx = (((days % 28) + 28) % 28).toInt()
        val x = xiu28[idx]
        return xiuAnimals[x] ?: x
    }

    private fun daysBetween(start: Calendar, end: Calendar): Long {
        val ms = end.timeInMillis - start.timeInMillis
        return ms / (1000 * 60 * 60 * 24)
    }

    private fun getConstellation(month: Int, day: Int): String {
        val dates = intArrayOf(20,19,21,20,21,22,23,23,23,24,23,22)
        val names = arrayOf(
            "水瓶座","双鱼座","白羊座","金牛座","双子座","巨蟹座","狮子座","处女座","天秤座","天蝎座","射手座","摩羯座"
        )
        return if (day < dates[month - 1]) names[month - 1] else names[month % 12]
    }

    fun solarToLunar(year: Int, month: Int, day: Int): LunarDate {
        val cal = GregorianCalendar(year, month - 1, day)
        var offset = daysBetween(baseDate, cal).toInt()
        var lunarYear = BASE_YEAR
        var daysInYear: Int
        while (true) {
            daysInYear = lunarYearDays(lunarYear)
            if (offset < daysInYear) break
            offset -= daysInYear
            lunarYear++
        }
        var lunarMonth = 1
        var isLeap = false
        var isBig = false
        var daysInMonth: Int
        var leapMonth = leapMonth(lunarYear)
        while (lunarMonth <= 12) {
            daysInMonth = lunarMonthDays(lunarYear, lunarMonth)
            if (offset < daysInMonth) {
                isBig = daysInMonth == 30
                break
            }
            offset -= daysInMonth
            if (leapMonth == lunarMonth) {
                daysInMonth = leapMonthDays(lunarYear)
                if (offset < daysInMonth) {
                    isLeap = true
                    isBig = daysInMonth == 30
                    break
                }
                offset -= daysInMonth
            }
            lunarMonth++
        }
        val lunarDay = offset + 1
        val monthGanZhi = getMonthGanZhi(lunarYear, year, month, day)
        return LunarDate(
            lunarYear = lunarYear,
            lunarMonth = lunarMonth,
            lunarDay = lunarDay,
            isLeapMonth = isLeap,
            isBigMonth = isBig,
            zodiac = zodiacAnimals[(lunarYear - 4) % 12],
            yearGanZhi = getYearGanZhi(lunarYear),
            monthGanZhi = monthGanZhi,
            dayGanZhi = getDayGanZhi(year, month, day),
            lunarMonthName = (if (isLeap) "闰" else "") + lunarMonths[lunarMonth - 1] + "月",
            lunarDayName = lunarDays[lunarDay - 1],
            festival = getFestival(lunarYear, lunarMonth, lunarDay, year, month, day, isLeap),
            solarTerm = getSolarTerm(year, month, day)
        )
    }

    private fun getYearGanZhi(year: Int): String {
        val index = (year - 4) % 60
        return heavenlyStems[index % 10] + earthlyBranches[index % 12]
    }

    private fun getMonthGanZhi(lunarYear: Int, solarYear: Int, solarMonth: Int, solarDay: Int): String {
        // 月柱以节气为界：立春后为寅月，惊蛰后为卯月...
        // 节令索引对应：立春(2),惊蛰(4),清明(6),立夏(8),芒种(10),小暑(12),
        //              立秋(14),白露(16),寒露(18),立冬(20),大雪(22),小寒(0)
        val jieIndices = intArrayOf(2,4,6,8,10,12,14,16,18,20,22,0)
        val monthZhiIndices = intArrayOf(2,3,4,5,6,7,8,9,10,11,0,1)
        var termIndex = 11
        for (i in 11 downTo 0) {
            val date = solarTermDate(solarYear, jieIndices[i])
            if (solarMonth > date[0] || (solarMonth == date[0] && solarDay >= date[1])) {
                termIndex = i
                break
            }
        }
        val monthZhiIndex = monthZhiIndices[termIndex]
        // 年干决定月干：甲己之年丙作首，乙庚之岁戊为头...
        val yearGanIndex = (lunarYear - 4) % 10
        val yearGan = heavenlyStems[yearGanIndex]
        val monthGanBase = when (yearGan) {
            "甲","己" -> 2 // 丙
            "乙","庚" -> 4 // 戊
            "丙","辛" -> 6 // 庚
            "丁","壬" -> 8 // 壬
            else -> 0 // 戊癸 甲
        }
        val monthGan = heavenlyStems[(monthGanBase + monthZhiIndex) % 10]
        return monthGan + earthlyBranches[monthZhiIndex]
    }

    private fun getDayGanZhi(year: Int, month: Int, day: Int): String {
        // 已知 1900-01-01 为甲戌日
        val base = GregorianCalendar(1900, 0, 1)
        val target = GregorianCalendar(year, month - 1, day)
        val diff = daysBetween(base, target).toInt()
        val index = (diff + 10) % 60 // 甲=0, 戌=10
        return heavenlyStems[index % 10] + earthlyBranches[index % 12]
    }

    private fun lunarInfoFor(year: Int): Int {
        val idx = (year - BASE_YEAR).coerceIn(0, lunarInfo.size - 1)
        return lunarInfo[idx]
    }

    private fun lunarYearDays(year: Int): Int {
        var sum = 0
        for (m in 1..12) {
            sum += lunarMonthDays(year, m)
        }
        val leap = leapMonth(year)
        if (leap > 0) sum += leapMonthDays(year)
        return sum
    }

    private fun lunarMonthDays(year: Int, month: Int): Int {
        // 月份大小存储在 bits 4-15：bit15=正月，bit4=腊月
        val info = lunarInfoFor(year)
        return if ((info shr (16 - month)) and 1 == 1) 30 else 29
    }

    private fun leapMonth(year: Int): Int {
        // 低 4 位（bits 0-3）存储闰月月份（0 表示无闰月）
        return lunarInfoFor(year) and 0xF
    }

    private fun leapMonthDays(year: Int): Int {
        // bit 16 存储闰月大小（1=30 天，0=29 天）
        val info = lunarInfoFor(year)
        return if ((info shr 16) and 1 == 1) 30 else 29
    }

    private fun lunarToSolar(lunarYear: Int, lunarMonth: Int, lunarDay: Int, isLeap: Boolean): Calendar {
        // 简化实现，仅用于内部28宿计算
        val cal = baseDate.clone() as Calendar
        var year = BASE_YEAR
        var offset = 0
        while (year < lunarYear) {
            offset += lunarYearDays(year)
            year++
        }
        var month = 1
        val leap = leapMonth(lunarYear)
        while (month < lunarMonth) {
            offset += lunarMonthDays(lunarYear, month)
            if (leap == month) offset += leapMonthDays(lunarYear)
            month++
        }
        if (leap == lunarMonth && isLeap) {
            offset += lunarMonthDays(lunarYear, lunarMonth)
        }
        offset += lunarDay - 1
        cal.add(Calendar.DAY_OF_YEAR, offset)
        return cal
    }

    // 24节气计算（简化天文公式）
    fun getSolarTerm(year: Int, month: Int, day: Int): String? {
        for (i in 0..23) {
            val termDate = solarTermDate(year, i)
            if (termDate[0] == month && termDate[1] == day) return solarTerms[i]
        }
        return null
    }

    private fun getNextSolarTerm(year: Int, month: Int, day: Int): Pair<String, Int>? {
        var currentIndex = -1
        for (i in 0..23) {
            val termDate = solarTermDate(year, i)
            if (termDate[0] < month || (termDate[0] == month && termDate[1] < day)) {
                currentIndex = i
            } else break
        }
        val nextIndex = (currentIndex + 1) % 24
        val termDate = solarTermDate(year, nextIndex)
        var y = year
        var m = termDate[0]
        var d = termDate[1]
        val current = GregorianCalendar(year, month - 1, day)
        val next = GregorianCalendar(y, m - 1, d)
        if (next.before(current)) {
            next.add(Calendar.YEAR, 1)
        }
        val days = daysBetween(current, next).toInt()
        return Pair(solarTerms[nextIndex], days)
    }

    private fun solarTermDate(year: Int, index: Int): IntArray {
        // 基于1900年回归年修正，精度足够万年历展示
        val baseYears = year - 1900
        val termNum = (index + 6) % 24  // 节气序号：小寒=0...冬至=23，先对齐到立春=2开始
        var c = termCs[index]
        c += baseYears * 0.2422
        val century = year / 100
        val leapOffset = when {
            index in 0..5 -> 6
            index in 6..11 -> 6
            index in 12..17 -> 7
            else -> 8
        }
        val yOffset = when (century) {
            19 -> if (index < 12) leapOffset else leapOffset + 1
            20 -> if (index < 12) leapOffset else leapOffset + 1
            21 -> if (index < 12) leapOffset else leapOffset + 1
            else -> leapOffset
        }
        val day = (c - yOffset).toInt()
        val month = when (index) {
            0,1 -> 1; 2,3 -> 2; 4,5 -> 3; 6,7 -> 4; 8,9 -> 5; 10,11 -> 6
            12,13 -> 7; 14,15 -> 8; 16,17 -> 9; 18,19 -> 10; 20,21 -> 11; else -> 12
        }
        return intArrayOf(month, day)
    }

    // 宜忌（简化规则）
    private fun getYiJi(lunar: LunarDate): Pair<String, String> {
        // 基于十二建星简单推断
        val shen = getShiErShen(lunar.lunarYear, lunar.lunarMonth, lunar.dayGanZhi[1].toString())
        val yiList = when (shen) {
            "建" -> "出行 上任 会友"
            "除" -> "沐浴 清洁 求医 安葬"
            "满" -> "开市 立券 交易"
            "平" -> "修造 动土 嫁娶"
            "定" -> "冠笄 嫁娶 祭祀"
            "执" -> "捕捉 狩猎 求财"
            "破" -> "求医 服药 拆除"
            "危" -> "安床 入殓 移柩"
            "成" -> "开市 入学 嫁娶"
            "收" -> "嫁娶 纳财 开市"
            "开" -> "开业 开工 出行 嫁娶 祭祀 祈福 求嗣 开光 解除 安床 栽种 移柩 进人口 会亲友 除服 成服"
            else -> "诸事不宜"
        }
        val jiList = when (shen) {
            "建" -> "动土 开仓"
            "除" -> "嫁娶 入宅"
            "满" -> "安葬 栽种"
            "平" -> "掘井 栽种"
            "定" -> "词讼 出行"
            "执" -> "开市 安葬"
            "破" -> "嫁娶 出行"
            "危" -> "出行 上任"
            "成" -> "诉讼 安葬"
            "收" -> "出行 求医"
            "开" -> "造屋 入殓 安葬 伐木 入宅 移徙 置产 纳畜"
            else -> "诸事不取"
        }
        return Pair(yiList, jiList)
    }

    // 节假日数据
    private val solarHolidays = mapOf(
        Pair(1,1) to "元旦",
        Pair(2,14) to "情人节",
        Pair(3,8) to "妇女节",
        Pair(3,12) to "植树节",
        Pair(4,1) to "愚人节",
        Pair(5,1) to "劳动节",
        Pair(5,4) to "青年节",
        Pair(6,1) to "儿童节",
        Pair(7,1) to "建党节",
        Pair(8,1) to "建军节",
        Pair(9,10) to "教师节",
        Pair(10,1) to "国庆节",
        Pair(10,24) to "联合国日",
        Pair(11,11) to "光棍节",
        Pair(12,25) to "圣诞节"
    )

    private val lunarFestivals = mapOf(
        Pair(1,1) to "春节",
        Pair(1,15) to "元宵节",
        Pair(2,2) to "龙抬头",
        Pair(4,4) to "寒食节",
        Pair(4,5) to "清明节",
        Pair(5,5) to "端午节",
        Pair(7,7) to "七夕节",
        Pair(7,15) to "中元节",
        Pair(8,15) to "中秋节",
        Pair(9,9) to "重阳节",
        Pair(10,1) to "寒衣节",
        Pair(12,8) to "腊八节",
        Pair(12,23) to "小年",
        Pair(12,30) to "除夕"
    )

    private fun getFestival(lunarYear: Int, lunarMonth: Int, lunarDay: Int, solarYear: Int, solarMonth: Int, solarDay: Int, isLeap: Boolean): String? {
        val lunarKey = Pair(lunarMonth, lunarDay)
        lunarFestivals[lunarKey]?.let { return it }
        if (lunarMonth == 12 && lunarDay == lunarMonthDays(lunarYear, 12)) {
            return "除夕"
        }
        val solarKey = Pair(solarMonth, solarDay)
        solarHolidays[solarKey]?.let { return it }
        return null
    }
}
