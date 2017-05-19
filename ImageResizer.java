package com.example.theopsyphertxt.tibbit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

public class ImageResizer {

    /*
     * Call this static method to resize an image to a specified width and height.
     *
     * @param targetWidth  The width to resize to.
     * @param targetHeight The height to resize to.
     * @returns 		   The resized image as a Bitmap.
     */
    private static Bitmap resizeImage(byte[] imageData, int targetWidth, int targetHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap reducedBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(reducedBitmap, targetWidth, targetHeight, false);

        return resizedBitmap;
    }

    protected static Bitmap resizeImageMaintainAspectRatio(byte[] imageData, int shorterSideTarget) {
        Pair<Integer, Integer> dimensions = getDimensions(imageData);

        // Determine the aspect ratio (width/height) of the image
        int imageWidth = dimensions.first;
        int imageHeight = dimensions.second;
        float ratio = (float) dimensions.first / dimensions.second;

        int targetWidth;
        int targetHeight;

        // Determine portrait or landscape
        if (imageWidth > imageHeight) {
            // Landscape image. ratio (width/height) is > 1
            targetHeight = shorterSideTarget;
            targetWidth = Math.round(shorterSideTarget * ratio);
        } else {
            // Portrait image. ratio (width/height) is < 1
            targetWidth = shorterSideTarget;
            targetHeight = Math.round(shorterSideTarget / ratio);
        }
        return resizeImage(imageData, targetWidth, targetHeight);
    }

    private static Pair<Integer, Integer> getDimensions(byte[] imageData) {
        // Use BitmapFactory to decode the image
        BitmapFactory.Options options = new BitmapFactory.Options();

        // Only decode the bounds of the image, not the whole image, to get the dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

        return new Pair<Integer, Integer>(options.outWidth, options.outHeight);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
