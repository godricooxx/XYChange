package xychange;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BmpToXY {
	
	/**
	 * λͼ�Ŀ�
	 */
	private static int width; 
	/**
	 * λͼ�ĸ�
	 */
	private static int height; 
	/**
	 * λͼ����ɫ��
	 */
	private static int[][] ColorTable;
	/**
	 * λͼ����ɫ����
	 */
	private static int[][] ImageData;
	
	int ColorNum;
	
	private static int biBitCount;
	

	public void ToXY()
	{
		/*
		if(biBitCount != 8){
			System.out.println("ͼ���256ɫ����ɫͼ�񣬲��ܽ���ת��," + "λ�� = " + biBitCount);
			return;
		}*/
			try
			{
				// �����ļ����������� ��Ĭ���ļ���ַ�ڵ�ǰĿ¼��
				File BmpFile = new File("������.bmp");
				FileInputStream fis = new FileInputStream(BmpFile);		
				BufferedInputStream bis=new BufferedInputStream(fis);     // �û�������������ȡ
				ReadBmp(bis);     // ��ȡ.bmp�ļ����õ�ͼ����Ϣ
				bis.close();;
				fis.close();
				
				File XYFile = new File("������2.xy");     // ����������ļ�����  
				FileOutputStream fos=new FileOutputStream(XYFile);
				BufferedOutputStream bos=new BufferedOutputStream(fos);
				DataOutputStream dos = new DataOutputStream(bos);     // ����ԭʼ�������������	
				WriteXY(dos);     // ����ȡ����bmp�ļ�ͼ����Ϣд��xy�ļ���
				dos.flush();
				dos.close();
				bos.flush();
				fos.close();
				
			}catch (Exception e){
				System.out.println("�ļ�ת��ʧ��");
				e.printStackTrace();
			}			
	}

	public void ReadBmp(BufferedInputStream bis)
	{
		try{
			// ���������ֽ��������õ��ļ�ͷ����Ϣͷ������
			byte[] BITMAPFILEHEADER = new byte[14];
            bis.read(BITMAPFILEHEADER, 0, 14);
            
            byte[] BITMAPINFOHEADER = new byte[40];
            bis.read(BITMAPINFOHEADER, 0, 40);
            
            width = ByteToInt(BITMAPINFOHEADER, 7);
            height = ByteToInt(BITMAPINFOHEADER, 11);
            biBitCount = ByteToInt(BITMAPINFOHEADER, 15);
            System.out.println("λ�� = " + biBitCount);
            ColorNum = ByteToInt(BITMAPINFOHEADER, 35);    
            
            if(biBitCount == 524289){
            	
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
		    		ColorTable[i][0] = bis.read()/ 4;    //B
		    		ColorTable[i][1] = bis.read()/ 4;    //G
		    		ColorTable[i][2] = bis.read()/ 4;    //R     
		    		ColorTable[i][3] = bis.read();    //��Ϊ0	    	      
		    	}

		    	ImageData = new int[height][width];     //����ͼ����������
		    	for (int i = 0; i < height; i++){
		    		for (int j = 0; j < width; j++){
		    			ImageData[i][j] = bis.read();
		    		}
			    }
            }else{
            	System.out.println("ͼ���256ɫ����ɫͼ�񣬲��ܽ���ת��," + "λ�� = " + biBitCount);
            }
			
		}catch (Exception e){
			System.out.println("ͼ���256ɫ����ɫͼ�񣬲��ܽ���ת��," + "λ�� = " + biBitCount);
			e.printStackTrace();
		}	
	}
	
    public int ByteToInt(byte[] array, int start) {  
        // ��Ϊchar,byte,short��Щ�������;������������Զ�תΪ��int�����࣬  
        // ����array2[start]&0xff��ʵ����˼����ͨ��&0xff���ַ�����ת��Ϊ��int���ݣ�Ȼ���ڽ���λ���㡣  
        // ������Ҫע�����<<�����ȼ����&�ߣ����Ա���������š�  
  
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
	        byte Warp_high = (byte)(width>>8); // XY�ļ���ȸ��ֽ�  
	        byte Weft_high = (byte)(height>>8); // XY�ļ��߶ȸ��ֽ�
	        int Ratio = 1;
	        int Bits = 8;
	        int Cn = ColorNum;// �ļ�ʹ����ɫ��

	        // �����ļ�ͷ����  
	        dos.writeByte(Warp_high); 
	        dos.writeByte(Weft_high);  
	        dos.writeByte(Ratio);
	        dos.writeByte(Bits);
	        dos.writeByte(Cn);

	        //������ɫ������
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

	        // ����Ϣͷ�ı�����ֵ  
	        byte Warp_low = (byte)((width<<8)>>8); // XY�ļ���ȵ��ֽ�
	        byte Weft_low = (byte)((height<<8)>>8); // XY�ļ��߶ȵ��ֽ�
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
	          
	        // ������Ϣͷ����  
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
