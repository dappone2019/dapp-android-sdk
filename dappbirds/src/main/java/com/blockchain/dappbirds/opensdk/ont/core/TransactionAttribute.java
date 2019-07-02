package com.blockchain.dappbirds.opensdk.ont.core;


import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.Serializable;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class TransactionAttribute extends Serializable {
	/**
	 *
	 */
	public TransactionAttributeUsage usage;
	/**
	 *
	 */
	public byte[] data;
	
	/**
	 *
	 */
	@Override
	public void serialize(BinaryWriter writer) throws Exception {
		// usage
        writer.writeByte(usage.value());
        // data
		if (usage == TransactionAttributeUsage.Script){
			writer.write(data);
		}else if( usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
            writer.writeVarBytes(data);
        } else {
            throw new IOException();
        }
	}

	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		// usage
		usage = TransactionAttributeUsage.valueOf(reader.readByte());
		// data
        if (usage == TransactionAttributeUsage.Script){
			data = reader.readBytes(20);
		}else if(usage == TransactionAttributeUsage.DescriptionUrl
        		|| usage == TransactionAttributeUsage.Description
        		|| usage == TransactionAttributeUsage.Nonce) {
        			data = reader.readVarBytes(255);
        } else {
            throw new IOException();
        }
	}

	
	@Override
	public String toString() {
		return "TransactionAttribute [usage=" + usage + ", data="
				+ Arrays.toString(data) + "]";
	}
}
