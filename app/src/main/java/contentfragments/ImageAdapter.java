package contentfragments;

import java.util.Arrays;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.classroom.applicationactivity.R;

/**
 * Adapter for FolderViewFragment's listview 
 * @author JACK
 *
 */
public class ImageAdapter extends BaseAdapter{

    //Instance variables
    private Context context;
    private String[] fileNames;

    /**
     * Constructor
     * @param context application context
     * @param fileNames names of files for adapter
     */
    public ImageAdapter(Context context, String[] fileNames) {
        this.context = context;
        this.fileNames = fileNames;
    }

    /**
     * get the modified grid view
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = new View(context);
            // get layout
            gridView = inflater.inflate(R.layout.gridlayout, null);
            // set value into textview
            textView = (TextView) gridView.findViewById(R.id.grid_item_label);
            //strip extension
            String filename = stripFileExtension(fileNames[position]);
            filename = fileNames[position];
            textView.setText(filename);
            // set image based on selected text
            imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
            Log.i("ImageADapter", "On filename: " + filename);
            try {
                if (filename.substring(filename.lastIndexOf('.')).equalsIgnoreCase(".txt")) {
                    imageView.setImageResource(R.drawable.text_icon);
                } else if (filename.substring(filename.lastIndexOf('.')).equalsIgnoreCase(".jpg")
                        || (filename.substring(filename.lastIndexOf('.')).equalsIgnoreCase(".jpeg"))
                        || (filename.substring(filename.lastIndexOf('.')).equalsIgnoreCase(".bmp"))
                        || (filename.substring(filename.lastIndexOf('.')).equalsIgnoreCase(".gif"))
                        || (filename.substring(filename.lastIndexOf('.')).equalsIgnoreCase(".png"))) {
                    imageView.setImageResource(R.drawable.image_icon);
                } else {
                    imageView.setImageResource(R.drawable.file_icon_generic_file);
                }
            }catch (StringIndexOutOfBoundsException sio){
                imageView.setImageResource(R.drawable.file_icon_generic_file);
            }
        } else {
            gridView = convertView;
        }
        return gridView;
    }

    /**
     * Strips file extensions of provided filenames to avoid confusion
     * @param filename
     * @return
     */
    private String stripFileExtension(String filename){
        int lastPeriod = filename.lastIndexOf(".");
        try {
            return filename.substring(0, lastPeriod);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return filename;
        }
    }

    public void updateAdapterData(String[] fileNames){
        this.fileNames = fileNames;
        //this.notifyDataSetChanged();
    }

    /**
     * getCount
     */
    @Override
    public int getCount() {
        return fileNames.length;
    }

    /**
     * getItem
     */
    @Override
    public Object getItem(int position) {
        return fileNames[position];
    }

    /**
     * getItemID
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public String toString(){
        return Arrays.toString(fileNames);
    }

}

