package com.example.frontend.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(AppComponents) -> Unit
) {

    val components = AppComponents()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content(components)
    }
}

class AppComponents {

    @Composable
    fun Title(text: String) {

        Text(
            text = text,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }

    @Composable
    fun PrimaryButton(
        text: String,
        onClick: () -> Unit
    ) {

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )

        }
    }

    @Composable
    fun AuthTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String = "",
        isPassword: Boolean = false
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSecondary
            )

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder) },

                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 48.dp),

                visualTransformation = if (isPassword)
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
    }
}