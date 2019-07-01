package com.blockchain.dappbirds.opensdk.ont.ontio.core.ontid;

import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.Serializable;

import java.io.IOException;

public class Attribute extends Serializable {
    public byte[] key;
    public byte[] valueType;
    public byte[] value;
    public Attribute(){}
    public Attribute(byte[] key,byte[] valueType,byte[] value){
        this.key = key;
        this.valueType = valueType;
        this.value = value;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.key = reader.readVarBytes();
        this.valueType = reader.readVarBytes();
        this.value = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarBytes(key);
        writer.writeVarBytes(valueType);
        writer.writeVarBytes(value);
    }
}
