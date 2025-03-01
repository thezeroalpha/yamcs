package org.yamcs.tctm.ccsds;

import org.yamcs.tctm.ccsds.TcManagedParameters.TcVcManagedParameters;
import org.yamcs.tctm.ccsds.TcTransferFrame.SegmentHeader;
import org.yamcs.tctm.ccsds.UplinkManagedParameters.FrameErrorDetection;
import org.yamcs.tctm.ccsds.error.CrcCciitCalculator;
import org.yamcs.utils.ByteArrayUtils;
import org.yamcs.utils.TimeEncoding;

public class TcFrameFactory {
    final private TcManagedParameters tcParams;
    final private TcVcManagedParameters vcParams;
    final CrcCciitCalculator crc;

    public TcFrameFactory(TcVcManagedParameters vcParams) {
        this.tcParams = vcParams.tcParams;
        this.vcParams = vcParams;
        FrameErrorDetection err = vcParams.getErrorDetection();
        if (err == FrameErrorDetection.CRC16) {
            crc = new CrcCciitCalculator();
        } else {
            crc = null;
        }
    }

    /**
     * Makes a new frame of the given length with the generation time set to the current wall clock time
     * 
     * @param dataLength
     * @return
     */
    public TcTransferFrame makeCtrlFrame(int dataLength) {
        return makeFrame(dataLength, true, TimeEncoding.getWallclockTime(), (byte) -1);
    }

    public TcTransferFrame makeDataFrame(int dataLength, long generationTime) {
        return makeDataFrame(dataLength, generationTime, (byte) -1);
    }

    public TcTransferFrame makeDataFrame(int dataLength, long generationTime, byte mapId) {
        if (vcParams.mapId >= 0) {
            if (mapId < 0) {
                mapId = vcParams.mapId;
            }
        } else if (mapId >= 0) {
            throw new IllegalArgumentException(
                    "mapId " + mapId + " specified but this virtual channel does not use the MAP service");
        }

        return makeFrame(dataLength, false, generationTime, mapId);
    }

    private TcTransferFrame makeFrame(int dataLength, boolean cmdControl, long generationTime, byte mapId) {
        int dataStart = 5;
        int length = dataLength + dataStart;
        if (crc != null) {
            length += 2;
        }

        if (!cmdControl && vcParams.mapId >= 0) {
            length += 1;
            dataStart += 1;
        }

        if (length > tcParams.getMaxFrameLength()) {
            throw new IllegalArgumentException("Resulting frame length " + length + " is more than the maximum allowed "
                    + tcParams.getMaxFrameLength());
        }
        byte[] data = new byte[length];

        TcTransferFrame ttf = new TcTransferFrame(data, tcParams.spacecraftId, vcParams.vcId, cmdControl);
        if (!cmdControl && mapId >= 0) {
            ttf.setSegmentHeader(new SegmentHeader((byte) 3, mapId));
        }

        ttf.setDataStart(dataStart);
        ttf.setDataEnd(dataStart + dataLength);

        return ttf;
    }

    /**
     * retrieves the headers size + CRC size
     */
    public int getFramingLength(int vcId) {
        int length = 5;
        if (crc != null) {
            length += 2;
        }
        return length;
    }

    public byte[] encodeFrame(TcTransferFrame ttf) {

        byte[] data = ttf.getData();
        int w0 = tcParams.spacecraftId;
        if (ttf.isBypass()) {
            w0 += (1 << 13);
        }
        if (ttf.isCmdControl()) {
            w0 += (1 << 12);
        }
        ByteArrayUtils.encodeUnsignedShort(w0, data, 0);
        int w1 = (ttf.getVirtualChannelId() << 10) + (data.length - 1);
        ByteArrayUtils.encodeUnsignedShort(w1, data, 2);
        data[4] = (byte) ttf.getVcFrameSeq();
        if (ttf.segmentHeader != null) {
            data[5] = ttf.segmentHeader.get();
        }
        if (crc != null) {
            int c = crc.compute(data, 0, data.length - 2);
            ByteArrayUtils.encodeUnsignedShort(c, data, data.length - 2);
        }
        return data;
    }
}
