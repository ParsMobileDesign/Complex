package ir.parsmobiledesign.quantum.Utility;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Currency;

public class MoneyTextWatcher implements TextWatcher
{
    private EditText editText;

    public MoneyTextWatcher(EditText ieditText)
    {
        editText = ieditText;
    }

    @Override
    public void afterTextChanged(Editable arg0)
    {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after)
    {
    }

    ///
    private String current = "";

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if (!s.toString().equals(current))
        {
            editText.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("[$,.]", "");
            if (!cleanString.isEmpty())
            {
                double parsed = Double.parseDouble(cleanString);
                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(0);
                Currency currency = Currency.getInstance("IRR");
                format.setCurrency(currency);
                String formatted = format.format(parsed);
                current = formatted;
                editText.setText(formatted);
                editText.setSelection(formatted.length());
            }
            editText.addTextChangedListener(this);
        }
    }
    ///
}