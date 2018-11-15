package io.horizontalsystems.bankwallet.modules.send

import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import io.horizontalsystems.bankwallet.entities.CoinValue
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class SendPresenterTest {


    private val interactor = mock(SendModule.IInteractor::class.java)
    private val router = mock(SendModule.IRouter::class.java)
    private val view = mock(SendModule.IView::class.java)
    //
//    private val cryptoAmountFormat = NumberFormatHelper.cryptoAmountFormat
//    private val fiatAmountFormat = NumberFormatHelper.fiatAmountFormat
//    private val baseCurrency = Currency(code = "USD", symbol = "\u0024")
//
//
    private val state = mock(SendModule.State::class.java)
    private val viewItemFactory = mock(StateViewItemFactory::class.java)
    private val presenter = SendPresenter(interactor, router, viewItemFactory, state)

    val amountType = "BTC"
    val amount = 12.34
    val switchButtonEnabled = true
    val sendButtonEnabled = false
    val amountInfo = mock(SendModule.AmountInfo::class.java)
    val addressInfo = mock(SendModule.AddressInfo::class.java)
    val fee = mock(CoinValue::class.java)
    val convertedFee = mock(CurrencyValue::class.java)

    val rate = 6500.0

    @Before
    fun setUp() {
        presenter.view = view

        val viewItem = mock(SendModule.StateViewItem::class.java)

        whenever(viewItemFactory.viewItemForState(state)).thenReturn(viewItem)
        whenever(viewItem.amountType).thenReturn(amountType)
        whenever(viewItem.amount).thenReturn(amount)
        whenever(viewItem.switchButtonEnabled).thenReturn(switchButtonEnabled)
        whenever(viewItem.amountInfo).thenReturn(amountInfo)
        whenever(viewItem.addressInfo).thenReturn(addressInfo)
        whenever(viewItem.fee).thenReturn(fee)
        whenever(viewItem.convertedFee).thenReturn(convertedFee)
        whenever(viewItem.sendButtonEnabled).thenReturn(sendButtonEnabled)

        whenever(interactor.rate).thenReturn(rate)
    }

    @Test
    fun onViewDidLoad() {
        presenter.onViewDidLoad()

        verify(view).setAmountType(amountType)
        verify(view).setAmount(amount)
        verify(view).setSwitchButtonEnabled(switchButtonEnabled)
        verify(view).setAmountInfo(amountInfo)
        verify(view).setAddressInfo(addressInfo)
        verify(view).setFee(fee)
        verify(view).setConvertedFee(convertedFee)
        verify(view).setSendButtonEnabled(sendButtonEnabled)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun onSwitchClicked_ConvertFromCoinToCurrency() {
        val previousAmount = 10.0
        val rate = 6500.0

        whenever(state.amount).thenReturn(previousAmount)
        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)

        presenter.onSwitchClicked()

        verify(state).inputType = SendModule.InputType.CURRENCY
        verify(state).amount = previousAmount * rate
    }

    @Test
    fun onSwitchClicked_ConvertFromCurrencyToCoin() {
        val previousAmount = 3000.0
        val rate = 6500.0

        whenever(state.amount).thenReturn(previousAmount)
        whenever(state.inputType).thenReturn(SendModule.InputType.CURRENCY)

        presenter.onSwitchClicked()

        verify(state).inputType = SendModule.InputType.COIN
        verify(state).amount = previousAmount / rate
    }

    @Test
    fun onSwitchClicked() {
        whenever(state.amount).thenReturn(0.0)
        whenever(state.inputType).thenReturn(SendModule.InputType.COIN)

        presenter.onSwitchClicked()

        verify(view).setAmountType(amountType)
        verify(view).setAmount(amount)
        verify(view).setAmountInfo(amountInfo)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun onAmountChanged() {
        presenter.onAmountChanged(amount)

        verify(state).amount = amount
        verify(view).setAmountInfo(amountInfo)
        verify(view).setFee(fee)
        verify(view).setConvertedFee(convertedFee)
        verify(view).setSendButtonEnabled(sendButtonEnabled)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun onPasteClicked_hasAddress() {
        val address = "address"

        whenever(interactor.addressFromClipboard).thenReturn(address)

        presenter.onPasteClicked()

        verify(state).address = address

        verify(view).setAddressInfo(addressInfo)
        verify(view).setFee(fee)
        verify(view).setConvertedFee(convertedFee)
        verify(view).setSendButtonEnabled(sendButtonEnabled)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun onPasteClicked_nullAddress() {
        whenever(interactor.addressFromClipboard).thenReturn(null)

        presenter.onPasteClicked()

        verifyNoMoreInteractions(state)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun onScanAddress() {
        val address = "address"

        presenter.onScanAddress(address)

        verify(state).address = address

        verify(view).setAddressInfo(addressInfo)
        verify(view).setFee(fee)
        verify(view).setConvertedFee(convertedFee)
        verify(view).setSendButtonEnabled(sendButtonEnabled)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun onDeleteClicked() {
        presenter.onDeleteClicked()

        verify(state).address = null

        verify(view).setAddressInfo(addressInfo)
        verify(view).setFee(fee)
        verify(view).setConvertedFee(convertedFee)
        verify(view).setSendButtonEnabled(sendButtonEnabled)
        verifyNoMoreInteractions(view)
    }

}
