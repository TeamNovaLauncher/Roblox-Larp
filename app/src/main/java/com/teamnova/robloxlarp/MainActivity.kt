package com.teamnova.robloxlarp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RobloxBlue = Color(0xFF335FFF)
private val Ink = Color(0xFF191B20)
private val Muted = Color(0xFF707783)
private val Page = Color(0xFFF4F5F7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RobloxApp() }
    }
}

private data class ShopItem(val id: Int, val name: String, val kind: String, val price: Int, val emoji: String, val detail: String)

private val shopItems = listOf(
    ShopItem(1, "Classic Black Shades", "Accessories", 75, "🕶️", "A clean classic look"),
    ShopItem(2, "Golden Crown", "Accessories", 450, "👑", "Stand out in every server"),
    ShopItem(3, "Blue Headphones", "Accessories", 120, "🎧", "Music-inspired style"),
    ShopItem(4, "Sparkle Wings", "Accessories", 650, "🪽", "A little extra shine"),
    ShopItem(5, "Messy Brown Hair", "Hair", 90, "💇", "Casual layered hair"),
    ShopItem(6, "Pink Star Hair", "Hair", 140, "🌸", "Bright and playful"),
    ShopItem(7, "Smiley Face", "Faces", 50, "😊", "A friendly expression"),
    ShopItem(8, "Cool Face", "Faces", 100, "😎", "Stay cool"),
    ShopItem(9, "Red Street Hoodie", "Clothing", 85, "🧥", "Streetwear essential"),
    ShopItem(10, "White Tee", "Clothing", 35, "👕", "Simple everyday fit"),
    ShopItem(11, "Neon Backpack", "Accessories", 180, "🎒", "Carry your style"),
    ShopItem(12, "Pixel Pet", "Accessories", 300, "🐾", "A tiny companion")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RobloxApp() {
    var tab by rememberSaveable { mutableStateOf("Home") }
    var menuOpen by remember { mutableStateOf(false) }
    var editOpen by remember { mutableStateOf(false) }
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var username by rememberSaveable { mutableStateOf("Builder_2048") }
    var displayName by rememberSaveable { mutableStateOf("Builder") }
    var bio by rememberSaveable { mutableStateOf("Just having fun and building worlds.") }
    var robux by rememberSaveable { mutableIntStateOf(1250) }
    var followers by rememberSaveable { mutableIntStateOf(248) }
    var following by rememberSaveable { mutableIntStateOf(120) }
    var friends by rememberSaveable { mutableIntStateOf(86) }
    var visits by rememberSaveable { mutableIntStateOf(12400) }
    var owned by remember { mutableStateOf(setOf<Int>()) }
    var equipped by rememberSaveable { mutableIntStateOf(0) }
    var notice by remember { mutableStateOf("") }

    val titles = listOf("Home", "Avatar", "Shop", "Profile")
    Scaffold(
        containerColor = Page,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(30.dp).background(RobloxBlue, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Text("R", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Text("roblox", fontWeight = FontWeight.ExtraBold, fontSize = 23.sp, color = Ink)
                    }
                },
                actions = {
                    Text("R$ $robux", color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "More") }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Edit profile & stats") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { menuOpen = false; editOpen = true })
                        DropdownMenuItem(text = { Text("My inventory") }, leadingIcon = { Icon(Icons.Default.Backpack, null) }, onClick = { menuOpen = false; tab = "Avatar" })
                        DropdownMenuItem(text = { Text("About this LARP") }, leadingIcon = { Icon(Icons.Default.Info, null) }, onClick = { menuOpen = false; notice = "Fan-made simulator. Not affiliated with Roblox." })
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val icons = listOf(Icons.Default.Home, Icons.Default.Face, Icons.Default.ShoppingBag, Icons.Default.Person)
                titles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = tab == title,
                        onClick = { tab = title },
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = RobloxBlue, indicatorColor = Color(0xFFE8EDFF), selectedTextColor = RobloxBlue)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = remember { SnackbarHostState() }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                "Home" -> HomeScreen(
                    search = search, onSearch = { search = it },
                    onShop = { tab = "Shop" }, onProfile = { tab = "Profile" },
                    robux = robux, username = username
                )
                "Avatar" -> AvatarScreen(owned, equipped, onEquip = { equipped = it }, onShop = { tab = "Shop" })
                "Shop" -> ShopScreen(
                    robux = robux, owned = owned, category = category, onCategory = { category = it },
                    onBuy = { item ->
                        if (item.id in owned) notice = "Already in your inventory"
                        else if (robux >= item.price) { robux -= item.price; owned = owned + item.id; notice = "Purchased ${item.name}!" }
                        else notice = "Not enough LARP Robux"
                    }
                )
                else -> ProfileScreen(username, displayName, bio, followers, following, friends, visits, owned.size, onEdit = { editOpen = true })
            }
            if (notice.isNotBlank()) {
                Surface(color = Ink, shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(12.dp).fillMaxWidth().clickable { notice = "" }) {
                    Text(notice, color = Color.White, modifier = Modifier.padding(14.dp), fontSize = 14.sp)
                }
            }
        }
    }

    if (editOpen) {
        EditStatsDialog(
            username, displayName, bio, robux, followers, following, friends, visits,
            onDismiss = { editOpen = false },
            onSave = { u, d, b, r, f, fg, fr, v ->
                username = u; displayName = d; bio = b
                robux = r; followers = f; following = fg; friends = fr; visits = v
                editOpen = false; notice = "Profile updated"
            }
        )
    }
}

