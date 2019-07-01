package com.blockchain.dappbirds.opensdk.ont.core.transaction;



import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Address;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Fixed8;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.TransactionType;


import java.io.IOException;

public class InvocationTransaction extends TransactionNeo {
	public byte[] script;
	public Fixed8 gas;

	public InvocationTransaction() {
		super(TransactionType.InvokeCode);
	}

	@Override
	protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
		try {
			script = reader.readVarBytes();
			gas = reader.readSerializable(Fixed8.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void serializeExclusiveData(BinaryWriter writer)  {
		try {
			writer.writeVarBytes(script);
			writer.writeSerializable(gas);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public Address[] getAddressU160ForVerifying() {
		return null;
	}
}
