package pogrebenko.labfive.model;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Comparator;

//******************************************************************************
//
// 21.05.2020

public class DICOMImage implements Comparable<DICOMImage> {
    public String FileName;
    public double SliceLocation;
    public int SeriesNumber, AcquNumber, InstNumber;
    public double AcquTime, ContTime, TrigTime;
    public BufferedImage Image;
    int Wd, Ht;

//******************************************************************************

    public DICOMImage(String PathName, String FileName)
            throws Exception {
        InputStream fis = new BufferedInputStream(
                new FileInputStream(Paths.get(PathName, FileName).toString())
        );

        // skipping 'File Preamble'
        skip(fis, 0x80);

// site for reading
        byte[] buf4 = new byte[4];

        // checking 'DICOM Prefix'
        int
                read = read(fis, buf4, 4);
        if (!new String(buf4).equals("DICM")) {
            fis.close();
            throw new Exception("NOT a valid DICOM file: " + PathName + "/" + FileName);
        }

        //looks like, going on

        this.FileName = FileName;
        SeriesNumber = AcquNumber = InstNumber = 0;
        SliceLocation = AcquTime = ContTime = TrigTime = 0;
        Image = null;

// DICOM tag
        short[]
                tag2 = new short[2];

// initial datatype assuming explicit
        boolean
                expl = true;

        // c'mon iterating metadata
        for (; ; ) {
            // reading tag bytes
            if ((read = read(fis, buf4, 4)) <= 0)
                break;

            // getting tag
            tag2[0] = (short) (((buf4[1] & 0xFF) << 8) | (buf4[0] & 0xFF));
            tag2[1] = (short) (((buf4[3] & 0xFF) << 8) | (buf4[2] & 0xFF));

            // site for datatype
            String
                    dt = "";
            // site for length
            int
                    len4 = 0;
            // site for text
            String
                    text = "";

            if (expl || tag2[0] == 0x0002)
            // explicit datatype
            {
                // reading datatype bytes
                read = read(fis, buf4, 2);
                // getting datatype
                dt = new String(buf4, 0, 2);

                // getting length
                if (dt.equals("OB") || dt.equals("OW") || dt.equals("OF") || dt.equals("SQ") || dt.equals("UN"))
                // 'Other Byte' || 'Other Word' || 'Other Float' || 'Sequence of Items' || 'Unknown'
                {
                    skip(fis, 2);
                    read = read(fis, buf4, 4);
                    len4 = ((buf4[3] & 0xFF) << 24) | ((buf4[2] & 0xFF) << 16) | ((buf4[1] & 0xFF) << 8) | (buf4[0] & 0xFF);
                } else {
                    read = read(fis, buf4, 2);
                    len4 = ((buf4[1] & 0xFF) << 8) | (buf4[0] & 0xFF);
                }
            } else
            // implicit datatype
            {
                dt = "  ";
                // getting length
                read = read(fis, buf4, 4);
                len4 = ((buf4[3] & 0xFF) << 24) | ((buf4[2] & 0xFF) << 16) | ((buf4[1] & 0xFF) << 8) | (buf4[0] & 0xFF);
            }

            if (tag2[1] == 0x0000)
            // group
            {
                // getting length
                if (tag2[0] == 0x0002) {
                    read = read(fis, buf4, 4);
                    len4 = ((buf4[3] & 0xFF) << 24) | ((buf4[2] & 0xFF) << 16) | ((buf4[1] & 0xFF) << 8) | (buf4[0] & 0xFF);
                } else
                    skip(fis, len4);
            } else if (tag2[0] == 0x0002 && tag2[1] == 0x0001)
            // version
            {
                short
                        vers;
                read = read(fis, buf4, 2);
                vers = (short) (((buf4[1] & 0xFF) << 8) | (buf4[0] & 0xFF));
            } else if (tag2[0] == 0x0008 && (tag2[1] == 0x1110 || tag2[1] == 0x1111 || tag2[1] == 0x1115 || tag2[1] == 0x1120))
            // 'Referenced Sequences'
            {
                byte[]
                        buff = new byte[12];
                read = read(fis, buff, 8);
            } else if (tag2[0] == 0x0028 && (tag2[1] == 0x0010 || tag2[1] == 0x0011))
            // width || height
            {
                byte[]
                        ch = new byte[len4];
                read = read(fis, ch, len4);
                if (tag2[1] == 0x0010)
                    Ht = ((ch[1] & 0xFF) << 8) | (ch[0] & 0xFF);
                else
                    Wd = ((ch[1] & 0xFF) << 8) | (ch[0] & 0xFF);

            } else if (tag2[0] == 0x7FE0 && tag2[1] == 0x0010)
            // pixel data
            {
                Image = new BufferedImage(Wd, Ht, BufferedImage.TYPE_3BYTE_BGR);

                int
                        bytes = Wd * Ht * 2;

                // reading 'pixels'
                byte[]
                        data = new byte[bytes];
                read = read(fis, data, bytes);

                // + scaling 'pixels' to '0-255' range
                int
                        min = 65535, max = 0;
                for (int pxlNo = 0; pxlNo < bytes; pxlNo += 2) {
                    int
                            val = (((int) data[pxlNo + 1] & 0xFF) << 8) | ((int) data[pxlNo] & 0xFF);
                    if (val > max) max = val;
                    if (val < min) min = val;
                }

                for (int pxlNo = 0, yy = 0; yy < Ht; yy++)
                    for (int xx = 0; xx < Wd; xx++, pxlNo += 2) {
                        int
                                val = (max != 0 ? ((((int) data[pxlNo + 1] & 0xFF) << 8) | ((int) data[pxlNo] & 0xFF)) * 255 / max : 0);
                        Image.setRGB(xx, yy, (val << 16) | (val << 8) | val);
                    }
                // - scaling 'pixels' to '0-255' range

                break;
            } else if (dt.equals("SQ"))
            // 'Sequence of Items'
            {
                byte[]
                        data = new byte[8];
                read = read(fis, data, 8);
            } else if (dt.equals("OB") || dt.equals("FL") || dt.equals("FD") || dt.equals("SS") || dt.equals("US") || len4 > 0)
            // 'Other Byte' || 'Floating Point Single' || 'Floating Point Double' || 'Signed Short' || 'Unsigned Short'
            {
                byte[]
                        data = new byte[len4];
                read = read(fis, data, len4);
                if (len4 > 0)
                    text = new String(data, 0, len4).trim();
            }

            // getting needed data
            if ((tag2[0] == 0x0008 && (tag2[1] == 0x0032 || tag2[1] == 0x0033)) || (tag2[0] == 0x0018 && tag2[1] == 0x1060))
            // 'Acquisition Time' || 'Content Time' || 'Trigger Time'
            {
                double
                        time = Double.parseDouble(text);
                if (tag2[0] == 0x0018)
                    TrigTime = time;
                else if (tag2[1] == 0x0032)
                    AcquTime = time;
                else
                    ContTime = time;
            } else if (tag2[0] == 0x0020 && (tag2[1] == 0x0011 || tag2[1] == 0x0012 || tag2[1] == 0x0013 || tag2[1] == 0x1041))
            // 'Series Number' || 'Acquisition Number' || 'Instance Number' || 'Slice Location'
            {
                switch (tag2[1]) {
                    case 0x0011 -> SeriesNumber = Integer.parseInt(text);
                    case 0x0012 -> AcquNumber = Integer.parseInt(text);
                    case 0x0013 -> InstNumber = Integer.parseInt(text);
                    case 0x1041 -> SliceLocation = Double.parseDouble(text);
                }
            }

            if (tag2[0] == 0x0002 && tag2[1] == 0x0010 && !text.equals("1.2.840.10008.1.2.1"))
                // implicit datatypes format ('Explicit VR Little Endian Transfer Syntax UID')
                expl = false;
        }

        fis.close();
    }

//******************************************************************************

    static private long skip(InputStream IS, int Size)
            throws IOException {
        long
                skipped = 0;
        while (Size > 0) {
            long
                    _skipped = IS.skip(Size);
            if (_skipped <= 0)
                break;
            skipped += _skipped;
            Size -= _skipped;
        }

        return skipped;
    }

//******************************************************************************

    static private int read(InputStream IS, byte[] Buff, int Size)
            throws IOException {
        byte[] _buff = new byte[Size];
        int read = 0;

        while (Size > 0) {
            int
                    _read = IS.read(_buff, 0, Size);
            if (_read < 0)
                break;
            for (int byteNo = 0; byteNo < _read; )
                Buff[read++] = _buff[byteNo++];

            Size -= _read;
        }

        return read;
    }

    @Override
    public int compareTo(DICOMImage other) {
        return Comparator.comparing((DICOMImage toCompare) -> toCompare.SliceLocation, Comparator.reverseOrder())
                .thenComparingInt((DICOMImage toCompare) -> toCompare.SeriesNumber)
                .thenComparingInt((DICOMImage toCompare) -> toCompare.AcquNumber)
                .thenComparingInt((DICOMImage toCompare) -> toCompare.InstNumber)
                .compare(this, other);
    }
}