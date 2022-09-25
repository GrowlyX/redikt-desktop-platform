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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import java.net.URL
import java.time.Duration
import java.util.concurrent.CompletableFuture

const val iconUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQv8Tg3nhk8LFg5HeT7Uzb3m0zNM0_gphSD5W1s47iRshELk3vmXZK1a_VGHpdRho010yo&usqp=CAU"

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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

            val text = remember {
                mutableStateOf("Click to connect!")
            }
            val keys = remember {
                mutableStateOf("Keys (none):")
            }

            val connection by lazy {
                RedisClient
                    .create(
                        RedisURI(
                            hostname.value.text, 6379,
                            Duration.ofSeconds(1L)
                        )
                    )
                    .connect()
            }

            Button(
                onClick = {
                    if (text.value == "Connecting...")
                    {
                        return@Button
                    }

                    text.value = "Connecting..."
                    val start = System.currentTimeMillis()

                    CompletableFuture
                        .runAsync {
                            connection.sync().ping()

                            text.value = "Connected! ${
                                System.currentTimeMillis() - start
                            }ms"

                            keys.value = """
                                Keys:
                                ${
                                connection.sync()
                                    .keys("*")
                                    .joinToString("\n")
                                }
                            """.trimIndent()
                        }
                        .exceptionally {
                            text.value = "Failed to connect! \n(${it.message})"
                            return@exceptionally null
                        }
                },
                colors = ButtonDefaults
                    .textButtonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    )
            ) {
                Text(text.value, textAlign = TextAlign.Center)
            }

            Text(keys.value)
        }
    }
}

fun main() = application {
    val stream = URL(iconUrl).openStream()

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
