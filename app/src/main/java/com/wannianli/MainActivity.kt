package com.wannianli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wannianli.LunarCalendar.DayInfo
import com.wannianli.LunarCalendar.RestType
import com.wannianli.ui.theme.WanNianLiTheme
import java.util.Calendar
import java.util.GregorianCalendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WanNianLiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5)
                ) {
                    CalendarApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarApp() {
    val today = remember { Calendar.getInstance() }
    var year by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(today.get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(today.get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val selectedInfo = remember(selectedYear, selectedMonth, selectedDay) {
        LunarCalendar.getDayInfo(selectedYear, selectedMonth, selectedDay)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "万年日历查询 - 在线日历",
                        fontSize = 18.sp,
                        color = Color(0xFF333333)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(8.dp))
            YearMonthSelector(
                year = year,
                month = month,
                onYearChange = { year = it },
                onMonthChange = { month = it },
                onToday = {
                    year = today.get(Calendar.YEAR)
                    month = today.get(Calendar.MONTH) + 1
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = today.get(Calendar.DAY_OF_MONTH)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            WeekdayHeader()
            CalendarGrid(
                year = year,
                month = month,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                selectedDay = selectedDay,
                onDateSelected = { y, m, d ->
                    selectedYear = y
                    selectedMonth = m
                    selectedDay = d
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SelectedDateCard(info = selectedInfo)
            Spacer(modifier = Modifier.height(12.dp))
            DetailSection(info = selectedInfo)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE74C3C)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("8", color = Color.White, fontSize = 10.sp)
                Text("12", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "万年日历查询",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
    }
}

@Composable
fun YearMonthSelector(
    year: Int,
    month: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF4A90E2))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onYearChange(year - 1) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.ArrowBackIos, contentDescription = "上一年", tint = Color.White)
        }
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("$year", fontSize = 15.sp, color = Color(0xFF333333))
        }
        IconButton(onClick = { onYearChange(year + 1) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.ArrowForwardIos, contentDescription = "下一年", tint = Color.White)
        }

        IconButton(onClick = {
            val newMonth = if (month == 1) 12 else month - 1
            if (month == 1) onYearChange(year - 1)
            onMonthChange(newMonth)
        }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.ArrowBackIos, contentDescription = "上一月", tint = Color.White)
        }
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(String.format("%02d月", month), fontSize = 15.sp, color = Color(0xFF333333))
        }
        IconButton(onClick = {
            val newMonth = if (month == 12) 1 else month + 1
            if (month == 12) onYearChange(year + 1)
            onMonthChange(newMonth)
        }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.ArrowForwardIos, contentDescription = "下一月", tint = Color.White)
        }

        Button(
            onClick = onToday,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66B3FF)),
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("回到今天", color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
fun WeekdayHeader() {
    val weekdays = listOf("一", "二", "三", "四", "五", "六", "日")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F5E9))
            .padding(vertical = 8.dp)
    ) {
        weekdays.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    day,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (index == 6) Color(0xFFE74C3C) else Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
fun CalendarGrid(
    year: Int,
    month: Int,
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    val firstDayOfMonth = GregorianCalendar(year, month - 1, 1)
    val dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
    val startOffset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val totalCells = startOffset + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - startOffset + 1
                    val isValid = dayNumber in 1..daysInMonth
                    val isSelected = isValid && year == selectedYear && month == selectedMonth && dayNumber == selectedDay
                    val isSunday = col == 6
                    val isSaturday = col == 5
                    val info = if (isValid) {
                        LunarCalendar.getDayInfo(year, month, dayNumber)
                    } else null
                    val isToday = isValid && isToday(year, month, dayNumber)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.85f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    isSelected -> Color(0xFFFFF3E0)
                                    isToday -> Color(0xFFE3F2FD)
                                    info?.restType == RestType.HOLIDAY || info?.restType == RestType.WEEKEND -> Color(0xFFFFF0F0)
                                    info?.restType == RestType.WORKDAY -> Color(0xFFF5F5F5)
                                    else -> Color.White
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Color(0xFFF39C12) else Color.Transparent,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable(enabled = isValid) {
                                onDateSelected(year, month, dayNumber)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isValid && info != null) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // 左上角休/末/班角标
                                if (info.restType != RestType.NORMAL) {
                                    val badgeText = when (info.restType) {
                                        RestType.HOLIDAY -> "休"
                                        RestType.WEEKEND -> "末"
                                        RestType.WORKDAY -> "班"
                                        else -> ""
                                    }
                                    Text(
                                        text = badgeText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (info.restType == RestType.WORKDAY) Color(0xFF666666) else Color(0xFFE74C3C),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(start = 3.dp, top = 2.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = String.format("%02d", dayNumber),
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (info.restType == RestType.HOLIDAY || info.restType == RestType.WEEKEND) Color(0xFFE74C3C) else Color(0xFF333333)
                                    )
                                    val bottomText = when {
                                        info.lunar.festival != null -> info.lunar.festival
                                        info.lunar.solarTerm != null -> info.lunar.solarTerm
                                        info.lunar.lunarDay == 1 -> info.lunar.lunarMonthName
                                        else -> info.lunar.lunarDayName
                                    }
                                    Text(
                                        text = bottomText,
                                        fontSize = 13.sp,
                                        color = when {
                                            info.lunar.festival != null -> Color(0xFFE74C3C)
                                            info.lunar.solarTerm != null -> Color(0xFF27AE60)
                                            else -> Color(0xFF666666)
                                        },
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isToday(year: Int, month: Int, day: Int): Boolean {
    val today = Calendar.getInstance()
    return year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH) + 1 && day == today.get(Calendar.DAY_OF_MONTH)
}

@Composable
fun SelectedDateCard(info: DayInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color(0xFF6B9B5A))
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                val monthSize = if (info.lunar.isBigMonth) "大" else "小"
                Text(
                    text = "${info.solarYear}年${String.format("%02d", info.solarMonth)}月（${monthSize}）${info.weekDay.replace("星期", "")}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(Color(0xFFF39C12)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${info.solarDay}",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${info.lunar.lunarMonthName}${info.lunar.lunarDayName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "${info.lunar.yearGanZhi}年【${info.lunar.zodiac}年】${info.lunar.monthGanZhi}月 ${info.lunar.dayGanZhi}日",
                fontSize = 14.sp,
                color = Color(0xFF555555),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            info.lunar.festival?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("节日", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(it, color = Color(0xFF4A90E2), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            YiJiRow(label = "宜", items = info.yi, bgColor = Color(0xFF8BC34A))
            Spacer(modifier = Modifier.height(6.dp))
            YiJiRow(label = "忌", items = info.ji, bgColor = Color(0xFFFF9800))
        }
    }
}

@Composable
fun YiJiRow(label: String, items: String, bgColor: Color) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(label, color = Color.White, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            items,
            fontSize = 13.sp,
            color = Color(0xFF333333),
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DetailSection(info: DayInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8FBC8F), RoundedCornerShape(4.dp))
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${info.solarYear}年${String.format("%02d", info.solarMonth)}月${String.format("%02d", info.solarDay)}日 详细信息",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            DetailGrid(info)
        }
    }
}

@Composable
fun DetailGrid(info: DayInfo) {
    val items = listOf(
        Pair("生肖", info.lunar.zodiac),
        Pair("星座", info.constellation),
        Pair("彭祖百忌", info.pzBaiJi),
        Pair("胎神占方", info.taiShen),
        Pair("年五行", info.yearNaYin),
        Pair("季节", info.season),
        Pair("月五行", info.monthWuXing),
        Pair("日五行", info.dayWuXing),
        Pair("星宿", info.xiu),
        Pair("儒略日", "%.1f".format(info.julianDay)),
        Pair("节气", info.solarTerm?.let { "$it" } ?: info.nextSolarTerm?.let { "距${it.first}${it.second}天" } ?: ""),
        Pair("佛历年", info.buddhaYear),
        Pair("伊斯兰历", info.hijri),
        Pair("冲", info.chong),
        Pair("煞", info.sha),
        Pair("六曜", info.liuYao),
        Pair("十二神", info.shiErShen)
    )

    Column {
        items.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (label, value) ->
                    DetailItem(label = label, value = value, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(label, color = Color.White, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            value,
            fontSize = 13.sp,
            color = Color(0xFF333333),
            maxLines = 2,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
