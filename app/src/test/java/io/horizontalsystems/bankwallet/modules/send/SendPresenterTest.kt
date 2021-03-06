package io.horizontalsystems.bankwallet.modules.send

import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.viewHelpers.NumberFormatHelper
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SendPresenterTest {

    private val interactor = Mockito.mock(SendModule.IInteractor::class.java)
    private val router = Mockito.mock(SendModule.IRouter::class.java)
    private val view = Mockito.mock(SendModule.IView::class.java)

    private val cryptoAmountFormat = NumberFormatHelper.cryptoAmountFormat
    private val fiatAmountFormat = NumberFormatHelper.fiatAmountFormat
    private val baseCurrency = Currency(code = "USD", symbol = "\u0024")


    private val presenter = SendPresenter(interactor, router, baseCurrency)

    @Before
    fun setUp() {
        presenter.view = view
    }

    @Test
    fun onViewDidLoad() {
        presenter.onViewDidLoad()

        verify(view).setAmount(null)
        verify(view).setAmountHint("${baseCurrency.symbol} ${cryptoAmountFormat.format(0)}")
    }

    @Test
    fun onViewDidLoad_fetchExchangeRate() {
        presenter.onViewDidLoad()

        verify(interactor).fetchExchangeRate()
    }

    @Test
    fun onScanClick() {
        presenter.onScanClick()

        verify(router).startScan()
    }

    @Test
    fun onPasteClick() {
        presenter.onPasteClick()

        verify(interactor).getCopiedText()
    }

    @Test
    fun onGetCopiedText() {
        val copiedText = "234"

        whenever(interactor.getCopiedText()).thenReturn(copiedText)

        presenter.onPasteClick()

        verify(view).setAddress(copiedText)
    }

    @Test
    fun onFiatAmountEntered() {
        val exchangeRate = 7000.0
        val amountEntered = "0.5"

        val secondaryAmountHint = "$ ${cryptoAmountFormat.format(3500.0)}"

        presenter.onViewDidLoad()
        presenter.didFetchExchangeRate(exchangeRate)
        reset(view)

        presenter.onAmountEntered(amountEntered)

        verify(view).setAmountHint(secondaryAmountHint)
    }

    @Test
    fun onCryptoAmountEntered() {
        val exchangeRate = 7000.0
        val amountEntered = "1.0"

        val secondaryAmountHint = "$ ${fiatAmountFormat.format(7000.0)}"

        presenter.onViewDidLoad()
        presenter.didFetchExchangeRate(exchangeRate)
        reset(view)
        presenter.onAmountEntered(amountEntered)

        verify(view).setAmountHint(secondaryAmountHint)
    }

    @Test
    fun onSendClick() {
        val exchangeRate = 7000.0
        val cryptoAmount = 0.5

        val cryptoCurrencyCode = "BTC"
        val address = "mxNEBQf2xQeLknPZW65rMbKxEban6udxFc"

        whenever(interactor.getCoinCode()).thenReturn(cryptoCurrencyCode)

        presenter.onViewDidLoad()
        presenter.didFetchExchangeRate(exchangeRate)
        presenter.onAmountEntered(cryptoAmount.toString())

        presenter.onSendClick(address)

        verify(interactor).send(address, cryptoAmount)
    }

//    @Test
//    fun didFailToSend_invalidAddress() {
//        val exception = InvalidAddress(Throwable())
//
//        presenter.didFailToSend(exception)
//
//        verify(view).showError(R.string.send_bottom_sheet_error_invalid_address)
//    }

//    @Test
//    fun didFailToSend_insufficientBalance() {
//        val exception = NotEnoughFundsException(Throwable())
//
//        presenter.didFailToSend(exception)
//
//        verify(view).showError(R.string.send_bottom_sheet_error_insufficient_balance)
//    }

    @Test
    fun didSend() {
        presenter.didSend()

        verify(view).showSuccess()
    }

    @Test
    fun onAddressEntered() {
        val address = "[address]"
        whenever(interactor.isValid(address)).thenReturn(true)
        presenter.onAddressEntered(address)
        verify(view).showAddressWarning(false)
    }

    @Test
    fun onAddressEntered_emptyAddress() {
        presenter.onAddressEntered(null)
        verify(view).showAddressWarning(false)
    }

}
