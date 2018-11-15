package io.horizontalsystems.bankwallet.modules.send

import io.horizontalsystems.bankwallet.core.IAdapter
import io.horizontalsystems.bankwallet.core.ICurrencyManager
import io.horizontalsystems.bankwallet.core.IRateManager
import io.horizontalsystems.bankwallet.entities.CoinValue
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.entities.CurrencyValue

class StateViewItemFactory(private val currencyManager: ICurrencyManager, private val rateManager: IRateManager, private val adapter: IAdapter) {

    fun viewItemForState(state: SendModule.State): SendModule.StateViewItem {
        val baseCurrency = currencyManager.baseCurrency

        val amountType = when (state.inputType) {
            SendModule.InputType.COIN -> state.coin
            SendModule.InputType.CURRENCY -> baseCurrency.symbol
        }

        val rate = rateManager.rateForCoin(state.coin, baseCurrency.code)

        var amountInfo: SendModule.AmountInfo? = null

        val balanceCoin = adapter.balance

        if (state.inputType == SendModule.InputType.COIN) {
            if (balanceCoin < state.amount) {
                amountInfo = SendModule.AmountInfo.ErrorInfo(SendModule.AmountError.InsufficientCoinBalance(CoinValue(state.coin, balanceCoin)))
            } else if (rate != null) {
                amountInfo = SendModule.AmountInfo.CurrencyValueInfo(CurrencyValue(baseCurrency, state.amount * rate.value))
            }
        } else if (rate != null) {
            val balanceCurrency = balanceCoin * rate.value
            if (balanceCurrency < state.amount) {
                amountInfo = SendModule.AmountInfo.ErrorInfo(SendModule.AmountError.InsufficientCurrencyBalance(CurrencyValue(baseCurrency, balanceCurrency)))
            } else {
                amountInfo = SendModule.AmountInfo.CoinValueInfo(CoinValue(state.coin, state.amount / rate.value))
            }
        }

        return SendModule.StateViewItem(
                amountType,
                state.amount,
                rate != null,
                amountInfo,
                null,
                CoinValue("", 0.0),
                CurrencyValue(Currency("", ""), 0.0),
                true
        )
    }

}
