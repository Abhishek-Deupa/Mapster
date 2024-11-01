package com.example.mapster.Screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.mapster.QRScannerViewModel

val indoorLocations = listOf(
    "306 IPDC Lab",
    "307A Classroom",
    "307B Control Lab",
    "308A Classroom",
    "309A Classroom",
    "309B Analog Lab",
    "Washroom",
    "310 Classroom",
    "311 Faculty Room",
    "312A Classroom",
    "313A Classroom",
    "313B Classroom",
    "314A Food Science Lab",
    "314B Biology Lab",
    "Elevator"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { QRScannerViewModel() }
    viewModel.onQRCodeScanned(result)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Current Position: $result") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IndoorLocationSearch(viewModel)
            val context = LocalContext.current
            Button(
                onClick = {
//                Toast.makeText(
//                    context,
//                    "You selected destination: ${viewModel.destination.value}",
//                    Toast.LENGTH_LONG
//                ).show()
                    CalculatePath(
                        viewModel.scannedResult.value.toString(),
                        viewModel.destination.value.toString(),
                        context, viewModel
                    )
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(text = "Show me direction")
            }

            Text(text = viewModel.calculatedPath.collectAsState().value.toString(), modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp))
        }
    }
}

@Composable
fun IndoorLocationSearch(
    viewModel: QRScannerViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                suggestions = if (newQuery.isBlank()) {
                    emptyList()
                } else {
                    indoorLocations.filter { location ->
                        location.contains(newQuery, ignoreCase = true)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isSearchFocused = it.isFocused },
            placeholder = { Text("Search destination") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            suggestions = emptyList()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors()
        )

        // Suggestions dropdown
        AnimatedVisibility(visible = isSearchFocused && suggestions.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionItem(
                            suggestion = suggestion,
                            searchQuery = searchQuery,
                            onClick = {
                                searchQuery = suggestion
                                viewModel.onLocationSearchResult(suggestion)
                                focusManager.clearFocus()
                                suggestions = emptyList()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: String,
    searchQuery: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        // Highlight matching text
        HighlightedText(
            text = suggestion,
            highlightText = searchQuery
        )
    }
}

@Composable
private fun HighlightedText(
    text: String,
    highlightText: String
) {
    val startIndex = text.indexOf(highlightText, ignoreCase = true)
    if (startIndex == -1) {
        Text(text = text)
        return
    }

    Row {
        Text(text = text.substring(0, startIndex))
        Text(
            text = text.substring(startIndex, startIndex + highlightText.length),
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = text.substring(startIndex + highlightText.length))
    }
}

private fun CalculatePath(currentLocation: String, endDestination: String, context: Context, viewModel: QRScannerViewModel) {
    if (!Python.isStarted()) {
        Python.start(AndroidPlatform(context))
    }

    val py = Python.getInstance()
    val mainModule = py.getModule("main")

    Log.d("Function Arguments",
        "Current Location: $currentLocation Destination Location: $endDestination"
    )

    val calculatedPath = mainModule["masterFunction"]?.call(viewModel.scannedResult.value, viewModel.destination.value)

    viewModel.onPathCalculation(calculatedPath.toString())
}