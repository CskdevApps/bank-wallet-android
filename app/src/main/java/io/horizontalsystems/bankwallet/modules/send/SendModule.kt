package io.horizontalsystems.bankwallet.modules.send

import android.support.v4.app.FragmentActivity
import io.horizontalsystems.bankwallet.core.IAdapter
import io.horizontalsystems.bankwallet.entities.CoinValue
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.modules.transactions.Coin

object SendModule {


    data class StateViewItem(
            val amountType: String,
            val amount: Double,
            val switchButtonEnabled: Boolean,
            val amountInfo: AmountInfo?,
            val addressInfo: AddressInfo?,
            val fee: CoinValue,
            val convertedFee: CurrencyValue?,
            val sendButtonEnabled: Boolean
    )

    sealed class AmountInfo {
        data class CoinValueInfo(val coinValue: CoinValue) : AmountInfo()
        data class CurrencyValueInfo(val currencyValue: CurrencyValue) : AmountInfo()
        data class ErrorInfo(val error: AmountError) : AmountInfo()
    }

    sealed class AddressInfo {
        class ValidAddressInfo(val address: String) : AddressInfo()
        class InvalidAddressInfo(val address: String, val error: Exception) : AddressInfo()
    }

    class State(val coin: Coin, var inputType: InputType, var amount: Double = 0.0, var address: String?)

    enum class InputType {
        COIN, CURRENCY
    }

    open class AmountError : Exception() {
        data class InsufficientCoinBalance(val coinValue: CoinValue) : AmountError()
        data class InsufficientCurrencyBalance(val currencyValue: CurrencyValue) : AmountError()
    }


    interface IView {
        fun setAmountType(type: String)
        fun setAmount(amount: Double)
        fun setSwitchButtonEnabled(enabled: Boolean)
        fun setAmountInfo(amountInfo: AmountInfo?)
        fun setAddressInfo(addressInfo: AddressInfo?)
        fun setFee(fee: CoinValue)
        fun setConvertedFee(convertedFee: CurrencyValue?)
        fun setSendButtonEnabled(sendButtonEnabled: Boolean)
    }

    interface IViewDelegate {
        fun onViewDidLoad()
        fun onAmountChanged(amount: Double)
        fun onSwitchClicked()
        fun onPasteClicked()
        fun onScanAddress(address: String)
        fun onDeleteClicked()
    }

    interface IInteractor {
        val rate: Double?
        val addressFromClipboard: String?
    }

    interface IInteractorDelegate {
//        fun stateChanged(state: State, rate: Rate?)
//        fun didFetchExchangeRate(exchangeRate: Double)
//        fun didFailToSend(exception: Exception)
//        fun didSend()
    }

    interface IRouter {
//        fun startScan()
    }

    fun init(view: SendViewModel, router: IRouter, adapter: IAdapter) {
//        val exchangeRateManager = App.exchangeRateManager
//        val baseCurrency = App.currencyManager.baseCurrency
//        val interactor = SendInteractor(TextHelper, adapter, exchangeRateManager)
//        val presenter = SendPresenter(interactor, router, baseCurrency)
//
//        view.delegate = presenter
//        presenter.view = view
//        interactor.delegate = presenter
    }

    fun start(activity: FragmentActivity, adapter: IAdapter) {
        SendFragment.show(activity, adapter)
    }

}
