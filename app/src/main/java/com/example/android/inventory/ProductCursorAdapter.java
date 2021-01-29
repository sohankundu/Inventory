package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;
import com.example.android.inventory.data.ProductProvider;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button sellButton = (Button) view.findViewById(R.id.sell_button);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        // Find the columns of product attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

        final int quantity = cursor.getInt(quantityColumnIndex);

        if(quantity < 1){
            sellButton.setEnabled(false);
        }
        else{
            sellButton.setEnabled(true);
        }

        // Read the product attributes from the Cursor for the current product
        final int id = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        String productQuantity = "Qty: " + String.valueOf(quantity);
        String productPrice = "₹ " + String.valueOf(cursor.getFloat(priceColumnIndex));
        byte[] imgByte = cursor.getBlob(imageColumnIndex);

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        quantityTextView.setText(productQuantity);
        priceTextView.setText(productPrice);
        if (imgByte != null) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            imageView.setImageBitmap(bitmapImage);
        }

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(context, EditorActivity.class);
                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductEntry#CONTENT_URI}.
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                Log.i("BUTTON CLICK", String.valueOf(currentProductUri));

                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);
                intent.putExtra("quantity", String.valueOf(quantity-1));

                // Launch the {@link EditorActivity} to display the data for the current product.
                context.startActivity(intent);
            }
        });
    }
}
