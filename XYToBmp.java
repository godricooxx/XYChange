package xychange;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class XYToBmp {
	
	/**
	 * λͼ�Ŀ�
	 */
	private static int width; 
	/**
	 * λͼ�ĸ�
	 */
	private static int height; 
	/**
	 * λͼ�Ĵ�С
	 */
	private static int bfSize;
	/**
	 * λͼ����ɫ��
	 */
	private static int[][] ColorTable;
	/**
	 * λͼ����ɫ����
	 */
	private static int[][] ImageData;
	
	int ColorNum;
	
	public void ToBmp()
	{
		try
		{
			// �����ļ����������� ��Ĭ���ļ���ַ�ڵ�ǰĿ¼��
			File XYFile = new File("������.xy");
			//RandomAccessFile raf = new RandomAccessFile(XYFile,"r");
			FileInputStream fis = new FileInputStream(XYFile);		
			BufferedInputStream bis=new BufferedInputStream(fis);     // �û�������������ȡ
			ReadXY(bis);     // ��ȡ.xy�ļ����õ�ͼ����Ϣ
			bis.close();;
			fis.close();
			//ReadXY(raf);
			//raf.close();
			
			File BmpFile = new File("������.bmp");     // ����������ļ�����  
			FileOutputStream fos=new FileOutputStream(BmpFile);
			BufferedOutputStream bos=new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(bos);     // ����ԭʼ�������������	
			WriteBmp(dos);     // ����ȡ����xy�ļ�ͼ����Ϣд��Bmp�ļ���
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

	    	int Warp_high =bis.read();     // ��0λ�ÿ�ʼ����ȡλͼ��ȵĸ��ֽ�����
	    	int Weft_high =bis.read();     // ��ȡλͼ�߶ȵĸ��ֽ�����
	    	int Ratio = bis.read();        
	    	bis.read();                    //����1�ֽڣ�bis.skip()
	    	int Bits = bis.read();
		    int Cn = ColorNum;

	    	ColorTable = new int[256][4];   //��5λ�ÿ�ʼ��������ɫ������
	    	//��ɫ����ʵ��һ��ӳ�����ʶ��ɫ����������������ɫ�Ķ�Ӧ��ϵ
	    	//�����ļ��еĲ��־���һ����ά����palette[N][4],����N��ʾ�ܵ���ɫ��������ÿ�е��ĸ�Ԫ�طֱ��ʾ��������Ӧ��B��G��R��Alpha��ֵ
            //ÿ������ռһ���ֽڡ��粻��͸��ͨ��ʱ��AlphaΪ0��
	    	//xy�ļ��������ֽڴ���һ����ɫ��˳��ΪRGB
	    	//Bmp�ļ���4���ֽڴ���һ����ɫ��˳��ΪBGR��
	    	
	    	//typedefstructtagRGBQUAD{
	    	//  BYTErgbBlue;//��ɫ�����ȣ�ֵ��ΧΪ0-255)
	    	//  BYTErgbGreen;//��ɫ�����ȣ�ֵ��ΧΪ0-255)
	    	//  BYTErgbRed;//��ɫ�����ȣ�ֵ��ΧΪ0-255)
	    	//  BYTErgbReserved;//����������Ϊ0
	    	//}RGBQUAD;
	    	for (int i = 0; i < 256; i++){
	    	      ColorTable[i][3] = 0;                //��Ϊ0
	    	      ColorTable[i][2] = bis.read()* 4;    //R
	    	      ColorTable[i][1] = bis.read()* 4;    //G
	    	      ColorTable[i][0] = bis.read()* 4;    //B
	    	}
	    	bis.skip(7);     //����7���ֽڣ���780λ��
		    int Warp_low = bis.read();     // ��ȡλͼ��ȵĵ��ֽ�����
	    	int Weft_low = bis.read();     // ��ȡλͼ�߶ȵĵ��ֽ�����
		    int No_use = bis.read();
		    ColorNum = bis.read();         //��ȡλͼ��ɫ��
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
	    	int Warp = (int) (((Warp_high & 0xff) << 8 ) | (Warp_low & 0xff));      //���λͼͼ����
	    	int Weft = (int) (((Weft_high & 0xff) << 8 ) | (Weft_low & 0xff));      //���λͼͼ����
	    	width = Warp;
	    	height = Weft;
	    	//int[][] ImageData = new int[height][width];
	    	ImageData = new int[height][width];     //����ͼ����������
	    	for (int i = 0; i < height; i++){
	    		for (int j = 0; j < width; j++){
	    			ImageData[i][j] = bis.read();
	    		}
		    }
		}catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	/*�ڴ�ӳ�䣬���Դ�����ļ�
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
	        int bfType = 0x424d; // λͼ�ļ����ͣ�0��1�ֽڣ�  
	        bfSize = 54 + 1024 + width * height;// bmp�ļ��Ĵ�С��2��5�ֽڣ�  
	        int bfReserved1 = 0;// λͼ�ļ������֣�����Ϊ0��6-7�ֽڣ�  
	        int bfReserved2 = 0;// λͼ�ļ������֣�����Ϊ0��8-9�ֽڣ�  
	        int bfOffBits = 1078;// �ļ�ͷ��ʼ��λͼʵ������֮����ֽڵ�ƫ������10-13�ֽڣ�  

	        // �����ļ�ͷ����  
	        dos.writeShort(bfType); // ����λͼ�ļ�����'BM'  
	        dos.write(IntToByte(bfSize),0,4); // ����λͼ�ļ���С  
	        dos.write(IntToByte(bfReserved1),0,2);// ����λͼ�ļ�������  
	        dos.write(IntToByte(bfReserved2),0,2);// ����λͼ�ļ�������  
	        dos.write(IntToByte(bfOffBits),0,4);// ����λͼ�ļ�ƫ����  

	        // ����Ϣͷ�ı�����ֵ  
	        int biSize = 40;// ��Ϣͷ������ֽ�����14-17�ֽڣ�  
	        int biWidth = width;// λͼ�Ŀ�18-21�ֽڣ�  
	        int biHeight = height;// λͼ�ĸߣ�22-25�ֽڣ�  
	        int biPlanes = 1; // Ŀ���豸�ļ��𣬱�����1��26-27�ֽڣ�  
	        int biBitcount = 8;// ÿ�����������λ����28-29�ֽڣ���������1λ��˫ɫ����4λ��16ɫ����8λ��256ɫ������24λ�����ɫ��֮һ��  
	        int biCompression = 0;// λͼѹ�����ͣ�������0����ѹ������30-33�ֽڣ���1��BI_RLEBѹ�����ͣ���2��BI_RLE4ѹ�����ͣ�֮һ��  
	        int biSizeImage = width * height;// ʵ��λͼͼ��Ĵ�С��������ʵ�ʻ��Ƶ�ͼ���С��34-37�ֽڣ�  
	        int biXPelsPerMeter = 0;// λͼˮƽ�ֱ��ʣ�ÿ����������38-41�ֽڣ��������ϵͳĬ��ֵ  
	        int biYPelsPerMeter = 0;// λͼ��ֱ�ֱ��ʣ�ÿ����������42-45�ֽڣ��������ϵͳĬ��ֵ  
	        int biClrUsed = 256;// λͼʵ��ʹ�õ���ɫ���е���ɫ����46-49�ֽڣ������Ϊ0�Ļ���˵��ȫ��ʹ����  
	        int biClrImportant = 0;// λͼ��ʾ��������Ҫ����ɫ��(50-53�ֽ�)�����Ϊ0�Ļ���˵��ȫ����Ҫ  
	          
	        // ������Ϣͷ����  
	        dos.write(IntToByte(biSize),0,4);// ������Ϣͷ���ݵ����ֽ���  
	        dos.write(IntToByte(biWidth),0,4);// ����λͼ�Ŀ�  
	        dos.write(IntToByte(biHeight),0,4);// ����λͼ�ĸ�  
	        dos.write(IntToByte(biPlanes),0,2);// ����λͼ��Ŀ���豸����  
	        dos.write(IntToByte(biBitcount),0,2);// ����ÿ������ռ�ݵ��ֽ���  
	        dos.write(IntToByte(biCompression),0,4);// ����λͼ��ѹ������  
	        dos.write(IntToByte(biSizeImage),0,4);// ����λͼ��ʵ�ʴ�С  
	        dos.write(IntToByte(biXPelsPerMeter),0,4);// ����λͼ��ˮƽ�ֱ���  
	        dos.write(IntToByte(biYPelsPerMeter),0,4);// ����λͼ�Ĵ�ֱ�ֱ���  
	        dos.write(IntToByte(biClrUsed),0,4);// ����λͼʹ�õ�����ɫ��  
	        dos.write(IntToByte(biClrImportant),0,4);// ����λͼʹ�ù�������Ҫ����ɫ��  

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
	        
	        //����Ҫ��0������Ҫ��4�ı���
	        //int skip=0;
	        //if(width/4!=0)
	        //{
	          //skip=4-width%4;
	       // }  
	        
	        //����bmp�ı����ʽ��λͼ������height��ֵ����������Ļ�����ô���ݾ��ǰ����µ��ϣ������ҵ�˳�������档
	        //Ҳ����˵ʵ��ͼ��ĵ�һ�еĵ����ڴ������һ�� 
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
     * ��һ��int����תΪ��С��˳�����е��ֽ����� 
     * @param data int���� 
     * @return  ��С��˳�����е��ֽ����� 
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
