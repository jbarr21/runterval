package io.github.jbarr21.runterval.ui.util

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TextSwitcher(
  text: String,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign? = null,
  modifier: Modifier = Modifier
) {
  var text1 by remember { mutableStateOf("") }
  var text2 by remember { mutableStateOf("") }
  var useFirst by remember { mutableStateOf(true) }

  val activeText = if (useFirst) text1 else text2
  if (text != activeText) {
    // change otherText
    if (useFirst) {
      text2 = text
    } else {
      text1 = text
    }
    useFirst = !useFirst
  }

  Crossfade(targetState = useFirst, modifier = modifier) {
    if (it) {
      Text(text1, style = style, textAlign = textAlign, modifier = Modifier.fillMaxWidth())
    } else {
      Text(text2, style = style, textAlign = textAlign, modifier = Modifier.fillMaxWidth())
    }
  }
}

@Preview(widthDp = 128)
@Composable
fun TextSwitcherPreview() {
  TextSwitcher(
    text = "Foo",
    textAlign = TextAlign.Center,
    modifier = Modifier.fillMaxWidth()
  )
}
