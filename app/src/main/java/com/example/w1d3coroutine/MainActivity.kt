package com.example.w1d3coroutine



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val N = 100

class MainActivity : ComponentActivity() {

    private class Account {
        private var amount: Double = 0.0
        private val mutex = Mutex()

        suspend fun deposit(amount: Double) {
            mutex.withLock {
                val x = this.amount
                delay(1) // simulates processing time
                this.amount = x + amount
            }
        }

        fun saldo(): Double = amount
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val results = runBlocking { bankProcess(Account()) }
        setContent {
            ShowResults(saldo1 = results.saldo1, saldo2 = results.saldo2)
        }
    }

    private suspend fun bankProcess(account: Account): Saldos {
        var saldo1: Double = 0.0
        var saldo2: Double = 0.0

        coroutineScope {
            launch {
                for (i in 1..N) {
                    account.deposit(0.0)
                    saldo1 = account.saldo()
                }
            }
        }

        coroutineScope {
            launch {
                for (i in 1..N) account.deposit(1.0)
            }
            launch {
                for (i in 1..N) account.deposit(1.0)
            }
            saldo2 = account.saldo()
        }

        return Saldos(saldo1, saldo2)
    }

    private data class Saldos(val saldo1: Double, val saldo2: Double)

    @Composable
    private fun ShowResults(saldo1: Double, saldo2: Double) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Saldo1: $saldo1")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Saldo2: $saldo2")
            }
        }
    }
}

