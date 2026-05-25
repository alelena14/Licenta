package com.example.frontend.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false
) {

    TextField(
        value = value,
        onValueChange = onValueChange,

        placeholder = {
            Text(text = placeholder)
        },

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),

        visualTransformation =
            if (isPassword)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,

        singleLine = true,

        colors = TextFieldDefaults.colors(

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,

            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray,

            cursorColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}