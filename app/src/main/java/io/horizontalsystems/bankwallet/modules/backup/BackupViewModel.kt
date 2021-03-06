package io.horizontalsystems.bankwallet.modules.backup

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.SingleLiveEvent

class BackupViewModel : ViewModel(), BackupModule.IView, BackupModule.IRouter {
    lateinit var delegate: BackupModule.IViewDelegate

    val errorLiveData = MutableLiveData<Int>()
    val wordsLiveData = MutableLiveData<List<String>>()
    val wordIndexesToConfirmLiveData = MutableLiveData<List<Int>>()

    val navigationWordsLiveEvent = SingleLiveEvent<Void>()
    val navigationConfirmLiveEvent = SingleLiveEvent<Void>()
    val closeLiveEvent = SingleLiveEvent<Void>()
    val navigateBackLiveEvent = SingleLiveEvent<Void>()
    val navigateToSetPinLiveEvent = SingleLiveEvent<Void>()

    fun init(dismissMode: BackupPresenter.DismissMode) {
        BackupModule.init(this, this, dismissMode)
    }

    // view

    override fun showWords(words: List<String>) {
        wordsLiveData.value = words
        navigationWordsLiveEvent.call()
    }

    override fun showConfirmationWithIndexes(indexes: List<Int>) {
        wordIndexesToConfirmLiveData.value = indexes
        navigationConfirmLiveEvent.call()
    }

    override fun hideWords() {
        navigateBackLiveEvent.call()
    }

    override fun hideConfirmation() {
        errorLiveData.value = null
        navigateBackLiveEvent.call()
    }

    override fun showConfirmationError() {
        errorLiveData.value = R.string.backup_words_error_no_match
    }

    // router

    override fun close() {
        closeLiveEvent.call()
    }

    override fun navigateToSetPin() {
        navigateToSetPinLiveEvent.call()
    }
}