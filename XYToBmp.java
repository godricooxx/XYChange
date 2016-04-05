package xychange;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XYToBmp {
	
	/**
	 * 位图的宽
	 */
	private static int width; 
	/**
	 * 位图的高
	 */
	private static int height; 
	/**
	 * 位图的大小
	 */
	private static int bfSize;
	/**
	 * 位图的颜色表
	 */
	private static int[][] ColorTable;
	/**
	 * 位图的颜色数据
	 */
	private static int[][] ImageData;
	
	int ColorNum;
	
	public void ToBmp()
	{
		try
		{
			// 创建文件输入流对象 ，默认文件地址在当前目录下
			File XYFile = new File("古屏龙.xy");
			//RandomAccessFile raf = new RandomAccessFile(XYFile,"r");
			FileInputStream fis = new FileInputStream(XYFile);		
			BufferedInputStream bis=new BufferedInputStream(fis);     // 用缓冲输入流来读取
			ReadXY(bis);     // 读取.xy文件，得到图像信息
			bis.close();;
			fis.close();
			//ReadXY(raf);
			//raf.close();
			
			File BmpFile = new File("古屏龙.bmp");     // 创建输出流文件对象  
			FileOutputStream fos=new FileOutputStream(BmpFile);
			BufferedOutputStream bos=new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(bos);     // 创建原始数据输出流对象	
			WriteBmp(dos);     // 将读取到的xy文件图像信息写入Bmp文件中
			dos.flush();
			dos.close();
			bos.flush();
			fos.close();
			
			//FileInputStream fis = new FileInputStream(BmpFile);
			//BufferedInputStream bis = new BufferedInputStream(fis);
			//ReadBmp(bis);
			//bis.close();
			//fis.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void ReadXY(BufferedInputStream bis)
	{
		try{

	    	int Warp_high =bis.read();     // 从0位置开始，读取位图宽度的高字节数据
	    	int Weft_high =bis.read();     // 读取位图高度的高字节数据
	    	int Ratio = bis.read();        
	    	bis.read();                    //跳过1字节，bis.skip()
	    	int Bits = bis.read();
		    int Cn = ColorNum;

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
	    	      ColorTable[i][3] = 0;                //α为0
	    	      ColorTable[i][2] = bis.read()* 4;    //R
	    	      ColorTable[i][1] = bis.read()* 4;    //G
	    	      ColorTable[i][0] = bis.read()* 4;    //B
	    	}
	    	bis.skip(7);     //跳过7个字节，到780位置
		    int Warp_low = bis.read();     // 读取位图宽度的低字节数据
	    	int Weft_low = bis.read();     // 读取位图高度的低字节数据
		    int No_use = bis.read();
		    ColorNum = bis.read();         //读取位图颜色数
		    int No_use2 = bis.read();
		    char[] Signature = new char[6];
		    for (int i = 0; i < 6; i++){
			    Signature[i] =(char) bis.read();
		    }
		    int UseDensity = bis.read();
	    	int DensityOfWeft = bis.read();
		    int DensityOfWarp = bis.read();
	    	/*
	    	for (int i = 0; i < 5; i++){
		    	Signature[i] = (char) (Signature[i] ^ ColorNum);
	    	}
	    	*/
	    	bis.skip(6);		
	    	int Warp = (int) (((Warp_high & 0xff) << 8 ) | (Warp_low & 0xff));      //获得位图图像宽度
	    	int Weft = (int) (((Weft_high & 0xff) << 8 ) | (Weft_low & 0xff));      //获得位图图像宽度
	    	width = Warp;
	    	height = Weft;
	    	//int[][] ImageData = new int[height][width];
	    	ImageData = new int[height][width];     //创建图像数据数组
	    	for (int i = 0; i < height; i++){
	    		for (int j = 0; j < width; j++){
	    			ImageData[i][j] = bis.read();
	    		}
		    }
		}catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	/*内存映射，可以处理大文件
	public void ReadXY(RandomAccessFile raf)
	{
		try{
			MappedByteBuffer Header = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 780 , 14);
			byte Warp_low = Header.get(0);
	    	byte Weft_low = Header.get(1);
		    byte No_use = Header.get(2);
		    byte ColorNum = Header.get(3);
		    byte No_use2 = Header.get(4);
		    char[] Signature = new char[6];
		    for (int i = 0; i < 6; i++){
			    Signature[i] =(char) Header.get(i+5);
		    }
		    byte UseDensity = Header.get(11);
	    	byte DensityOfWeft = Header.get(12);
		    byte DensityOfWarp = Header.get(13);
		
	    	for (int i = 0; i < 5; i++){
		    	Signature[i] = (char) ((byte)Signature[i] ^ ColorNum);
	    	}
	    	
	    	MappedByteBuffer ImageDescriptor = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0 , 5);
	    	short Warp =(short) (ImageDescriptor.get(0)*256 + Warp_low);
	    	short Weft =(short) (ImageDescriptor.get(1)*256 + Weft_low);
	    	byte Ratio = ImageDescriptor.get(2);
	    	byte Bits = ImageDescriptor.get(4);
		    byte Cn = ColorNum;
		    
		    short TableSize = Cn;
		    ColorUsed = (int) ((TableSize & 0x0FFFF) + 1);
	    	int ColorTableSize = 0;
	    	ColorTableSize = ColorUsed*3;
	    	
	    	MappedByteBuffer Palette = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 5 , ColorTableSize);
	    	//int[][] ColorTable = new int[256][4];
	    	for (int i = 0; i < 256; i++){
	    	      ColorTable[i][3] = 0;
	    	      ColorTable[i][2] = Palette.get(3*i)* 4;    //R
	    	      ColorTable[i][1] = Palette.get(3*i+1)* 4;  //G
	    	      ColorTable[i][0] = Palette.get(3*i+2)* 4;  //B
	    	}
		
	    	int ImageDataSize = 0;
	    	ImageDataSize = (int) (Warp & 0x0FFFF);
	    	MappedByteBuffer XYImageData = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 800 , ImageDataSize);
	    	width = (int) (Weft & 0x0FFFF);
	    	height = (int) (Warp & 0x0FFFF);
	    	//int[][] ImageData = new int[height][width];
	    	for (int i = 0; i < height; i++){
	    		for (int j = 0; j < width; j++){
	    			ImageData[i][j] = XYImageData.get();
	    		}
		    }
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}*/
		
	public void WriteBmp(DataOutputStream dos)
	{
		try
		{
	        int bfType = 0x424d; // 位图文件类型（0—1字节）  
	        bfSize = 54 + 1024 + width * height;// bmp文件的大小（2—5字节）  
	        int bfReserved1 = 0;// 位图文件保留字，必须为0（6-7字节）  
	        int bfReserved2 = 0;// 位图文件保留字，必须为0（8-9字节）  
	        int bfOffBits = 1078;// 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）  

	        // 输入文件头数据  
	        dos.writeShort(bfType); // 输入位图文件类型'BM'  
	        dos.write(IntToByte(bfSize),0,4); // 输入位图文件大小  
	        dos.write(IntToByte(bfReserved1),0,2);// 输入位图文件保留字  
	        dos.write(IntToByte(bfReserved2),0,2);// 输入位图文件保留字  
	        dos.write(IntToByte(bfOffBits),0,4);// 输入位图文件偏移量  

	        // 给信息头的变量赋值  
	        int biSize = 40;// 信息头所需的字节数（14-17字节）  
	        int biWidth = width;// 位图的宽（18-21字节）  
	        int biHeight = height;// 位图的高（22-25字节）  
	        int biPlanes = 1; // 目标设备的级别，必须是1（26-27字节）  
	        int biBitcount = 8;// 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。  
	        int biCompression = 0;// 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。  
	        int biSizeImage = width * height;// 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）  
	        int biXPelsPerMeter = 0;// 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值  
	        int biYPelsPerMeter = 0;// 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值  
	        int biClrUsed = 256;// 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了  
	        int biClrImportant = 0;// 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要  
	          
	        // 输入信息头数据  
	        dos.write(IntToByte(biSize),0,4);// 输入信息头数据的总字节数  
	        dos.write(IntToByte(biWidth),0,4);// 输入位图的宽  
	        dos.write(IntToByte(biHeight),0,4);// 输入位图的高  
	        dos.write(IntToByte(biPlanes),0,2);// 输入位图的目标设备级别  
	        dos.write(IntToByte(biBitcount),0,2);// 输入每个像素占据的字节数  
	        dos.write(IntToByte(biCompression),0,4);// 输入位图的压缩类型  
	        dos.write(IntToByte(biSizeImage),0,4);// 输入位图的实际大小  
	        dos.write(IntToByte(biXPelsPerMeter),0,4);// 输入位图的水平分辨率  
	        dos.write(IntToByte(biYPelsPerMeter),0,4);// 输入位图的垂直分辨率  
	        dos.write(IntToByte(biClrUsed),0,4);// 输入位图使用的总颜色数  
	        dos.write(IntToByte(biClrImportant),0,4);// 输入位图使用过程中重要的颜色数  

	        for (int i = 0; i < 256; i++) 
	        {
	        	int a = 0;
	            int red = ColorTable[i][2];
	            int green = ColorTable[i][1];
	            int blue = ColorTable[i][0];
	            
	            dos.write(blue);  
	            dos.write(green);  
	            dos.write(red);  
	            dos.write(a);  
	        }  
	        
	        //不需要补0，不需要是4的倍数
	        //int skip=0;
	        //if(width/4!=0)
	        //{
	          //skip=4-width%4;
	       // }  
	        
	        //根据bmp的保存格式，位图数据中height的值如果是正数的话，那么数据就是按从下到上，从左到右的顺序来保存。
	        //也就是说实际图像的第一行的点在内存是最后一行 
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
	
	/** 
     * 将一个int数据转为按小端顺序排列的字节数组 
     * @param data int数据 
     * @return  按小端顺序排列的字节数组 
     */  
	public byte[] IntToByte(int data)
	{  
        byte b4 = (byte)((data)>>24);  
        byte b3 = (byte)(((data)<<8)>>24);  
        byte b2= (byte)(((data)<<16)>>24);  
        byte b1 = (byte)(((data)<<24)>>24);  
        byte[] bytes = {b1,b2,b3,b4};  
        return bytes;  
    }  

}
