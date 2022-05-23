package dfg.newsapp.util

import android.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.MutableLiveData


class Spinners {

    var inhibitSpinner: Boolean = false

    fun setupSpinner(
        context: Context,
        spinner: Spinner,
        spinnerList: List<String>,
        selectedItemLiveData: MutableLiveData<String>
    ) {
        val spinnerAdapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item,
            spinnerList
        )

        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(spinnerList.indexOf(selectedItemLiveData.value))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedItemLiveData.value = spinnerList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(
                    context,
                    "Nothing Selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

val countryList = mutableListOf(
    "us",
    "gb",
    "de",
    "ro"
)

val newsTypeList = mutableListOf(
    "business",
    "entertainment",
    "general",
    "health",
    "science",
    "sports",
    "technology"
)