@Composable
private fun HomeScreen(search: String, onSearch: (String) -> Unit, onShop: () -> Unit, onProfile: () -> Unit, robux: Int, username: String) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            OutlinedTextField(value = search, onValueChange = onSearch, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(14.dp))
        }
        item {
            Surface(shape = RoundedCornerShape(18.dp), color = RobloxBlue, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("WELCOME TO ROBLOX", color = Color.White.copy(alpha = .8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("Your world. Your avatar.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Hey, $username! Jump into your LARP experience.", color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onShop, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = RobloxBlue)) { Text("Explore shop") }
                        OutlinedButton(onClick = onProfile, border = null, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("My profile") }
                    }
                }
            }
        }
        item { SectionTitle("Continue exploring", "Discover") }
        items(listOf("Brookhaven RP" to "🏡", "Adopt Me!" to "🐶", "Tower of Fun" to "🗼", "Fashion Studio" to "👗").filter { search.isBlank() || it.first.contains(search, true) }) { (name, emoji) ->
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(58.dp).background(Color(0xFFE9EDFA), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 30.sp) }
                Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(name, fontWeight = FontWeight.Bold, color = Ink); Text("Experience • LARP preview", color = Muted, fontSize = 12.sp) }
                Icon(Icons.Default.ChevronRight, null, tint = Muted)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Your balance", color = Muted)
                Text("R$ $robux", color = RobloxBlue, fontWeight = FontWeight.ExtraBold)
            }
        }
        item { Text("This is a local LARP preview. Experience cards are illustrative and do not launch Roblox games.", color = Muted, fontSize = 11.sp) }
    }
}

@Composable
private fun AvatarScreen(owned: Set<Int>, equipped: Int, onEquip: (Int) -> Unit, onShop: () -> Unit) {
    val inventoryItems = shopItems.filter { it.id in owned }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Avatar", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink) }
        item {
            Surface(color = Color(0xFFE6EBF7), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(116.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) { Text("🧍", fontSize = 70.sp) }
                    Spacer(Modifier.height(10.dp))
                    Text("Your avatar", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Text(if (equipped == 0) "Classic look" else "Equipped: ${shopItems.firstOrNull { it.id == equipped }?.name ?: "Item"}", color = Muted)
                }
            }
        }
        item { SectionTitle("Inventory", "${inventoryItems.size} items") }
        if (inventoryItems.isEmpty()) item { EmptyState("Your inventory is empty", "Visit the shop to get your first item.", onShop) }
        items(inventoryItems) { item ->
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(item.emoji, fontSize = 32.sp)
                Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(item.name, color = Ink, fontWeight = FontWeight.Bold); Text(item.kind, color = Muted, fontSize = 12.sp) }
                Button(onClick = { onEquip(item.id) }, colors = ButtonDefaults.buttonColors(containerColor = if (equipped == item.id) Color(0xFFE6EBF7) else RobloxBlue, contentColor = if (equipped == item.id) RobloxBlue else Color.White)) { Text(if (equipped == item.id) "Equipped" else "Wear") }
            }
        }
        item { OutlinedButton(onClick = onShop, modifier = Modifier.fillMaxWidth()) { Text("Browse shop") } }
    }
}

