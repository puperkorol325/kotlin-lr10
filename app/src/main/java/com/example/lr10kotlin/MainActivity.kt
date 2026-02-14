package com.example.lr10kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.lr10kotlin.ui.theme.Lr10kotlinTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.delay
import kotlin.text.Regex

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lr10kotlinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    CoroutinesScreen()
                }
            }
        }
    }
}


suspend fun simulateLongOperation(duration: Long): String {
    delay(duration)
    return "Операция завершена за $duration мс"
}

suspend fun calculateSum(numbers: List<Int>): Int {
    return withContext(Dispatchers.Default) {
        delay(1000)
        numbers.sum()
    }
}

@Composable
fun CoroutinesScreen() {
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var textFieldValue by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            scope.coroutineContext[Job]?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Выполнение операции...")
        }

        if (result != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = result.toString(),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    try {
                        val res = simulateLongOperation(2000)
                        result = res
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить долгую операцию")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                if (it.matches(Regex("[\\d\\s]*"))) {
                    textFieldValue = it
                }
            },
            enabled = !isLoading,
            placeholder = { Text("Введите порядок чисел") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    try {
                        val numbers: List<Int> = listOf<Int>(
                            *textFieldValue.trim().split("\\s+".toRegex()).map { it.toInt() }.toTypedArray()
                        );
                        val sum = calculateSum(numbers)
                        result = "Сумма чисел: $sum"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вычислить сумму")
        }
    }
}