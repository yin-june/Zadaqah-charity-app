/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hackaton;


 
import cn.hutool.core.io.file.FileReader;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
 
 
public class SolidityERC721Utils {
    
    private static String basePath = "C:\\Users\\vyyh\\Documents\\NetBeansProjects\\hackaton\\src\\main\\java\\";
 
    private static SolidityERC721Utils solidityGenerateUtil = new SolidityERC721Utils();
 

    private SolidityERC721Utils() {}
 

    public static SolidityERC721Utils getInstance() {
        return solidityGenerateUtil;
    }
 
    
    public static String getAbiJson() throws FileNotFoundException{
        String abiPath = basePath + "/com/mycompany/hackaton/abi.json";
        FileReader abiReader = new FileReader(abiPath);
        String abiResult = abiReader.readString();
        return abiResult;
    }
    
    public static String getBytecodeJson() throws FileNotFoundException{
        String bytecodePath = basePath + "/com/mycompany/hackaton/bytecode.json";
        FileReader bytecodePathReader = new FileReader(bytecodePath);
        String bytecodeResult = bytecodePathReader.readString();
        return bytecodeResult;
    }
 
    public static void genAbiAndBin() throws FileNotFoundException{
 
        String abiJson = SolidityERC721Utils.getInstance().getAbiJson();
        String bytecodeJson = SolidityERC721Utils.getInstance().getBytecodeJson();
 
        String abiFileName = "SolidityERC721.abi";
        String binFileName = "SolidityERC721.bin";
        generateAbiAndBin(abiJson,bytecodeJson,abiFileName,binFileName);
    }
 
    public static void generateAbiAndBin(String abi,String bin,String abiFileName,String binFileName){
        File abiFile = new File(basePath + "contract/"+abiFileName);
        File binFile = new File(basePath + "contract/"+binFileName);
        BufferedOutputStream abiBos = null;
        BufferedOutputStream binBos = null;
        try{
            FileOutputStream abiFos = new FileOutputStream(abiFile);
            FileOutputStream binFos = new FileOutputStream(binFile);
            abiBos = new BufferedOutputStream(abiFos);
            binBos = new BufferedOutputStream(binFos);
            abiBos.write(abi.getBytes());
            abiBos.flush();
            binBos.write(bin.getBytes());
            binBos.flush();
            generateJavaFile(abiFile.getPath(),binFile.getPath());
        }catch (Exception e){
            e.printStackTrace();
 
        }finally {
            if(abiBos != null){
                try{
                    abiBos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(binBos != null){
                try {
                    binBos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
 
    public static void generateJavaFile(String abiFile, String binFile){
        String generateFile = basePath + "/contract/";
        generateClass(abiFile,binFile,generateFile);
    }
 
    public static void generateClass(String abiFile,String binFile,String generateFile){
        String[] args = Arrays.asList(
                "-a",abiFile,
                "-b",binFile,
                "-p","",
                "-o",generateFile
        ).toArray(new String[0]);
        Stream.of(args).forEach(System.out::println);
        SolidityFunctionWrapperGenerator.main(args);
    }
 
    public static void main(String[] args) throws FileNotFoundException {
        genAbiAndBin();
    }
 
}