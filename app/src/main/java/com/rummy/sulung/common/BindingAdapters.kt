package com.rummy.sulung.common

import com.rummy.sulung.R
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.rummy.sulung.network.response.Diary
import com.rummy.sulung.network.response.DisplayableItem
import com.rummy.sulung.network.response.Item
import com.rummy.sulung.view.ui.diary.DiaryAdapter
import com.rummy.sulung.view.ui.store.store_paging.StoreAdapter
import java.lang.Integer.max

@BindingAdapter("listData")
        fun RecyclerView.setListData(diary: PagedList<DisplayableItem>?) {
            val adapter = adapter as DiaryAdapter
            adapter.submitList(diary)
        }

    @BindingAdapter("storelistData")
    fun RecyclerView.setStoreListData(diary: PagedList<DisplayableItem>?) {
        val adapter = adapter as StoreAdapter
        adapter.submitList(diary)
    }

    @BindingAdapter("hideKeyboardOnInputDone")
    fun hideKeyboardOnInputDone(view: EditText, enabled: Boolean) {
        if (!enabled) return
        val listener = TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == 0) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            false
        }
        view.setOnEditorActionListener(listener)
    }

    @BindingAdapter("hideKeyboardOnOutTouch")
    fun hideKeyboardOnOutTouch(view: EditText, enabled: Boolean) {
        if (!enabled) return
        val listener = View.OnFocusChangeListener { v, hasFocus ->
            Log.e("BindingAdapter", "!hasFocus")
            if (!hasFocus) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                Log.e("BindingAdapter", "!hasFocus")
            }
        }
        view.onFocusChangeListener = listener
    }

    @BindingAdapter("hideKeyboardOnOutTouchHashTagInput")
    fun hideKeyboardOnOutTouchHashTagInput(view: EditText, enabled: Boolean) {
        if (!enabled) return
        val listener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
                view.setText("")
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                view.setBackgroundResource(android.R.color.transparent)
            } else {
                 view.setBackgroundResource(R.drawable.hash_tag_input_box_bg)
            }
        }
        view.onFocusChangeListener = listener

        // set minimum width
        val minimumWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68f, view.resources.displayMetrics).toInt()
        view.minWidth = minimumWidthInPixels

        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val textWidth = view.paint.measureText(s.toString())
                val additionalWidth = if (s.length > 3) {
                    view.paint.measureText("0") // 2글자 너비 추가
                } else {
                    0f
                }
                val padding = view.paddingStart + view.paddingEnd
                val newWidth = textWidth + padding + additionalWidth

                // 초기 아무것도 입력하지 않았을 때는 68dp를 갖다가 68dp와 동일해지는 순간 2글자씩 너비가 넓어지도록 합니다.
                val minimumWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68f, view.resources.displayMetrics).toInt()
                if (newWidth < minimumWidthInPixels) {
                    view.minWidth = minimumWidthInPixels
                } else {
                    val layoutParams = view.layoutParams
                    layoutParams.width = newWidth.toInt()
                    view.layoutParams = layoutParams
                }

                // 스크롤을 오른쪽 끝으로 이동합니다.
                if(s.isNotBlank()) {
                    val scrollView = (view.parent as View).parent as HorizontalScrollView
                    scrollView.post { scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }
                }
            }
        })
    }



/*@BindingAdapter("hideKeyboardOnOutTouchHashTagInput")
fun hideKeyboardOnOutTouchHashTagInput(view: EditText, enabled: Boolean) {
    if (!enabled) return
    val listener = View.OnFocusChangeListener { v, hasFocus ->
        Log.e("BindingAdapter", "!hasFocus")
        if (!hasFocus) {
            view.clearFocus()
            view.setText("")
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.setBackgroundResource(android.R.color.transparent)
            Log.e("BindingAdapter", "!hasFocus")
        }else{
            view.setBackgroundResource(R.drawable.hash_tag_input_box_bg)
        }
    }
    view.onFocusChangeListener = listener
}*/


    @BindingAdapter("hideKeyboardOnOutTouchDrinkCount")
    fun hideKeyboardOnOutTouchDrinkCount(view: EditText, enabled: Boolean) {
        if (!enabled) return
        val listener = View.OnFocusChangeListener { v, hasFocus ->
            Log.e("BindingAdapter", "!hasFocus")
            if (!hasFocus) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                Log.e("BindingAdapter", "!hasFocus")
            }else{
                view.setSelection(view.text?.length ?: 0) // 커서를 끝 부분으로 이동
            }
        }
        view.onFocusChangeListener = listener
    }