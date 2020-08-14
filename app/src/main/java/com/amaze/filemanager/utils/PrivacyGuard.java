package com.amaze.filemanager.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

public class PrivacyGuard {
    final private String TEMPORARY_DIRECTORY = "amaze-temp";
    private String processedDirectory = "";
    private String inputFilePath = "";
    private String inputFileName = "";

    public PrivacyGuard(String inputFilePath, String inputFileName) {
        this.inputFilePath = inputFilePath;
        this.inputFileName = inputFileName;
    }

    private String getSelectedFileExtention() {
        String fileExtension = "";
        int lastIndexOf = inputFilePath.lastIndexOf(".");
        if (lastIndexOf != -1) {
            fileExtension =  inputFilePath.substring(lastIndexOf);
        }
        fileExtension = fileExtension.toLowerCase();
        return fileExtension;
    }
    private String getNewFileName() {
        Random randomNumberC = new Random();
        String randomNumber = randomNumberC.nextInt(99999999)+"";
        String newFile = processedDirectory + "/" + inputFileName + "_" + randomNumber + "_secure"
                + getSelectedFileExtention();
        return newFile;
    }
    private void makeTemporaryDirectory() {
        File internalRootDir = Environment.getExternalStorageDirectory();
        String internalRootDirPath = internalRootDir.getAbsolutePath();
        processedDirectory = internalRootDirPath+"/"+TEMPORARY_DIRECTORY;
        File processedDirectoryF = new File(processedDirectory);
        if(!processedDirectoryF.exists()) {
            processedDirectoryF.mkdir();
        }
    }

    private String processFile() {
        String fileExtension = getSelectedFileExtention();
        String copiedFile = getNewFileName();
        switch (fileExtension){
            case ".jpg":
                try{
                    FileInputStream inStream = new FileInputStream(new File(inputFilePath));
                    FileOutputStream outStream = new FileOutputStream(new File(copiedFile));
                    new ExifRewriter().removeExifMetadata(inStream,outStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ".cr2":
            case ".heic":
            case ".gif":
            case ".png":
                try{
                    FileInputStream inStream = new FileInputStream(new File(inputFilePath));
                    FileOutputStream outStream = new FileOutputStream(new File(copiedFile));
                    Bitmap picBitmap = BitmapFactory.decodeFile(inputFilePath);
                    picBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                copiedFile = inputFilePath;
        }
        return copiedFile;
    }
    public String applyPrivacyGuard() {
        makeTemporaryDirectory();
        return processFile();
    }
}
