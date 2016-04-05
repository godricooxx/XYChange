package xychange;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 类说明
 * @author 董佳星
 * 
 * @version 1.0 创建于2015-6-16 上午11：00
 * 
 */
public class XYChange{
	
	public static void main(String[] args) {
		//XYToBmp Bmp = new XYToBmp();
		//Bmp.ToBmp();
		BmpToXY XY = new BmpToXY();
		XY.ToXY();
		//System.out.println("图像大小："+bfSize+" byte");
		//System.out.println("图像像素："+width+"*"+height);
		// TODO Auto-generated method stub      
	}
	

}

