package com.emon.savetrack.ui.history

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emon.savetrack.R
import com.emon.savetrack.data.local.Transaction
import com.emon.savetrack.data.local.TransactionType
import com.emon.savetrack.ui.theme.SaveTrackTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formats a number as NZ dollars, e.g. $1,250.00 */
private fun nzd(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-NZ")).format(amount)

/**
 * Entry composable: collects the weekly summaries from HistoryViewModel.
 */
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel
) {
    val weeks by viewModel.weeks.collectAsStateWithLifecycle()
    HistoryScreenContent(weeks = weeks, onNavigateBack = onNavigateBack)
}

/**
 * Stateless UI: a list of week cards, newest first.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    weeks: List<WeekSummary>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (weeks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(weeks, key = { it.weekStart }) { week ->
                    WeekCard(week)
                }
            }
        }
    }
}

/**
 * One week card. Tap to expand and see what the money was spent on.
 */
@Composable
private fun WeekCard(week: WeekSummary) {
    // Remembers open/closed per week, even after screen rotation
    var expanded by rememberSaveable(week.weekStart) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: date range + expand arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = week.dateRangeText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(
                        if (expanded) R.string.cd_collapse else R.string.cd_expand
                    )
                )
            }

            // Earned / Spent / Saved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Stat(label = stringResource(R.string.label_earned), value = nzd(week.earned))
                Stat(label = stringResource(R.string.label_spent), value = nzd(week.spent))
                Stat(
                    label = stringResource(R.string.label_saved),
                    value = nzd(week.saved),
                    highlight = true
                )
            }

            // Percentages + progress bar (how much of the earnings was spent)
            if (week.earned > 0) {
                Text(
                    text = stringResource(
                        R.string.history_percent,
                        week.spentPercent,
                        week.savedPercent
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                LinearProgressIndicator(
                    progress = { (week.spentPercent / 100).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (week.spent > week.earned) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.history_no_earnings),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Expense list, shown only when the card is expanded
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    if (week.expenses.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_expenses_this_week),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        val dateFormat = remember { SimpleDateFormat("EEE d MMM", Locale.ENGLISH) }
                        week.expenses.forEach { tx ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.itemName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = dateFormat.format(Date(tx.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = "-" + nzd(tx.amount),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, highlight: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ---------- Previews ----------

private val previewWeeks = listOf(
    WeekSummary(
        weekStart = 2L,
        dateRangeText = "Mon 23 Nov – Sun 29 Nov",
        earned = 500.0, spent = 120.0, saved = 380.0,
        spentPercent = 24.0, savedPercent = 76.0,
        expenses = listOf(
            Transaction(id = 1, type = TransactionType.EXPENSE, amount = 120.0, itemName = "Groceries")
        )
    ),
    WeekSummary(
        weekStart = 1L,
        dateRangeText = "Mon 16 Nov – Sun 22 Nov",
        earned = 400.0, spent = 250.0, saved = 150.0,
        spentPercent = 62.5, savedPercent = 37.5,
        expenses = emptyList()
    )
)

@Preview(showBackground = true, name = "History Light Mode")
@Composable
fun HistoryScreenPreviewLight() {
    SaveTrackTheme(darkTheme = false) {
        HistoryScreenContent(weeks = previewWeeks, onNavigateBack = {})
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "History Dark Mode")
@Composable
fun HistoryScreenPreviewDark() {
    SaveTrackTheme(darkTheme = true) {
        HistoryScreenContent(weeks = previewWeeks, onNavigateBack = {})
    }
}
