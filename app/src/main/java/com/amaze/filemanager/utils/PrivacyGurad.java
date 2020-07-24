package com.amaze.filemanager.utils;

import android.os.Environment;

import com.amaze.filemanager.filesystem.HybridFileParcelable;

import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

public class PrivacyGurad {
    final private String TEMPORARY_DIRECTORY = "amaze-temp";
    private HybridFileParcelable selectedFile = null;
    private String processedDirectory = "";

    public PrivacyGurad(HybridFileParcelable baseFile) {
        this.selectedFile = baseFile;
    }

    private String getSelectedFileExtention() {
        String fileExtension = "";
        int lastIndexOf = selectedFile.getPath().lastIndexOf(".");
        if (lastIndexOf != -1) {
            fileExtension =  selectedFile.getPath().substring(lastIndexOf);
        }
        fileExtension = fileExtension.toLowerCase();
        return fileExtension;
    }
    private String getNewFileName() {
        Random randomNumberC = new Random();
        String randomNumber = randomNumberC.nextInt(99999999)+"";
        String newFile = processedDirectory + "/" + selectedFile.getName() + "_" + randomNumber + "_secure" + getSelectedFileExtention();
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
    private HybridFileParcelable processFile() {
        String fileExtension = getSelectedFileExtention();
        HybridFileParcelable hfp = null;
        switch (fileExtension){
            case ".jpg":
                String copiedFile = getNewFileName();
                hfp = new HybridFileParcelable(copiedFile);
                try{
                    FileInputStream inStream = new FileInputStream(new File(selectedFile.getPath()));
                    FileOutputStream outStream = new FileOutputStream(new File(copiedFile));
                    new ExifRewriter().removeExifMetadata(inStream,outStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                hfp = selectedFile;
        }
        return hfp;
    }
    public HybridFileParcelable applyPrivacyGuard() {
        makeTemporaryDirectory();
        return processFile();
    }
}
