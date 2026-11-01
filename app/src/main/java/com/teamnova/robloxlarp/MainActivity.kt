package com.teamnova.robloxlarp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch

private val Bg = Color(0xFF090B12)
private val Panel = Color(0xFF171923)
private val Wine = Color(0xFF3B080B)
private val Accent = Color(0xFFB51E2B)
private val White = Color(0xFFF7F7FA)
private val Grey = Color(0xFF9B9DA8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(31, 5, 8)
        window.navigationBarColor = android.graphics.Color.rgb(31, 5, 8)
        setContent { RobloxLarpApp() }
    }
}

private data class Experience(val name: String, val rating: String, val emoji: String, val colors: List<Color>, val tag: String = "Experience")
private val experiences = listOf(
    Experience("Сбросить вещи и лететь", "80%", "🧍", listOf(Color(0xFF315A9D), Color(0xFFB34A36))),
    Experience("Опыт смертельный", "93%", "🐒", listOf(Color(0xFF353B43), Color(0xFF9A9B8B))),
    Experience("Car Dealership Tycoon", "92%", "🏎️", listOf(Color(0xFF5C160F), Color(0xFFB85A22))),
    Experience("+1 ползунок клавиш", "", "🏊", listOf(Color(0xFF0874C8), Color(0xFF42C9F2)), "Ad"),
    Experience("Brookhaven RP", "95%", "🏡", listOf(Color(0xFF2A8C77), Color(0xFF8ED6BD))),
    Experience("Adopt Me!", "97%", "🐶", listOf(Color(0xFFB779B6), Color(0xFFF4C3D4)))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RobloxLarpApp() {
    var page by rememberSaveable { mutableStateOf("Home") }
    var drawer by rememberSaveable { mutableStateOf(false) }
    var homeTab by rememberSaveable { mutableStateOf("For you") }
    var username by rememberSaveable { mutableStateOf("Vexo") }
    var handle by rememberSaveable { mutableStateOf("Red33391732") }
    var robux by rememberSaveable { mutableIntStateOf(0) }
    var followers by rememberSaveable { mutableIntStateOf(248) }
    var following by rememberSaveable { mutableIntStateOf(120) }
    var friends by rememberSaveable { mutableIntStateOf(28) }
    var visits by rememberSaveable { mutableIntStateOf(12400) }
    var notice by remember { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }
    var edit by remember { mutableStateOf(false) }
    val owned = remember { mutableStateListOf<String>() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    LaunchedEffect(drawer) {
        if (drawer) drawerState.open() else drawerState.close()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Wine,
                drawerContentColor = White,
                modifier = Modifier.fillMaxHeight().fillMaxWidth(.88f)
            ) {
                DrawerPanel(username, handle, robux, friends,
                    onSelect = { selected -> page = selected; drawer = false },
                    onEdit = { edit = true; drawer = false },
                    onClose = { drawer = false })
            }
        },
        scrimColor = Color.Black.copy(alpha = .62f)
    ) {
        Scaffold(
            containerColor = Bg,
            bottomBar = {
                NavigationBar(containerColor = Wine, contentColor = White, tonalElevation = 0.dp) {
                    val nav = listOf("Home", "Moments", "Chat", "Me")
                    val icons = listOf(Icons.Default.Home, Icons.Default.Slideshow, Icons.Default.ChatBubbleOutline, Icons.Default.Person)
                    nav.forEachIndexed { i, item ->
                        NavigationBarItem(
                            selected = page == item,
                            onClick = { page = item },
                            icon = { Icon(icons[i], item, tint = if (page == item) White else Color(0xFFBDB4B7), modifier = Modifier.size(25.dp)) },
                            label = { Text(item, color = if (page == item) White else Color(0xFFBDB4B7), fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF681016))
                        )
                    }
                }
            }
        ) { pad ->
            when (page) {
                "Home" -> HomeFeed(
                    Modifier.padding(pad), username, homeTab, { homeTab = it }, search, { search = it },
                    onMenu = { drawer = true }, onSearch = { page = "Search" }, onNotifications = { notice = "Уведомления пока демонстрационные" },
                    onProfile = { page = "Me" }, notice = notice, clearNotice = { notice = "" }
                )
                "Me", "Profile" -> ProfilePage(Modifier.padding(pad), username, handle, robux, followers, following, friends, visits, owned.size, { edit = true })
                "Shop" -> ShopPage(Modifier.padding(pad), robux, owned, { robux -= it; owned.add("Предмет #${owned.size + 1}"); notice = "Покупка добавлена в инвентарь" })
                "Inventory" -> SimplePage(Modifier.padding(pad), "Inventory", if (owned.isEmpty()) listOf("Инвентарь пока пуст") else owned.toList())
                "Search" -> SearchPage(Modifier.padding(pad), search, { search = it }, { page = "Home" })
                else -> SimplePage(Modifier.padding(pad), page, when (page) {
                    "Friends" -> listOf("Друзья: $friends", "Добавить друзей")
                    "Messages" -> listOf("Сообщений пока нет")
                    "Settings" -> listOf("Настройки аккаунта", "Уведомления", "Конфиденциальность")
                    else -> listOf("Раздел скоро появится")
                })
            }
        }
    }

    if (edit) {
        EditProfileDialog(username, handle, robux, followers, following, friends, visits,
            onDismiss = { edit = false },
            onSave = { u, h, r, f, fg, fr, v ->
                username = u; handle = h; robux = r; followers = f; following = fg; friends = fr; visits = v
                edit = false; notice = "Профиль обновлён"
            })
    }
}

