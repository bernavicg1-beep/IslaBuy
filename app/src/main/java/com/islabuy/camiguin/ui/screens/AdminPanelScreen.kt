package com.islabuy.camiguin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islabuy.camiguin.data.repository.CategoryRepository
import com.islabuy.camiguin.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onBack: () -> Unit = {}
) {
    val categoryRepo = remember { CategoryRepository() }
    val scope = rememberCoroutineScope()
    var categories by remember { mutableStateOf<List<com.islabuy.camiguin.data.model.Category>>(emptyList()) }
    var newCategory by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            try { categories = categoryRepo.getAll() } catch (_: Exception) { }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Admin Panel") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Manage Categories", fontWeight = FontWeight.Bold, color = PrimaryText)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = newCategory, onValueChange = { newCategory = it }, label = { Text("New category") }, modifier = Modifier.weight(1f))
                Button(onClick = {
                    scope.launch {
                        if (newCategory.isNotBlank()) {
                            categoryRepo.add(com.islabuy.camiguin.data.model.Category(name = newCategory))
                            categories = categoryRepo.getAll()
                            newCategory = ""
                        }
                    }
                }) { Text("Add") }
            }
            Spacer(Modifier.height(12.dp))
            categories.forEach { c ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(c.name, color = PrimaryText)
                        TextButton(onClick = { scope.launch { categoryRepo.delete(c.categoryId); categories = categoryRepo.getAll() } }) {
                            Text("Delete", color = ErrorRed)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Other admin tools (users, products, announcements, GCash verification) require Cloud Functions / admin SDK setup.", color = SecondaryText, fontSize = 12.sp)
        }
    }
}