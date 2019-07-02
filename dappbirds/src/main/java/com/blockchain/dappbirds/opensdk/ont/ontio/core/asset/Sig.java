/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.blockchain.dappbirds.opensdk.ont.ontio.core.asset;

import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.program.Program;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.program.ProgramInfo;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.Serializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Sig extends Serializable {
    public byte[][] pubKeys = null;
    public int M;
    public byte[][] sigData;

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        byte[] invocationScript = reader.readVarBytes();
        byte[] verificationScript = reader.readVarBytes();
        sigData = Program.getParamInfo(invocationScript);
        ProgramInfo info = Program.getProgramInfo(verificationScript);
        pubKeys = info.publicKey;
        M = info.m;
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarBytes(Program.ProgramFromParams(sigData));
        try {
            if(pubKeys.length == 1){
                writer.writeVarBytes(Program.ProgramFromPubKey(pubKeys[0]));
            }else if(pubKeys.length > 1){
                writer.writeVarBytes(Program.ProgramFromMultiPubKey(M,pubKeys));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object json() {
        Map json = new HashMap<>();
        json.put("M", M);
        String[] pks = new String[pubKeys.length];
        for(int i=0;i<pubKeys.length;i++){
            pks[i] = Helper.toHexString(pubKeys[i]);
        }
        String[] sigs = new String[sigData.length];
        for(int i=0;i<sigData.length;i++){
            sigs[i] = Helper.toHexString(sigData[i]);
        }
        json.put("PubKeys", pks);
        json.put("sigData", sigs);
        return json;
    }

}
