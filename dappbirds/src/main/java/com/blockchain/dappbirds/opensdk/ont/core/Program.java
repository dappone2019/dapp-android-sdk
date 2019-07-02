package com.blockchain.dappbirds.opensdk.ont.core;


import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.Serializable;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.exception.SDKException;

import java.io.IOException;

/**
 *
 */
public class Program extends Serializable {
    public byte[] parameter;
    public byte[] code;
    public Program(){}
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        parameter = reader.readVarBytes();	// sign data
        code = reader.readVarBytes();		// pubkey
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarBytes(parameter);
        writer.writeVarBytes(code);
    }
    public static byte[] ProgramFromParams(byte[][] sigData) throws IOException, SDKException {
        return com.blockchain.dappbirds.opensdk.ont.ontio.core.program.Program.ProgramFromParams(sigData);
    }
    public static byte[] ProgramFromPubKey(byte[] publicKey) throws Exception {
        return com.blockchain.dappbirds.opensdk.ont.ontio.core.program.Program.ProgramFromPubKey(publicKey);
    }
    public static byte[] ProgramFromMultiPubKey(int m, byte[]... publicKeys) throws Exception {
        return com.blockchain.dappbirds.opensdk.ont.ontio.core.program.Program.ProgramFromMultiPubKey(m,publicKeys);
    }

}
