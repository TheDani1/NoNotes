package com.danielgs.nonotes.notes.presentation.notes.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.danielgs.nonotes.notes.domain.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
) {

    Column() {

        Card(
            modifier = modifier.padding(top=8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(note.color))
        ) {

            Box(
                modifier = modifier
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(end = 32.dp)
                ) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.BottomEnd)
                ){
                    IconButton(
                        onClick = onDeleteClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete note"
                        )

                    }
                }

            }
        }

        Column(modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Row(modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){

                val format = SimpleDateFormat("d MMM",  Locale.getDefault())

                Text(
                    text = format.format(note.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                if(note.favourite){
                    Icon(
                        imageVector = Icons.Default.BookmarkAdded,
                        contentDescription = "Delete note",
                        modifier = Modifier.size(15.dp)
                    )
                }

            }



        }



    }

}