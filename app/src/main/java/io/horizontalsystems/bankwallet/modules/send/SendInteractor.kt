package io.horizontalsystems.bankwallet.modules.send

import io.horizontalsystems.bankwallet.core.ICurrencyManager
import io.horizontalsystems.bankwallet.core.IRateManager

class SendInteractor(private val state: SendModule.State,
                     private val currencyManager: ICurrencyManager,
                     private val rateManager: IRateManager) : SendModule.IInteractor {

    var delegate: SendModule.IInteractorDelegate? = null

    override val rate: Double?
        get() = TODO("not implemented")
    override val addressFromClipboard: String?
        get() = TODO("not implemented")

}
