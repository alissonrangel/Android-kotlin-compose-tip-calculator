package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiptime.ui.theme.TipTimeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeScreen()
                }
            }
        }
    }
}

@Composable
fun TipTimeScreen() {
    var amountInput by remember {
        mutableStateOf("")
    }

    var tipInput by remember {
        mutableStateOf("")
    }

    var roundUp by remember {
        mutableStateOf(false)
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0

    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0

    val tip = calculateTip(amount, roundUp, tipPercent)

    val focusManager = LocalFocusManager.current



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF317383))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            text = stringResource(id = R.string.calculate_tip),
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Cursive,
            color = Color(0xFF131836),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        EditNumberField(
            id = R.string.bill_amount,
            KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            value = amountInput,
            onValueChange = {
                amountInput = it
            })
        EditNumberField(
            id = R.string.how_was_the_service,
            KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            value = tipInput,
            onValueChange = {
                tipInput = it
            })
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                tipInput = "5.0"
            }) {
                Text(text = "5%", fontSize = 20.sp)
            }
            Button(onClick = {
                tipInput = "10.0"
            }) {
                Text(text = "10%", fontSize = 20.sp)
            }
            Button(onClick = {
                tipInput = "15.0"
            }) {
                Text(text = "15%", fontSize = 20.sp)
            }
            Button(onClick = {
                tipInput = "20.0"
            }) {
                Text(text = "20%", fontSize = 20.sp)
            }
        }
        RoundTheTipRow(
            id = R.string.round_up_tip,
            isRound = roundUp,
            onCheckChange = {
                roundUp = it
            })
        Text(
            text = stringResource(id = R.string.tip_amount, tip),
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
//                .fillMaxSize()
//                .wrapContentSize(Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color(0xFF131836)
        )
        Button(
            onClick = {
                amountInput = ""
                tipInput = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.BottomCenter),
        ) {
            //Text(text = stringResource(id = R.string.recaulculate))
            Text(text = "Recalculate", fontSize = 24.sp)
        }
    }
}

@Composable
fun RoundTheTipRow(@StringRes id: Int, isRound: Boolean, onCheckChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(48.dp)
            .padding(end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(id = id),
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentWidth(Alignment.Start),
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Switch(
            checked = isRound,
            onCheckedChange = onCheckChange,
            //modifier = Modifier
            //.fillMaxWidth()
            //.wrapContentWidth(Alignment.End),
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray,
                uncheckedBorderColor = Color.White,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    @StringRes id: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier,
    value: String, onValueChange: (String) -> Unit
) {

    TextField(
        modifier = modifier,
        value = value,
        keyboardActions = keyboardActions,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(id = id))
        },
        singleLine = true,
        keyboardOptions = keyboardOptions
        //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@VisibleForTesting
internal fun calculateTip(amount: Double, roundUp: Boolean, tipPercent: Double = 15.0): String {
    var tip = (tipPercent / 100) * amount

    if (roundUp) {
        tip = ceil(tip)
    }

    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}