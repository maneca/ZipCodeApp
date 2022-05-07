package com.joao.zipcodeapp

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joao.zipcodeapp.viewmodels.ZipCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("OPT_IN_IS_NOT_ENABLED")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ){
                var query by remember { mutableStateOf("") }
                val keyboardController = LocalSoftwareKeyboardController.current
                val viewModel = hiltViewModel<ZipCodeViewModel>()
                val isLoading = remember { mutableStateOf(true) }
                val state = viewModel.state.collectAsState()

                SystemBroadcastReceiver(DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    isLoading.value = false
                    viewModel.populateDatabase()
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .imePadding()
                ){
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = MaterialTheme.colors.primary,
                        elevation = 8.dp,
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextField(
                                value = query,
                                onValueChange = {
                                    query = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                label = { Text(text = "Search") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                leadingIcon = { Icon(Icons.Filled.Search, "") },
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        keyboardController?.hide()
                                        viewModel.searchZipCode(query)
                                    }
                                ),
                                textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = MaterialTheme.colors.surface
                                )
                            )
                        }
                    }
                    if(isLoading.value && state.value.zipCodes.isEmpty()){
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ){
                            CircularProgressIndicator()
                        }
                    }else{


                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)) {
                            items(state.value.zipCodes.size) { position ->
                                val zipCode = state.value.zipCodes[position]
                                Card(Modifier.fillMaxSize().padding(horizontal = 1.dp, vertical = 4.dp)) {
                                    Row(modifier = Modifier
                                        .fillMaxSize().padding(8.dp)) {

                                        Text(
                                            text = zipCode.codigoPostal,
                                            style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = zipCode.designacaoPostal,
                                            style = TextStyle(fontSize = 15.sp)
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

@Composable
fun SystemBroadcastReceiver(
    systemAction: String,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    // Grab the current context in this part of the UI tree
    val context = LocalContext.current

    // Safely use the latest onSystemEvent lambda passed to the function
    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

    // If either context or systemAction changes, unregister and register again
    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentOnSystemEvent(intent)
            }
        }

        context.registerReceiver(broadcast, intentFilter)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}