package com.blockchain.dappbirds.opensdk.ont.core;



import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.Serializable;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Address;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Fixed8;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.UInt256;

import java.io.IOException;

/**
 *
 */
public class TransactionOutput extends Serializable {
    /**
     *
     */
    public UInt256 assetId;
    /**
     *
     */
    public Fixed8 value;
    /**
     *
     */
    public Address scriptHash;
    
    /**
	 * byte
	 */
	@Override
	public void serialize(BinaryWriter writer) throws Exception {
		writer.writeSerializable(assetId);
		writer.writeSerializable(value);
		writer.writeSerializable(scriptHash);
	}
	
	@Override
	public void deserialize(BinaryReader reader) throws IOException {
		try {
			assetId = reader.readSerializable(UInt256.class);
			value = reader.readSerializable(Fixed8.class);
			scriptHash = reader.readSerializable(Address.class);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IOException();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public String toString() {
		return "TransactionOutput [assetId=" + assetId + ", value=" + value
				+ ", scriptHash=" + scriptHash + "]";
	}
}
