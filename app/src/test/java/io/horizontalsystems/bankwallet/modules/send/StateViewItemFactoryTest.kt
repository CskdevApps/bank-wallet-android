package io.horizontalsystems.bankwallet.modules.send

import com.nhaarman.mockito_kotlin.whenever
import io.horizontalsystems.bankwallet.core.IAdapter
import io.horizontalsystems.bankwallet.core.ICurrencyManager
import io.horizontalsystems.bankwallet.core.IRateManager
import io.horizontalsystems.bankwallet.entities.CoinValue
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.entities.Rate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class StateViewItemFactoryTest {

    private val state = mock(SendModule.State::class.java)
    private val currencyManager = mock(ICurrencyManager::class.java)
    private val rateManager = mock(IRateManager::class.java)
    private val adapter = mock(IAdapter::class.java)

    private val factory = StateViewItemFactory(currencyManager, rateManager, adapter)

    val coin = "BTC"
    val currencyCode = "USD"
    val baseCurrency = mock(Currency::class.java)
    val amount = 123.12

    @Before
    fun before() {
        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)
        whenever(state.coin).thenReturn(coin)
        whenever(state.amount).thenReturn(amount)

        whenever(currencyManager.baseCurrency).thenReturn(baseCurrency)
        whenever(baseCurrency.code).thenReturn(currencyCode)
        whenever(baseCurrency.symbol).thenReturn("")
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(null)
        whenever(adapter.balance).thenReturn(amount + 1)
    }

    @Test
    fun viewItemForState_amountType_coin() {
        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)

        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(coin, viewItem.amountType)
    }

    @Test
    fun viewItemForState_amountType_currency() {
        val symbol = "$"

        whenever(state.inputType).thenReturn(SendModule.InputType.CURRENCY)
        whenever(baseCurrency.symbol).thenReturn(symbol)

        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(symbol, viewItem.amountType)
    }

    @Test
    fun viewItemForState_amount() {
        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(amount, viewItem.amount, 0.0)
    }

    @Test
    fun viewItemForState_switchButtonEnabled() {
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(mock(Rate::class.java))

        val viewItem = factory.viewItemForState(state)

        Assert.assertTrue(viewItem.switchButtonEnabled)
    }

    @Test
    fun viewItemForState_switchButtonDisabled() {
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(null)

        val viewItem = factory.viewItemForState(state)

        Assert.assertFalse(viewItem.switchButtonEnabled)
    }

    @Test
    fun viewItemForState_amountInfo_currencyValue() {
        val rate = mock(Rate::class.java)
        val rateValue = 6500.0
        val expectedValue = amount * rateValue

        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)
        whenever(state.amount).thenReturn(amount)
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(rate)
        whenever(rate.value).thenReturn(rateValue)

        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(SendModule.AmountInfo.CurrencyValueInfo(CurrencyValue(baseCurrency, expectedValue)), viewItem.amountInfo)
    }

    @Test
    fun viewItemForState_amountInfo_coinValue() {
        val rate = mock(Rate::class.java)
        val rateValue = 6500.0
        val expectedValue = amount / rateValue

        whenever(state.inputType).thenReturn(SendModule.InputType.CURRENCY)
        whenever(state.amount).thenReturn(amount)
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(rate)
        whenever(rate.value).thenReturn(rateValue)

        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(SendModule.AmountInfo.CoinValueInfo(CoinValue(coin, expectedValue)), viewItem.amountInfo)
    }

    @Test
    fun viewItemForState_amountInfo_nullRate() {
        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(null)

        val viewItem = factory.viewItemForState(state)

        Assert.assertNull(viewItem.amountInfo)
    }

    @Test
    fun viewItemForState_amountInfo_errorInsufficientCoinBalance() {
        val balance = amount - 1

        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)
        whenever(state.amount).thenReturn(amount)
        whenever(adapter.balance).thenReturn(balance)

        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(SendModule.AmountInfo.ErrorInfo(SendModule.AmountError.InsufficientCoinBalance(CoinValue(coin, balance))), viewItem.amountInfo)
    }

    @Test
    fun viewItemForState_amountInfo_errorInsufficientCurrencyBalance() {
        val rateValue = 6500.0
        val balance = amount / rateValue - 1
        val balanceCurrency = balance * rateValue

        val rate = mock(Rate::class.java)

        whenever(state.inputType).thenReturn(SendModule.InputType.CURRENCY)
        whenever(state.amount).thenReturn(amount)
        whenever(rateManager.rateForCoin(coin, currencyCode)).thenReturn(rate)
        whenever(rate.value).thenReturn(rateValue)
        whenever(adapter.balance).thenReturn(balance)

        val viewItem = factory.viewItemForState(state)

        Assert.assertEquals(SendModule.AmountInfo.ErrorInfo(SendModule.AmountError.InsufficientCurrencyBalance(CurrencyValue(baseCurrency, balanceCurrency))), viewItem.amountInfo)
    }
}