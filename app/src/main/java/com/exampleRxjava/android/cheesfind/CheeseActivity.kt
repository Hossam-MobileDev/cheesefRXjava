

package com.exampleRxjava.android.cheesfind

import android.text.Editable
import android.text.TextWatcher
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cheeses.*
import java.util.concurrent.TimeUnit

class CheeseActivity : BaseSearchActivity() {

    private lateinit var disposable: Disposable

    private fun createButtonClickObservable(): Observable<String> {

        return Observable.create { emitter ->

            searchButton.setOnClickListener {

                emitter.onNext(queryEditText.text.toString())
            }


            emitter.setCancellable {

                searchButton.setOnClickListener(null)
            }
        }
    }

override fun onStart() {
    super.onStart()


        val searchTextObservable = createButtonClickObservable().toFlowable(BackpressureStrategy.LATEST)

    val searchTextwatcherObservable = createTextChangeObservable().toFlowable(BackpressureStrategy.LATEST)
    val searchTextFlowable = Flowable.merge<String>(searchTextObservable, searchTextwatcherObservable)

//    val searchbutoonMergeObservable = Observable.merge<String>(searchTextObservable,
//            searchTextwatcherObservable)
    disposable = searchTextFlowable

             //   .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { showProgress() }
                .observeOn(Schedulers.io())

                .map { cheeseSearchEngine.search(it) }

                .observeOn(AndroidSchedulers.mainThread())

                .subscribe {
                    hideProgress()
                    showResult(it)
                }


//    searchTextwatcherObservable
//
//         //   .subscribeOn(AndroidSchedulers.mainThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext { showProgress() }
//            .observeOn(Schedulers.io())
//
//            .map { cheeseSearchEngine.search(it) }
//
//            .observeOn(AndroidSchedulers.mainThread())
//
//            .subscribe {
//                hideProgress()
//                showResult(it)
//            }
//
//

}

    private fun createTextChangeObservable(): Observable<String> {

        val textChangeObservable = Observable.create<String> { emitter ->

            val textWatcher = object : TextWatcher {

                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    s?.toString()?.let { emitter.onNext(it) }
                }

            }

            queryEditText.addTextChangedListener(textWatcher)

            emitter.setCancellable {
                queryEditText.removeTextChangedListener(textWatcher)
            }
        }

        return textChangeObservable
                .filter { it.length >= 2 }
                .debounce(5000, TimeUnit.MILLISECONDS)

    }
    @Override
    override fun onStop() {
        super.onStop()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}