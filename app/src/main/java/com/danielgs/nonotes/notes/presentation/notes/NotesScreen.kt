package com.danielgs.nonotes.notes.presentation.notes

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.danielgs.nonotes.R
import com.danielgs.nonotes.notes.domain.model.NoteDatabaseData
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType
import com.danielgs.nonotes.notes.presentation.APP_UID
import com.danielgs.nonotes.notes.presentation.dataStore
import com.danielgs.nonotes.notes.presentation.notes.components.NoteItem
import com.danielgs.nonotes.notes.presentation.util.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        Log.d("DEBUGNAME","Se ejecuta getNotes")
        viewModel.getNotes(NoteOrder.Date(OrderType.Descending), context)
    }

    val state = viewModel.state.value

    val token = stringResource(R.string.default_web_client_id)

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->

            viewModel.loading.value = true
            Log.d("DEBUGNAME", "Loading true1")

            val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
                preferences[APP_UID] ?: ""
            }

            val db = FirebaseDatabase.getInstance()

            scope.launch {
                userNameFlow.collect { userName ->

                    val refNube = db.getReference("notes/${result.user?.uid}")
                    val refLocal = db.getReference("notes/${userName}")

                    val dataSnapshot = refLocal.get().await()

                    val notesList = mutableListOf<NoteDatabaseData>()

                    dataSnapshot.children.forEach { innerSnapshot ->

                        val title = innerSnapshot.child("title").getValue(String::class.java)
                        val content = innerSnapshot.child("content").getValue(String::class.java)
                        val favourite =
                            innerSnapshot.child("favourite").getValue(Boolean::class.java)
                        val timestamp = innerSnapshot.child("timestamp").getValue(Long::class.java)
                        val color = innerSnapshot.child("color").getValue(Int::class.java)
                        val id = innerSnapshot.key

                        val note = NoteDatabaseData(
                            title ?: "",
                            content ?: "",
                            favourite ?: false,
                            timestamp ?: 0L,
                            color ?: 0,
                            id ?: "",
                        )
                        notesList.add(note)
                    }

                    val childUpdates = hashMapOf<String, Any>()

                    notesList.forEach {
                        note ->
                        note.id?.let { childUpdates.put(it, note) }
                    }

                    refNube.updateChildren(childUpdates)
                    refLocal.removeValue()
                    viewModel.loadAgain.value = !viewModel.loadAgain.value
                    viewModel.loading.value = false
                    Log.d("DEBUGNAME", "Loading false1")
                    viewModel.user.value = result.user
                }
            }


        },
        onAuthError = {
            viewModel.user.value = null
        }
    )

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    val snackbarHostState = SnackbarHostState()

    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background,
        darkIcons = useDarkIcons
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditNoteScreen.route)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {

                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")

            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { viewModel.onLoginButtonPressed(context, launcher, token) },
                    contentAlignment = Alignment.Center
                ) {

                    if (viewModel.user.value == null) {

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://icon-library.com/images/anonymous-avatar-icon/anonymous-avatar-icon-25.jpg")
                                .crossfade(true)
                                .build(),
                            loading = {
                                CircularProgressIndicator()
                            },
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(35.dp)
                        )

                    } else {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(viewModel.user.value!!.photoUrl)
                                .crossfade(true)
                                .build(),
                            loading = {
                                CircularProgressIndicator()
                            },
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(35.dp)
                        )
                    }
                }

                Text(
                    text = "Tus notas",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp)
                )

                IconButton(onClick = {

                    viewModel.loading.value = true
                    Log.d("DEBUGNAME", "Loading true2")
                    viewModel.getNotes(NoteOrder.Date(OrderType.Descending), context)
                    viewModel.loading.value = false
                    Log.d("DEBUGNAME", "Loading false2")

                }) {
                    Icon(Icons.Default.CloudSync, "Resync")
                }

                /*OrderSection(
                    onOrderChange = { viewModel.onEvent(NotesEvent.Order(it)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    noteOrder = state.noteOrder
                )*/
            }


            if(viewModel.loading.value){
                
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }
            }else{

                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    items(state.notes) { note ->
                        NoteItem(
                            note = note,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    val userNameFlow: Flow<String> =
                                        context.dataStore.data.map { preferences ->
                                            preferences[APP_UID] ?: ""
                                        }

                                    scope.launch {

                                        val user = Firebase.auth.currentUser

                                        if (user != null) {
                                            navController.navigate(
                                                Screen.AddEditNoteScreen.route + "?noteId=${note.id}&noteColor=${note.color}&userId=${user.uid}"
                                            )
                                        } else {

                                            userNameFlow.collect { userName ->
                                                navController.navigate(
                                                    Screen.AddEditNoteScreen.route + "?noteId=${note.id}&noteColor=${note.color}&userId=${userName}"
                                                )
                                            }
                                        }
                                    }
                                },
                            onDeleteClick = {
                                /*viewModel.onEvent(/*NotesEvent.DeleteNote(note)*/)*/
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Indefinite
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            },
                            onIntentClick = {

                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "*${note.title}*\n${note.content}")
                                    type = "text/plain"
                                }

                                startActivity(context, sendIntent, null)

                            }
                        )
                    }
                }

            }
        }
    }
}