@Composable
private fun HomeFeed(
    modifier: Modifier, username: String, selected: String, onSelected: (String) -> Unit,
    search: String, onSearch: (String) -> Unit, onMenu: () -> Unit, onSearchOpen: () -> Unit,
    onNotifications: () -> Unit, onProfile: () -> Unit, notice: String, clearNotice: () -> Unit
) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
        item {
            Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF3C080D), Bg, Color(0xFF15121C))))) {
                Canvas(Modifier.matchParentSize()) {
                    for (i in 0..7) {
                        val x = size.width * (i * .19f - .1f)
                        drawLine(Color(0xFF70121A).copy(alpha = .22f), Offset(x, 0f), Offset(x + size.width * .35f, size.height), strokeWidth = 22f)
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 17.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMenu) { Icon(Icons.Default.Menu, "Menu", tint = White, modifier = Modifier.size(31.dp)) }
                    Text("ROBLOX", color = White, fontSize = 27.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f), letterSpacing = (-1).sp)
                    IconButton(onClick = onSearchOpen) { Icon(Icons.Default.Search, "Search", tint = White, modifier = Modifier.size(31.dp)) }
                    IconButton(onClick = { }) { Icon(Icons.Default.Hexagon, "Robux", tint = White, modifier = Modifier.size(27.dp)) }
                    Box {
                        IconButton(onClick = onNotifications) { Icon(Icons.Default.NotificationsNone, "Notifications", tint = White, modifier = Modifier.size(29.dp)) }
                        Box(Modifier.align(Alignment.TopEnd).padding(top = 1.dp, end = 1.dp).size(20.dp).background(Color(0xFF3867F5), CircleShape), contentAlignment = Alignment.Center) { Text("4", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                listOf("For you", "Charts").forEach { tab ->
                    Column(Modifier.weight(1f).clickable { onSelected(tab) }.padding(vertical = 13.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(tab, color = if (selected == tab) White else Grey, fontSize = 19.sp, fontWeight = if (selected == tab) FontWeight.SemiBold else FontWeight.Normal)
                        Spacer(Modifier.height(13.dp))
                        Box(Modifier.fillMaxWidth().height(if (selected == tab) 2.dp else 1.dp).background(if (selected == tab) White else Color(0xFF34343D)))
                    }
                }
            }
        }
        item {
            Row(Modifier.padding(start = 20.dp, top = 26.dp, bottom = 12.dp).clickable { onProfile() }, verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle("👑", 48)
                Text(username, color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 14.dp))
            }
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item { FriendBubble("Add Friends", "＋", true) }
                items(listOf("Dadon_419", "METRORO...", "Batman", "Alex", "Guest")) { name ->
                    FriendBubble(name, when (name) { "Dadon_419" -> "😎"; "METRORO..." -> "🙂"; "Batman" -> "🦇"; else -> "🧑" }, false)
                }
            }
        }
        item {
            Text(if (selected == "For you") "Recommended For You" else "Top Charts", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 18.dp, top = 27.dp, bottom = 14.dp))
        }
        val shown = if (search.isBlank()) experiences else experiences.filter { it.name.contains(search, true) }
        val cards = if (selected == "Charts") shown.sortedByDescending { it.rating } else shown
        items(cards.chunked(2)) { pair ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { exp -> ExperienceCard(exp, Modifier.weight(1f)) }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Continue", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ChevronRight, null, tint = White)
            }
        }
        if (notice.isNotBlank()) item {
            Text(notice, color = White, modifier = Modifier.padding(18.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Panel).clickable { clearNotice() }.padding(14.dp))
        }
        item { Text("Fan-made LARP simulator • Not affiliated with Roblox", color = Grey, fontSize = 10.sp, modifier = Modifier.padding(18.dp)) }
    }
}

