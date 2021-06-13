package com.example.sockettest.bean;

import java.nio.ByteOrder;

import javolution.io.Struct;

public class ConverEnvInfo extends Struct  {
    
    public final Signed32 snum = new Signed32();
    public final Float32 temperature = new Float32();
    public final Float32 humidity = new Float32();
    public final Float32 ill = new Float32();
    public final Float32 bet = new Float32();
    public final Float32 adc = new Float32();
    public final Signed8 x = new Signed8();
    public final Signed8 y = new Signed8();
    public final Signed8 z = new Signed8();


    @Override
    public boolean isPacked() {
        return true;
    }


    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }

}
