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

package com.blockchain.dappbirds.opensdk.ont.ontio.core.payload;

import com.blockchain.dappbirds.opensdk.ont.ontio.common.Address;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.Transaction;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.TransactionType;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvokeCode extends Transaction {
    public byte[] code;

    public InvokeCode() throws Exception {
        super(TransactionType.InvokeCode);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        code = reader.readVarBytes();
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws Exception {
        writer.writeVarBytes(code);
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    @Override
    public Object json() throws Exception {
        Map obj = (Map) super.json();
        Map payload = new HashMap();
        payload.put("Code", Helper.toHexString(code));
        obj.put("Payload", payload);
        return obj;
    }
}