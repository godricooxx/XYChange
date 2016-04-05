package xychange;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BmpToXY {
	
	/**
	 * 位图的宽
	 */
	private static int width; 
	/**
	 * 位图的高
	 */
	private static int height; 
	/**
	 * 位图的颜色表
	 */
	private static int[][] ColorTable;
	/**
	 * 位图的颜色数据
	 */
	private static int[][] ImageData;
	
	int ColorNum;
	
	private static int biBitCount;
	

	public void ToXY()
	{
		/*
		if(biBitCount != 8){
			System.out.println("图像非256色索引色图像，不能进行转换," + "位数 = " + biBitCount);
			return;
		}*/
			try
			{
				// 创建文件输入流对象 ，默认文件地址在当前目录下
				File BmpFile = new File("古屏龙.bmp");
				FileInputStream fis = new FileInputStream(BmpFile);		
				BufferedInputStream bis=new BufferedInputStream(fis);     // 用缓冲输入流来读取
				ReadBmp(bis);     // 读取.bmp文件，得到图像信息
				bis.close();;
				fis.close();
				
				File XYFile = new File("古屏龙2.xy");     // 创建输出流文件对象  
				FileOutputStream fos=new FileOutputStream(XYFile);
				BufferedOutputStream bos=new BufferedOutputStream(fos);
				DataOutputStream dos = new DataOutputStream(bos);     // 创建原始数据输出流对象	
				WriteXY(dos);     // 将读取到的bmp文件图像信息写入xy文件中
				dos.flush();
				dos.close();
				bos.flush();
				fos.close();
				
			}catch (Exception e){
				System.out.println("文件转换失败");
				e.printStackTrace();
			}			
	}

	public void ReadBmp(BufferedInputStream bis)
	{
		try{
			// 建立两个字节数组来得到文件头和信息头的数据
			byte[] BITMAPFILEHEADER = new byte[14];
            bis.read(BITMAPFILEHEADER, 0, 14);
            
            byte[] BITMAPINFOHEADER = new byte[40];
            bis.read(BITMAPINFOHEADER, 0, 40);
            
            width = ByteToInt(BITMAPINFOHEADER, 7);
            height = ByteToInt(BITMAPINFOHEADER, 11);
            biBitCount = ByteToInt(BITMAPINFOHEADER, 15);
            System.out.println("位数 = " + biBitCount);
            ColorNum = ByteToInt(BITMAPINFOHEADER, 35);    
            
            if(biBitCount == 524289){
            	
	    	    ColorTable = new int[256][4];   //从5位置开始，创建颜色表数组	    	    
		    	//颜色表其实是一张映射表，标识颜色索引号与其代表的颜色的对应关系
		    	//它在文件中的布局就像一个二维数组palette[N][4],其中N表示总的颜色索引数，每行的四个元素分别表示该索引对应的B、G、R和Alpha的值
	            //每个分量占一个字节。如不设透明通道时，Alpha为0。
		    	//xy文件中三个字节代表一种颜色，顺序为RGB
		    	//Bmp文件中4个字节代表一个颜色，顺序为BGRα
		    	
		    	//typedefstructtagRGBQUAD{
		    	//  BYTErgbBlue;//蓝色的亮度（值范围为0-255)
		    	//  BYTErgbGreen;//绿色的亮度（值范围为0-255)
		    	//  BYTErgbRed;//红色的亮度（值范围为0-255)
		    	//  BYTErgbReserved;//保留，必须为0
		    	//}RGBQUAD;
		    	for (int i = 0; i < 256; i++){
		    		ColorTable[i][0] = bis.read()/ 4;    //B
		    		ColorTable[i][1] = bis.read()/ 4;    //G
		    		ColorTable[i][2] = bis.read()/ 4;    //R     
		    		ColorTable[i][3] = bis.read();    //α为0	    	      
		    	}

		    	ImageData = new int[height][width];     //创建图像数据数组
		    	for (int i = 0; i < height; i++){
		    		for (int j = 0; j < width; j++){
		    			ImageData[i][j] = bis.read();
		    		}
			    }
            }else{
            	System.out.println("图像非256色索引色图像，不能进行转换," + "位数 = " + biBitCount);
            }
			
		}catch (Exception e){
			System.out.println("图像非256色索引色图像，不能进行转换," + "位数 = " + biBitCount);
			e.printStackTrace();
		}	
	}
	
    public int ByteToInt(byte[] array, int start) {  
        // 因为char,byte,short这些数据类型经过运算符后会自动转为成int数据类，  
        // 所以array2[start]&0xff的实际意思就是通过&0xff将字符数据转化为正int数据，然后在进行位运算。  
        // 这里需要注意的是<<的优先级别比&高，所以必须加上括号。  
  
        int i = (int) ((array[start] & 0xff) << 24)  
                | ((array[start - 1] & 0xff) << 16)  
                | ((array[start - 2] & 0xff) << 8)  
                | (array[start - 3] & 0xff);  
        return i;  
    }  
    
	public void WriteXY(DataOutputStream dos)
	{
		try
		{
	        byte Warp_high = (byte)(width>>8); // XY文件宽度高字节  
	        byte Weft_high = (byte)(height>>8); // XY文件高度高字节
	        int Ratio = 1;
	        int Bits = 8;
	        int Cn = ColorNum;// 文件使用颜色数

	        // 输入文件头数据  
	        dos.writeByte(Warp_high); 
	        dos.writeByte(Weft_high);  
	        dos.writeByte(Ratio);
	        dos.writeByte(Bits);
	        dos.writeByte(Cn);

	        //输入颜色表数据
	        for (int i = 0; i < 256; i++) 
	        {
	            int red = ColorTable[i][2];
	            int green = ColorTable[i][1];
	            int blue = ColorTable[i][0];
	            
	            dos.write(red);  
	            dos.write(green);  
	            dos.write(blue);  
	        }  
	        
	        byte[] block = new byte[7];
	        dos.write(block);

	        // 给信息头的变量赋值  
	        byte Warp_low = (byte)((width<<8)>>8); // XY文件宽度低字节
	        byte Weft_low = (byte)((height<<8)>>8); // XY文件高度低字节
	        int No_use = 0x98; 
	        int No_use2 = 0x80; 
	        
	        byte[] Signature = new byte[5];
	        int[] tem ={0x40, 0x42, 0x24, 0x5E, 0x25};
	        for (int i = 0; i < 5; i++){
		    	Signature[i] = (byte)(tem[i] ^ ColorNum);
	    	}
	        int test = 0;
	    
	        int UseDensity = 0x5A;
	        int DensityOfWeft = 0x18;
	        int DensityOfWarp = 0x24;
	          
	        // 输入信息头数据  
	        dos.writeByte(Warp_low);
	        dos.writeByte(Weft_low);
	        dos.writeByte(No_use);
	        dos.writeByte(ColorNum);
	        dos.writeByte(No_use2);
	        dos.write(Signature);
	        dos.writeByte(test);
	        dos.writeByte(UseDensity);
	        dos.writeByte(DensityOfWeft);
	        dos.writeByte(DensityOfWarp);
	        
	        byte[] block2 = new byte[6];
	        dos.write(block2);

	        for (int i = height -1; i >= 0; i--) {  
	            for (int j = 0; j < width; j++) {  
	                int IndexValue = ImageData[i][j];
	                dos.write(IndexValue); 
	            }  
	            //if(skip!=0)
	    	        //dos.write(new byte[skip]);
	        }	       
		}catch (Exception e) {
		      e.printStackTrace();
	    }
	}
}
