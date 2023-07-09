package com.danielgs.nonotes.notes.presentation.notes.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.danielgs.nonotes.notes.domain.util.NoteOrder
import com.danielgs.nonotes.notes.domain.util.OrderType

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    onOrderChange: (NoteOrder) -> Unit
) {

    var expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        Row() {

            TextButton(onClick = { expanded.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onBackground)
            ) {

                when (noteOrder) {
                    is NoteOrder.Title -> {
                        Text("Title", style = MaterialTheme.typography.titleMedium)
                    }

                    is NoteOrder.Date -> {
                        Text("Date", style = MaterialTheme.typography.titleMedium)
                    }

                    is NoteOrder.Color -> {
                        Text("Color", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort",
                modifier = Modifier.padding(start = 10.dp))
            }

            DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {

                DropdownMenuItem(
                    text = { Text("Title") },
                    onClick = { onOrderChange(NoteOrder.Title(noteOrder.orderType)) },
                    trailingIcon = {
                        if (noteOrder is NoteOrder.Title) Icon(
                            Icons.Default.Check,
                            "Check"
                        )
                    }
                )

                DropdownMenuItem(
                    text = { Text("Date") },
                    onClick = { onOrderChange(NoteOrder.Date(noteOrder.orderType)) },
                    trailingIcon = {
                        if (noteOrder is NoteOrder.Date) Icon(
                            Icons.Default.Check,
                            "Check"
                        )
                    }
                )

                DropdownMenuItem(
                    text = { Text("Color") },
                    onClick = { onOrderChange(NoteOrder.Color(noteOrder.orderType)) },
                    trailingIcon = {
                        if (noteOrder is NoteOrder.Color) Icon(
                            Icons.Default.Check,
                            "Check"
                        )
                    }
                )

                /*DefaultRadioButton(
                    text = "Title",
                    selected = noteOrder is NoteOrder.Title,
                    onSelect = { onOrderChange(NoteOrder.Title(noteOrder.orderType)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                DefaultRadioButton(
                    text = "Date",
                    selected = noteOrder is NoteOrder.Date,
                    onSelect = { onOrderChange(NoteOrder.Date(noteOrder.orderType)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                DefaultRadioButton(
                    text = "Color",
                    selected = noteOrder is NoteOrder.Color,
                    onSelect = { onOrderChange(NoteOrder.Color(noteOrder.orderType)) }
                )*/
                /*Spacer(modifier = Modifier.height(16.dp))
                DefaultRadioButton(
                    text = "Ascending",
                    selected = noteOrder.orderType is OrderType.Ascending,
                    onSelect = {
                        onOrderChange(noteOrder.copy(OrderType.Ascending))
                    }

                )
                Spacer(modifier = Modifier.width(8.dp))
                DefaultRadioButton(
                    text = "Ascending",
                    selected = noteOrder.orderType is OrderType.Descending,
                    onSelect = {
                        onOrderChange(noteOrder.copy(OrderType.Descending))
                    }

                )*/
            }

            IconButton(onClick = {
                if (noteOrder.orderType is OrderType.Descending) {
                    onOrderChange(noteOrder.copy(OrderType.Ascending))
                } else {
                    onOrderChange(noteOrder.copy(OrderType.Descending))
                }
            }) {

                if (noteOrder.orderType is OrderType.Descending) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Descending"
                    )
                } else {
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Ascending")
                }

            }

        }

    }

    /*Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            DefaultRadioButton(
                text = "Title",
                selected = noteOrder is NoteOrder.Title,
                onSelect = { onOrderChange(NoteOrder.Title(noteOrder.orderType)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Date",
                selected = noteOrder is NoteOrder.Date,
                onSelect = { onOrderChange(NoteOrder.Date(noteOrder.orderType)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Color",
                selected = noteOrder is NoteOrder.Color,
                onSelect = { onOrderChange(NoteOrder.Color(noteOrder.orderType)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DefaultRadioButton(
                text = "Ascending",
                selected = noteOrder.orderType is OrderType.Ascending,
                onSelect = {
                    onOrderChange(noteOrder.copy(OrderType.Ascending))
                }

            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = "Ascending",
                selected = noteOrder.orderType is OrderType.Descending,
                onSelect = {
                    onOrderChange(noteOrder.copy(OrderType.Descending))
                }

            )
        }
        
    }*/


}