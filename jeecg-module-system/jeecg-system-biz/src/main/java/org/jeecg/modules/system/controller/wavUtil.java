package org.jeecg.modules.system.controller;

public class wavUtil {
	public short[] samples;
	public int fs;
	public int channels;
	public int sample_width;
	
	public wavUtil() {
		
	}
	
	public wavUtil(short[] samples, int fs, int channels, int sample_width) {
		this.samples = samples;
		this.fs = fs;
		this.channels = channels;
		this.sample_width = sample_width;
		
	}


	
	public boolean GetFromBytes(byte[] BytesData) {
		try
        {
            byte[] riff = new byte[4];
            byte[] riffSize = new byte[4];
            byte[] waveID = new byte[4];
            byte[] junkID = new byte[4];
            boolean hasjunk = false;
            byte[] junklength = new byte[4];

            byte[] fmtID = new byte[4];
            byte[] cksize = new byte[4];
            int waveType = 0; // �޷���int�������ڻ�ȡʱ��Ҫ�����ֽ�ת�� (Byte.toUnsignedInt(byte x))
            byte[] channel = new byte[2];
            byte[] sample_rate = new byte[4];
            byte[] bytespersec = new byte[4];
            byte[] blocklen_sample = new byte[2];
            byte[] bitNum = new byte[2];
            byte[] unknown = new byte[2];
            byte[] dataID = new byte[4];  //52
            byte[] dataLength = new byte[4];  //56 ���ֽ�

            
            
            int p = 0; //ģ������ָ��λ��
            
            System.arraycopy(BytesData, p, riff, 0, 4); // RIFF
            p += 4;
            
            if (DataParseUtils.convertFoutUnsignLong(riff[3], riff[2], riff[1], riff[0]) != 0x52494646) //0x52494646
            {
                Exception e = new Exception("���ļ�����WAVE�ļ�");
                throw e;
            }
            //System.out.println("test");

            /*if (riff[0]!=82 || riff[1]!=73  || riff[2]!=70  || riff[3]!=70) //0x52494646
            {
                Exception e = new Exception("���ļ�����WAVE�ļ�");
                throw e;
            }*/

            System.arraycopy(BytesData, p, riffSize, 0, 4); // �ļ�ʣ�೤��
            p += 4;

            if (DataParseUtils.convertFoutUnsignLong(riffSize[3], riffSize[2], riffSize[1], riffSize[0]) != BytesData.length - p)
            {
                //Exception e = new Exception("��WAVE�ļ��𻵣��ļ��������ǲ�һ��");
                //throw e;
            }

            //waveID = bread.ReadBytes(4);
            System.arraycopy(BytesData, p, waveID, 0, 4);
            p += 4;

            if (DataParseUtils.convertFoutUnsignLong(waveID[3], waveID[2], waveID[1], waveID[0]) != 0x57415645)
            {
                Exception e = new Exception("���ļ�����WAVE�ļ�");
                throw e;
            }
            //System.out.println("test2");

            byte[] tmp = new byte[4];
            System.arraycopy(BytesData, p, tmp, 0, 4);
            p += 4;

            if (DataParseUtils.convertFoutUnsignLong(tmp[3], tmp[2], tmp[1], tmp[0]) == 0x4A554E4B)
            {
                //����junk��ǵ�wav
                junkID = tmp;
                hasjunk = true;
                
                System.arraycopy(BytesData, p, junklength, 0, 4);
                p += 4;
                
                //junklength = bread.ReadBytes(4);
                //uint junklen = BitConverter.ToUInt32(junklength, 0);
                long junklen = DataParseUtils.convertFoutUnsignLong(junklength[3], junklength[2], junklength[1], junklength[0]);
                
                
                //����Ҫ��junk���ֶ���
                //bread.ReadBytes((int)junklen);
                p += (int)junklen;

                //��fmt ���
                //fmtID = bread.ReadBytes(4);
                System.arraycopy(BytesData, p, fmtID, 0, 4);
                p += 4;
            }
            else if (DataParseUtils.convertFoutUnsignLong(tmp[3], tmp[2], tmp[1], tmp[0]) == 0x666D7420)
            {
                fmtID = tmp;
            }
            else
            {
                Exception e = new Exception("�޷��ҵ�WAVE�ļ���junk��fmt���");
                throw e;
            }
            //System.out.println("test2.3");


            if (DataParseUtils.convertFoutUnsignLong(fmtID[3], fmtID[2], fmtID[1], fmtID[0]) != 0x666D7420)
            {
                //fmt ���
                Exception e = new Exception("�޷��ҵ�WAVE�ļ�fmt���");
                throw e;
            }
            //System.out.println("test2.4");

            //cksize = bread.ReadBytes(4);
            System.arraycopy(BytesData, p, cksize, 0, 4);
            p += 4;
            
            long p_data_start = DataParseUtils.convertFoutUnsignLong(cksize[3], cksize[2], cksize[1], cksize[0]);
            int p_wav_start = (int)p_data_start + 8;
            //System.out.println("test2.5");
            //waveType = bread.ReadUInt16();
            byte[] tmp_waveType = new byte[2];
            System.arraycopy(BytesData, p, tmp_waveType, 0, 2);
            p += 2;
            waveType = DataParseUtils.convertTwoUnsignInt(tmp_waveType[0], tmp_waveType[1]);

            if (waveType != 1)
            {
                // ��pcm��ʽ���ݲ�֧��
                Exception e = new Exception("WAVE�ļ�����pcm��ʽ����ʱ��֧��");
                throw e;
            }
            
            //������
            //channel = bread.ReadBytes(2);
            System.arraycopy(BytesData, p, channel, 0, 2);
            p += 2;

            //����Ƶ��
            //sample_rate = bread.ReadBytes(4);
            System.arraycopy(BytesData, p, sample_rate, 0, 4);
            p += 4;
            
            int fs = (int)DataParseUtils.convertFoutUnsignLong(sample_rate[0], sample_rate[1], sample_rate[2], sample_rate[3]);

            //ÿ�����ֽ���
            //bytespersec = bread.ReadBytes(4);
            System.arraycopy(BytesData, p, bytespersec, 0, 4);
            p += 4;

            //ÿ�β������ֽڴ�С��2Ϊ��������4Ϊ��������
            //blocklen_sample = bread.ReadBytes(2);
            System.arraycopy(BytesData, p, blocklen_sample, 0, 2);
            p += 2;

            //ÿ�������Ĳ������ȣ�Ĭ��16bit
            //bitNum = bread.ReadBytes(2);
            System.arraycopy(BytesData, p, bitNum, 0, 2);
            p += 2;

            //tmp = bread.ReadBytes(2);
            System.arraycopy(BytesData, p, tmp, 0, 2);
            p += 2;
            //Ѱ��da���
            while (DataParseUtils.convertTwoUnsignInt(tmp[1], tmp[0]) != 0x6461)
            {
                //tmp = bread.ReadBytes(2);
                System.arraycopy(BytesData, p, tmp, 0, 2);
                p += 2;
            }
            
            //tmp = bread.ReadBytes(2);
            System.arraycopy(BytesData, p, tmp, 0, 2);
            p += 2;

            if (DataParseUtils.convertTwoUnsignInt(tmp[1], tmp[0]) != 0x7461)
            {
                //ta���
                Exception e = new Exception("�޷��ҵ�WAVE�ļ�data���");
                throw e;
            }

            //wav����byte����
            byte[] data_size_byte = new byte[4];
            
            System.arraycopy(BytesData, p, data_size_byte, 0, 4);
            p += 4;
            
            long DataSize = DataParseUtils.convertFoutUnsignLong(data_size_byte[0], data_size_byte[1], data_size_byte[2], data_size_byte[3]);
            //System.out.println("test3");
            //����������
            long NumSamples = (long)DataSize / 2;
            
            if (NumSamples == 0)
            {
                NumSamples = (BytesData.length - p) / 2;
            }
            //if (BitConverter.ToUInt32(notDefinition, 0) == 18)
            //{
            //    unknown = bread.ReadBytes(2);
            //}
            //dataID = bread.ReadBytes(4);

            short[] data = new short[(int) NumSamples];
            
            for (int i = 0; i < NumSamples; i++)
            {
                //����2�ֽ��з�������
                //data[i] = bread.ReadInt16();
            	byte[] tmp_sample = new byte[2];
                System.arraycopy(BytesData, p, tmp_sample, 0, 2);
                p += 2;
                data[i] = (short)DataParseUtils.convertTwoUnsignInt(tmp_sample[0],tmp_sample[1]);
            }
            //System.out.println("test4");
            //s.Dispose();
            //fstream.Close();
            //fstream.Dispose();
            //bread.Dispose();

            //wav wave = new wav();
            this.samples = data;
            //wave.wavs = data;
            this.fs = fs;
            //wave.fs = fs;
            this.channels = DataParseUtils.convertTwoUnsignInt(channel[0],channel[1]);
            this.sample_width = DataParseUtils.convertTwoUnsignInt(bitNum[0],bitNum[1]);
            return true;
        }
        catch (Exception ex)
        {
        	System.out.println(ex);
            return false;
            //throw ex;
        }
	}
}
