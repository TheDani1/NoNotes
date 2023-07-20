package com.danielgs.nonotes.notes.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.danielgs.nonotes.notes.presentation.add_util_note.AddEditNoteScreen
import com.danielgs.nonotes.notes.presentation.notes.NotesScreen
import com.danielgs.nonotes.notes.presentation.util.Screen
import com.example.compose.NoNotesTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

val Context.dataStore by preferencesDataStore("user_preferences")
val APP_UID = stringPreferencesKey("APP_UID")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Firebase.database.setPersistenceEnabled(true)

        val userAgeFlow: Flow<String> = dataStore.data.map { preferences ->
            preferences[APP_UID] ?: ""
        }

        lifecycleScope.launch {
            userAgeFlow.collect { userName ->
                if(userName.isBlank() || userName.isEmpty()){
                    dataStore.edit {
                            preferences ->
                        preferences[APP_UID] = UUID.randomUUID().toString()
                        Log.d("DEBUGNAME", "se edita UUID")
                    }
                }
            }
        }

        setContent {
            NoNotesTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NotesScreen.route
                    ) {
                        composable(route = Screen.NotesScreen.route) {
                            NotesScreen(navController = navController)
                        }
                        composable(route = Screen.AddEditNoteScreen.route + "?noteId={noteId}&noteColor={noteColor}&userId={userId}",
                            arguments = listOf(

                                navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.StringType
                                defaultValue = ""
                            },
                                navArgument(
                                    name = "noteColor"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },

                                navArgument(
                                    name = "userId"
                                ) {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) {
                            val color = it.arguments?.getInt("noteColor") ?: -1
                            val id = it.arguments?.getString("userId") ?: ""
                            AddEditNoteScreen(navController = navController, noteColor = color)

                        }
                    }
                }
            }
        }
    }
}