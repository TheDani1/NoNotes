package com.danielgs.nonotes.notes.presentation.notes

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.danielgs.nonotes.R
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType
import com.danielgs.nonotes.notes.presentation.notes.components.NoteItem
import com.danielgs.nonotes.notes.presentation.notes.components.OrderSection
import com.danielgs.nonotes.notes.presentation.util.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()


    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.getNotes(NoteOrder.Date(OrderType.Descending), context)
    }

    val state = viewModel.state.value

    val token = stringResource(R.string.default_web_client_id)

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            viewModel.user.value = result.user

            //viewModel.user.value?.uid?.let { viewModel.ref.child(it) }?.setValue(state.notes)
            // refSelfNotes.updateChildren(childUpdates)

            scope.launch {
                result.user?.let { viewModel.storeUserName(it.uid, context) }
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

                    /*val noteref = ref.child("users")

                    val notes: MutableMap<String, Note> = HashMap()

                    notes.put(
                        "alanisawesome", Note(
                            "Ejemplo",
                            "Hola nota",
                            false,
                            System.currentTimeMillis(),
                            md_theme_dark_tertiary.toArgb()
                        )
                    )

                    ref.setValue(notes)*/

                }) {
                    Icon(Icons.Default.CloudSync, "Hola")
                }

                OrderSection(
                    onOrderChange = { viewModel.onEvent(NotesEvent.Order(it)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    noteOrder = state.noteOrder
                )
            }


            if(false){
                
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

                                    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
                                        preferences[USER_NAME_KEY] ?: ""
                                    }

                                    scope.launch {
                                        userNameFlow.collect { userName ->

                                            navController.navigate(
                                                Screen.AddEditNoteScreen.route + "?noteId=${note.id}&noteColor=${note.color}&userId=${userName}"
                                            )

                                        }
                                    }




                                    Log.d("INCIADO", Screen.AddEditNoteScreen.route + "?noteId=${note.id}&noteColor=${note.color}")
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
                        )
                    }
                }

            }
        }
    }
}

