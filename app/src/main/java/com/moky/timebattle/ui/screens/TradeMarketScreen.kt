package com.moky.timebattle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moky.timebattle.data.AppViewModel
import com.moky.timebattle.data.model.TradeOffer
import com.moky.timebattle.ui.components.AppButton
import com.moky.timebattle.ui.components.AppInput
import com.moky.timebattle.ui.components.ButtonVariant
import com.moky.timebattle.ui.theme.AbyssBlack
import com.moky.timebattle.ui.theme.DimWhite
import com.moky.timebattle.ui.theme.LifeRed
import com.moky.timebattle.ui.theme.MutedWhite
import com.moky.timebattle.ui.theme.StrokeLight
import com.moky.timebattle.ui.theme.StrokeRed
import com.moky.timebattle.ui.theme.TimebattleTheme
import com.moky.timebattle.util.VibrationHelper

@Composable
fun TradeMarketScreen(
    viewModel: AppViewModel,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsState()

    var sellHours by remember { mutableStateOf("1") }
    var sellPrice by remember { mutableStateOf("10") }

    TradeMarketContent(
        reputationPoints = state.reputationPoints,
        offers = state.tradeOffers,
        sellHours = sellHours,
        onSellHoursChange = { sellHours = it.filter { c -> c.isDigit() } },
        sellPrice = sellPrice,
        onSellPriceChange = { sellPrice = it.filter { c -> c.isDigit() || c == '.' } },
        onBuy = { offerId ->
            if (viewModel.buyTime(offerId)) {
                VibrationHelper.vibrateSuccess(context)
                onShowMessage("购买成功，时间已到账")
            } else {
                VibrationHelper.vibrateWarning(context)
                onShowMessage("信誉点不足")
            }
        },
        onSell = {
            val hours = sellHours.toLongOrNull() ?: 0
            val price = sellPrice.toDoubleOrNull() ?: 0.0
            if (hours <= 0 || price <= 0) {
                VibrationHelper.vibrateWarning(context)
                onShowMessage("请输入有效的数量与价格")
                return@TradeMarketContent
            }
            if (viewModel.sellTime(hours * 3600, price)) {
                VibrationHelper.vibrateSuccess(context)
                onShowMessage("挂单成功")
                sellHours = "1"
                sellPrice = "10"
            } else {
                VibrationHelper.vibrateWarning(context)
                onShowMessage("生命时间不足")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun TradeMarketContent(
    reputationPoints: Int,
    offers: List<TradeOffer>,
    sellHours: String,
    onSellHoursChange: (String) -> Unit,
    sellPrice: String,
    onSellPriceChange: (String) -> Unit,
    onBuy: (String) -> Unit,
    onSell: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssBlack)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "交易中心",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = "信誉点：$reputationPoints",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sell section
        Text(
            text = "出售时间",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppInput(
                value = sellHours,
                onValueChange = onSellHoursChange,
                placeholder = "小时",
                modifier = Modifier.weight(1f)
            )
            AppInput(
                value = sellPrice,
                onValueChange = onSellPriceChange,
                placeholder = "单价/小时",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        AppButton(
            text = "挂牌出售",
            onClick = onSell,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Outline
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Market list
        Text(
            text = "市场挂单",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (offers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无挂单",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MutedWhite)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                offers.forEach { offer ->
                    OfferCard(offer = offer, onBuy = { onBuy(offer.id) })
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OfferCard(
    offer: TradeOffer,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StrokeLight, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "@${offer.sellerName}",
                style = MaterialTheme.typography.bodySmall.copy(color = DimWhite)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${offer.amountSeconds / 3600}h ${(offer.amountSeconds % 3600) / 60}m",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp)
            )
            Text(
                text = String.format("单价 %.1f / 总价 %.1f", offer.pricePerHour, offer.totalPrice),
                style = MaterialTheme.typography.bodySmall.copy(color = MutedWhite)
            )
        }
        Box(
            modifier = Modifier
                .border(1.dp, StrokeRed, RoundedCornerShape(6.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onBuy
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "购买",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LifeRed,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TradeMarketContentPreview() {
    TimebattleTheme {
        TradeMarketContent(
            reputationPoints = 100,
            offers = listOf(
                TradeOffer(sellerName = "星河", amountSeconds = 3600, pricePerHour = 12.0, totalPrice = 12.0),
                TradeOffer(sellerName = "夜行者", amountSeconds = 7200, pricePerHour = 11.0, totalPrice = 22.0)
            ),
            sellHours = "1",
            onSellHoursChange = {},
            sellPrice = "10",
            onSellPriceChange = {},
            onBuy = {},
            onSell = {}
        )
    }
}
