package com.example.imagepicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class ImagePicker extends AppCompatActivity implements ItemLongClickListener {

    private static final String TAG = ImagePicker.class.getName();
    ArrayList<String> picList = new ArrayList<>();

    RecyclerView imageRecyclerView;
    ImageAdapter imageAdapter;

    GridLayoutManager gridLayoutManager;
    int COLS;

    Stack<String> imageStack = new Stack<>();
    int mode;
    MyTextView dropSpinner;
    TextView show;

    ListView listView;

    Uri finalUri;
    String[] paths = {"Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images" ,
            "Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/Sent" ,
            "DCIM/Camera" ,
            "DCIM/Screenshots" ,
             "Pictures" ,
    "Download"};

    List<String> listPath = new ArrayList<>();
    RelativeLayout main;
    boolean isShown;

    LinearLayout listHolder , toolbar;

    String[]perms = {Manifest.permission.READ_MEDIA_IMAGES , Manifest.permission.READ_MEDIA_VIDEO , Manifest.permission.CAMERA};
    String[] oldPerms = {Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.CAMERA};
    private String currentPhotoPath;

    public static void openImagePicker(Context context) {

        Intent intent = new Intent(context , ImagePicker.class);
        context.startActivity(intent);




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepicker);

        main = (RelativeLayout) findViewById(R.id.main);
        listHolder = (LinearLayout) findViewById(R.id.listViewHlder);
        toolbar = (LinearLayout) findViewById(R.id.toolbar);

        RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        listView = new ListView(this);
        listView.setLayoutParams(listParams);
        listView.setBackgroundColor(getResources().getColor(R.color.white));

        listView.setVisibility(View.GONE);

        listParams.addRule(RelativeLayout.BELOW , toolbar.getId());
        main.addView(listView);

//        View view = getLayoutInflater().inflate(R.layout.resusable_listview , null,true);
//        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.reusable_list_holder);
//        ListView listView = (ListView) rl.findViewById(R.id.reusable_list);

        SpinnerAdapter adapter = new SpinnerAdapter(ImagePicker.this , paths);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        listHolder.addView(view);
//        listHolder.setVisibility(View.GONE);




        for (String path : paths) {

            listPath.add(path);
        }



        COLS = Utils.getScreenWidth(this)/Utils.dPtoPixel(this , 120);

        dropSpinner = (MyTextView) findViewById(R.id.drop_spinner);
        show = (TextView) findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!imageStack.isEmpty()) {

                    Intent intent = new Intent(ImagePicker.this , ResultActivity.class);
                    intent.putExtra("images" , new ArrayList<String>(imageStack));
                    startActivity(intent);

                }




            }
        });

        dropSpinner.setOnDrawableClickListener(new MyTextView.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {

                switch (target) {

                    case RIGHT:


                        if (!isShown) {

                            listView.setVisibility(View.VISIBLE);
                            isShown = true;


                        } else {

                            listView.setVisibility(View.GONE);
                            isShown = false;
                        }

                        break;


                }

            }
        });

        imageRecyclerView = (RecyclerView) findViewById(R.id.image_recycler);
        imageAdapter = new ImageAdapter(this , picList , this);
        gridLayoutManager = new GridLayoutManager(this , COLS);
        imageRecyclerView.setLayoutManager(gridLayoutManager);
        imageRecyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();







        checkPerms();
        showPathImages(paths[2]);
        dropSpinner.setText(paths[2]);

        //checkforImages();
//        addSpinnerData();

        //new ImageLoadingTask().execute();

    }