@Composable
private fun FriendBubble(name: String, face: String, add: Boolean) {
    Column(Modifier.width(112.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(112.dp).background(Color(0xFF292B35), CircleShape).border(1.dp, Color(0xFF383A46), CircleShape), contentAlignment = Alignment.Center) {
            if (add) Icon(Icons.Default.PersonAdd, null, tint = White, modifier = Modifier.size(52.dp))
            else Text(face, fontSize = 54.sp)
            if (!add) Box(Modifier.align(Alignment.BottomEnd).padding(5.dp).size(18.dp).background(Color(0xFF24C77A), CircleShape).border(2.dp, Bg, CircleShape))
        }
        Text(name, color = White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
        if (!add && name == "Dadon_419") Text("[🎃]...", color = Grey, fontSize = 12.sp)
        if (!add && name == "METRORO...") Text("Поход за...", color = Grey, fontSize = 12.sp)
    }
}

@Composable
private fun ExperienceCard(exp: Experience, modifier: Modifier = Modifier) {
    Column(modifier) {
        Box(Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(13.dp)).background(Brush.linearGradient(exp.colors)), contentAlignment = Alignment.Center) {
            Text(exp.emoji, fontSize = 62.sp)
            Text("▶", color = White.copy(alpha = .7f), fontSize = 20.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(9.dp))
        }
        Text(exp.name, color = White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (exp.tag == "Ad") Text("Ad", color = Grey, fontSize = 14.sp)
            else {
                Icon(Icons.Default.ThumbUp, null, tint = Color(0xFFD9DCE4), modifier = Modifier.size(16.dp))
                Text(" ${exp.rating} Rating", color = Color(0xFFD9DCE4), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun DrawerPanel(username: String, handle: String, robux: Int, friends: Int, onSelect: (String) -> Unit, onEdit: () -> Unit, onClose: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(Modifier.fillMaxWidth().padding(start = 22.dp, end = 8.dp, top = 32.dp, bottom = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle("👑", 82)
                Column(Modifier.weight(1f).padding(start = 18.dp)) {
                    Text(username, color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("@$handle", color = Color(0xFFD7C6C8), fontSize = 18.sp)
                }
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, null, tint = White) }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 18.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, Color(0xFF572126), RoundedCornerShape(16.dp))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hexagon, null, tint = White, modifier = Modifier.size(26.dp))
                    Text(robux.toString(), color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 14.dp).weight(1f))
                    Text("Buy Robux", color = White, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFF51272C)).clickable { onSelect("Shop") }.padding(horizontal = 14.dp, vertical = 12.dp))
                }
                HorizontalDivider(color = Color(0xFF572126))
                Row(Modifier.fillMaxWidth().clickable { onSelect("Shop") }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayCircleOutline, null, tint = White)
                    Column(Modifier.padding(start = 14.dp)) {
                        Text("More fun for less Robux.", color = White, fontSize = 15.sp)
                        Text("Subscribe to Roblox Plus.     ›", color = White, fontSize = 15.sp)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
        val entries = listOf(
            Triple("Favorites", Icons.Default.BookmarkBorder, "Favorites"),
            Triple("Continue Playing", Icons.Default.History, "Continue"),
            Triple("Friends", Icons.Default.PeopleOutline, "Friends"),
            Triple("Communities", Icons.Default.Groups, "Communities"),
            Triple("Inventory", Icons.Default.Backpack, "Inventory"),
            Triple("Trades", Icons.Default.SwapHoriz, "Trades"),
            Triple("Messages", Icons.Default.MailOutline, "Messages"),
            Triple("Roblox Plus", Icons.Default.Hexagon, "Plus"),
            Triple("Buy Gift Cards", Icons.Default.CardGiftcard, "Gift Cards"),
            Triple("Settings", Icons.Default.Settings, "Settings"),
            Triple("Help & Safety", Icons.Default.Shield, "Help")
        )
        items(entries) { (label, icon, destination) ->
            Row(Modifier.fillMaxWidth().clickable { onSelect(if (destination == "Favorites" || destination == "Continue" || destination == "Communities" || destination == "Trades" || destination == "Plus" || destination == "Gift Cards" || destination == "Help") destination else destination }.padding(horizontal = 24.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = White, modifier = Modifier.size(25.dp))
                Text(label, color = White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 24.dp).weight(1f))
                if (label == "Friends") CountPill(friends.toString())
                if (label == "Messages") CountPill("6")
            }
        }
        item {
            HorizontalDivider(color = Color(0xFF572126), modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            Row(Modifier.fillMaxWidth().clickable { onSelect("Learn") }.padding(horizontal = 24.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, null, tint = White)
                Text("Learn", color = White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 24.dp))
            }
            Text("Edit profile & stats", color = Color(0xFFE5BFC3), fontSize = 14.sp, modifier = Modifier.padding(start = 74.dp, top = 8.dp).clickable { onEdit() })
        }
    }
}