@Composable
private fun ShopScreen(robux: Int, owned: Set<Int>, category: String, onCategory: (String) -> Unit, onBuy: (ShopItem) -> Unit) {
    val categories = listOf("All", "Accessories", "Hair", "Faces", "Clothing")
    val visible = shopItems.filter { (category == "All" || it.kind == category) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Avatar Shop", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink); Text("Find your next look", color = Muted) }
                Surface(color = Color.White, shape = RoundedCornerShape(12.dp)) { Text("R$ $robux", modifier = Modifier.padding(12.dp), color = RobloxBlue, fontWeight = FontWeight.Bold) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                categories.forEach { c ->
                    FilterChip(selected = category == c, onClick = { onCategory(c) }, label = { Text(c) })
                }
            }
        }
        items(visible) { item ->
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(68.dp).background(Color(0xFFF0F2F6), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(item.emoji, fontSize = 34.sp) }
                Column(Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(item.name, fontWeight = FontWeight.Bold, color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.detail, color = Muted, fontSize = 11.sp)
                    Text("R$ ${item.price}", color = Ink, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { onBuy(item) }, enabled = item.id !in owned, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(if (item.id in owned) "Owned" else "Buy")
                }
            }
        }
        item { Text("Demo catalog: these are sample LARP items, not the live Roblox Marketplace catalogue.", color = Muted, fontSize = 11.sp) }
    }
}

@Composable
private fun ProfileScreen(username: String, displayName: String, bio: String, followers: Int, following: Int, friends: Int, visits: Int, owned: Int, onEdit: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Profile", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                OutlinedButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(5.dp)); Text("Edit") }
            }
        }
        item {
            Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(92.dp).background(Color(0xFFE6EBF7), CircleShape), contentAlignment = Alignment.Center) { Text("🧑", fontSize = 58.sp) }
                    Spacer(Modifier.height(10.dp))
                    Text(displayName, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text("@$username", color = Muted)
                    Text(bio, color = Ink, modifier = Modifier.padding(top = 12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Text("Fan-made LARP profile", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        item {
            Text("Statistics", color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile("Followers", followers.toString(), Modifier.weight(1f))
                StatTile("Following", following.toString(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile("Friends", friends.toString(), Modifier.weight(1f))
                StatTile("Visits", visits.toString(), Modifier.weight(1f))
                StatTile("Items", owned.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = Color.White, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(Modifier.padding(12.dp)) { Text(value, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Ink); Text(label, fontSize = 11.sp, color = Muted) }
    }
}

@Composable
private fun SectionTitle(title: String, trailing: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
        Text(trailing, color = RobloxBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String, action: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🎒", fontSize = 36.sp)
        Text(title, fontWeight = FontWeight.Bold, color = Ink)
        Text(subtitle, color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Button(onClick = action) { Text("Open shop") }
    }
}

@Composable
private fun EditStatsDialog(
    initialUser: String, initialDisplay: String, initialBio: String,
    initialRobux: Int, initialFollowers: Int, initialFollowing: Int, initialFriends: Int, initialVisits: Int,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, Int, Int, Int, Int) -> Unit
) {
    var user by remember { mutableStateOf(initialUser) }
    var display by remember { mutableStateOf(initialDisplay) }
    var bio by remember { mutableStateOf(initialBio) }
    var r by remember { mutableStateOf(initialRobux.toString()) }
    var f by remember { mutableStateOf(initialFollowers.toString()) }
    var fg by remember { mutableStateOf(initialFollowing.toString()) }
    var fr by remember { mutableStateOf(initialFriends.toString()) }
    var v by remember { mutableStateOf(initialVisits.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize your LARP profile") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 470.dp)) {
                item { EditField("Username", user) { user = it } }
                item { EditField("Display name", display) { display = it } }
                item { EditField("About", bio) { bio = it } }
                item { Text("Profile statistics", fontWeight = FontWeight.Bold, color = Ink, modifier = Modifier.padding(top = 8.dp)) }
                item { EditField("Robux", r, true) { r = it } }
                item { EditField("Followers", f, true) { f = it } }
                item { EditField("Following", fg, true) { fg = it } }
                item { EditField("Friends", fr, true) { fr = it } }
                item { EditField("Visits", v, true) { v = it } }
                item { Text("Changes apply only inside this local LARP app. They do not change a real Roblox account.", color = Muted, fontSize = 11.sp) }
            }
        },
        confirmButton = { Button(onClick = { onSave(user.ifBlank { "Player" }, display.ifBlank { "Player" }, bio, r.toIntOrNull()?.coerceAtLeast(0) ?: 0, f.toIntOrNull()?.coerceAtLeast(0) ?: 0, fg.toIntOrNull()?.coerceAtLeast(0) ?: 0, fr.toIntOrNull()?.coerceAtLeast(0) ?: 0, v.toIntOrNull()?.coerceAtLeast(0) ?: 0) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EditField(label: String, value: String, number: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = { text -> onChange(if (number) text.filter { it.isDigit() }.take(9) else text) },
        label = { Text(label) }, modifier = Modifier.fillMaxWidth(), singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (number) KeyboardType.Number else KeyboardType.Text)
    )
}