//    private void addSpinnerData() {
//
//        //String[] typeArray = {"Select Business Type", "Proprietor", "LLP", "Pvt Ltd", "Ltd"};
//        SpinnerAdapter typeAdapter = new SpinnerAdapter(this, R.layout.single_item_spinner , R.id.spinnerText , listPath);
//        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropSpinner.setAdapter(typeAdapter);
//
//
//
//    }

    private void showPathImages(String path) {

        Log.d(TAG, "showPathImages: " + path);

        File file = Environment.getExternalStorageDirectory();

        picList.clear();

        picList.add("Camera");

        File whtsapp = new File(file.getPath()+File.separator+path);


        for (File file1 : whtsapp.listFiles()) {

            if (file1.isFile()) {

                if (file1.getName().endsWith(".png")
                        || file1.getName().endsWith(".jpeg")
                        || file1.getName().endsWith(".jpg")) {

                    picList.add(file1.getPath());


                }


            }
        }

        imageAdapter.notifyDataSetChanged();

    }

    private void checkforImages() {

        picList.add("Camera");

        File file = Environment.getExternalStorageDirectory();

        for (int i = 0; i < paths.length; i++) {

            File whtsapp = new File(file.getPath()+File.separator+paths[i]);

            for (File file1 : whtsapp.listFiles()) {

                if (file1.isFile()) {

                    if (file1.getName().endsWith(".png")
                            || file1.getName().endsWith(".jpeg")
                    || file1.getName().endsWith(".jpg")) {

                        picList.add(file1.getPath());


                    }


                }
            }

        }

        imageAdapter.notifyDataSetChanged();

        Log.d(TAG, "checkforImages: " + picList.toString());






    }

    private void checkPerms() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            ActivityResultLauncher perms = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions() , (isGranted)->{

                if (!isGranted.containsValue(false)) {

                    showPathImages(paths[2]);
                }




            });

            perms.launch(this.perms);


        } else {

            if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this , oldPerms , 200);
            }

        }




    }

    @Override
    public void onItemLongClicked(int mode , String path) {

        if (mode == 2) {

            if (!imageStack.contains(path)) {

                imageStack.add(path);


            }


        }






    }

    @Override
    public void remove(String path) {

        if (!imageStack.isEmpty()) {

            imageStack.remove(path);
        }

        if (imageStack.isEmpty()) {

            mode = 1;
        }

    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {


        Context context;
        ArrayList<String> images;

        ItemLongClickListener itemLongClickListener;


        public ImageAdapter(Context context , ArrayList<String> images , ItemLongClickListener itemLongClickListener) {

            this.context = context;
            this.images = images;
            this.itemLongClickListener = itemLongClickListener;


        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_image , parent , false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            String path = images.get(position);

            if (position == 0) {


                Bitmap bitmap = BitmapFactory.decodeResource(getResources() , R.drawable.camera);
                holder.singleImage.setImageBitmap(bitmap);
            }



            holder.singleImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (path.equals("Camera")) {

                        return false;
                    }

                    mode = 2;

                    holder.checkBackground.setVisibility(View.VISIBLE);

                    itemLongClickListener.onItemLongClicked(mode , path);

                    return true;

                }
            });

            holder.singleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (path.equals("Camera")) {

                        openCamera();
                    }

                    if (mode == 2) {

                        if (holder.checkBackground.getVisibility() == View.VISIBLE) {

                            holder.checkBackground.setVisibility(View.GONE);
                            itemLongClickListener.remove(path);


                        } else {

                            holder.checkBackground.setVisibility(View.VISIBLE);
                            itemLongClickListener.onItemLongClicked(mode , path);


                        }




                    } else if (mode == 1) {

                        Toast.makeText(context, "Path: " + path, Toast.LENGTH_SHORT).show();
                    }





                }
            });

            if (!path.equals("Camera")) {

                File file = new File(path);

                new AsyncTask<String , Integer , Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        holder.singleImage.setImageDrawable(getResources().getDrawable(R.drawable.imageholder));
                    }

                    @Override
                    protected Bitmap doInBackground(String ...objects) {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath() , options);


                        int sampleSize = calculateInSampleSize(options , Utils.dPtoPixel(context , 120) , Utils.dPtoPixel(context, 120));

                        options.inSampleSize = sampleSize;
                        options.inJustDecodeBounds = false;

                        Bitmap compBitmap = BitmapFactory.decodeFile(file.getPath() , options);

                        return compBitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        holder.singleImage.setImageBitmap(bitmap);
                    }
                }.execute();

            }







        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView singleImage;
            RelativeLayout checkBackground;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                singleImage = (ImageView) itemView.findViewById(R.id.single_image);
                checkBackground = (RelativeLayout) itemView.findViewById(R.id.check_background);
            }
        }
    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = createImageFile();
        finalUri = FileProvider.getUriForFile(this , "com.example.imagepicker.fileprovider" , photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT , finalUri);

        startActivityForResult(intent , 100);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }



//    private class ImageLoadingTask extends AsyncTask<String , Integer , String> {
//
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            imageAdapter.notifyDataSetChanged();
//        }
//    }

    private class SpinnerAdapter extends BaseAdapter {

        Context context;


        String[] paths;


        public SpinnerAdapter(Context context, String[] paths) {

            this.context = context;
            this.paths = paths;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            String path = this.paths[position];

            View view = getLayoutInflater().inflate(R.layout.sigmle_item_reusable_list , parent , false);
            TextView txt = (TextView) view.findViewById(R.id.list_text);
            txt.setText(path);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView txt = (TextView) view.findViewById(R.id.list_text);
                    String path = txt.getText().toString().trim();

                    showPathImages(path);

                    listView.setVisibility(View.GONE);
                    isShown = false;

                    dropSpinner.setText(path);

                }
            });

            return view;
        }

        @Override
        public int getCount() {
            return this.paths.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private File createImageFile() {

        File image = null;
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {

            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        } catch (Exception exception) {

            exception.printStackTrace();
        }


        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            try {

                InputStream is = getContentResolver().openInputStream(finalUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                addImageToGallery(bitmap);

            } catch (Exception e) {

                e.printStackTrace();
            }





//            Toast.makeText(this, "Uri: " + finalUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {

            if (grantResults.length>0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {

                    showPathImages(paths[2]);
                }
            }
        }
    }

    public void addImageToGallery(Bitmap yourBitmap) {

        MediaStore.Images.Media.insertImage(getContentResolver(), yourBitmap,"" , "");

//        ContentValues values = new ContentValues();
//
//        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.MediaColumns.DATA, filePath);
//
//        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}