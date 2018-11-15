package io.horizontalsystems.bankwallet.modules.send

import io.horizontalsystems.bankwallet.core.BitcoinAdapter
import io.horizontalsystems.bankwallet.core.IClipboardManager
import io.horizontalsystems.bankwallet.core.IExchangeRateManager
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.entities.coins.CoinOld
import io.horizontalsystems.bankwallet.entities.coins.bitcoin.Bitcoin
import io.horizontalsystems.bankwallet.modules.RxBaseTest
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SendInteractorTest {

    private val delegate = Mockito.mock(SendModule.IInteractorDelegate::class.java)
    private val clipboardManager = Mockito.mock(IClipboardManager::class.java)
    private val bitcoinAdapter = Mockito.mock(BitcoinAdapter::class.java)
    private val exchangeRateManager = Mockito.mock(IExchangeRateManager::class.java)

    private val interactor = SendInteractor(clipboardManager, bitcoinAdapter, exchangeRateManager)
    private val currencyUsd = Currency(code = "USD", symbol = "\u0024")
    private var exchangeRates = mutableMapOf(Bitcoin() as CoinOld to CurrencyValue(currencyUsd, 10_000.0))

    @Before
    fun setUp() {
        interactor.delegate = delegate
    }

//    @Test
//    fun fetchInitialState_hasRate() {
//        val baseCurrency = Currency("USD", "$")
//        val rate = Rate()
//
//        whenever(state.coin).thenReturn(coin)
//        whenever(currencyManager.baseCurrency).thenReturn(baseCurrency)
//        whenever(rateManager.rateForCoin(coin, baseCurrency.code)).thenReturn(rate)
//
//        interactor.fetchInitialState()
//
//        verify(delegate).amountTypeChanged(state)
//    }

//    @Test
//    fun fetchInitialState() {
//        val baseCurrency = Currency("USD", "$")
//        val rate = Rate()
//
//        whenever(state.coin).thenReturn(coin)
//        whenever(currencyManager.baseCurrency).thenReturn(baseCurrency)
//        whenever(rateManager.rateForCoin(coin, baseCurrency.code)).thenReturn(rate)
//
//        interactor.fetchInitialState()
//
//        verify(delegate).amountTypeChanged(state)
//        verify(delegate).amountChanged(state)
//        verify(delegate).convertedAmountChanged(state, rate)
//        verify(delegate).convertedAmountChanged(state, rate)
//    }

    @Test
    fun send() {
        val address = "address"
        val amountBTC = 1.0

        interactor.send(address, amountBTC)

        verify(bitcoinAdapter).send(address, amountBTC)
    }

//    @Test
//    fun send_invalidAddress() {
//
//        val coinCode = "BTC"
//        val address = "address"
//        val amountBTC = 1.0
//        val amountSatoshi = 100_000_000L
//
//        val exception = InvalidAddress(Throwable())
//
//        interactor.send(coinCode, address, amountBTC)
//
//        verify(delegate).didFailToSend(exception)
//    }

//    @Test
//    fun send_insufficientAmount() {
//
//        val coinCode = "BTC"
//        val address = "address"
//        val amountBTC = 1.0
//        val amountSatoshi = 100_000_000L
//
//        val exception = NotEnoughFundsException(Throwable())
//
//        interactor.send(coinCode, address, amountBTC)
//
//        verify(delegate).didFailToSend(exception)
//    }


    @Test
    fun send_success() {

        val address = "address"
        val amountBTC = 1.0

        interactor.send(address, amountBTC)

        verify(delegate).didSend()
    }

}