@Composable
private fun CountPill(value: String) {
    Text(value, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(CircleShape).background(Color(0xFF315FEA)).padding(horizontal = 10.dp, vertical = 2.dp))
}

@Composable
private fun AvatarCircle(face: String, size: Int) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF48505D), Color(0xFF20232D)))), contentAlignment = Alignment.Center) { Text(face, fontSize = (size * .48f).sp) }
}

@Composable
private fun ProfilePage(modifier: Modifier, username: String, handle: String, robux: Int, followers: Int, following: Int, friends: Int, visits: Int, items: Int, onEdit: () -> Unit) {
    Column(modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle("👑", 86)
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text(username, color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("@$handle", color = Grey, fontSize = 16.sp)
            }
            Text("Edit", color = White, modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Panel).clickable { onEdit() }.padding(12.dp))
        }
        Spacer(Modifier.height(22.dp))
        Text("R$ $robux", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(22.dp))
        Text("Statistics", color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        listOf("Followers" to followers, "Following" to following, "Friends" to friends, "Visits" to visits, "Inventory" to items).forEach {
            Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(it.first, color = Grey, fontSize = 17.sp); Text(it.second.toString(), color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = Panel)
        }
    }
}

@Composable
private fun ShopPage(modifier: Modifier, robux: Int, owned: List<String>, buy: (Int) -> Unit) {
    Column(modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Text("Avatar Shop", color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("Balance  R$ $robux", color = Grey, modifier = Modifier.padding(vertical = 12.dp))
        listOf("Classic Shades" to 75, "Golden Crown" to 450, "Blue Headphones" to 120, "Street Hoodie" to 85).forEach { (name, price) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(14.dp)).background(Panel).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🛍️", fontSize = 30.sp)
                Text(name, color = White, modifier = Modifier.weight(1f).padding(start = 12.dp))
                Text("R$ $price", color = White, modifier = Modifier.clickable { if (robux >= price) buy(price) }.background(Accent, RoundedCornerShape(8.dp)).padding(10.dp))
            }
        }
    }
}

@Composable
private fun SearchPage(modifier: Modifier, value: String, onValue: (String) -> Unit, back: () -> Unit) {
    Column(modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back) { Icon(Icons.Default.ArrowBack, null, tint = White) }
            Text("Search", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(value, onValue, Modifier.fillMaxWidth(), placeholder = { Text("Search experiences and people") }, singleLine = true)
        experiences.filter { value.isBlank() || it.name.contains(value, true) }.forEach { Text(it.name, color = White, fontSize = 18.sp, modifier = Modifier.padding(14.dp)) }
    }
}

@Composable
private fun SimplePage(modifier: Modifier, title: String, rows: List<String>) {
    Column(modifier.fillMaxSize().background(Bg).padding(20.dp)) {
        Text(title, color = White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        rows.forEach { Text(it, color = White, fontSize = 17.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) }
    }
}

@Composable
private fun EditProfileDialog(user: String, handle: String, robux: Int, followers: Int, following: Int, friends: Int, visits: Int,
    onDismiss: () -> Unit, onSave: (String, String, Int, Int, Int, Int, Int) -> Unit) {
    var u by remember { mutableStateOf(user) }; var h by remember { mutableStateOf(handle) }
    var r by remember { mutableStateOf(robux.toString()) }; var f by remember { mutableStateOf(followers.toString()) }
    var fg by remember { mutableStateOf(following.toString()) }; var fr by remember { mutableStateOf(friends.toString()) }; var v by remember { mutableStateOf(visits.toString()) }
    AlertDialog(onDismissRequest = onDismiss, containerColor = Panel,
        title = { Text("Edit profile & stats", color = White) },
        text = {
            Column(Modifier.heightIn(max = 430.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Display name" to u, "Username" to h, "Robux" to r, "Followers" to f, "Following" to fg, "Friends" to fr, "Visits" to v).forEach { (label, value) ->
                    OutlinedTextField(value, { nv ->
                        when (label) { "Display name" -> u = nv; "Username" -> h = nv; "Robux" -> r = nv.filter(Char::isDigit); "Followers" -> f = nv.filter(Char::isDigit); "Following" -> fg = nv.filter(Char::isDigit); "Friends" -> fr = nv.filter(Char::isDigit); else -> v = nv.filter(Char::isDigit) }
                    }, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
                Text("Данные меняются только в локальном LARP-профиле.", color = Grey, fontSize = 11.sp)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(u.ifBlank { "Player" }, h.ifBlank { "player" }, r.toIntOrNull() ?: 0, f.toIntOrNull() ?: 0, fg.toIntOrNull() ?: 0, fr.toIntOrNull() ?: 0, v.toIntOrNull() ?: 0) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
