package ir.parsmobiledesign.quantum.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import ir.parsmobiledesign.quantum.ConfigApp;
import com.pax.dal.IDAL;
import com.pax.dal.exceptions.PrinterDevException;
import com.pax.dal.IPrinter;

public class PrintThread extends Thread
{
    private IPrinter iPrinter;
    private Bitmap mBitmap;
    private Context context;
    private IDAL idal;

    public void Initial(Bitmap iBitmap, Context icontext)
    {
        mBitmap = iBitmap;
        context = icontext;
        idal = ConfigApp.getInstance().getIdal();
        if (idal != null)
            iPrinter = idal.getPrinter();
    }

    @Override
    public void run()
    {
        if (iPrinter != null)
        {
            try
            {
                iPrinter.init();
                iPrinter.setGray(100);
                iPrinter.printBitmap(mBitmap);
                iPrinter.start();
            } catch (PrinterDevException e)
            {
                Toast.makeText(context, "Thread Run Exception" + e.getMessage() + e.getErrMsg(), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        } else
            Toast.makeText(context, "تنظیمات پرینتر به درستی انجام نشده است !", Toast.LENGTH_SHORT);

    }
}
