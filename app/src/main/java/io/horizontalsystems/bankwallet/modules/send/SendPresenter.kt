package io.horizontalsystems.bankwallet.modules.send

class SendPresenter(
        private val interactor: SendModule.IInteractor,
        private val router: SendModule.IRouter,
        private val viewItemFactory: StateViewItemFactory,
        private val state: SendModule.State
) : SendModule.IViewDelegate {

    var view: SendModule.IView? = null

    override fun onViewDidLoad() {
        view?.let { view ->
            val viewItem = viewItemFactory.viewItemForState(state)

            view.setAmountType(viewItem.amountType)
            view.setAmount(viewItem.amount)
            view.setSwitchButtonEnabled(viewItem.switchButtonEnabled)
            view.setAmountInfo(viewItem.amountInfo)
            view.setAddressInfo(viewItem.addressInfo)
            view.setFee(viewItem.fee)
            view.setConvertedFee(viewItem.convertedFee)
            view.setSendButtonEnabled(viewItem.sendButtonEnabled)
        }
    }

    override fun onAmountChanged(amount: Double) {
        state.amount = amount

        val viewItem = viewItemFactory.viewItemForState(state)

        view?.setAmountInfo(viewItem.amountInfo)
        view?.setFee(viewItem.fee)
        view?.setConvertedFee(viewItem.convertedFee)
        view?.setSendButtonEnabled(viewItem.sendButtonEnabled)
    }

    override fun onSwitchClicked() {
        interactor.rate?.let {rate ->
            when (state.inputType) {
                SendModule.InputType.COIN -> {
                    state.inputType = SendModule.InputType.CURRENCY
                    state.amount = state.amount * rate
                }
                SendModule.InputType.CURRENCY -> {
                    state.inputType = SendModule.InputType.COIN
                    state.amount = state.amount / rate
                }
            }
        }

        val viewItem = viewItemFactory.viewItemForState(state)

        view?.setAmountType(viewItem.amountType)
        view?.setAmount(viewItem.amount)
        view?.setAmountInfo(viewItem.amountInfo)

    }

    override fun onPasteClicked() {
        interactor.addressFromClipboard?.let { onAddressChange(it) }
    }


    override fun onScanAddress(address: String) {
        onAddressChange(address)
    }

    override fun onDeleteClicked() {
        onAddressChange(null)
    }

    private fun onAddressChange(address: String?) {
        state.address = address

        val viewItem = viewItemFactory.viewItemForState(state)

        view?.setAddressInfo(viewItem.addressInfo)
        view?.setFee(viewItem.fee)
        view?.setConvertedFee(viewItem.convertedFee)
        view?.setSendButtonEnabled(viewItem.sendButtonEnabled)
    }

}
