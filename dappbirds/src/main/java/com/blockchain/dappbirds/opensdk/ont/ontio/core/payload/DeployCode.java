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

import java.io.IOException;

import com.blockchain.dappbirds.opensdk.ont.ontio.common.Address;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.Transaction;
import com.blockchain.dappbirds.opensdk.ont.ontio.core.transaction.TransactionType;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryReader;
import com.blockchain.dappbirds.opensdk.ont.ontio.io.BinaryWriter;

public class DeployCode extends Transaction {
    public byte[] code;
    public boolean needStorage;
    public String name;
    public String version;
    public String author;
    public String email;
    public String description;


    public DeployCode() throws Exception {
        super(TransactionType.DeployCode);
    }

    @Override
    protected void deserializeExclusiveData(BinaryReader reader) throws IOException {
        code = reader.readVarBytes();
        needStorage = reader.readBoolean();
        name = reader.readVarString();
        version = reader.readVarString();
        author = reader.readVarString();
        email = reader.readVarString();
        description = reader.readVarString();
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws Exception {
        writer.writeVarBytes(code);
        writer.writeBoolean(needStorage);
        writer.writeVarString(name);
        writer.writeVarString(version);
        writer.writeVarString(author);
        writer.writeVarString(email);
        writer.writeVarString(description);
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }
}
