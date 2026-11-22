package com.emon.savetrack.ui.addweek

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emon.savetrack.R
import com.emon.savetrack.data.local.Transaction
import com.emon.savetrack.data.local.TransactionType
import com.emon.savetrack.ui.theme.SaveTrackTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Entry composable for AddWeekScreen collecting state from AddWeekViewModel.
 */
@Composable
fun AddWeekScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddWeekViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddWeekScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEarningAmountChange = viewModel::onEarningAmountChange,
        onAddEarning = viewModel::addEarning,
        onExpenseAmountChange = viewModel::onExpenseAmountChange,
        onExpenseItemNameChange = viewModel::onExpenseItemNameChange,
        onAddExpense = viewModel::addExpense,
        onDeleteTransaction = viewModel::deleteTransaction
    )
}

/**
 * Stateless UI content composable for AddWeekScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWeekScreenContent(
    uiState: AddWeekUiState,
    onNavigateBack: () -> Unit,
    onEarningAmountChange: (String) -> Unit,
    onAddEarning: ((String) -> Unit) -> Unit,
    onExpenseAmountChange: (String) -> Unit,
    onExpenseItemNameChange: (String) -> Unit,
    onAddExpense: ((String) -> Unit) -> Unit,
    onDeleteTransaction: (Transaction, (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_week_title)) },
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Total Savings Card at top
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.total_savings_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.formattedTotalSavings(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2. Add Earning Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.card_add_earning),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = uiState.earningAmountInput,
                        onValueChange = onEarningAmountChange,
                        label = { Text(stringResource(R.string.label_amount)) },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = uiState.earningAmountError != null,
                        supportingText = uiState.earningAmountError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onAddEarning { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.btn_add_earning))
                    }
                }
            }

            // 3. Add Expense Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.card_add_expense),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = uiState.expenseAmountInput,
                        onValueChange = onExpenseAmountChange,
                        label = { Text(stringResource(R.string.label_amount)) },
                        prefix = { Text("$ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = uiState.expenseAmountError != null,
                        supportingText = uiState.expenseAmountError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.expenseItemNameInput,
                        onValueChange = onExpenseItemNameChange,
                        label = { Text(stringResource(R.string.label_spent_on)) },
                        isError = uiState.expenseItemNameError != null,
                        supportingText = uiState.expenseItemNameError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onAddExpense { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.btn_add_expense))
                    }
                }
            }

            // 4. This Week Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.card_this_week),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.weekDateRangeText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    HorizontalDivider()

                    // Summary Stats: Earned, Spent, Saved
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.label_earned),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = uiState.formattedThisWeekEarned(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.label_spent),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = uiState.formattedThisWeekSpent(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.label_saved),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = uiState.formattedThisWeekSaved(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Saving Percent Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.label_saving_rate),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "%.1f%%".format(uiState.savingPercent),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (uiState.savingPercent / 100).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    }

                    HorizontalDivider()

                    // List of expenses for this week
                    Text(
                        text = "This Week's Expenses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (uiState.thisWeekExpenses.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_expenses_this_week),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        val dateFormat = remember { SimpleDateFormat("d MMM", Locale.ENGLISH) }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.thisWeekExpenses.forEach { tx ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tx.itemName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = dateFormat.format(Date(tx.date)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Text(
                                        text = "-$%.2f".format(tx.amount),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.error
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            onDeleteTransaction(tx) { msg ->
                                                scope.launch { snackbarHostState.showSnackbar(msg) }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.cd_delete_icon),
                                            tint = MaterialTheme.colorScheme.error
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
}

@Preview(showBackground = true, name = "AddWeek Screen Light")
@Composable
fun AddWeekScreenPreviewLight() {
    SaveTrackTheme(darkTheme = false) {
        AddWeekScreenContent(
            uiState = AddWeekUiState(
                totalSavings = 1250.00,
                weekDateRangeText = "Mon 23 Nov – Sun 29 Nov",
                thisWeekEarned = 500.0,
                thisWeekSpent = 150.0,
                thisWeekSaved = 350.0,
                savingPercent = 70.0,
                thisWeekExpenses = listOf(
                    Transaction(id = 1, type = TransactionType.EXPENSE, amount = 15.50, itemName = "Lunch"),
                    Transaction(id = 2, type = TransactionType.EXPENSE, amount = 134.50, itemName = "Groceries")
                )
            ),
            onNavigateBack = {},
            onEarningAmountChange = {},
            onAddEarning = {},
            onExpenseAmountChange = {},
            onExpenseItemNameChange = {},
            onAddExpense = {},
            onDeleteTransaction = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "AddWeek Screen Dark")
@Composable
fun AddWeekScreenPreviewDark() {
    SaveTrackTheme(darkTheme = true) {
        AddWeekScreenContent(
            uiState = AddWeekUiState(
                totalSavings = 1250.00,
                weekDateRangeText = "Mon 23 Nov – Sun 29 Nov",
                thisWeekEarned = 500.0,
                thisWeekSpent = 150.0,
                thisWeekSaved = 350.0,
                savingPercent = 70.0,
                thisWeekExpenses = listOf(
                    Transaction(id = 1, type = TransactionType.EXPENSE, amount = 15.50, itemName = "Lunch")
                )
            ),
            onNavigateBack = {},
            onEarningAmountChange = {},
            onAddEarning = {},
            onExpenseAmountChange = {},
            onExpenseItemNameChange = {},
            onAddExpense = {},
            onDeleteTransaction = { _, _ -> }
        )
    }
}