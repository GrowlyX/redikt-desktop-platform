// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.net.URL

@Composable
@Preview
fun App()
{
    MaterialTheme {
        val hostname = remember {
            mutableStateOf(TextFieldValue())
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Enter the Redis hostname:")

            TextField(
                value = hostname.value,
                onValueChange = {
                    hostname.value = it
                }
            )

            Text("Enter the Redis port:")

            TextField(
                value = "6379",
                onValueChange = {},
                readOnly = true
            )

            Button(
                onClick = {

                },
                colors = ButtonDefaults
                    .textButtonColors(
                        backgroundColor = Color.Red
                    )
            ) {
                Text("Click to connect!")
            }
        }
    }
}

fun main() = application {
    val stream =
        URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQv8Tg3nhk8LFg5HeT7Uzb3m0zNM0_gphSD5W1s47iRshELk3vmXZK1a_VGHpdRho010yo&usqp=CAU").openStream()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Redify Redis Navigator",
        icon = BitmapPainter(
            image = loadImageBitmap(stream)
        )
    ) {
        App()
    }
}
