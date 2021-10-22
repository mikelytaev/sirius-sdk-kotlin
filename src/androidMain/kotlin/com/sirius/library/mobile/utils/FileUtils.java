package com.sirius.library.mobile.utils;




import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;



/**
 * Created by REIGOR on 09.02.2015.
 */
public class FileUtils {
    private FileUtils() {
    } //private constructor to enforce Singleton pattern

    /**
     * TAG for log messages.
     */
    static final String TAG = "FileUtils";
    private static final boolean DEBUG = false; // Set to true to enable logging

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String HIDDEN_PREFIX = ".";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;


/*
    public static File getOutputMediaFile(Context context) {
        try {
            File storageDir = getAlbumDir(context);
            String timeStamp = DateUtils.getStringFromDate(new Date(), "yyyyMMdd_HHmmss", false);
            String imageFileName = "IMG_" + timeStamp + "_";
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
*/

    /*private static File getAlbumDir(Context context) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    // Utils.logText("Utils", "failed to create directory");
                    return null;
                }
            }
        } else {
            //  Utils.logText("Utils", "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }*/

    public static String generateFileName() {
        return String.valueOf(new Date().getTime()) + "_photo.jpeg";
    }

    public static String generateFileVideoName() {
        return String.valueOf(new Date().getTime()) + "_video.mp4";
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
 /*   public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }*/

    /**
     * Convert File into Uri.
     *
     * @return uri
     */
  /*  public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }*/

    /**
     * Returns the path only (without file name).
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
  /*  public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }

        return "application/octet-stream";
    }*/

    /**
     * @return The MIME type for the give Uri.
     */
   /* public static String getMimeType(Context context, Uri uri) {
        File file = new File(getPath(context, uri));
        return getMimeType(file);
    }*/

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is {@link LocalStorageProvider}.
     * @author paulburke
     */
   /* public static boolean isLocalStorageDocument(Uri uri) {
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
    }*/

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
 /*   public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }*/

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
  /*  public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
*/
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
 /*   public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }*/

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
  /*  public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }*/

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
 /*   public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) {
                    DatabaseUtils.dumpCursor(cursor);
                }

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
*/
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
/*
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG) {
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );
        }

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
          */
/*  if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }*//*

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
*/

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */
  /*  public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }
*/
    /**
     * Get the file size in a human-readable string.
     *
     * @author paulburke
     */
    public static String getReadableFileSize(int size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @author paulburke
     */
  /*  public static Bitmap getThumbnail(Context context, File file) {
        return getThumbnail(context, getUri(file), getMimeType(file));
    }*/

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @author paulburke
     */
 /*   public static Bitmap getThumbnail(Context context, Uri uri) {
        return getThumbnail(context, uri, getMimeType(context, uri));
    }*/

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @author paulburke
     */
/*
    public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
        if (DEBUG) {
            Log.d(TAG, "Attempting to get thumbnail");
        }

        if (!isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.");
            return null;
        }

        Bitmap bm = null;
        if (uri != null) {
            final ContentResolver resolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final int id = cursor.getInt(0);
                    if (DEBUG) {
                        Log.d(TAG, "Got thumb ID: " + id);
                    }

                    if (mimeType.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Video.Thumbnails.MINI_KIND,
                                null);
                    } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
                    }
                }
            } catch (Exception e) {
                if (DEBUG) {
                    Log.e(TAG, "getThumbnail", e);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return bm;
    }
*/

    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */


    private static String getRootOfExternalStorage(File file) {
        if (file == null) {
            return null;
        }
        String path = file.getAbsolutePath();
        return path;
        //return path.replaceAll("/Android/data/" + App.getContext().getPackageName() + "/files", "");
    }

/*    public static String getDirs(Context context) {
        File[] dirs = ContextCompat.getExternalFilesDirs(context, null);
        if (dirs != null) {
            for (File file : dirs) {
                String path = getRootOfExternalStorage(file);
                if (!TextUtils.isEmpty(path)) {
                    return path;
                }
            }
        } else {

        }
        return "";
    }*/

/*    private String getRootOfExternalStorage(File file) {
        if (file == null)
            return null;
        String path = file.getAbsolutePath();
        return path.replaceAll("/Android/data/" + mContext.getPackageName() + "/files", "");
    }

    public void getDirs() {
        File[] dirs = ContextCompat.getExternalFilesDirs(mContext, null);
        if (dirs != null) {
            for (File file : dirs) {
                String path = getRootOfExternalStorage(file);
                if (path.contains("emulated")) {
                    Constants.internalStorageRoot = new File(path);
                } else {
                    Constants.externalStorageRoot =  new File(path);
                }
            }

        }
    }*/


    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void forceDeleteWithoutException(String url) {
        try {
            forceDelete(new File(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void forceDeleteWithoutException(File file) {
        try {
            forceDelete(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (!isSymlink(directory)) {
                cleanDirectory(directory);
            }

            if (!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }

    static boolean isSystemWindows() {
        char SYSTEM_SEPARATOR = File.separatorChar;
        return SYSTEM_SEPARATOR == '\\';
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        } else if (FileUtils.isSystemWindows()) {
            return false;
        } else {
            File fileInCanonicalDir = null;
            if (file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                File canonicalDir = file.getParentFile().getCanonicalFile();
                fileInCanonicalDir = new File(canonicalDir, file.getName());
            }

            return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
        }
    }

    public static void cleanDirectory(File directory) throws IOException {
        String message;
        if (!directory.exists()) {
            message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        } else if (!directory.isDirectory()) {
            message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        } else {
            File[] files = directory.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                File[] arr$ = files;
                int len$ = files.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    File file = arr$[i$];

                    try {
                        forceDelete(file);
                    } catch (IOException var8) {
                        exception = var8;
                    }
                }

                if (null != exception) {
                    throw exception;
                }
            }
        }
    }

/*    private static File getAlbumDir(Context context) {
        File storageDir = context.getExternalFilesDir(null);
        if (storageDir != null) {
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null;
                }
            }
            return storageDir;
        }
        return null;
    }*/

  /*  public static File getExternalPublicAlbumDir() {
        File storageDir = null;
        //  if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        String storageDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "DigitalSignate";
        storageDir = new File(storageDirPath);
        //Log.d("mylog1090","getAlbumDir storageDir=" + storageDir.getAbsolutePath());
        if (!storageDir.mkdirs()) {
            //   Log.d("mylog1090","getAlbumDir storageDir NOT exist=");
            if (!storageDir.exists()) {
                //    Log.d("mylog1090","getAlbumDir storageDir NOT exist2=");
                //     Log.d("mylog1090","getAlbumDir failed to create directory");
                return null;
            }
        }
        //  } else {
        //   Log.d("mylog1090", "External storage is not mounted READ/WRITE.");
        //  }
        return storageDir;
    }
*/
/*
    public static File getOutputMediaFileWithScore(Context context, String score) {
        try {
            File storageDir = getAlbumDir(context);
            String timeStamp = DateUtils.getStringFromDate(new Date(), "yyyyMMdd_HHmmss", false);
            String imageFileName = "IMG_" + timeStamp + "_" + score;
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
*/

  /*  public static File getOutputMediaFile(Context context) {
        try {
            File storageDir = getAlbumDir(context);
            String timeStamp = DateUtils.getStringFromDate(new Date(), "yyyyMMdd_HHmmss", false);
            String imageFileName = "IMG_" + timeStamp + "_";
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

 /*   public static File getOutputMediaFile(Context context, String dir, String name) {
        try {
            File storageDir = getAlbumDir(context);
            String pathToDir = storageDir.getAbsolutePath() + "/" + dir + "/";
            try {
                File file = new File(pathToDir);
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new File(pathToDir + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
*/
 /*   public static boolean isFileExist(Context context, String dir, String filename) {
        if (TextUtils.isEmpty(filename)) {
            return false;
        }
        Uri uri = Uri.parse(filename);
        File file = FileUtils.getOutputMediaFile(context, dir, uri.getLastPathSegment());
        if (file == null) {
            return false;
        }
        return file.exists();
    }*/

 /*   public static boolean isFileExist(String filepath) {
        try {
            if (TextUtils.isEmpty(filepath)) {
                return false;
            }
            Uri uri = Uri.parse(filepath);
            File file = new File(filepath);
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/

    //From FileUtil
   /* public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }
*/
   /* public static void deletefrom(Context context, Uri uri) {
        try {
            if (uri == null) {
                return;
            }
            String authority = uri.getAuthority();
            context.grantUriPermission(authority, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.grantUriPermission(authority, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //    App.getContext().gra
            int row = context.getContentResolver().delete(uri, null, null);

            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(0);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

    /*    InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;*/
  //  }

 /*   public static File fromWithoutThrow(Context context, Uri uri) {
        try {
            return from(context, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
*/
    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

   /* private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
*/
    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {

            }
            if (file.renameTo(newFile)) {

            }
        }
        return newFile;
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    //From Utils
 /*   public static boolean checkFileExists(String localPath) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String path = dir.getPath() + File.separator + localPath;
        File f = new File(path);
        if (f.length() == 0) {
            f.delete();
        }
        return f.exists();
    }*/

 /*   public static String buildPath(String localPath) {
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return f.getAbsolutePath() + File.separator + localPath;
    }*/

/*
    public static void copyFileToCache(String filename) {
        int idx = filename.lastIndexOf(File.separator);
        String filepath = filename.substring(idx + 1, filename.length());
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String filenameCopy = f.getAbsolutePath() + File.separator + filepath;

        if (filename.equals(filenameCopy)) {
            return;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] buf = new byte[4096];
        try {
            fos = new FileOutputStream(filenameCopy);
            fis = new FileInputStream(filename);
            int c = -1;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/

    public static byte[] fileToBytes(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

/*    public static File getOutputMediaFile(String dir, String name) {
        try {
            File storageDir = getAlbumDir(null);
            String pathToDir = storageDir.getAbsolutePath() + "/" + dir + "/";
            try {
                File file = new File(pathToDir);
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new File(pathToDir + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

/*    private static File getAlbumDir(Context context) {
        File storageDir = App.getContext().getExternalFilesDir(null);
        if(storageDir!=null){
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    return null;
                }
            }
            return storageDir;
        }
        return null;
    }*/
}
