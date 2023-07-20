package com.danielgs.nonotes.notes.presentation.add_util_note

import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.danielgs.nonotes.notes.domain.model.Note
import com.danielgs.nonotes.notes.presentation.add_util_note.components.TransparentHintTextField
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val isReadOnly = rememberSaveable{ mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()

    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val favouriteState = viewModel.favourite.value

    val snackbarHostState = SnackbarHostState()

    val noteBackGroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
        )

    }

    val colorsVisitibility = rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    systemUiController.setSystemBarsColor(noteBackGroundAnimatable.value)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.showSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }

                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    Log.d("INCIADO", "Viene de abajo")
                    navController.navigateUp()
                }
            }
        }
        Log.d("INCIADO", "Viene de abajo2")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.background(noteBackGroundAnimatable.value),
                title = {
                    TransparentHintTextField(
                        text = titleState.text,
                        hint = titleState.hint,
                        onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                        onFocusChange = {
                            viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it))
                        },
                        isHintVisible = titleState.isHintVisible,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        readOnly = isReadOnly.value,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowLeft,
                            contentDescription = "Backward",
                        )
                    }
                },
                actions = {
                    if(isReadOnly.value){

                        IconButton(onClick = { isReadOnly.value = !isReadOnly.value }) {
                            Icon(
                                imageVector = Icons.Filled.Book,
                                contentDescription = "Localized description",
                            )
                        }

                    }else{

                        IconButton(onClick = { isReadOnly.value = !isReadOnly.value }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Localized description",
                            )
                        }

                    }

                    Log.d("FAVORITE", favouriteState.isFavorite.toString())

                    if(favouriteState.isFavorite){

                        IconButton(onClick = { viewModel.onEvent(AddEditNoteEvent.ChangeFavourite(false)) }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdded,
                                contentDescription = "Localized description",
                            )
                        }

                    }else{

                        IconButton(onClick = { viewModel.onEvent(AddEditNoteEvent.ChangeFavourite(true)) }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdd,
                                contentDescription = "Localized description",
                            )
                        }

                    }


                    IconButton(onClick = { colorsVisitibility.value = !colorsVisitibility.value }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description",
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = noteBackGroundAnimatable.value)
            )


        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditNoteEvent.SaveNote(context))
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save note")
            }
        },
        containerColor = Color.Red,
        contentColor = Color.Red,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackGroundAnimatable.value)
                .padding(innerPadding)
        ) {

            AnimatedVisibility(visible = colorsVisitibility.value,
                enter = slideInVertically() + expandVertically() + fadeIn(),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Note.noteColors.forEach { color ->
                        val colorInt = color.toArgb()

                        Box(modifier = Modifier
                            .size(50.dp)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (viewModel.noteColor.value == colorInt) {
                                    Color.Black
                                } else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    noteBackGroundAnimatable.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(
                                            durationMillis = 1500
                                        )
                                    )
                                }
                                viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                            })
                    }
                }

            }

            //Spacer(modifier = Modifier.height(16.dp))
            /*TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                onFocusChange = {
                    viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it))
                },
                isHintVisible = titleState.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )*/
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                onFocusChange = {
                    viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it))
                },
                isHintVisible = contentState.isHintVisible,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                readOnly = isReadOnly.value
            )
        }
    }
}